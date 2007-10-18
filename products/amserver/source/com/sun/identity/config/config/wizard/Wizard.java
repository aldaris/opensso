package com.sun.identity.config.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Wizard extends AjaxPage {

    public int startingTab = 1;

    public ActionLink createConfigLink = new ActionLink("createConfig", this, "createConfig" );
    public ActionLink testUrlLink = new ActionLink("testNewInstanceUrl", this, "testNewInstanceUrl" );
    public ActionLink pushConfigLink = new ActionLink("pushConfig", this, "pushConfig" );

    public void onInit() {
        startingTab = Step1.isSkipped( getContext() ) ? 2 : 1;
    }

    /**
     * This is the 'execute' operation for the entire wizard.  This method aggregates all data submitted across the 
     * wizard pages here in one lump and hands it off to the back-end for processing.
     */
    public boolean createConfig() {

        //collect data from the HTTP session (using the constants in the Step1 ... Step5 pages), put them in a
        //format the back-end expects, and submit all in a single transaction.
        
        //TODO - submit all wizard data to back-end

        //for now, just return 'true' to indicate to the javascript that the submission was successful.
        writeToResponse("true");

        setPath(null);
        return false;
    }

    /**
     * Supports FAM 8.0 Final wireframes, page 52.
     */
    public boolean testNewInstanceUrl() {
        //TODO - submit newInstanceUrl to back-end for testing

        //for now, just return 'true' to indicate to the javascript that the submission was successful.
        writeToResponse("true");

        setPath(null);
        return false;
    }

    /**
     * In a multi-instance configuration, this method will push the the new instance configuration to that instance.
     */
    public boolean pushConfig() {

        //TODO - push config data to new instance

        //for now, just return 'true' to indicate to the javascript that the submission was successful.
        writeToResponse("true");

        setPath(null);
        return false;
    }
}
