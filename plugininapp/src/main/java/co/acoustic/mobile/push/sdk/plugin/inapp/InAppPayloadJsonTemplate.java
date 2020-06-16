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

import co.acoustic.mobile.push.sdk.util.Iso8601;
import co.acoustic.mobile.push.sdk.util.Logger;
import co.acoustic.mobile.push.sdk.util.json.JsonTemplate;
import co.acoustic.mobile.push.sdk.util.json.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

class InAppPayloadJsonTemplate implements JsonTemplate<InAppPayload> {

    private static final String TAG = "InAppPayloadJsonTemplate";

    static class InAppActionJsonTemplate implements JsonTemplate<InAppAction> {
        static enum Keys {
            name, value
        }

        @Override
        public InAppAction fromJSON(JSONObject actionJSON) throws JSONException {
            String name = actionJSON.getString(Keys.name.name());
            JSONObject value = actionJSON.optJSONObject(Keys.value.name());
            return new InAppAction(name, value);
        }

        @Override
        public JSONObject toJSON(InAppAction inAppAction) throws JSONException {
            JSONObject actionJSON = new JSONObject();
            actionJSON.put(Keys.name.name(), inAppAction.getName());
            if(inAppAction.getValue() != null) {
                actionJSON.put(Keys.value.name(), inAppAction.getValue());
            }
            return actionJSON;
        }

        public static final InAppActionJsonTemplate INSTANCE = new InAppActionJsonTemplate();

        public static final JsonUtil.ListTemplate<InAppAction> LIST_TEMPLATE = new JsonUtil.ListTemplate<InAppAction>();

    }

    public static final InAppPayloadJsonTemplate INSTANCE = new InAppPayloadJsonTemplate();

    enum Keys {
        actions, rules, maxViews, template, content, triggerDate, expirationDate, sendDate, inAppMessageId, inAppContentId, numViews, attribution, mailingId
    }

    @Override
    public InAppPayload fromJSON(JSONObject inAppPayloadJSON) throws JSONException {
        List<InAppAction> actions = InAppActionJsonTemplate.LIST_TEMPLATE.fromJSONArray(inAppPayloadJSON.optJSONArray(Keys.actions.name()), InAppActionJsonTemplate.INSTANCE);
        List<String> rules = JsonUtil.fromJsonStringArray(inAppPayloadJSON.optJSONArray(Keys.rules.name()));
        int maxViews = inAppPayloadJSON.getInt(Keys.maxViews.name());
        String template = inAppPayloadJSON.getString(Keys.template.name());
        JSONObject content = inAppPayloadJSON.getJSONObject(Keys.content.name());
        Date triggerDate = null;
        Date expirationDate = null;
        String triggerDateStr = null;
        if(inAppPayloadJSON.has(Keys.triggerDate.name())) {
            triggerDateStr = inAppPayloadJSON.optString(Keys.triggerDate.name());
        } else{
            triggerDateStr = inAppPayloadJSON.optString(Keys.sendDate.name());
        }
        try {
            if(triggerDateStr != null) {
                triggerDate = Iso8601.toDate(triggerDateStr);
            }
            if(triggerDate == null) {
                triggerDate = new Date(System.currentTimeMillis());
                Logger.d(TAG, "default trigger date "+Iso8601.toPrintableString(triggerDate)+" ("+triggerDate.getTime()+")");
            }
        } catch (ParseException pe) {
            Logger.e(TAG, "Failed to parse trigger date", pe);
            throw new JSONException("Failed to parse trigger date: "+inAppPayloadJSON.optString(Keys.triggerDate.name()));
        }
        try {
            expirationDate = Iso8601.toDate(inAppPayloadJSON.optString(Keys.expirationDate.name()));
            if(expirationDate == null) {
                expirationDate = new Date(System.currentTimeMillis()+(365L*24L*60L*60L*1000L));
                Logger.d(TAG, "default expiration date "+Iso8601.toPrintableString(expirationDate)+" ("+expirationDate.getTime()+")");

            }
        } catch (ParseException pe) {
            Logger.e(TAG, "Failed to parse expiration date", pe);
            throw new JSONException("Failed to parse expiration date: "+inAppPayloadJSON.optString(Keys.expirationDate.name()));
        }
        String contentId = inAppPayloadJSON.optString(Keys.inAppContentId.name());
        String messageId = inAppPayloadJSON.optString(Keys.inAppMessageId.name());
        if(messageId == null) {
            messageId = UUID.randomUUID().toString();
        }
        String attribution = inAppPayloadJSON.optString(Keys.attribution.name());
        String mailingId = inAppPayloadJSON.optString(Keys.mailingId.name());
        int numViews = inAppPayloadJSON.optInt(Keys.numViews.name(), 0);
        return new InAppPayload(messageId, contentId, attribution, triggerDate, expirationDate, rules, maxViews, numViews, template, content, actions, mailingId, false);
    }

    @Override
    public JSONObject toJSON(InAppPayload inAppPayload) throws JSONException {
        JSONObject inAppPayloadJSON = new JSONObject();
        inAppPayloadJSON.put(Keys.actions.name(), InAppActionJsonTemplate.LIST_TEMPLATE.toJsonArray(inAppPayload.getActions(), InAppActionJsonTemplate.INSTANCE));
        inAppPayloadJSON.put(Keys.rules.name(), JsonUtil.toJsonStringArray(inAppPayload.getRules()));
        inAppPayloadJSON.put(Keys.maxViews.name(), inAppPayload.getMaxViews());
        inAppPayloadJSON.put(Keys.template.name(), inAppPayload.getTemplateName());
        inAppPayloadJSON.put(Keys.content.name(), inAppPayload.getTemplateContent());
        inAppPayloadJSON.put(Keys.triggerDate.name(), Iso8601.toString(inAppPayload.getTriggerDate()));
        inAppPayloadJSON.put(Keys.expirationDate.name(), Iso8601.toString(inAppPayload.getExpirationDate()));
        return inAppPayloadJSON;
    }

    public static final InAppPayload inAppPayloadFromJSON(JSONObject inAppPayloadJSON) throws JSONException{
        return INSTANCE.fromJSON(inAppPayloadJSON);
    }

    public static class InAppPayloadListTemplate extends JsonUtil.ListTemplate<InAppPayload> {

        public List<InAppPayload> fromJSONArray(JSONArray listJSONArray) throws JSONException {
            return super.fromJSONArray(listJSONArray, new InAppPayloadJsonTemplate());
        }
    }
}
