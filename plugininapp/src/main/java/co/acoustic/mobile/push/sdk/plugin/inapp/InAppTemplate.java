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


import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;
import android.content.Context;

import java.util.Map;

/**
 * This is the interface for imApp templates
 */
public interface InAppTemplate {

   /**
     * This method checks if the template requires offline resources before displaying
     * @return true if required, false otherwise
     */
    boolean requiresOfflineResources();

    /**
     * This method creates offline resources required for the message display
     * @param context The application's context
     * @param message The inapp message
     * @return A map from url to downloaded image
     */
    Map<String, Object> createOfflineResources(Context context, InAppPayload message);

    /**
     * Thisd method shows the inapp message
     * @param context The application's context
     * @param fragmentManager The fragment manager
     * @param containerViewId The container view id
     * @param message The inapp message
     * @param offlineResources Offline resources required for display
     */
    void show(Context context, FragmentManager fragmentManager, @IdRes int containerViewId,InAppPayload message, Map<String, Object> offlineResources);

}
