package com.sun.identity.config.pojos;

/**
 * @author Jeffrey Bermudez
 */
public class WindowStore extends AuthenticationStore {

    private String kerberosServicePrincipal;
    private String kerberosFileName;
    private String kerberosRealm;
    private String kerberosServiceName;
    private String domainName;


    public String getKerberosServicePrincipal() {
        return kerberosServicePrincipal;
    }

    public void setKerberosServicePrincipal(String kerberosServicePrincipal) {
        this.kerberosServicePrincipal = kerberosServicePrincipal;
    }

    public String getKerberosFileName() {
        return kerberosFileName;
    }

    public void setKerberosFileName(String kerberosFileName) {
        this.kerberosFileName = kerberosFileName;
    }

    public String getKerberosRealm() {
        return kerberosRealm;
    }

    public void setKerberosRealm(String kerberosRealm) {
        this.kerberosRealm = kerberosRealm;
    }

    public String getKerberosServiceName() {
        return kerberosServiceName;
    }

    public void setKerberosServiceName(String kerberosServiceName) {
        this.kerberosServiceName = kerberosServiceName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }
    
}
