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

import java.util.HashMap;


/**
 * This is the registry for inApp message templates
 */
public class InAppTemplateRegistry {

   private static InAppTemplateRegistry ourInstance = new InAppTemplateRegistry();

    private HashMap<String, Class<? extends InAppTemplate>> registry = new HashMap<String, Class<? extends InAppTemplate>>();

    /**
     * Retrieves the registry instance
     * @return The inatance
     */
    public static InAppTemplateRegistry getInstance() {
        return ourInstance;
    }

    private InAppTemplateRegistry() {
        registry.put("default", MiniTemplate.class);
        registry.put("image", ImageTemplate.class);
        registry.put("video", VideoTemplate.class);
    }

    /**
     * Registers an inApp template
     * @param templateName The template name
     * @param template The template
     */
    public void register(String templateName, Class<? extends InAppTemplate> template){
        registry.put(templateName, template);
    }

    /**
     * Removes an inApp template
     * @param templateName The template name
     */
    public void remove(String templateName){
        registry.remove(templateName);
    }

    /**
     * Gets a template by name
     * @param templateName The template name
     * @return The template
     */
    public Class<? extends InAppTemplate> get(String templateName){
        return registry.get(templateName);
    }

}
