package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class AnonymousStore extends AuthenticationStore {

    private String anonymousName = "anonymous";


    public String getAnonymousName() {
        return anonymousName;
    }

    public void setAnonymousName(String anonymousName) {
        this.anonymousName = anonymousName;
    }
    
}
