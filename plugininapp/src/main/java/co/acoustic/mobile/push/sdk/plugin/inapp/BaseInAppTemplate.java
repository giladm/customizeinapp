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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import co.acoustic.mobile.push.sdk.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * This class is the base inapp template class
 */
public abstract class BaseInAppTemplate implements InAppTemplate {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2015, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";

    static final String MESSAGE_ID_KEY = "messageId";
    static final String ATTRIBUTION_KEY = "attribution";
    static final String MAILING_ID_KEY = "mailingId";
    static final String ACTION_KEY = "action";
    static final String ACTION_TYPE_KEY = "actionType";
    static final String TYPE_KEY = "type";
    static final String PAYLOAD_KEY = "payload";
    private static final String TAG ="BaseInAppTemplate";

    /**
     * Thisd method shows the inapp message
     * @param context The application's context
     * @param fragmentManager The fragment manager
     * @param containerViewId The container view id
     * @param message The inapp message
     * @param offlineResources Offline resources required for display
     */
    @Override
    public void show(Context context, FragmentManager fragmentManager, int containerViewId, InAppPayload message, Map<String, Object> offlineResources) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        InAppFragment fragment = createFragment();
        Bundle arguments = new Bundle();
        try {
            arguments.putString(MESSAGE_ID_KEY, message.getId());
            arguments.putString(ATTRIBUTION_KEY, message.getAttribution());
            arguments.putString(MAILING_ID_KEY, message.getMailingId());
            JSONObject actionJSON = message.getTemplateContent().optJSONObject(ACTION_KEY);
            if(actionJSON != null) {
                arguments.putString(ACTION_TYPE_KEY, actionJSON.getString(TYPE_KEY));
                Bundle payload = new Bundle();
                Iterator<String> keys = actionJSON.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    if(!key.equals(TYPE_KEY)) {
                        payload.putString(key, actionJSON.get(key).toString());
                    }
                }
                arguments.putBundle(PAYLOAD_KEY, payload);
            }
            setupArguments(context, message, arguments, offlineResources);
            fragment.setArguments(arguments);
            fragmentTransaction.add(containerViewId, fragment, getFragmentLayoutId());
            fragmentTransaction.commit();
        } catch (JSONException e) {
            Logger.d(TAG, "Error while showing notification.", e);
        }
    }

    protected abstract void setupArguments(Context context, InAppPayload message, Bundle arguments, Map<String, Object> offlineResources) throws JSONException;

    protected abstract String getFragmentLayoutId();

    protected abstract InAppFragment createFragment();
}
