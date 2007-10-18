package com.sun.identity.config.tasks.confUserStore;

import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.util.AjaxPage;
import com.sun.identity.config.util.LDAPStoreValidator;
import net.sf.click.control.ActionLink;

/**
 * @author Cesar Bejarano
 * @author Jeffrey Bermudez
 */
public class ConfigureUserStore extends AjaxPage {

    protected LDAPStoreValidator ldapStoreValidator = new LDAPStoreValidator(this);

    public LDAPStore userStore = null;

    public ActionLink validateStoreNameLink = new ActionLink("validateStoreName", ldapStoreValidator, "validateStoreName");
    public ActionLink validateHostLink = new ActionLink("validateHost", ldapStoreValidator, "validateHost");
    public ActionLink validatePortLink = new ActionLink("validatePort", ldapStoreValidator, "validatePort");
    public ActionLink validateLoginLink = new ActionLink("validateLogin", ldapStoreValidator, "validateLogin");
    public ActionLink validatePasswordLink = new ActionLink("validatePassword", ldapStoreValidator, "validatePassword");
    public ActionLink validateBaseDNLink = new ActionLink("validateBaseDN", ldapStoreValidator, "validateBaseDN");


    public void onInit() {
        userStore = new LDAPStore();
    }

    public void onPost() {
        userStore.setName(toString("user_storeName"));
        userStore.setHostName(toString("user_hostName"));
        userStore.setHostPort(toInt("user_hostPort"));
        userStore.setHostPortSecure(toBoolean("user_hostPortSecure"));
        userStore.setUsername(toString("user_login"));
        userStore.setPassword(toString("user_password"));
        userStore.setBaseDN(toString("user_baseDN"));
        save(userStore);
    }

    /**
     * Save LDAPStore object in the back-end.
     * TODO We are temporaly saving information only in the Session while a Model tier is created
     * @param userStore
     */
    protected void save( LDAPStore userStore) {
        getContext().setSessionAttribute("UserStore", userStore);
    }

}
