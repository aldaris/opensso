package com.sun.identity.config.authc;

import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.config.util.LDAPStoreValidator;
import net.sf.click.control.ActionLink;

/**
 * @author Jeffrey Bermudez
 */
public class LDAP extends AjaxPage {

    protected LDAPStoreValidator ldapStoreValidator = new LDAPStoreValidator(this);

    public LDAPStore ldapStore = null;

    public ActionLink validateStoreNameLink = new ActionLink("validateStoreName", ldapStoreValidator, "validateStoreName");
    public ActionLink validateHostLink = new ActionLink("validateHost", ldapStoreValidator, "validateHost");
    public ActionLink validatePortLink = new ActionLink("validatePort", ldapStoreValidator, "validatePort");
    public ActionLink validateLoginLink = new ActionLink("validateLogin", ldapStoreValidator, "validateLogin");
    public ActionLink validatePasswordLink = new ActionLink("validatePassword", ldapStoreValidator, "validatePassword");
    public ActionLink validateBaseDNLink = new ActionLink("validateBaseDN", ldapStoreValidator, "validateBaseDN");


    public void onInit() {
        ldapStore = new LDAPStore();
    }

    public void onPost() {
        ldapStore.getRealm().setName(toString("realmName"));
        ldapStore.setName(toString("user_storeName"));
        ldapStore.setHostName(toString("user_hostName"));
        ldapStore.setHostPort(toInt("user_hostPort"));
        ldapStore.setHostPortSecure(toBoolean("user_hostPortSecure"));
        ldapStore.setUsername(toString("user_login"));
        ldapStore.setPassword(toString("user_password"));
        ldapStore.setBaseDN(toString("user_baseDN"));

        save(ldapStore);
    }

    protected void save(LDAPStore ldapStore) {
        getConfigurator().addAuthenticationStore(ldapStore);
    }

}
