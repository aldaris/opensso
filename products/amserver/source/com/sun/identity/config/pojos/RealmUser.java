package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class RealmUser {

    private String firstName;
    private String lastName;
    private RealmRole realmRole = null;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public RealmRole getRealmRole() {
        return realmRole;
    }

    public void setRealmRole(RealmRole realmRole) {
        this.realmRole = realmRole;
    }

}
