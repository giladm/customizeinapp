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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.acoustic.mobile.push.sdk.util.Logger;

import java.util.HashMap;

/**
 * This is the mini template fragment
 */
public class MiniFragment extends InAppFragment implements UiInteractionController.Listener {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2015, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";

    private static final String TAG = "MiniFragment";

    /**
     * This method is called when the fragment is created
     * @param inflater The layout inflater
     * @param container The fragment container
     * @param savedInstanceState The saved state
     * @return The created view
     */
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HashMap<String, Integer> icons = new HashMap<String, Integer>();
        icons.put("note", getResources().getIdentifier("ic_event_note_black_24dp", "drawable", inflater.getContext().getApplicationContext().getPackageName()));
        icons.put("notification", getResources().getIdentifier("ic_notifications_black_24dp", "drawable", inflater.getContext().getApplicationContext().getPackageName()));
        icons.put("store", getResources().getIdentifier("ic_local_grocery_store_black_24dp", "drawable", inflater.getContext().getApplicationContext().getPackageName()));
        icons.put("offer", getResources().getIdentifier("ic_local_offer_black_24dp", "drawable", inflater.getContext().getApplicationContext().getPackageName()));
        icons.put("comment", getResources().getIdentifier("ic_mode_comment_black_24dp", "drawable", inflater.getContext().getApplicationContext().getPackageName()));

        // Inflate the layout for this fragment
        View view = inflater.inflate(getResources().getIdentifier("mini_fragment", "layout", inflater.getContext().getApplicationContext().getPackageName()), container, false);
        TextView textView = (TextView) view.findViewById(getResources().getIdentifier("label", "id", inflater.getContext().getApplicationContext().getPackageName()));
        if (getArguments().getString(MiniTemplate.TEXT_KEY) != null) {
            textView.setText(getArguments().getString(MiniTemplate.TEXT_KEY));
        }

        LinearLayout miniLayout = (LinearLayout) view.findViewById(getResources().getIdentifier("mini_layout", "id", inflater.getContext().getApplicationContext().getPackageName()));
        if (getArguments().getString(MiniTemplate.ORIENTATION_KEY).equals(MiniTemplate.ORIENTATION_TOP)) {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                setupOrientation(miniLayout);
            }

        }

        UiInteractionController uiInteractionController = new UiInteractionController(textView, inflater.getContext().getApplicationContext(), null, this);
        textView.setOnTouchListener(uiInteractionController);

        if (getArguments().getString(MiniTemplate.COLOR_KEY) != null) {
            miniLayout.setBackgroundColor(Color.parseColor(getArguments().getString(MiniTemplate.COLOR_KEY)));
        }
        if (getArguments().getString(MiniTemplate.FOREGROUND_KEY) != null) {
            textView.setTextColor(Color.parseColor(getArguments().getString(MiniTemplate.FOREGROUND_KEY)));
        }

        if(getArguments().getInt(MiniTemplate.DURATION_KEY) > 0) {
            final int duration = getArguments().getInt(MiniTemplate.DURATION_KEY);
            closeAfter(duration);
        }

        ImageView icon = (ImageView) view.findViewById(getResources().getIdentifier("icon", "id", inflater.getContext().getApplicationContext().getPackageName()));
        icon.setImageResource(icons.get(getArguments().getString(MiniTemplate.ICON_KEY)));

        view.findViewById(getResources().getIdentifier("close", "id", inflater.getContext().getApplicationContext().getPackageName())).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        return view;

    }

    @TargetApi(17)
    void setupOrientation(LinearLayout miniLayout) {
        // crashes when uncommented
//        ((RelativeLayout.LayoutParams) miniLayout.getLayoutParams()).removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        ((RelativeLayout.LayoutParams) miniLayout.getLayoutParams()).addRule(RelativeLayout.ALIGN_PARENT_TOP);
        miniLayout.setGravity(Gravity.TOP);
    }


    /**
     * This method is called when the fragment view is tapped
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onTap(Context context, View view) {
        try {
            performAction(context);
        } catch (Throwable t) {
            Logger.e(TAG, "Failed onTap", t);
        }
        return true;
    }

    /**
     * This method is called when the fragment view is double tapped
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onDoubleTap(Context context, View view) {
        return true;
    }

    /**
     * This method is called when the fragment view is pressed for a long time
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onLongPress(Context context, View view) {
        return true;
    }

    /**
     * This method is called when the fragment view gets a "finger down" event
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onDown(Context context, View view) {
        return true;
    }

    /**
     * This method is called when the fragment view is showed
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onShow(Context context, View view) {
        return true;
    }

    /**
     * This method is called when the fragment view is swiped
     * @param context The application's context
     * @param view The view
     * @return true for delivering the event forward, false otherwise
     */
    @Override
    public boolean onSwipe(Context context, View view, UiInteractionController.MotionDirection direction) {
        return true;
    }

    @Override
    protected String getFragmentTagName() {
        return "mce-mini";
    }
}
