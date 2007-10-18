package com.sun.identity.config.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * @author Les Hazlewood
 * @since 2007-09-12
 */
public abstract class TemplatedPage extends AjaxPage {

    public static final String STATUS_MESSAGE_CODES_SESSION_KEY = "statusMessageCodes";

    public int currentYear = Calendar.getInstance().get( Calendar.YEAR );

    public List statusMessages = new ArrayList(); //list of Strings

    public String getTemplate() {
        return "assets/templates/main.html";
    }

    /**
     * Return the i18n code of the page title.
     * @return the i18n code of the page title;
     */
    protected abstract String getTitle();


    protected List getStatusMessageCodes() {
        return (List)getContext().getSessionAttribute( STATUS_MESSAGE_CODES_SESSION_KEY );
    }

    protected void addStatusMessageCode( String statusMessageCode ) {
        List codes = getStatusMessageCodes(); //strings
        if ( codes == null ) {
            codes = new ArrayList();
        }
        codes.add( statusMessageCode );
        getContext().setSessionAttribute( STATUS_MESSAGE_CODES_SESSION_KEY, codes );
    }

    protected void clearStatusMessageCodes() {
        getContext().removeSessionAttribute( STATUS_MESSAGE_CODES_SESSION_KEY );
    }

    protected String i18n( String key ) {
        return getMessage( key );
    }

    protected String i18n( String key, Object arg ) {
        return getMessage( key, arg );
    }

    protected String i18n( String key, Object[] args ) {
        return getMessage( key, args );
    }

    public final void onInit() {
        addModel( "title", i18n( getTitle() ) );
        List sessionStatusMessages = getStatusMessageCodes();
        if ( sessionStatusMessages != null && !sessionStatusMessages.isEmpty() ) {
            Iterator i = sessionStatusMessages.iterator();
            while( i.hasNext() ) {
                String messageCode = (String)i.next();
                statusMessages.add( i18n( messageCode ) );
            }
            clearStatusMessageCodes();
        }
        doInit();
    }

    protected void doInit() {
        //subclass implementation hook.
    }

}
