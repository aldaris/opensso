package com.sun.identity.config.wizard;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.Context;
import net.sf.click.control.Checkbox;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;

import javax.servlet.http.Cookie;

/**
 * @author Les Hazlewood
 */
public class Step1 extends AjaxPage {

    public Form form = new Form("wizardStep1Form");

    public Step1() {
    }

    public void onInit() {
        HiddenField hidden = new HiddenField( "skipStep1", "false" );
        form.add( hidden );

        Checkbox cb = new Checkbox( "skipStep1Checkbox", "Skip this step in the future." );
        cb.setAttribute( "onclick", "toggleSkipStep1();");
        form.add( cb );

        if ( isSkipped( getContext() ) ) {
            ((Checkbox)form.getField("skipStep1Checkbox")).setChecked( true );
            form.getField("skipStep1").setValue("true");    
        }
    }

    public static boolean isSkipped( Context context ) {
        Cookie[] cookies = context.getRequest().getCookies();
        if ( cookies != null && cookies.length > 0 ) {
            for( int i = 0; i < cookies.length; i++ ) {
                Cookie cookie = cookies[i];
                if ( cookie.getName().equals( "wizardSkipStep1" ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean disableStep() {
        Cookie cookie = new Cookie( "wizardSkipStep1", "true" );
        cookie.setMaxAge( Integer.MAX_VALUE );
        cookie.setPath( getContext().getRequest().getContextPath() );
        getContext().getResponse().addCookie( cookie );
        form.getField("skipStep1").setValue("true");
        return true;
    }

    public boolean enableStep() {
        Cookie cookie = new Cookie( "wizardSkipStep1", null );
        cookie.setMaxAge( 0 );
        cookie.setPath( getContext().getRequest().getContextPath() );
        getContext().getResponse().addCookie( cookie );
        form.getField("skipStep1").setValue("false");
        return true;
    }

    public void onPost() {
        if ( form.isValid() ) {
            if ( "true".equals( form.getField("skipStep1" ).getValue() ) ) {
                disableStep();
            } else {
                enableStep();
            }
        }
    }
}
