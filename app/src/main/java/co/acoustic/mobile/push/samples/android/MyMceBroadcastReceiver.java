package co.acoustic.mobile.push.samples.android;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import co.acoustic.mobile.push.sdk.api.MceBroadcastReceiver;
import co.acoustic.mobile.push.sdk.api.MceSdk;
import co.acoustic.mobile.push.sdk.api.attribute.AttributesOperation;
import co.acoustic.mobile.push.sdk.api.broadcast.EventBroadcastUtil;
import co.acoustic.mobile.push.sdk.api.event.Event;
import co.acoustic.mobile.push.sdk.api.notification.NotificationDetails;
import co.acoustic.mobile.push.sdk.api.registration.RegistrationDetails;
import co.acoustic.mobile.push.sdk.location.MceLocation;
import co.acoustic.mobile.push.sdk.plugin.inapp.InAppManager;
import co.acoustic.mobile.push.sdk.util.Logger;

import java.util.Date;
import java.util.List;

/**
 * Created by giladm on 5/31/18.
 */

public class MyMceBroadcastReceiver extends MceBroadcastReceiver {
    @Override
    public void onSdkRegistered(Context context) {
        // Handle the SDK registration event
        // context - The application context
        String TAG ="MyMceBroadcastReceiver";
        Log.i(TAG,"registration complete");
        RegistrationDetails registrationDetails = MceSdk.getRegistrationClient().getRegistrationDetails(context);
        Log.i(TAG, "-- SDK registered");
        Log.i(TAG, "Channel ID is: " + registrationDetails.getChannelId());
        Log.i(TAG, "User ID is: " + registrationDetails.getUserId());

    }
    @Override
    public void onMessagingServiceRegistered(Context context) {
// Handle the FCM registration event
        // context - The application context
    }
    @Override
    public void onSdkRegistrationChanged(Context context) {
// context - The application `context
    }
    public void onSdkRegistrationUpdated(Context context){

    }

    @Override
    public void onMessage(Context context, NotificationDetails notificationDetails, final Bundle bundle) {

// Handle the notification received event
        // context - The application context
        // notificationDetails - The received notification
        // extraPayload- Additional payload that arrived with the notification
        if(notificationDetails != null) {
            Log.i("MyMceBroadcastReceiver", "-- SDK delivery channel message received");
            Log.i("MyMceBroadcastReceiver", "Subject is: " + notificationDetails.getSubject());
            Log.i("MyMceBroadcastReceiver", "Message is: " + notificationDetails.getMessage());
        }
        String attribution = null;
        if(notificationDetails != null && notificationDetails.getMceNotificationPayload() != null) {
            attribution = notificationDetails.getMceNotificationPayload().getAttribution();
        }
        String mailingId = null;
        if(notificationDetails != null && notificationDetails.getMceNotificationPayload() != null) {
            mailingId = notificationDetails.getMceNotificationPayload().getMailingId();
        }

    Log.i("MyMceBroadcastReceiver", "before inapp handler: " + notificationDetails.getMessage());
        InAppManager.handleNotification(context, bundle, attribution, mailingId);


    }

    @Override
    public void onC2dmError(Context context, String s) {

    }

    @Override
    public void onSessionStart(Context context, Date sessionStartDate) {
// context - The application context
        // sessionStartDate- The new session start time
    }
    @Override
    public void onSessionEnd(Context context, Date sessionEndDate, long sessionDurationInMinutes) {
        // context - The application context
// sessionEndDate- The session end time
        // sessionDurationInMinutes - The session duration in minutes
    }
    @Override
    public void onNotificationAction(Context context, Date actionTime, String pushType, String actionType, String actionValue) {
// context - The application context
        // actionTime- The time the action was clicked on
        // pushType - always "simple"
        // actionType - The type of the action
        // actionValue - the value of the "value" key in the payload.
    }
    @Override
    public void onAttributesOperation(Context context, AttributesOperation attributesOperation) {
// context - The application context
        // attributesOperation - The operation that was executed
    }

    @Override
    public void onEventsSend(Context context, List<Event> list) {

    }
/*
    @Override
    public void onEventsSend(Context context, List<UsageEvents.Event> list) {
// context - The application context
        // events- The events that were sent
    } */
    @Override
    public void onIllegalNotification(Context context, Intent intent) {
        // context - The application context
        // intent- The intent that contains the illegal notification
    }
    @Override
    public void onNonMceBroadcast(Context context, Intent intent) {
        // context - The application context
        // intent- The intent that contains the non MCE broadcast
    }

    @Override
    public void onLocationEvent(Context context, MceLocation mceLocation, LocationType locationType, LocationEventType locationEventType) {

    }

    @Override
    public void onLocationUpdate(Context context, Location location) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || intent.getAction() == null) {
            return;
        }
        try {
            EventBroadcastUtil.handleBroadcast(context, intent, this);
        } catch(Throwable t) {
            Logger.e("MyMceBroadcastReceiver:", "Unexpected error", t);
        }
    }

}

