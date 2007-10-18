package com.sun.identity.config.util;

import net.sf.click.Page;

import java.io.IOException;

/**
 * @author Les Hazlewood
 */
public abstract class AjaxPage extends Page {

    public static final String RESPONSE_TEMPLATE = "{\"isValid\":${isValid}, \"errorMessage\":\"${errorMessage}\"}";

    private boolean rendering = false;

    public boolean isRendering() {
        return rendering;
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

    protected void writeToResponse( String text ) {
        try {
            getContext().getResponse().getWriter().write( text );
            this.rendering = true;
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    protected void writeToResponse(boolean isValid, String errorMessage) {
        String response = RESPONSE_TEMPLATE;
        response = response.replaceFirst("\\$\\{" + "isValid" +  "\\}", String.valueOf(isValid));
        response = response.replaceFirst("\\$\\{" + "errorMessage" +  "\\}", errorMessage);
        writeToResponse(response);
    }
}
