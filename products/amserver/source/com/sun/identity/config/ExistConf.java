package com.sun.identity.config;

import com.sun.identity.config.util.TemplatedPage;
import net.sf.click.control.ActionLink;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Les Hazlewood
 */
public class ExistConf extends TemplatedPage {

    public static final String INDEX_PARAM_NAME = "indices";
    public static final String INDEX_DELIMITER = ",";

    public ActionLink writeConfigLink = new ActionLink("writeConfig", this, "writeConfig" );

    private List existingConfigs = null;

    protected String getTitle() {
        return "existConf.title";
    }

    public void doInit() {
        existingConfigs = getConfigurator().getExistingConfigurations();
        if ( existingConfigs == null ) {
            existingConfigs = new ArrayList();
        }
        addModel( "existingConfigs", existingConfigs );
    }

    private Integer integer( String sval ) {
        try {
            return Integer.valueOf( sval );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public boolean writeConfig() {
        //comma delimited values of the indices of the original list (easier than encoding/decoding urls):
        String commaDelimited = toString( INDEX_PARAM_NAME );
        if ( commaDelimited == null ) {
            writeInvalid("Invalid access.");
            setPath(null);
            return false;
        }

        String[] indicesArray = commaDelimited.split( INDEX_DELIMITER );
        List indices = new ArrayList(indicesArray.length);
        for( int i = 0; i < indicesArray.length; i++ ) {
            Integer index = integer(indicesArray[i]);
            if ( index == null ) {
                writeInvalid("Invalid access." );
                setPath(null);
                return false;
            } else {
                indices.add( index );
            }
        }

        List configs = new ArrayList(indices.size());
        for( int i = 0; i < indices.size(); i++ ) {
            configs.add( existingConfigs.get(i) );
        }

        try {
            getConfigurator().writeConfiguration( configs );
            writeValid("Configuration complete.");
        } catch ( Exception e ) {
            writeInvalid(e.getMessage());
        }
        setPath(null);
        return false;
    }
}