/*
 * Licensed Materials - Property of IBM
 *
 * 5725E28, 5725I03
 *
 * © Copyright IBM Corp. 2011, 2015
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
package co.acoustic.mobile.push.samples.android;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import co.acoustic.mobile.push.sdk.api.notification.Action;
import co.acoustic.mobile.push.sdk.api.notification.MceNotificationAction;
import co.acoustic.mobile.push.sdk.api.notification.NotificationDetails;
import co.acoustic.mobile.push.sdk.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyUrlAction implements MceNotificationAction {

    public static final String TAG = "MyUrlAction";


    @Override
    public void handleAction(Context context, String type, String name, String attribution, String mailingId, Map<String, String> payload, boolean fromNotification) {
        String valueJSONStr = payload.get(Action.KEY_VALUE);

        if(valueJSONStr != null && !(valueJSONStr.trim().length()==0)) {
            try {
                JSONObject valueJSON = new JSONObject(valueJSONStr);
                Log.i(TAG,"json:"+valueJSONStr);
            } catch (JSONException jsone) {
                Logger.e(TAG, "Failed to parse JSON  message", jsone);
            }
        } else {
            Log.e(TAG, "No content for URL action:"+valueJSONStr);
        }
    }

    @Override
    public void init(Context context, JSONObject jsonObject) {

    }

    @Override
    public void update(Context context, JSONObject jsonObject) {

    }

    @Override
    public boolean shouldDisplayNotification(Context context, NotificationDetails notificationDetails, Bundle bundle) {
        return true;
    }
}
