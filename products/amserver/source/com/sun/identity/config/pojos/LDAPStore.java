package com.sun.identity.config.pojos;

/**
 * @author Les Hazlewood
 */
public class LDAPStore {

    private String name;
    private String hostName;
    private int hostPort = 389;
    private boolean hostPortSecure = false;
    private String baseDN = null;
    private String username;
    private String password;

    public LDAPStore(){}

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName( String hostName ) {
        this.hostName = hostName;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort( int hostPort ) {
        this.hostPort = hostPort;
    }

    public boolean isHostPortSecure() {
        return hostPortSecure;
    }

    public void setHostPortSecure( boolean hostPortSecure ) {
        this.hostPortSecure = hostPortSecure;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN( String baseDN ) {
        this.baseDN = baseDN;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername( String username ) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword( String password ) {
        this.password = password;
    }
}
