package com.sun.identity.config.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class DefaultSummary extends TemplatedPage {

    public ActionLink createDefaultConfigLink = new ActionLink("createDefaultConfig", this, "createDefaultConfig" );

    protected String getTitle() {
        return "defaultSummary.title";
    }

    public boolean createDefaultConfig() {
        //TODO - tell back-end to write default configuration
        //for now, just indicate the writing was successful.
        writeToResponse("true");

        setPath(null); //ajax call - response rendered directly - prevent Velocity template from being rendered.
        return false; //prevent the request from continuing to other click components.
    }

}
