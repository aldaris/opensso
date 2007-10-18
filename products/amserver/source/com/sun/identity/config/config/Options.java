package com.sun.identity.config.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Options extends TemplatedPage {

    public ActionLink createConfigLink = new ActionLink("upgradeLink", this, "upgrade" );
    public ActionLink testUrlLink = new ActionLink("coexistLink", this, "coexist" );
    public ActionLink pushConfigLink = new ActionLink("olderUpgradeLink", this, "olderUpgrade" );

    protected boolean passwordUpdateRequired = Boolean.TRUE;
    protected boolean upgrade = false;

    protected String getTitle() {
        return "configOptions.title";
    }

    public void doInit() {
        //TODO - Determine if a password update is required by checking w/ back-end.
        //remove the session access (and corresponding setting logic in NewPassword class)
        passwordUpdateRequired = getContext().getSessionAttribute( NewPassword.PASSWORD_SET_KEY ) == null;
        addModel("passwordUpdateRequired", passwordUpdateRequired );

        //TODO - determine upgrade case from back-end call
        //only using request param as an easy toggle for testing
        upgrade = toBoolean("upgrade");
        addModel( "upgrade", upgrade );
    }

    public boolean upgrade() {
        //TODO - call back-end to perform upgrade when there are no legacy servers sharing config
        //this supports Wireframes use case  "3G Upgrade Case".

        //for now, just return true to simulate a successful upgrade:
        writeToResponse("true");
        setPath(null);
        return false;
    }

    public boolean coexist() {
        //TODO - call back-end to enable coexistence with AM7.1 and FM7.0 instances
        //This supports Wireframes use case "3G Alternate Flow | Co-existence".

        //for now, simulate success:
        writeToResponse("true");
        setPath(null);
        return false;
    }

    public boolean olderUpgrade() {
        //TODO - call back-end to upgrade from older version
        //This supports Wireframes use case "3G Alternate Flow | Upgrade from older version."

        //for now, simulate success:
        writeToResponse("true");
        setPath(null);
        return false;
    }
}
