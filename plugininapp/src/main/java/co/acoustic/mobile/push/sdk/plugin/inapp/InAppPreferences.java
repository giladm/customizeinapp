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

import co.acoustic.mobile.push.sdk.Preferences;
import co.acoustic.mobile.push.sdk.util.Logger;

class InAppPreferences extends Preferences {

   private static final String SQLITE_ACTIVATED_FLAG = "sqliteActivated";
    private static final String TAG = "InAppPreferences";

    public static boolean isSqliteActivated(Context context) {
        return getBoolean(context, SQLITE_ACTIVATED_FLAG, false);
    }

    public static void setSqliteActivated(Context context) {
        setBoolean(context, SQLITE_ACTIVATED_FLAG, true);
    }

    private static final String INAPP_STATUS_UPDATE_PAYLOAD = "inAppStatusUpdatePayload";


    public static String getInAppStatusUpdatePayload(Context context) {
        return getString(context, INAPP_STATUS_UPDATE_PAYLOAD, null);
    }

    public static void setInAppStatusUpdatePayload(Context context, String payload) {
        setString(context, INAPP_STATUS_UPDATE_PAYLOAD, payload);
        Logger.d(TAG,"Setting inApp status payload to "+payload);
    }
}
