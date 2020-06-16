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
import androidx.fragment.app.Fragment;

import co.acoustic.mobile.push.sdk.api.notification.MceNotificationAction;
import co.acoustic.mobile.push.sdk.api.notification.MceNotificationActionRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

abstract class InAppFragment extends Fragment{

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2016, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";

    private static final String TAG = "InAppFragment";

    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    private boolean closed = false;
    private boolean hibernated = false;

    protected void closeAfter(int seconds) {
        Runnable closeTask = new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!closed) {
                            closed = true;
                            if(!hibernated) {
                                close();
                            }
                        }
                    }
                });

            }
        };
        scheduledExecutor.schedule(closeTask, seconds, TimeUnit.SECONDS);
    }

    protected void close() {
        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(getFragmentTagName());
        getActivity().getSupportFragmentManager().beginTransaction().hide(fragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction().detach(fragment).commit();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        hibernated = true;
    }



    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        hibernated = false;
        if(closed) {
            close();
        }
    }

    protected void performAction(Context context) {
        InAppManager.delete(context, getArguments().getString(BaseInAppTemplate.MESSAGE_ID_KEY));
        close();
        String actionType = getArguments().getString("actionType");
        if (actionType != null) {
            MceNotificationAction actionImpl = MceNotificationActionRegistry.getNotificationAction(actionType);
            if (actionImpl != null) {
                Map<String, String> payload = new HashMap<String, String>();
                Bundle payloadBundle = getArguments().getBundle("payload");
                for (String key : payloadBundle.keySet()) {
                    payload.put(key, payloadBundle.getString(key));
                }
                actionImpl.handleAction(context, actionType, actionType, null, null, payload, false);
                InAppEvents.sendInAppMessageActionTakenEvent(context, actionType, payloadBundle, getArguments().getString("attribution"), getArguments().getString("mailingId"));
            }
        }
    }

    protected abstract String getFragmentTagName();
}
