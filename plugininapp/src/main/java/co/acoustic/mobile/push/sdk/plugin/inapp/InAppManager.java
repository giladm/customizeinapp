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
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

import co.acoustic.mobile.push.sdk.api.MceSdk;
import co.acoustic.mobile.push.sdk.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class manages inapp messages
 */
public class InAppManager {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2015, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";
    private static final String TAG = "InAppManager";


    /**
     * Show the first inapp message. The message can be the first of all the messages or it can be the first of messages with a given key value
     *
     * @param context         The application's context
     * @param key             The key for a key value search. For no search use null
     * @param values          The value for a key value search. For no search use null
     * @param fragmentManager The fragment manager that contains the fragment on which the view will be displayed
     * @param containerViewId The id of the view that the message will be displayed on
     */
    public static void show(final Context context, InAppStorage.KeyName key, List<String> values, final FragmentManager fragmentManager, final @IdRes int containerViewId) {
        try {
            if(!MceSdk.isSdkStopped(context)) {
                final InAppPayload inAppPayload = InAppStorage.findFirst(context, key, values);
                if (inAppPayload == null) return;

                Class<? extends InAppTemplate> templateClass = InAppTemplateRegistry.getInstance().get(inAppPayload.getTemplateName());
                if (templateClass == null) return;
                try {
                    final InAppTemplate template = templateClass.newInstance();
                    if(!template.requiresOfflineResources()) {
                        template.show(context, fragmentManager, containerViewId, inAppPayload, null); // pass InAppMessage
                    } else {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Map<String, Object> resources = template.createOfflineResources(context, inAppPayload);
                                template.show(context, fragmentManager, containerViewId, inAppPayload, resources); // pass InAppMessage
                            }
                        }).start();
                    }
                    InAppEvents.sendInAppMessageOpenedEvent(context, inAppPayload);
                    InAppStorage.updateMaxViews(context, inAppPayload);
                    if(inAppPayload.isFromPull()) {
                        InAppPlugin.updateInAppMessage(context, inAppPayload);
                    }
                } catch (Exception e) {
                    Logger.e(TAG, "Error while showing the InApp Message. Details: " + e.getMessage(), e);
                }
            }
        } catch (Throwable t) {
            Logger.e(TAG, "Failed show inApp", t);
        }
    }

    /**
     * Show the first inapp message from all the inapp messages.
     *
     * @param context         The application's context
     * @param fragmentManager The fragment manager that contains the fragment on which the view will be displayed
     * @param containerViewId The id of the view that the message will be displayed on
     */
    public static void show(Context context, FragmentManager fragmentManager, @IdRes int containerViewId) {
        show(context, null, null, fragmentManager, containerViewId);
    }

    /**
     * Extracts an inapp segement of the notification if exists and adds it to the database
     *
     * @param context     The application's context
     * @param msgExtras   The bundle that contains the notification
     * @param attribution The attribution for the inapp message
     * @param mailingId The notification mailing id
     */
    public static void handleNotification(Context context, Bundle msgExtras, String attribution, String mailingId) {
        if (!msgExtras.containsKey("inApp")) {
            Logger.d(TAG, "inApp payload not found");
            return;
        }
        try {
            String inAppJSON = msgExtras.getString("inApp");
            Logger.d(TAG,"processing inApp payload: "+inAppJSON);
            InAppPayload inAppPayload = InAppPayloadJsonTemplate.inAppPayloadFromJSON(new JSONObject(inAppJSON));
            inAppPayload.setMceContext(attribution, mailingId);
            InAppStorage.save(context, inAppPayload, false);
            Logger.d(TAG,"Saved inApp payload: "+inAppPayload);
        } catch (JSONException jsone) {
            Logger.e(TAG, "Error while parsing the InApp Message. Details: " + jsone.getMessage(), jsone);
        } catch (IOException ioe) {
            Logger.e(TAG, "Error while parsing the InApp Message. Details: " + ioe.getMessage(), ioe);
        }

    }

    /**
     * Deletes an inapp message
     * @param context The application's context
     * @param messageId The inapp message id
     */
    public static void delete(Context context, String messageId) {
        InAppPayload inAppPayload = InAppStorage.getInappPayload(context, messageId);
        if(inAppPayload != null && inAppPayload.isFromPull()) {
            inAppPayload.finish();
            InAppPlugin.updateInAppMessage(context, inAppPayload);
        }
        InAppStorage.delete(context, messageId);

    }



    /**
     * This method clears all inapp messages
     * @param context The application's context
     */
    public static void clearData(Context context) {
        Logger.d(TAG, "Clearing inApp data");
        InAppStorage.clear(context);
    }




}
