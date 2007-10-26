package com.sun.identity.config;

import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.PasswordField;
import net.sf.click.control.Submit;

/**
 * @author Les Hazlewood
 */
public class NewPassword extends AjaxPage {

    public Form newPasswordForm = new Form("newPasswordForm");

    public void onInit() {
        newPasswordForm.add( new HiddenField("username", "amAdmin" ) );
        newPasswordForm.add( new PasswordField( "password", getMessage("newPassword.password"), true ) );
        newPasswordForm.add( new PasswordField( "passwordConfirm", getMessage("newPassword.passwordConfirm"), true ) );

        Submit submit = new Submit("save", getMessage("save"), this, "onSubmit" );
        submit.setAttribute( "onclick", "submitNewPasswordForm(); return false;");

        newPasswordForm.add( submit );
    }

    public boolean onSubmit() {

        if ( newPasswordForm.isValid() ) {
            String password = newPasswordForm.getField( "password" ).getValue();
            String passwordConfirm = newPasswordForm.getField( "passwordConfirm" ).getValue();

            if ( !password.equals(passwordConfirm) ) {
                newPasswordForm.setError( getMessage( "newPassword.error" ) );
                getContext().getResponse().setHeader( "formError", "true" );
            } else {
                String username = newPasswordForm.getField("username").getValue();
                getConfigurator().setPassword( username, password );
            }
        } else {
            getContext().getResponse().setHeader("formError", "true" );
        }
        return true;
    }
}
