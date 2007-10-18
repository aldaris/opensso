package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class UserStore {

    private String userStoreName;
    private Boolean storeName;
    private String directory;
    private Boolean directoryLocated;
    private Integer port;
    private String securePort;
    private String login;
    private String password;
    private String baseDN;


    public String getUserStoreName() {
        return userStoreName;
    }

    public void setUserStoreName(String userStoreName) {
        this.userStoreName = userStoreName;
    }

    public Boolean getStoreName() {
        return storeName;
    }

    public void setStoreName(Boolean storeName) {
        this.storeName = storeName;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Boolean getDirectoryLocated() {
        return directoryLocated;
    }

    public void setDirectoryLocated(Boolean directoryLocated) {
        this.directoryLocated = directoryLocated;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getSecurePort() {
        return securePort;
    }

    public void setSecurePort(String securePort) {
        this.securePort = securePort;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }
    
}
