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
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

class UiInteractionController extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public static final String COPYRIGHT =
            "\n\nLicensed Materials - Property of IBM\n5725E28, 5725S01, 5725I03\n© Copyright IBM Corp. 2016, ${YEAR}.\nUS Government Users Restricted Rights - Use, duplication or disclosure\nrestricted by GSA ADP Schedule Contract with IBM Corp.\n\n";


    private GestureDetector gestureDetector;
    private Context context;
    private View view;
    private Preferences preferences;
    private Listener listener;


    public UiInteractionController(View view, Context context, Preferences preferences, Listener listener) {
        this.context = context;
        this.view = view;
        this.listener = listener;
        this.preferences = preferences != null ? preferences : new Preferences();
        this.gestureDetector = new GestureDetector(context, this);
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        listener.onLongPress(context, view);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return listener.onDown(context, view);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return listener.onDoubleTap(context, view);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return listener.onTap(context, view);
    }

    @Override
    public boolean onFling(MotionEvent from, MotionEvent to, float velocityX, float velocityY) {
        float deltaY = Math.abs(to.getY() - from.getY());
        float delataX = Math.abs(to.getX() - from.getX());

        if (delataX > deltaY) {
            if (delataX > preferences.swipeThreshold && Math.abs(velocityX) > preferences.swipeVelocityThreshold) {
                if (to.getX() > from.getX()) {
                    listener.onSwipe(context, view, MotionDirection.right);
                } else {
                    listener.onSwipe(context, view, MotionDirection.left);
                }
                return true;
            }
        } else if (deltaY > preferences.swipeThreshold && Math.abs(velocityY) > preferences.swipeVelocityThreshold) {
            if (to.getY() > from.getY()) {
                listener.onSwipe(context, view, MotionDirection.down);
            } else {
                listener.onSwipe(context, view, MotionDirection.up);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        listener.onShow(context, view);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public static class Preferences {
        private static final int DEFAULT_SWIPE_THRESHOLD = 100;
        private static final int DEFAULT_SWIPE_VELOCITY_THRESHOLD = 100;

        private int swipeThreshold;
        private int swipeVelocityThreshold;

        public Preferences(int swipeThreshold, int swipeVelocityThreshold) {
            this.swipeThreshold = swipeThreshold > 0 ? swipeThreshold : DEFAULT_SWIPE_THRESHOLD;
            this.swipeVelocityThreshold = swipeVelocityThreshold > 0 ? swipeVelocityThreshold : DEFAULT_SWIPE_VELOCITY_THRESHOLD;
        }

        public Preferences() {
            this(-1, -1);
        }
    }

    public static enum MotionDirection {
        up, down, left, right
    }

    public static interface Listener {
        public boolean onTap(Context context, View view);

        public boolean onDoubleTap(Context context, View view);

        public boolean onLongPress(Context context, View view);

        public boolean onDown(Context context, View view);

        public boolean onShow(Context context, View view);

        public boolean onSwipe(Context context, View view, MotionDirection direction);


    }
}


