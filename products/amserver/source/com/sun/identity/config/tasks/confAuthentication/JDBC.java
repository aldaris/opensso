package com.sun.identity.config.tasks.confAuthentication;

import com.sun.identity.config.pojos.JDBCStore;
import com.sun.identity.config.util.TemplatedForm;
import net.sf.click.Page;
import net.sf.click.control.TextField;

/**
 * @author Laureen Gutierrez
 * @author Jeffrey Bermudez
 */
public class JDBC extends Page {

    private TemplatedForm form = new TemplatedForm("jdbcForm");


    public void onInit() {
        form.add(new TextField("connectionType"));
        form.add(new TextField("storeDetails"));
        form.add(new TextField("jdbcURL"));
        form.add(new TextField("jndiName"));
        form.add(new TextField("username"));
        form.add(new TextField("password"));
        form.add(new TextField("passwordField"));
        form.add(new TextField("retrievalStatement"));
        form.add(new TextField("transformationClass"));

        addControl(form);
    }

    public void onPost() {
        if (form.onProcess()) {
            JDBCStore jdbcStore = new JDBCStore();
            form.copyTo(jdbcStore, true);
            // Process the POST with the information received
            doPost(jdbcStore);
        }
    }

    /**
     * Process the POST with the information received as parameter.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     * @param jdbcStore
     */
    private void doPost(JDBCStore jdbcStore) {
        getContext().setSessionAttribute("JDBCStore", jdbcStore);
    }

}