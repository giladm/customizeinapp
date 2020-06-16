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

import org.json.JSONException;

import java.util.Map;

/**
 * This is the mini inApp template
 */
public class MiniTemplate extends BaseInAppTemplate {

   static final String ORIENTATION_TOP="top";
    static final String ORIENTATION_BOTTOM="bottom";
    static final String ICON_KEY = "icon";
    static final String COLOR_KEY = "color";
    static final String TEXT_KEY = "text";
    static final String ORIENTATION_KEY = "orientation";
    static final String FOREGROUND_KEY = "foreground";
    static final String DURATION_KEY = "duration";

    /**
     * No reslources are needed
     * @return false
     */
    @Override
    public boolean requiresOfflineResources() {
        return false;
    }

    /**
     * No resources aer created
     * @param context The application's context
     * @param message The inapp message
     * @return null
     */
    @Override
    public Map<String, Object> createOfflineResources(Context context, InAppPayload message) {
        return null;
    }

    @Override
    protected void setupArguments(Context context, InAppPayload message, Bundle arguments, Map<String, Object> offlineResources) throws JSONException {
        arguments.putString(ICON_KEY, message.getTemplateContent().getString(ICON_KEY));
        arguments.putString(COLOR_KEY, message.getTemplateContent().getString(COLOR_KEY));
        arguments.putString(TEXT_KEY, message.getTemplateContent().getString(TEXT_KEY));
        arguments.putString(ORIENTATION_KEY, message.getTemplateContent().optString(ORIENTATION_KEY, ORIENTATION_BOTTOM));
        arguments.putString(FOREGROUND_KEY, message.getTemplateContent().optString(FOREGROUND_KEY, null));
        arguments.putInt(DURATION_KEY, message.getTemplateContent().optInt(DURATION_KEY, 5));

    }

    @Override
    protected String getFragmentLayoutId() {
        return "mce-mini";
    }

    @Override
    protected InAppFragment createFragment() {
        return new MiniFragment();
    }

}

