package com.sun.identity.config.tasks.confAuthentication;

import com.sun.identity.config.pojos.CertificateStore;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.config.util.LDAPStoreValidator;
import net.sf.click.control.ActionLink;

/**
 * @author Laureen Gutierrez
 * @author Jeffrey Bermudez
 */
public class Certificate extends AjaxPage {

    protected LDAPStoreValidator ldapStoreValidator = new LDAPStoreValidator(this);

    public CertificateStore certificateStore = null;

    public ActionLink validateStoreNameLink = new ActionLink("validateStoreName", ldapStoreValidator, "validateStoreName");
    public ActionLink validateHostLink = new ActionLink("validateHost", ldapStoreValidator, "validateHost");
    public ActionLink validatePortLink = new ActionLink("validatePort", ldapStoreValidator, "validatePort");
    public ActionLink validateLoginLink = new ActionLink("validateLogin", ldapStoreValidator, "validateLogin");
    public ActionLink validatePasswordLink = new ActionLink("validatePassword", ldapStoreValidator, "validatePassword");
    public ActionLink validateBaseDNLink = new ActionLink("validateBaseDN", ldapStoreValidator, "validateBaseDN");

    public void onInit() {
        certificateStore = new CertificateStore();
    }

    public void onPost() {
        certificateStore.setUserId(toString("userId"));
        certificateStore.setCheckAgainstLDAP(toBoolean("checkAgainstLDAP"));
        certificateStore.setCheckAgainstCRL(toBoolean("checkAgainstCRL"));
        certificateStore.setSearchAttribute(toString("searchAttribute"));
        certificateStore.setCheckAgainstOSCP(toBoolean("checkAgainstOSCP"));
        certificateStore.getUserStore().setName(toString("user_storeName"));
        certificateStore.getUserStore().setHostName(toString("user_hostName"));
        certificateStore.getUserStore().setHostPort(toInt("user_hostPort"));
        certificateStore.getUserStore().setHostPortSecure(toBoolean("user_hostPortSecure"));
        certificateStore.getUserStore().setUsername(toString("user_login"));
        certificateStore.getUserStore().setPassword(toString("user_password"));
        certificateStore.getUserStore().setBaseDN(toString("user_baseDN"));

        save(certificateStore);
    }

    /**
     * Save CertificateStore object in the back-end.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     * @param certificateStore
     */
    protected void save(CertificateStore certificateStore) {
        getContext().setSessionAttribute("CertificateStore", certificateStore);
    }

}