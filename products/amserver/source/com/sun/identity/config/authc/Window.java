package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.WindowStore;
import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Jeffrey Bermudez
 */
public class Window extends AjaxPage {

    public WindowStore windowStore = null;

    public ActionLink validateServicePrincipalLink = new ActionLink("validateServicePrincipal", this, "validateServicePrincipal");
    public ActionLink validateFileNameLink = new ActionLink("validateFileName", this, "validateFileName");    
    public ActionLink validateRealmLink = new ActionLink("validateRealm", this, "validateRealm");
    public ActionLink validateServiceNameLink = new ActionLink("validateServiceName", this, "validateServiceName");
    public ActionLink validateDomainNameLink = new ActionLink("validateDomainName", this, "validateDomainName");


    public void onInit() {
        windowStore = new WindowStore();
    }

    public boolean validateServicePrincipal() {
        return validateRequiredField("kerberosServicePrincipal");
    }

    public boolean validateFileName() {
        return validateRequiredField("kerberosFileName");
    }

    public boolean validateRealm() {
        return validateRequiredField("kerberosRealm");
    }

    public boolean validateServiceName() {
        return validateRequiredField("kerberosServiceName");
    }

    public boolean validateDomainName() {
        return validateRequiredField("domainName");
    }

    protected boolean validateRequiredField(String fieldName) {
        String servicePrincipal = toString(fieldName);
        boolean isValid = (servicePrincipal != null);
        writeToResponse(isValid, (isValid) ? "" : "Field required.");
        setPath(null);
        return false;
    }

    public void onPost() {
        windowStore.getRealm().setName(toString("realmName"));
        windowStore.setKerberosServicePrincipal(toString("kerberosServicePrincipal"));
        windowStore.setKerberosFileName(toString("kerberosFileName"));
        windowStore.setKerberosRealm(toString("kerberosRealm"));
        windowStore.setKerberosServiceName(toString("kerberosServiceName"));
        windowStore.setDomainName(toString("domainName"));

        save(windowStore);
    }

    protected void save(WindowStore windowStore) {
        getConfigurator().addAuthenticationStore(windowStore);
    }

}