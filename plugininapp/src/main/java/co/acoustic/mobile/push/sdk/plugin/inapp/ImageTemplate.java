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

import co.acoustic.mobile.push.sdk.api.MediaManager;
import co.acoustic.mobile.push.sdk.util.Logger;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the image inapp template
 */
public class ImageTemplate extends BaseInAppTemplate {

    static final String IMAGE_KEY = "image";
    static final String TITLE_KEY = "title";
    static final String TEXT_KEY = "text";
    static final String DURATION_KEY = "duration";
    private static final String TAG = "ImageTemplate";


    /**
     * This method downloads the required images, puts the in the cache and adds their url reference to the offline resources map
     * @param context The application's context
     * @param message The inapp message
     * @return A map from image key to image url
     */
    @Override
    public Map<String, Object> createOfflineResources(Context context, InAppPayload message) {
        Map<String, Object> resources = new HashMap<String, Object>();
        try {
            String src = message.getTemplateContent().getString(IMAGE_KEY);
            MediaManager.loadImageInCurrentThread(src);
            resources.put(IMAGE_KEY, src);
            Logger.d(TAG,"Setting image resource as url: "+src);
         } catch (Exception e) {

        }
        return resources;
    }

    /**
     * This method checks if the template requires offline resources before displaying
     * @return true
     */
    @Override
    public boolean requiresOfflineResources() {
        return true;
    }



    @Override
    protected void setupArguments(Context context, InAppPayload message, Bundle arguments, Map<String, Object> offlineResources) throws JSONException {
        if(offlineResources.containsKey(IMAGE_KEY)) {
            Logger.d(TAG,"Setting image url in arguments: "+offlineResources.get(IMAGE_KEY));
            arguments.putString(IMAGE_KEY, (String)offlineResources.get(IMAGE_KEY));
        }
        arguments.putString(TITLE_KEY, message.getTemplateContent().optString(TITLE_KEY));
        arguments.putString(TEXT_KEY, message.getTemplateContent().optString(TEXT_KEY));
        arguments.putInt(DURATION_KEY, message.getTemplateContent().optInt(DURATION_KEY, 5));
    }

    @Override
    protected String getFragmentLayoutId() {
        return "mce-image";
    }

    @Override
    protected InAppFragment createFragment() {
        return new ImageFragment();
    }

    private static void getBitmapFromURL(String bitmapURL) {
        MediaManager.loadImageInCurrentThread(bitmapURL);
    }




}

