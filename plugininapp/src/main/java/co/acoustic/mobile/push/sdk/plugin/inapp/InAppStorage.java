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

import co.acoustic.mobile.push.sdk.util.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is the inApp messages dao.
 */
public class InAppStorage {

    // TODO change it to run off the main thread.

    private static final String DB_NAME = "mce-inapp.realm";
    private static final String TAG = "InAppStorage";

    /**
     * This is the find key name enum
     */
    public enum KeyName {

        TEMPLATE("template"), RULE("rules.val");

        private final String name;

        private KeyName(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }

    }

    /**
     * This is the find condition enum
     */
    public enum Condition {
        ANY, ALL;
    }

    static List<InAppPayload> find(Context context, Condition condition, KeyName key, List<String> values, boolean findFirst) {

        if(context == null) {
            return new LinkedList<InAppPayload>();
        }
        List<String> templates = (key != null && key.equals(KeyName.TEMPLATE) ? values : null);
        List<String> rules = (key != null && key.equals(KeyName.RULE) ? values : null);
        return InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).getAllMessages(templates, rules, Condition.ALL.equals(condition), findFirst);
    }


    static List<InAppPayload> find(Context context, KeyName key, List<String> values) {
        return find(context, Condition.ANY, key, values, false);
    }

    static List<InAppPayload> find(Context context, Condition condition, KeyName key, List<String> values) {
        return find(context, condition, key, values, false);
    }

    /**
     * Finds the first inApp message that matches the search criteria
     * @param context The application's context
     * @param key The key we look for (null for no key)
     * @param values The required values for the key (null for no values).
     * @return The first message matching the search criteria
     */
    public static InAppPayload findFirst(Context context, KeyName key, List<String> values) {
        if(context == null) {
            return null;
        }
        List<InAppPayload> messages = find(context, Condition.ANY, key, values, true);
        Logger.d(TAG,"inApp storage messages: "+messages+" ("+messages.size()+")");
        if(!messages.isEmpty()) {
            return messages.get(0);
        } else {
            return null;
        }
    }

    static InAppPayload findFirst(Context context, Condition condition, KeyName key, List<String> values) {
        return find(context, condition, key, values, true).get(0);
    }

    static InAppPayload getInappPayload(Context context, String id) {
        if(context == null) {
            return null;
        }
        InAppPayload dbPayload = InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).getMessageById(id);
        return dbPayload;
    }

    static void save(Context context, final InAppPayload inAppPayload, boolean fromPull) throws IOException{
        if(context == null) {
            return;
        }
        InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).addMessage(inAppPayload, fromPull);
    }

    static void update(Context context, final InAppPayload inAppPayload, boolean fromPull) throws IOException {
        if(context == null) {
            return;
        }
        InAppPayload dbPayload = InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).getMessageById(inAppPayload.getId());
        if(dbPayload != null) {
            if(inAppPayload.getMaxViews() <= inAppPayload.getViews()) {
                delete(context, dbPayload.getId());
            } else {
                if(inAppPayload.getViews() < dbPayload.getViews()) {
                    inAppPayload.setViews(dbPayload.getViews());
                }
                InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).updateMessage(inAppPayload, fromPull);
            }
        } else {
            if(inAppPayload.getMaxViews() > inAppPayload.getViews()) {
                save(context, inAppPayload, fromPull);
            }
        }


    }

    /**
     * This method updates the views count of the message
     * @param context The application's context
     * @param inAppPayload The inApp message
     */
    public static void updateMaxViews(Context context, final InAppPayload inAppPayload) {
        if(context == null) {
            return;
        }
        inAppPayload.toggleViewed();
        InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).updateViews(inAppPayload);

    }

    public static void delete(Context context, String id) {
        if(context == null) {
            return;
        }
        InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).deleteMessageById(id);

    }

    public static void clear(Context context) {
        if(context == null) {
            return;
        }
        InAppMessagesDatabaseHelper.getInAppMessagesDatabaseHelper(context).clear();
    }
}
