package com.sun.identity.config.tasks.confAuthentication;

import com.sun.identity.config.pojos.AuthenticationStore;
import com.sun.identity.config.util.TemplatedForm;
import net.sf.click.Page;
import net.sf.click.control.Checkbox;
import net.sf.click.control.HiddenField;
import net.sf.click.control.TextField;

/**
 * @author Laureen Gutierrez
 * @author Jeffrey Bermudez
 */
public class ConfigureAuthenticationStore extends Page {

    private TemplatedForm form = new TemplatedForm("authenticationStoreForm");


    public void onInit() {
        form.add(new Checkbox("realmName"));
        form.add(new TextField("authenticationSource"));
        form.add(new HiddenField("authenticationSourcePage", String.class));

        addControl(form);
    }

    public void onPost() {
        if (form.onProcess()) {
            AuthenticationStore authenticationStore = new AuthenticationStore();
            form.copyTo(authenticationStore, true);
            // Process the POST with the information received
            doPost(authenticationStore);
        }
    }

    /**
     * Process the POST with the information received as parameter.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     * @param authenticationStore
     */
    private void doPost(AuthenticationStore authenticationStore) {
        getContext().setSessionAttribute("AuthenticationStore", authenticationStore);
        setRedirect(authenticationStore.getAuthenticationSourcePage());
    }

}
