package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class CertificateStore extends AuthenticationStore {

    private String userId;
    private boolean checkAgainstLDAP;
    private boolean checkAgainstCRL;
    private String searchAttribute;
    private boolean checkAgainstOSCP;
    private LDAPStore userStore = new LDAPStore();


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCheckAgainstLDAP() {
        return checkAgainstLDAP;
    }

    public void setCheckAgainstLDAP(boolean checkAgainstLDAP) {
        this.checkAgainstLDAP = checkAgainstLDAP;
    }

    public boolean isCheckAgainstCRL() {
        return checkAgainstCRL;
    }

    public void setCheckAgainstCRL(boolean checkAgainstCRL) {
        this.checkAgainstCRL = checkAgainstCRL;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public boolean isCheckAgainstOSCP() {
        return checkAgainstOSCP;
    }

    public void setCheckAgainstOSCP(boolean checkAgainstOSCP) {
        this.checkAgainstOSCP = checkAgainstOSCP;
    }

    public LDAPStore getUserStore() {
        return userStore;
    }

    public void setUserStore(LDAPStore userStore) {
        this.userStore = userStore;
    }
    
}
