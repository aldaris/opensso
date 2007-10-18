package com.sun.identity.config.tasks.confAuthentication;

import com.sun.identity.config.pojos.AnonymousStore;
import com.sun.identity.config.util.TemplatedForm;
import net.sf.click.Page;
import net.sf.click.control.TextField;

/**
 * @author Laureen Gutierrez
 * @author Jeffrey Bermudez
 */
public class Anonymous extends Page {

    private TemplatedForm form = new TemplatedForm("anonymousForm");


    public void onInit() {
        form.add(new TextField("anonymousName"));

        addControl(form);
    }

    public void onPost() {
        if (form.onProcess()) {
            AnonymousStore anonymousStore = new AnonymousStore();
            form.copyTo(anonymousStore, true);
            // Process the POST with the information received
            doPost(anonymousStore);
        }
    }

    /**
     * Process the POST with the information received as parameter.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     *
     * @param anonymousStore
     */
    private void doPost(AnonymousStore anonymousStore) {
        getContext().setSessionAttribute("AnonymousStore", anonymousStore);
    }

}