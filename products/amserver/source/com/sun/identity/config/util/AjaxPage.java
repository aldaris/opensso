package com.sun.identity.config.util;

import com.sun.identity.config.Configurator;
import com.sun.identity.config.DummyConfigurator;
import net.sf.click.Page;

import java.io.IOException;

/**
 * @author Les Hazlewood
 */
public abstract class AjaxPage extends Page {

    public static final String RESPONSE_TEMPLATE = "{\"valid\":${valid}, \"body\":\"${body}\"}";
    public static final String OLD_RESPONSE_TEMPLATE = "{\"isValid\":${isValid}, \"errorMessage\":\"${errorMessage}\"}";

    private Configurator configurator = null;

    private boolean rendering = false;

    public AjaxPage() {
    }

    public boolean isRendering() {
        return rendering;
    }

    protected Configurator getConfigurator() {
        if ( this.configurator == null ) {
            //TODO - retrieve Configuration instance from runtime environment
            //servlet context lookup?  JNDI?  Still awaiting word.  Use dummy for now:
            this.configurator = new DummyConfigurator( this );
        }
        return this.configurator;
    }

    protected String toString( String paramName ) {
        String value = getContext().getRequest().getParameter( paramName );
        value = ( value != null ? value.trim() : null );
        value = ("".equals(value) ? null : value );
        return value;
    }

    protected boolean toBoolean( String paramName ) {
        String value = toString( paramName );
        return ( value != null && value.toLowerCase().equals( "true" ) );
    }

    protected int toInt( String paramName ) {
        int intValue = 0;
        String value = toString( paramName );
        if ( value != null ) {
            try {
                intValue = Integer.parseInt( value );
            } catch ( NumberFormatException e ) {}
        }
        return intValue;
    }

    protected void writeValid() {
        writeValid( null );
    }
    protected void writeValid( String message ) {
        String out = ( message != null ? message : "" );
        writeJsonResponse(true, out);
    }
    protected void writeInvalid( String message ) {
        String out = ( message != null ? message : "" );
        writeJsonResponse( false, out );
    }

    protected void writeJsonResponse( boolean valid, String responseBody ) {
        String response = RESPONSE_TEMPLATE;
        response = response.replaceFirst("\\$\\{" + "valid" +  "\\}", String.valueOf(valid));
        response = response.replaceFirst("\\$\\{" + "body" +  "\\}", responseBody);
        writeToResponse(response);
    }

    protected void writeToResponse( String text ) {
        try {
            getContext().getResponse().getWriter().write( text );
            this.rendering = true;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected void writeToResponse(boolean isValid, String errorMessage) {
        String response = OLD_RESPONSE_TEMPLATE;
        response = response.replaceFirst("\\$\\{" + "isValid" +  "\\}", String.valueOf(isValid));
        response = response.replaceFirst("\\$\\{" + "errorMessage" +  "\\}", errorMessage);
        writeToResponse(response);
    }
}
