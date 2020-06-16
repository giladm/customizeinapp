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

import co.acoustic.mobile.push.sdk.api.MceSdk;
import co.acoustic.mobile.push.sdk.api.OperationCallback;
import co.acoustic.mobile.push.sdk.api.OperationResult;
import co.acoustic.mobile.push.sdk.api.attribute.Attribute;
import co.acoustic.mobile.push.sdk.api.attribute.StringAttribute;
import co.acoustic.mobile.push.sdk.api.event.Event;
import co.acoustic.mobile.push.sdk.notification.MceNotificationActionImpl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is responsible for sending the inApp events
 */
public class InAppEvents {

    /**
     * The inApp message type value
     */
    public static final String IN_APP_MESSAGE_TYPE = "inAppMessage";


    /**
     * Send inApp message opened event
     * @param context The application's context
     * @param message The opened message
     */
    public static void sendInAppMessageOpenedEvent(final Context context,InAppPayload message) {
        List<Attribute> eventAttributes = new LinkedList<Attribute>();

        final Event event = new Event(IN_APP_MESSAGE_TYPE, "messageOpened", new Date(), eventAttributes, message.getAttribution(), message.getMailingId());
        MceSdk.getEventsClient(false).sendEvent(context, event, new OperationCallback<Event>() {
            @Override
            public void onSuccess(Event event, OperationResult result) {

            }

            @Override
            public void onFailure(Event event, OperationResult result) {
                MceSdk.getQueuedEventsClient().sendEvent(context, event);
            }
        });
    }

    /**
     * Send inApp message action taken event
     * @param context The application's context
     * @param actionType The action's type
     * @param payload The action's payload
     * @param attribution The action's attribution
     * @param mailingId The action's mailing id
     */
    public static void sendInAppMessageActionTakenEvent(final Context context,String actionType, Bundle payload, String attribution, String mailingId) {
        String name = actionType;
        List<Attribute> eventAttributes = new LinkedList<Attribute>();
        MceNotificationActionImpl.ClickEventDetails clickEventDetails = MceNotificationActionImpl.getClickEventDetails(actionType);
        if(clickEventDetails != null) {
            name = clickEventDetails.eventName;
            String value = payload.getString("value");
            eventAttributes.add(new StringAttribute(clickEventDetails.valueName, value));
        } else {
            for(String key : payload.keySet()) {
                eventAttributes.add(new StringAttribute(key, payload.getString(key)));
            }
        }

        final Event event = new Event(IN_APP_MESSAGE_TYPE, name, new Date(), eventAttributes, attribution, mailingId);
        MceSdk.getEventsClient(false).sendEvent(context, event , new OperationCallback<Event>() {
            @Override
            public void onSuccess(Event event, OperationResult result) {

            }

            @Override
            public void onFailure(Event event, OperationResult result) {
                MceSdk.getQueuedEventsClient().sendEvent(context, event);
            }
        });
    }
}

