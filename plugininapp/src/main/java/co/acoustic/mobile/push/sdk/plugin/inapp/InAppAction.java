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

import org.json.JSONObject;

/**
 * This class is an inapp message action
 */
public class InAppAction {

   private String name;
    private JSONObject value;

    InAppAction(String name, JSONObject value){
        this.name = name;
        this.value = value;
    }

    /**
     * Retrieves the action name
     * @return The name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the action value
     * @return The value
     */
    public JSONObject getValue() {
        return this.value;
    }
}
