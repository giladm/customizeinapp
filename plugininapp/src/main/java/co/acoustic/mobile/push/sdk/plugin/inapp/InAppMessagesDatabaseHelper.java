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

import android.content.ContentValues;
import android.content.Context;

import co.acoustic.mobile.push.sdk.api.db.SdkDatabase;
import co.acoustic.mobile.push.sdk.api.db.SdkDatabaseCursor;
import co.acoustic.mobile.push.sdk.api.db.SdkDatabaseOpenHelper;
import co.acoustic.mobile.push.sdk.api.db.SdkDatabaseQueryBuilder;
import co.acoustic.mobile.push.sdk.db.DbAdapter;
import co.acoustic.mobile.push.sdk.util.Logger;
import co.acoustic.mobile.push.sdk.util.json.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

class InAppMessagesDatabaseHelper {
    private static final String TAG="InAppMessagesDatabaseHelper";

    private static final String DB_NAME = "mceInappMessages.sqlite";
    private static final int VERSION = 2;

    private static final String MESSAGES_TABLE_NAME = "messages";

    private static final String COLUMN_MESSAGE_ID = "_id";
    private static final String COLUMN_CONTENT_ID = "contentId";
    private static final String COLUMN_MAILING_ID = "mailingId";
    private static final String COLUMN_ATTRIBUTION = "attribution";
    private static final String COLUMN_TRIGGER_DATE = "triggerDate";
    private static final String COLUMN_EXPIRATION_DATE = "expirationDate";
    private static final String COLUMN_MAX_VIEWS = "maxViews";
    private static final String COLUMN_VIEWS = "views";
    private static final String COLUMN_TEMPLATE_NAME = "templateName";
    private static final String COLUMN_TEMPLATE = "template";
    private static final String COLUMN_ACTIONS = "actions";
    private static final String COLUMN_RULES = "rules";
    private static final String COLUMN_FROM_PULL = "pull";

    private static final String[] MESSAGES_TABLE_COLUMNS = {MESSAGES_TABLE_NAME+"."+COLUMN_MESSAGE_ID, MESSAGES_TABLE_NAME+"."+COLUMN_CONTENT_ID, MESSAGES_TABLE_NAME+"."+COLUMN_MAILING_ID, MESSAGES_TABLE_NAME+"."+COLUMN_ATTRIBUTION, MESSAGES_TABLE_NAME+"."+COLUMN_TRIGGER_DATE, MESSAGES_TABLE_NAME+"."+COLUMN_EXPIRATION_DATE,
            MESSAGES_TABLE_NAME+"."+COLUMN_MAX_VIEWS, MESSAGES_TABLE_NAME+"."+COLUMN_VIEWS, MESSAGES_TABLE_NAME+"."+COLUMN_TEMPLATE_NAME, MESSAGES_TABLE_NAME+"."+COLUMN_TEMPLATE, MESSAGES_TABLE_NAME+"."+COLUMN_ACTIONS, MESSAGES_TABLE_NAME+"."+COLUMN_RULES, MESSAGES_TABLE_NAME+"."+COLUMN_FROM_PULL};

    private static final String RULES_TABLE_NAME = "rules";

    private static final String COLUMN_RULE_NAME = "name";





    private static InAppMessagesDatabaseHelper inAppMessagesDatabaseHelper;
    public static InAppMessagesDatabaseHelper getInAppMessagesDatabaseHelper(Context context) {
        if (inAppMessagesDatabaseHelper == null) {
            try {
                inAppMessagesDatabaseHelper = new InAppMessagesDatabaseHelper(context);
            } catch (Exception e) {
                Logger.e(TAG, "Failed to create inapp database helper", e);
            }
        }
        return inAppMessagesDatabaseHelper;
    }

    protected SdkDatabaseOpenHelper databaseHelper;

    public InAppMessagesDatabaseHelper(Context context)
    {
        databaseHelper = DbAdapter.getDatabaseImpl(context).createOpenHelper(context, DB_NAME, VERSION, new SdkDatabaseOpenHelper.LifeCycleListener() {
            @Override
            public void onCreate(SdkDatabase database) {
                // create inApp messages table
                String createTableSql = "CREATE TABLE IF NOT EXISTS \""+MESSAGES_TABLE_NAME+"\"("+"" +
                        "\""+ COLUMN_MESSAGE_ID +"\" TEXT, " +
                        "\""+ COLUMN_CONTENT_ID +"\" TEXT, " +
                        "\""+COLUMN_MAILING_ID+"\" Text, "+
                        "\""+COLUMN_ATTRIBUTION+"\" Text, "+
                        "\""+COLUMN_TRIGGER_DATE+"\" INTEGER, "+
                        "\""+COLUMN_EXPIRATION_DATE+"\" INTEGER, "+
                        "\""+COLUMN_MAX_VIEWS+"\" INTEGER, "+
                        "\""+COLUMN_VIEWS+"\" INTEGER, "+
                        "\""+COLUMN_TEMPLATE+"\" Text, "+
                        "\""+COLUMN_TEMPLATE_NAME+"\" Text, "+
                        "\""+COLUMN_RULES+"\" Text, "+
                        "\""+COLUMN_ACTIONS+"\" Text, "+
                        "\""+COLUMN_FROM_PULL+"\" INTEGER);";
                Logger.d(TAG, createTableSql);
                database.execSQL(createTableSql);

                // create inApp messages rules table
                createTableSql = "CREATE TABLE IF NOT EXISTS \""+RULES_TABLE_NAME+"\"("+"" +
                        "\""+ COLUMN_MESSAGE_ID +"\" TEXT, "+
                        "\""+COLUMN_RULE_NAME+"\" Text);";
                Logger.d(TAG, createTableSql);
                database.execSQL(createTableSql);

                String messageIdInRulesIndexTableSql = "CREATE INDEX IF NOT EXISTS \"messageIdRulesIndex\" ON \""+RULES_TABLE_NAME+"\"( \""+COLUMN_MESSAGE_ID+"\" );";
                Logger.d(TAG, messageIdInRulesIndexTableSql);
                database.execSQL(messageIdInRulesIndexTableSql);

                String ruleNameInRulesIndexTableSql = "CREATE INDEX IF NOT EXISTS \"ruleNameRulesIdIndex\" ON \""+RULES_TABLE_NAME+"\"( \""+COLUMN_RULE_NAME+"\" );";
                Logger.d(TAG, ruleNameInRulesIndexTableSql);
                database.execSQL(ruleNameInRulesIndexTableSql);

                String messageIdInMessagesIndexTableSql = "CREATE INDEX IF NOT EXISTS \"messageIdMessagesIndex\" ON \""+MESSAGES_TABLE_NAME+"\"( \""+COLUMN_MESSAGE_ID+"\" );";
                Logger.d(TAG, messageIdInMessagesIndexTableSql);
                database.execSQL(messageIdInMessagesIndexTableSql);

                String templateNameInMessagesIndexTableSql = "CREATE INDEX IF NOT EXISTS \"messageIdTemplateIndex\" ON \""+MESSAGES_TABLE_NAME+"\"( \""+COLUMN_TEMPLATE_NAME+"\" );";
                Logger.d(TAG, templateNameInMessagesIndexTableSql);
                database.execSQL(templateNameInMessagesIndexTableSql);
            }

            @Override
            public void onUpgrade(SdkDatabase database,int oldVersion, int newVersion) {
                Logger.d(TAG, "Updating inApp table from "+oldVersion+" to "+newVersion);
                if(oldVersion <= 1) {
                    database.execSQL("ALTER TABLE " + MESSAGES_TABLE_NAME + " ADD COLUMN " + COLUMN_CONTENT_ID + " TEXT");
                    database.execSQL("ALTER TABLE " + MESSAGES_TABLE_NAME + " ADD COLUMN " + COLUMN_FROM_PULL + " INTEGER");
                }
            }

            @Override
            public void onDowngrade(SdkDatabase database, int oldVersion, int newVersion) {

            }

            @Override
            public void onConfigure(SdkDatabase database) {

            }
        });
    }

    public void delete() {
        databaseHelper.getWritableDatabase().delete();
    }


    public void addMessage(InAppPayload inAppPayload, boolean fromPull) throws IOException{
        Logger.d(TAG, "Adding new inApp payload: "+inAppPayload);
        SdkDatabase db =  databaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_MESSAGE_ID, String.valueOf(inAppPayload.getId()));
            cv.put(COLUMN_CONTENT_ID, String.valueOf(inAppPayload.getContentId()));
            cv.put(COLUMN_MAILING_ID, inAppPayload.getMailingId());
            cv.put(COLUMN_ATTRIBUTION, inAppPayload.getAttribution());
            Logger.d(TAG, "Adding trigger date: "+inAppPayload.getTriggerDate().getTime());
            cv.put(COLUMN_TRIGGER_DATE, inAppPayload.getTriggerDate().getTime());
            Logger.d(TAG, "Adding expiration date: "+inAppPayload.getTriggerDate().getTime());
            cv.put(COLUMN_EXPIRATION_DATE, inAppPayload.getExpirationDate().getTime());
            cv.put(COLUMN_MAX_VIEWS, inAppPayload.getMaxViews());
            cv.put(COLUMN_VIEWS, inAppPayload.getViews());
            cv.put(COLUMN_TEMPLATE_NAME, inAppPayload.getTemplateName());
            cv.put(COLUMN_TEMPLATE, inAppPayload.getTemplateContent().toString());
            JSONArray rules = JsonUtil.toJsonStringArray(inAppPayload.getRules());
            if(rules != null) {
                cv.put(COLUMN_RULES, rules.toString());
            }
            JSONArray actions = InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.toJsonArray(inAppPayload.getActions(), InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE);
            if(actions != null) {
                cv.put(COLUMN_ACTIONS, actions.toString());
            }
            cv.put(COLUMN_FROM_PULL, (fromPull ? 1 : 0));
            long res = db.insert(MESSAGES_TABLE_NAME, null, cv);
            Logger.d(TAG, " new message id is "+inAppPayload.getId());
            if(res >= 0) {
                for(String rule : inAppPayload.getRules()) {
                    cv = new ContentValues();
                    cv.put(COLUMN_MESSAGE_ID, inAppPayload.getId());
                    cv.put(COLUMN_RULE_NAME, rule);
                    res = db.insert(RULES_TABLE_NAME, null, cv);
                    Logger.d(TAG, "Rule insert ID: "+res);
                    if(res < 0) {
                        throw new IOException("Failed to store rule");
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (JSONException jsone) {
            throw new IOException("Failed to store inapp", jsone);
        }
        finally {
            db.endTransaction();
            printTables();
        }
    }

    public void updateViews(InAppPayload inAppPayload) {
        Logger.d(TAG, "Update views of "+inAppPayload.getId()+" views is "+inAppPayload.getViews()+" max views is "+inAppPayload.getMaxViews());
        Logger.d(TAG, "Max views not reached - updating message");
        String[] whereArgs = new String[] {String.valueOf(inAppPayload.getId())};
        String whereClause = COLUMN_MESSAGE_ID + " = ?";
        SdkDatabase db =  databaseHelper.getWritableDatabase();
        Logger.d(TAG, "Update where clause: "+whereClause);
        if(inAppPayload.getViews() == inAppPayload.getMaxViews()) {
            Logger.d(TAG, "Max views reached - deleting message");
            deleteMessageById(inAppPayload.getId());
        } else {
            Logger.d(TAG, "Max views not reached - updating message");
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_VIEWS, inAppPayload.getViews());
            int res = db.update(MESSAGES_TABLE_NAME, cv, whereClause, whereArgs);
            Logger.d(TAG, "Views update result: "+res);
        }
    }

    public void updateMessage(InAppPayload inAppPayload, boolean fromPull) {
        Logger.d(TAG, "Update message of "+inAppPayload);
        String[] whereArgs = new String[] {String.valueOf(inAppPayload.getId())};
        String whereClause = COLUMN_MESSAGE_ID + " = ?";
        SdkDatabase db =  databaseHelper.getWritableDatabase();
        Logger.d(TAG, "Update where clause: "+whereClause);
        if(inAppPayload.getViews() == inAppPayload.getMaxViews()) {
            Logger.d(TAG, "Max views reached - deleting message");
            deleteMessageById(inAppPayload.getId());
        } else {
            try {
                Logger.d(TAG, "Max views not reached - updating message");
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_VIEWS, inAppPayload.getViews());
                cv.put(COLUMN_CONTENT_ID, String.valueOf(inAppPayload.getContentId()));
                cv.put(COLUMN_MAILING_ID, inAppPayload.getMailingId());
                cv.put(COLUMN_ATTRIBUTION, inAppPayload.getAttribution());
                cv.put(COLUMN_TRIGGER_DATE, inAppPayload.getTriggerDate().getTime());
                cv.put(COLUMN_EXPIRATION_DATE, inAppPayload.getExpirationDate().getTime());
                cv.put(COLUMN_MAX_VIEWS, inAppPayload.getMaxViews());
                cv.put(COLUMN_VIEWS, inAppPayload.getViews());
                cv.put(COLUMN_TEMPLATE_NAME, inAppPayload.getTemplateName());
                cv.put(COLUMN_TEMPLATE, inAppPayload.getTemplateContent().toString());
                JSONArray rules = JsonUtil.toJsonStringArray(inAppPayload.getRules());
                if (rules != null) {
                    cv.put(COLUMN_RULES, rules.toString());
                }
                JSONArray actions = InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.toJsonArray(inAppPayload.getActions(), InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE);
                if (actions != null) {
                    cv.put(COLUMN_ACTIONS, actions.toString());
                }
                cv.put(COLUMN_FROM_PULL, (fromPull ? 1 : 0));
                int res = db.update(MESSAGES_TABLE_NAME, cv, whereClause, whereArgs);
                Logger.d(TAG, "inApp message update result: " + res);
            } catch (JSONException jsone) {
                Logger.e(TAG,"Failed to update inApp message "+inAppPayload,jsone);
            }
        }
    }

    public boolean deleteMessage(InAppPayload inAppPayload) {
        return deleteMessageById(inAppPayload.getId());
    }

    public boolean deleteMessageById(String messageId) {
        SdkDatabase db =  databaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String[] whereArgs = {messageId};
            String condition = COLUMN_MESSAGE_ID + " = ?";
            int res = db.delete(MESSAGES_TABLE_NAME, condition, whereArgs);
            if (res >= 0) {
                res = db.delete(RULES_TABLE_NAME, condition, whereArgs);
                if (res >= 0) {
                    db.setTransactionSuccessful();
                    return true;
                }
            }
            return false;
        } finally {
            db.endTransaction();
        }
    }

    public void clearExpiredMessages() {
        long currentTime = System.currentTimeMillis();
        SdkDatabaseCursor expiredMessagesCursor = databaseHelper.getReadableDatabase().query(MESSAGES_TABLE_NAME, new String[] {COLUMN_MESSAGE_ID},COLUMN_EXPIRATION_DATE+" < "+currentTime, null, null, null, null);
        if(expiredMessagesCursor.getCount() > 0) {
            Logger.d(TAG, "Found expired messages: "+expiredMessagesCursor.getCount());
            while (expiredMessagesCursor.moveToNext()) {
                String messageId = expiredMessagesCursor.getString(expiredMessagesCursor.getColumnIndex(COLUMN_MESSAGE_ID));
                Logger.d(TAG, "Deleting expired message: "+messageId);
                deleteMessageById(messageId);
            }
        }
    }

    public InAppPayload getMessageById(String messageId) {
        clearExpiredMessages();
        SdkDatabaseQueryBuilder messageQuery = databaseHelper.createQueryBuilder();
        String[] whereArgs = {messageId};
        String condition = MESSAGES_TABLE_NAME+"."+COLUMN_MESSAGE_ID + " = ?";
        messageQuery.setTables(MESSAGES_TABLE_NAME + " INNER JOIN " + RULES_TABLE_NAME + " ON " +MESSAGES_TABLE_NAME+"."+COLUMN_MESSAGE_ID+" = "+RULES_TABLE_NAME+"."+COLUMN_MESSAGE_ID);
        SdkDatabase db =  databaseHelper.getReadableDatabase();
        SdkDatabaseCursor messageCursor = messageQuery.query(db, MESSAGES_TABLE_COLUMNS, condition, whereArgs, null, null, MESSAGES_TABLE_NAME+"."+COLUMN_TRIGGER_DATE+" ASC", "1");
        if(messageCursor.moveToNext()) {
            try {
                String contentId = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_CONTENT_ID));
                String attribution = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_ATTRIBUTION));
                String mailngId = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_MAILING_ID));
                long triggerDate = messageCursor.getLong(messageCursor.getColumnIndex(COLUMN_TRIGGER_DATE));
                long expirationDate = messageCursor.getLong(messageCursor.getColumnIndex(COLUMN_EXPIRATION_DATE));
                int maxViews = messageCursor.getInt(messageCursor.getColumnIndex(COLUMN_MAX_VIEWS));
                int views = messageCursor.getInt(messageCursor.getColumnIndex(COLUMN_VIEWS));
                String templateName = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_TEMPLATE_NAME));
                JSONObject templateContent = new JSONObject(messageCursor.getString(messageCursor.getColumnIndex(COLUMN_TEMPLATE)));
                String rulesStr = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_RULES));
                JSONArray rulesJSONArray = null;
                if(rulesStr != null) {
                    rulesJSONArray = new JSONArray(rulesStr);
                }
                int fromPullValue = messageCursor.getInt(messageCursor.getColumnIndex(COLUMN_FROM_PULL));
                List<String> inAppRules = JsonUtil.fromJsonStringArray(rulesJSONArray);
                String actionsStr = messageCursor.getString(messageCursor.getColumnIndex(COLUMN_ACTIONS));
                JSONArray actionsJSONArray = null;
                if(actionsStr != null) {
                    actionsJSONArray = new JSONArray(actionsStr);
                }
                List<InAppAction> inAppActions = InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.fromJSONArray(
                        actionsJSONArray,
                        InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE);
                return new InAppPayload(messageId, contentId, attribution, new Date(triggerDate), new Date(expirationDate), inAppRules, maxViews, views, templateName, templateContent, inAppActions, mailngId, fromPullValue == 1);
            } catch (JSONException jsone) {
                return null;
            }
        } else {
            return null;
        }
    }


    public List<InAppPayload> getAllMessages(List<String> templates, List<String> rules, boolean matchAll, boolean onlyOne) {
        clearExpiredMessages();
        SdkDatabaseQueryBuilder allMessagesQuery = databaseHelper.createQueryBuilder();
        allMessagesQuery.setTables(MESSAGES_TABLE_NAME + " INNER JOIN " + RULES_TABLE_NAME + " ON " +MESSAGES_TABLE_NAME+"."+COLUMN_MESSAGE_ID+" = "+RULES_TABLE_NAME+"."+COLUMN_MESSAGE_ID);
        List<InAppPayload> messagesResult = new LinkedList<InAppPayload>();
        SdkDatabase db =  databaseHelper.getReadableDatabase();
        String currentTime = String.valueOf(System.currentTimeMillis());
        String whereClause = MESSAGES_TABLE_NAME+"."+COLUMN_EXPIRATION_DATE + " > "+currentTime+" AND "+MESSAGES_TABLE_NAME+"."+COLUMN_TRIGGER_DATE + " <= "+currentTime;
        List<String> whereArgs = new LinkedList<String>();
        if(templates != null && !templates.isEmpty()){
            whereClause+=" AND "+MESSAGES_TABLE_NAME+"."+COLUMN_TEMPLATE_NAME+" in (?";
            whereArgs.add(templates.get(0));
            for(int i = 1; i<templates.size() ; ++i) {
                whereClause+=", ?";
                whereArgs.add(templates.get(i));
            }
            whereClause+=")";
        }
        if(rules != null && !rules.isEmpty()){
            whereClause+=" AND "+RULES_TABLE_NAME+"."+COLUMN_RULE_NAME+" in (?";
            whereArgs.add(rules.get(0));
            for(int i = 1; i<rules.size() ; ++i) {
                whereClause+=", ?";
                whereArgs.add(rules.get(i));
            }
            whereClause+=")";
        }
        allMessagesQuery.setDistinct(true);
        SdkDatabaseCursor allMessagesCursor = allMessagesQuery.query(db, MESSAGES_TABLE_COLUMNS, whereClause, whereArgs.toArray(new String[whereArgs.size()]), null, null, MESSAGES_TABLE_NAME+"."+COLUMN_TRIGGER_DATE+" ASC", (onlyOne ? "1" : null));
        while(allMessagesCursor.moveToNext()) {
            try {
                String messageId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_MESSAGE_ID));
                String contentId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_CONTENT_ID));
                String attribution = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_ATTRIBUTION));
                String mailngId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_MAILING_ID));
                long triggerDate = allMessagesCursor.getLong(allMessagesCursor.getColumnIndex(COLUMN_TRIGGER_DATE));
                long expirationDate = allMessagesCursor.getLong(allMessagesCursor.getColumnIndex(COLUMN_EXPIRATION_DATE));
                int maxViews = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_MAX_VIEWS));
                int views = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_VIEWS));
                String templateName = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_TEMPLATE_NAME));
                JSONObject templateContent = new JSONObject(allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_TEMPLATE)));
                String rulesStr = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_RULES));
                JSONArray rulesJSONArray = null;
                if(rulesStr != null) {
                    rulesJSONArray = new JSONArray(rulesStr);
                }
                int fromPullValue = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_FROM_PULL));
                List<String> inAppRules = JsonUtil.fromJsonStringArray(rulesJSONArray);
                String actionsStr = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_ACTIONS));
                JSONArray actionsJSONArray = null;
                if(actionsStr != null) {
                    actionsJSONArray = new JSONArray(actionsStr);
                }
                List<InAppAction> inAppActions = InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.fromJSONArray(
                        actionsJSONArray,
                        InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE);
                boolean matchFound = true;
                if(rules != null && matchAll) {
                    if(inAppRules != null && inAppRules.size() >= rules.size()) {
                        for(String rule : rules) {
                            if(!inAppRules.contains(rule)) {
                                matchFound = false;
                                break;
                            }
                        }
                    } else {
                        matchFound = false;
                    }
                }
                if(matchFound) {
                    messagesResult.add(new InAppPayload(messageId, contentId, attribution, new Date(triggerDate), new Date(expirationDate), inAppRules, maxViews, views, templateName, templateContent, inAppActions, mailngId, fromPullValue == 1));
                    if(onlyOne) {
                        break;
                    }
                }
            } catch (JSONException jsone) {

            }
        }
        return messagesResult;
    }

    public void printTables() {
        SdkDatabase db = databaseHelper.getReadableDatabase();
        SdkDatabaseCursor allMessagesCursor = db.query(MESSAGES_TABLE_NAME, new String[] {COLUMN_MESSAGE_ID, COLUMN_CONTENT_ID, COLUMN_ATTRIBUTION, COLUMN_MAILING_ID, COLUMN_TRIGGER_DATE, COLUMN_EXPIRATION_DATE, COLUMN_MAX_VIEWS, COLUMN_VIEWS, COLUMN_TEMPLATE_NAME, COLUMN_TEMPLATE, COLUMN_RULES, COLUMN_ACTIONS, COLUMN_FROM_PULL },null, null, null, null, COLUMN_TRIGGER_DATE+" ASC");
        Logger.d(TAG, "Messages: "+allMessagesCursor.getCount());
        if(allMessagesCursor.getCount() > 0) {
            while (allMessagesCursor.moveToNext()) {
                try {
                    String messageId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_MESSAGE_ID));
                    String contentId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_CONTENT_ID));                    String attribution = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_ATTRIBUTION));
                    String mailngId = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_MAILING_ID));
                    long triggerDate = allMessagesCursor.getLong(allMessagesCursor.getColumnIndex(COLUMN_TRIGGER_DATE));
                    long expirationDate = allMessagesCursor.getLong(allMessagesCursor.getColumnIndex(COLUMN_EXPIRATION_DATE));
                    int maxViews = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_MAX_VIEWS));
                    int views = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_VIEWS));
                    String templateName = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_TEMPLATE_NAME));
                    JSONObject templateContent = new JSONObject(allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_TEMPLATE)));
                    String rulesStr = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_RULES));
                    JSONArray rulesJSONArray = null;
                    if(rulesStr != null) {
                        rulesJSONArray = new JSONArray(rulesStr);
                    }
                    List<String> inAppRules = JsonUtil.fromJsonStringArray(rulesJSONArray);
                    String actionsStr = allMessagesCursor.getString(allMessagesCursor.getColumnIndex(COLUMN_ACTIONS));
                    int fromPullValue = allMessagesCursor.getInt(allMessagesCursor.getColumnIndex(COLUMN_FROM_PULL));
                    JSONArray actionsJSONArray = null;
                    if(actionsStr != null) {
                        actionsJSONArray = new JSONArray(actionsStr);
                    }
                    List<InAppAction> inAppActions = InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.fromJSONArray(
                            actionsJSONArray,
                            InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE);
                    Logger.d(TAG, (new InAppPayload(messageId, contentId, attribution, new Date(triggerDate), new Date(expirationDate), inAppRules, maxViews, views, templateName, templateContent, inAppActions, mailngId, fromPullValue == 1)).toString());
                } catch (JSONException jsone) {

                }
            }
        }
        SdkDatabaseCursor allRulesCursor = db.query(RULES_TABLE_NAME, new String[] {COLUMN_MESSAGE_ID, COLUMN_RULE_NAME},null, null, null, null, COLUMN_MESSAGE_ID+" ASC");
        Logger.d(TAG, "Rules: "+allRulesCursor.getCount());
        if(allRulesCursor.getCount() > 0) {
            while (allRulesCursor.moveToNext()) {
                String messageId = allRulesCursor.getString(allRulesCursor.getColumnIndex(COLUMN_MESSAGE_ID));
                String ruleName = allRulesCursor.getString(allRulesCursor.getColumnIndex(COLUMN_RULE_NAME));
                Logger.d(TAG, messageId + "\t" + ruleName);
            }
        }
    }

    public void clear() {
        SdkDatabase db =  databaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(MESSAGES_TABLE_NAME, null, null);
        } finally {
            db.endTransaction();
        }
    }


}

