package com.sun.identity.config.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Step2 extends AjaxPage {

    public static final String NEW_INSTANCE_URL_SESSION_KEY = "wizardNewInstanceUrl";

    public ActionLink clearLink = new ActionLink("clear", this, "clear" );
    public ActionLink checkNewInstanceUrlLink = new ActionLink("checkNewInstanceUrl", this, "checkNewInstanceUrl" );

    public Step2(){}

    public void onInit() {
        String newInstanceUrl = (String)getContext().getSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY );
        if ( newInstanceUrl != null ) {
            addModel( "newInstanceUrl", newInstanceUrl );
        }
    }

    public boolean clear() {
        getContext().removeSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY );
        setPath(null);
        return false;
    }

    public boolean checkNewInstanceUrl() {

        String newInstanceUrl = toString( "newInstanceUrl" );
        if ( newInstanceUrl == null ) {
            writeToResponse("Please specify a url." );
        } else {
            //TODO - validate instance URL via backend call here
            //assuming above validation is successful, write to session here for review in the summary screen later:
            getContext().setSessionAttribute( NEW_INSTANCE_URL_SESSION_KEY, newInstanceUrl );
            writeToResponse( "true" );
        }

        setPath(null);
        return false;
    }
}
