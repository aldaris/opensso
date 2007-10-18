package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class AuthenticationStore {

    private String realmName;
    private String authenticationSource;
    private String authenticationSourcePage;


    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getAuthenticationSource() {
        return authenticationSource;
    }

    public void setAuthenticationSource(String authenticationSource) {
        this.authenticationSource = authenticationSource;
    }

    public String getAuthenticationSourcePage() {
        return authenticationSourcePage;
    }

    public void setAuthenticationSourcePage(String authenticationSourcePage) {
        this.authenticationSourcePage = authenticationSourcePage;
    }
    
}
