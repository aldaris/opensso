package com.sun.identity.config.tasks.confAuthentication;

import com.sun.identity.config.pojos.WindowStore;
import com.sun.identity.config.util.AjaxPage;
import net.sf.click.control.ActionLink;

/**
 * @author Laureen Gutierrez
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
        return validateRequiredField("servicePrincipal");
    }

    public boolean validateFileName() {
        return validateRequiredField("fileName");
    }

    public boolean validateRealm() {
        return validateRequiredField("realm");
    }

    public boolean validateServiceName() {
        return validateRequiredField("serviceName");
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
        windowStore.setServicePrincipal(toString("servicePrincipal"));
        windowStore.setFileName(toString("fileName"));
        windowStore.setRealm(toString("realm"));
        windowStore.setServiceName(toString("serviceName"));
        windowStore.setDomainName(toString("domainName"));

        save(windowStore);
    }

    /**
     * Save CertificateStore object in the back-end.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     * @param windowStore
     */
    protected void save(WindowStore windowStore) {
        getContext().setSessionAttribute("WindowStore", windowStore);
    }

}