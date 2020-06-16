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
 * This is the inApp video template
 */
public class VideoTemplate extends BaseInAppTemplate {

   public static final String TITLE_KEY = "title";
    public static final String TEXT_KEY = "text";
    public static final String VIDEO_KEY = "video";


    /**
     * No resources are created
     * @param context The application's context
     * @param message The inapp message
     * @return null
     */
    @Override
    public Map<String, Object> createOfflineResources(Context context, InAppPayload message) {
        return null;
    }

    /**
     * Resources are not required
     * @return false
     */
    @Override
    public boolean requiresOfflineResources() {
        return false;
    }

    @Override
    protected void setupArguments(Context context, InAppPayload message, Bundle arguments, Map<String, Object> offlineResources) throws JSONException {
        arguments.putString(TITLE_KEY, message.getTemplateContent().optString(TITLE_KEY));
        arguments.putString(TEXT_KEY, message.getTemplateContent().optString(TEXT_KEY));
        arguments.putString(VIDEO_KEY, message.getTemplateContent().optString(VIDEO_KEY));
    }

    @Override
    protected String getFragmentLayoutId() {
        return "mce-video";
    }

    @Override
    protected InAppFragment createFragment() {
        return new VideoFragment();
    }

}

