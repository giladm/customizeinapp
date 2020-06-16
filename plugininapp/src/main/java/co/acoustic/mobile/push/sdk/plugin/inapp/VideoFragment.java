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
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import co.acoustic.mobile.push.sdk.util.Logger;

/**
 * This is the video template fragment
 */
public class VideoFragment extends InAppFragment implements UiInteractionController.Listener {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2015, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";

    private static final String TAG = "VideoFragment";

    public static final String POSITION_KEY = "position";
    public static final String TEXT_EXPANDED_KEY = "textExpanded";

    private VideoView inAppVideoView;

    protected int lastPosition;
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
        View view = inflater.inflate(getResources().getIdentifier("video_fragment", "layout", inflater.getContext().getApplicationContext().getPackageName()), container, false);



        final TextView subjectView = (TextView) view.findViewById(getResources().getIdentifier("subject", "id", inflater.getContext().getApplicationContext().getPackageName()));
        final TextView messageView = (TextView) view.findViewById(getResources().getIdentifier("message", "id", inflater.getContext().getApplicationContext().getPackageName()));


        final RelativeLayout textLayout = (RelativeLayout) view.findViewById(getResources().getIdentifier("text_layout", "id", inflater.getContext().getApplicationContext().getPackageName()));


        String subject = getArguments().getString(VideoTemplate.TITLE_KEY, null);
        String message = getArguments().getString(VideoTemplate.TEXT_KEY, null);
        if((subject != null && subject.length() > 0) || (message != null && message.length() > 0)) {
            subjectView.setText(getArguments().getString(VideoTemplate.TITLE_KEY));
            messageView.setText(getArguments().getString(VideoTemplate.TEXT_KEY));
            if(textExpanded) {
                textLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
            View.OnClickListener listener = new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(!textExpanded) {
                        textLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                        textExpanded = true;
                    } else {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        textLayout.setLayoutParams(params);
                        textLayout.setGravity(Gravity.BOTTOM);
                        textExpanded = false;
                    }
                }
            };
            subjectView.setOnClickListener(listener);
            messageView.setOnClickListener(listener);
        } else{
            textLayout.setVisibility(RelativeLayout.GONE);
        }

        inAppVideoView = (VideoView) view.findViewById(getResources().getIdentifier("video", "id", inflater.getContext().getApplicationContext().getPackageName()));
        inAppVideoView.setBackgroundColor(Color.TRANSPARENT);
        inAppVideoView.setVideoURI(Uri.parse(getArguments().getString(VideoTemplate.VIDEO_KEY)));
        inAppVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                close();
            }
        });
        inAppVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                inAppVideoView.requestFocus();
                inAppVideoView.setZOrderOnTop(false);
                if (lastPosition > 0) {
                    try {
                        mp.seekTo(lastPosition);
                    } catch (Throwable t1) {
                        Logger.e(TAG, "Failed to seek video");
                    }
                    mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            mp.start();
                        }
                    });

                } else {
                    mp.start();
                }

            }
        });
        inAppVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                performAction(getActivity().getApplicationContext());
                return true;
            }
        });

        view.findViewById(getResources().getIdentifier("close", "id", inflater.getContext().getApplicationContext().getPackageName())).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        return view;
    }

    @Override
    protected void close() {
        if(inAppVideoView.isPlaying()) {
            inAppVideoView.stopPlayback();
        }
        inAppVideoView.setVisibility(View.GONE);
        super.close();
    }

    /**
     * This method is called when the fragment view state is stored
     * @param outState The view state
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(inAppVideoView != null) {
            if (inAppVideoView.isPlaying()) {
                inAppVideoView.pause();
            }
            lastPosition = inAppVideoView.getCurrentPosition();
            super.onSaveInstanceState(outState);
            outState.putInt(POSITION_KEY, lastPosition);
            outState.putBoolean(TEXT_EXPANDED_KEY, textExpanded);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * This method is called when the fragment view state is restored
     * @param savedInstanceState The view state
     */
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(POSITION_KEY)) {
            lastPosition = savedInstanceState.getInt(POSITION_KEY);
            textExpanded = savedInstanceState.getBoolean(TEXT_EXPANDED_KEY);
        }
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
        try {
            if(direction == UiInteractionController.MotionDirection.right) {
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
        return "mce-video";
    }




}
