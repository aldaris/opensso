package com.sun.identity.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;

/**
 * @author Les Hazlewood
 */
public class Options extends TemplatedPage {

    public ActionLink createConfigLink = new ActionLink("upgradeLink", this, "upgrade" );
    public ActionLink testUrlLink = new ActionLink("coexistLink", this, "coexist" );
    public ActionLink pushConfigLink = new ActionLink("olderUpgradeLink", this, "olderUpgrade" );

    protected boolean passwordUpdateRequired = true;
    protected boolean upgrade = false;

    protected String getTitle() {
        return "configOptions.title";
    }

    public void doInit() {
        passwordUpdateRequired = getConfigurator().isPasswordUpdateRequired();
        addModel("passwordUpdateRequired", Boolean.valueOf( passwordUpdateRequired ) );

        upgrade = !getConfigurator().isNewInstall();
        addModel( "upgrade", Boolean.valueOf( upgrade ) );
    }

    public boolean upgrade() {
        try {
            getConfigurator().upgrade();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse( e.getMessage());
        }
        setPath(null);
        return false;
    }

    public boolean coexist() {
        try {
            getConfigurator().coexist();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }
        setPath(null);
        return false;
    }

    public boolean olderUpgrade() {
        try {
            getConfigurator().olderUpgrade();
            writeToResponse("true");
        } catch ( Exception e ) {
            writeToResponse(e.getMessage());
        }
        setPath(null);
        return false;
    }
}
