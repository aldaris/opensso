package com.sun.identity.config;

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
        try {
            getConfigurator().writeConfiguration();
            writeToResponse("true");
        } catch( Exception e ) {
            writeToResponse(e.getMessage());
        }

        setPath(null); //ajax call - response rendered directly - prevent Velocity template from being rendered.
        return false; //prevent the request from continuing to other click components.
    }

}
