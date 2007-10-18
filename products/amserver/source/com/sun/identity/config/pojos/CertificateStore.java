package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class CertificateStore {

    private String userId;
    private Boolean checkAgainstLDAP;
    private Boolean checkAgainstCRL;
    private String searchAttribute;
    private Boolean checkAgainstOSCP;
    private LDAPStore userStore = new LDAPStore();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getCheckAgainstLDAP() {
        return checkAgainstLDAP;
    }

    public void setCheckAgainstLDAP(Boolean checkAgainstLDAP) {
        this.checkAgainstLDAP = checkAgainstLDAP;
    }

    public Boolean getCheckAgainstCRL() {
        return checkAgainstCRL;
    }

    public void setCheckAgainstCRL(Boolean checkAgainstCRL) {
        this.checkAgainstCRL = checkAgainstCRL;
    }

    public String getSearchAttribute() {
        return searchAttribute;
    }

    public void setSearchAttribute(String searchAttribute) {
        this.searchAttribute = searchAttribute;
    }

    public Boolean getCheckAgainstOSCP() {
        return checkAgainstOSCP;
    }

    public void setCheckAgainstOSCP(Boolean checkAgainstOSCP) {
        this.checkAgainstOSCP = checkAgainstOSCP;
    }

    public LDAPStore getUserStore() {
        return userStore;
    }

    public void setUserStore(LDAPStore userStore) {
        this.userStore = userStore;
    }
    
}
