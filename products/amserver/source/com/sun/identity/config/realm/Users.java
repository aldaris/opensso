package com.sun.identity.config.realm;

import com.sun.identity.config.pojos.Realm;
import com.sun.identity.config.util.AjaxPage;

import java.util.List;

/**
 * @author Les Hazlewood
 */
public class Users extends AjaxPage {

    public List users;
    public int totalUsers;

    public void onGet() {
        String realmName = toString("realmName");
        String filter = toString("filter");
        Realm realm = new Realm();

        realm.setName(realmName);
        users = getConfigurator().getUsers(realm, filter);
        totalUsers = users.size();
    }

}
