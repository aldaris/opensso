package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class JDBCStore {

    private String connectionType;
    private String storeDetails;
    private String jdbcURL;
    private String jndiName;
    private String username;
    private String password;
    private String passwordField;
    private String retrievalStatement;
    private String transformationClass;


    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getStoreDetails() {
        return storeDetails;
    }

    public void setStoreDetails(String storeDetails) {
        this.storeDetails = storeDetails;
    }

    public String getJdbcURL() {
        return jdbcURL;
    }

    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(String passwordField) {
        this.passwordField = passwordField;
    }

    public String getRetrievalStatement() {
        return retrievalStatement;
    }

    public void setRetrievalStatement(String retrievalStatement) {
        this.retrievalStatement = retrievalStatement;
    }

    public String getTransformationClass() {
        return transformationClass;
    }

    public void setTransformationClass(String transformationClass) {
        this.transformationClass = transformationClass;
    }

}
