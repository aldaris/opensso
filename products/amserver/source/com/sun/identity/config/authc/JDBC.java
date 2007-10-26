package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.JDBCStore;
import com.sun.identity.config.util.AjaxPage;

/**
 * @author Jeffrey Bermudez
 */
public class JDBC extends AjaxPage {

    public JDBCStore jdbcStore;


    public void onInit() {
        jdbcStore = new JDBCStore();
    }

    public void onPost() {
        jdbcStore.getRealm().setName(toString("realmName"));
        jdbcStore.setConnectionType(toString("connectionType"));
        jdbcStore.setStoreDetails(toString("storeDetails"));
        jdbcStore.setJdbcURL(toString("jdbcURL"));
        jdbcStore.setJndiName(toString("jndiName"));
        jdbcStore.setUsername(toString("username"));
        jdbcStore.setPassword(toString("password"));
        jdbcStore.setPasswordField(toString("passwordField"));
        jdbcStore.setRetrievalStatement(toString("retrievalStatement"));
        jdbcStore.setTransformationClass(toString("transformationClass"));

        save(jdbcStore);
    }

    protected void save(JDBCStore jdbcStore) {
        getConfigurator().addAuthenticationStore(jdbcStore);
    }

}