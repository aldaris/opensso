package com.sun.identity.config.util;

/**
 * @author Jeffrey Bermudez
 */
public class LDAPStoreValidator {

    private AjaxPage page;


    public LDAPStoreValidator(AjaxPage page) {
        this.page = page;
    }

    public boolean validateStoreName() {
        String storeName = page.toString("storeName");
        String response = "true";
        if (storeName == null) {
            response = "Please specify a user store name.";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

    public boolean validateHost() {
        String host = page.toString("hostName");
        String response = "true";
        if (host == null) {
            response = "Please specify a user store name.";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

    public boolean validatePort() {
        int port  = page.toInt("hostPort");
        String response = "true";
        if ( port < 1 || port > 65535 ) {
            response = "Please use a port from 1 to 65535";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

    public boolean validateLogin() {
        String login = page.toString("login");
        String response = "true";
        if (login == null) {
            response = "Please specify a login ID.";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

    public boolean validatePassword() {
        String password = page.toString("password");
        String response = "true";
        if (password == null) {
            response = "Please specify a password.";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

    public boolean validateBaseDN() {
        String password = page.toString("baseDN");
        String response = "true";
        if (password == null) {
            response = "Please specify a Base DN.";
        }
        page.writeToResponse(response);
        page.setPath(null);
        return false;
    }

}
