/*
 * Copyright © 2011, 2019 Acoustic, L.P. All rights reserved.
 *
 * NOTICE: This file contains material that is confidential and proprietary to
 * Acoustic, L.P. and/or other developers. No license is granted under any intellectual or
 * industrial property rights of Acoustic, L.P. except as may be provided in an agreement with
 * Acoustic, L.P. Any unauthorized copying or distribution of content from this file is
 * prohibited.
 */
package co.acoustic.mobile.push.sdk.plugin.inapp;

import android.content.Context;

import co.acoustic.mobile.push.sdk.MceServerUrl;
import co.acoustic.mobile.push.sdk.api.MceSdk;
import co.acoustic.mobile.push.sdk.api.registration.RegistrationDetails;
import co.acoustic.mobile.push.sdk.plugin.Plugin;
import co.acoustic.mobile.push.sdk.util.HttpHelper;
import co.acoustic.mobile.push.sdk.util.Iso8601;
import co.acoustic.mobile.push.sdk.util.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class InAppPlugin implements Plugin {

    private static final String TAG = "InAppPlugin";

    private static class InAppUpdateUrl extends MceServerUrl {
        static final String INBOX_PART = "inbox";
        static final String STATUS_PART = "status";


        final String getInAppUpdateUrl(String baseUrl, Context context) {
            RegistrationDetails registrationDetails = MceSdk.getRegistrationClient().getRegistrationDetails(context);
            return buildURL(baseUrl, VERSION_PART, APPS_PART, MceSdk.getRegistrationClient().getAppKey(context), INBOX_PART, USERS_PART, registrationDetails.getUserId(), CHANNELS_PART, registrationDetails.getChannelId(), STATUS_PART);

        }

        final String getInAppUpdateUrl(Context context) {
            return getInAppUpdateUrl(getBaseUrl(), context);
        }
    }

    private static final Object INAPP_STATUS_UPDATE_SYNC = new Object();

    public static void updateInAppMessage(final Context context, final InAppPayload inAppPayload) {
        (new Thread() {
            @Override
            public void run() {
                synchronized (INAPP_STATUS_UPDATE_SYNC) {
                    Logger.d(TAG, "inApp message state update is performed for: " + inAppPayload);
                    HttpHelper.Response response = null;
                    try {
                        JSONObject payload = new JSONObject();
                        JSONArray updates = new JSONArray();
                        String inappStatusUpdatePayload = InAppPreferences.getInAppStatusUpdatePayload(context);
                        if(inappStatusUpdatePayload != null) {
                            JSONObject oldPayload = new JSONObject(inappStatusUpdatePayload);
                            updates = oldPayload.getJSONArray("inAppUpdates");
                        }
                        JSONObject updatePayload = new JSONObject();
                        updatePayload.put("inAppMessageId", inAppPayload.getId());
                        updatePayload.put("numViews", inAppPayload.getViews());
                        updatePayload.put("maxViews", inAppPayload.getMaxViews());
                        updatePayload.put("timestamp", Iso8601.toString(new Date(System.currentTimeMillis())));

                        updates.put(updatePayload);
                        payload.put("inAppUpdates", updates);
                        InAppPreferences.setInAppStatusUpdatePayload(context, payload.toString());
                        sendInAppStatusUpdate(context);
                    } catch (Exception e) {
                        Logger.e(TAG, "Failed to send inapp status update" + (response != null ? response.getHttpResponseCode() + " " + response.getHttpResponseMessage() : "null response"), e);
                    }
                }
            }
        }).start();
    }

    private static boolean sendInAppStatusUpdate(Context context) throws IOException {
        synchronized (INAPP_STATUS_UPDATE_SYNC) {
            String payloadString = InAppPreferences.getInAppStatusUpdatePayload(context);
            if(payloadString == null) {
                return true;
            }
            String inAppUpdateUrl = new InAppUpdateUrl().getInAppUpdateUrl(context);
            HttpHelper.Response response = HttpHelper.postJson(inAppUpdateUrl, payloadString);
            if (response != null && response.getHttpResponseCode() == 202) {
                Logger.d(TAG, "inApp status update succeeded for: " + payloadString);
                InAppPreferences.setInAppStatusUpdatePayload(context, null);
                return true;
            } else {
                Logger.e(TAG, "Failed updating inApp status: " + payloadString + " " + (response != null ? response.getHttpResponseCode() + " " + response.getHttpResponseMessage() : "null response"));
                return false;
            }
        }
    }

    private static void sendInAppStatusUpdateInNewThread(final Context context)  {
        (new Thread() {
            @Override
            public void run() {
                try {
                    sendInAppStatusUpdate(context);
                } catch (Exception e) {
                    Logger.e(TAG, "Failed to send inapp status update");
                }
            }
        }).start();
    }

    @Override
    public void sync(Context context, JSONObject syncData) {
        try {
            JSONArray inAppMessages = syncData.optJSONArray("inAppMessages");
            if(inAppMessages != null) {
                Logger.d(TAG, "Processing inApp sync data: "+inAppMessages);
                List<InAppPayload> messages = (new InAppPayloadJsonTemplate.InAppPayloadListTemplate()).fromJSONArray(inAppMessages);
                for(InAppPayload inAppMessage : messages) {
                    try {
                        InAppStorage.update(context, inAppMessage, true);
                    } catch (IOException ioe) {
                        Logger.e(TAG,"Failed to update inApp message", ioe);
                    }
                }
            } else {
                Logger.d(TAG, "No inApp sync data found");
            }
        } catch (JSONException jsone) {
            Logger.e(TAG,"Failed to load inApp messages sync data", jsone);
        }
    }

    @Override
    public void clearData(Context context) {
        Logger.d(TAG,"Clearing inApp data");
        InAppStorage.clear(context);
    }

    @Override
    public boolean sendStatusUpdates(Context context, boolean newThread) {
        if(newThread) {
            sendInAppStatusUpdateInNewThread(context);
            return false;
        } else {
            try {
                return sendInAppStatusUpdate(context);
            } catch (Exception e) {
                Logger.e(TAG, "Failed to send inApp status update", e);
                return false;
            }
        }
    }
}
