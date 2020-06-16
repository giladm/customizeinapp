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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import co.acoustic.mobile.push.sdk.api.MediaManager;
import co.acoustic.mobile.push.sdk.util.Logger;
import co.acoustic.mobile.push.sdk.util.media.MediaCache;

/**
 * This is the image template fragment
 */
public class ImageFragment extends InAppFragment implements UiInteractionController.Listener {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2016, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";

    private static final String TAG = "ImageFrament";

    protected boolean textExpanded = false;

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


        // Inflate the layout for this fragment
        View view = inflater.inflate(getResources().getIdentifier("image_fragment", "layout", inflater.getContext().getApplicationContext().getPackageName()), container, false);

        final ImageView imageView = (ImageView) view.findViewById(getResources().getIdentifier("img", "id", inflater.getContext().getApplicationContext().getPackageName()));
        final TextView subjectView = (TextView) view.findViewById(getResources().getIdentifier("subject", "id", inflater.getContext().getApplicationContext().getPackageName()));
        final TextView messageView = (TextView) view.findViewById(getResources().getIdentifier("message", "id", inflater.getContext().getApplicationContext().getPackageName()));


        Bitmap image = null;
        String imageUrl = getArguments().getString(ImageTemplate.IMAGE_KEY);
        MediaCache imageCache = MediaManager.getImageCache();
        image = imageCache.getImage(imageUrl, false);
        final RelativeLayout textLayout = (RelativeLayout) view.findViewById(getResources().getIdentifier("text_layout", "id", inflater.getContext().getApplicationContext().getPackageName()));

        String subject = getArguments().getString(ImageTemplate.TITLE_KEY, null);
        String message = getArguments().getString(ImageTemplate.TEXT_KEY, null);
        if((subject != null && subject.length() > 0) || (message != null && message.length() > 0)) {
            subjectView.setText(getArguments().getString(ImageTemplate.TITLE_KEY));
            messageView.setText(getArguments().getString(ImageTemplate.TEXT_KEY));
            if(textExpanded) {
                textLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
            View.OnClickListener listener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(!textExpanded) {
                        textLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                        textExpanded = true;
                    } else {
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
                        params.gravity = Gravity.BOTTOM;
                        textLayout.setLayoutParams(params);
                        textExpanded = false;
                    }
                }
            };
            subjectView.setOnClickListener(listener);
            messageView.setOnClickListener(listener);
        } else{
            textLayout.setVisibility(RelativeLayout.GONE);
        }




        final FrameLayout imageLayout = (FrameLayout) view.findViewById(getResources().getIdentifier("image_layout", "id", inflater.getContext().getApplicationContext().getPackageName()));
        imageLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        UiInteractionController uiInteractionController = new UiInteractionController(imageLayout, inflater.getContext().getApplicationContext(), null, this);
        imageView.setOnTouchListener(uiInteractionController);
        if(image != null) {
            imageView.setImageBitmap(image);
        } else {
            MediaManager.loadImage(imageUrl, null, new MediaManager.UpdateViewTask(getActivity(), imageView));
        }
        view.findViewById(getResources().getIdentifier("close", "id", inflater.getContext().getApplicationContext().getPackageName())).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        if(getArguments().getInt(ImageTemplate.DURATION_KEY) > 0) {
            final int duration = getArguments().getInt(ImageTemplate.DURATION_KEY);
            closeAfter(duration);
        }

        return view;

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
        try {
            if (direction == UiInteractionController.MotionDirection.right) {
                close();
                return true;
            }
        } catch (Throwable t) {
            Logger.e(TAG, "Failed onSwipe", t);
        }
        return false;
    }

    @Override
    protected String getFragmentTagName() {
        return "mce-image";
    }
}
