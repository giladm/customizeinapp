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
import co.acoustic.mobile.push.sdk.util.json.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * This is the inApp message class
 */
public class InAppPayload {

    private final String id;
    private String contentId;
    private String attribution;
    private String mailingId;
    private final Date triggerDate;
    private final Date expirationDate;
    private final List<String> rules;
    private final int maxViews;
    private int views;
    private final String templateName;
    private final JSONObject templateContent;
    private final List<InAppAction> actions;
    private boolean fromPull;



    InAppPayload(String id, String contentId, String attribution, Date triggerDate, Date expirationDate, List<String> rules, int maxViews, int views, String templateName, JSONObject templateContent, List<InAppAction> actions, String mailingId, boolean fromPull) {
        this.id = id;
        this.contentId = contentId;
        this.attribution = attribution;
        this.triggerDate = triggerDate;
        this.expirationDate = expirationDate;
        this.rules = rules;
        this.maxViews = maxViews;
        this.views = views;
        this.templateName = templateName;
        this.templateContent = templateContent;
        this.actions = actions;
        this.mailingId = mailingId;
        this.fromPull = fromPull;
    }

    /**
     * Retrieves the inApp message id
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * Retrieves the inApp message content id
     * @return
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * Retrieves the inApp message attribution
     * @return The attribution
     */
    public String getAttribution() {
        return attribution;
    }

    /**
     * Retrieves the inApp message mailing id
     * @return The mailing id
     */
    public String getMailingId() {
        return mailingId;
    }

    /**
     * Retrieves the inApp message trigger date
     * @return The trigger date
     */
    public Date getTriggerDate() {
        return triggerDate;
    }

    /**
     * Retrieves the inApp message expiration date
     * @return The expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Retrieves the inApp message rules
     * @return The rules
     */
    public List<String> getRules() {
        return rules;
    }

    /**
     * Retrieves the inApp message maxviews count
     * @return The max views count
     */
    public int getMaxViews() {
        return maxViews;
    }

    /**
     * Retrieves the inApp message views count
     * @return The views count
     */
    public int getViews() {
        return views;
    }

    void setViews(int views) {
        this.views = views;
    }

    /**
     * Retrieves the inApp message template name
     * @return The template name
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * Retrieves the inApp message template content
     * @return The template content
     */
    public JSONObject getTemplateContent() {
        return templateContent;
    }

    /**
     * Retrieves the inApp message actions
     * @return The actions
     */
    public List<InAppAction> getActions() {
        return actions;
    }

    /**
     * Checks if the inApp message is from a pull request
     * @return
     */
    public boolean isFromPull() {
        return fromPull;
    }

    /**
     * Add to the view count of the message
     */
    public void toggleViewed() {
        views++;
    }

    void finish() {
        this.views = this.maxViews;
    }

    void setMceContext(String attribution, String mailingId) {
        this.attribution = attribution;
        this.mailingId = mailingId;
    }

    /**
     * Retrieves a string representation of the message
     * @return
     */
    @Override
    public String toString() {
        try {
            return "InAppPayload{" +
                    "id=" + id +
                    ", contentId='" + contentId + '\'' +
                    ", attribution='" + attribution + '\'' +
                    ", mailingId='" + mailingId + '\'' +
                    ", triggerDate=" + Iso8601.toPrintableString(triggerDate) +" ("+triggerDate.getTime()+")"+
                    ", expirationDate=" + Iso8601.toPrintableString(expirationDate) +" ("+expirationDate.getTime()+")"+
                    ", rules=" + JsonUtil.toJsonStringArray(rules) +
                    ", maxViews=" + maxViews +
                    ", views=" + views +
                    ", templateName='" + templateName + '\'' +
                    ", templateContent=" + templateContent +
                    ", actions=" + InAppPayloadJsonTemplate.InAppActionJsonTemplate.LIST_TEMPLATE.toJsonArray(actions, InAppPayloadJsonTemplate.InAppActionJsonTemplate.INSTANCE) +
                    '}';
        } catch (JSONException jsone) {
            return jsone.getMessage();
        }
    }
}
