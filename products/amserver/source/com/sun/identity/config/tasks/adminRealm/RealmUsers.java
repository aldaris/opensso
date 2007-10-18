package com.sun.identity.config.tasks.adminRealm;

import com.sun.identity.config.pojos.RealmUser;
import com.sun.identity.config.util.AjaxPage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jeffrey Bermudez
 */
public class RealmUsers extends AjaxPage {

    public Integer totalRealmUsers = new Integer(500);
    public Integer returnedRealmUsers;
    public Integer startIndex;
    public List realmUsers;

    public void onGet() {
        int expectedResults = toInt( "results" );
        startIndex = new Integer( toInt( "startIndex" ) );
        realmUsers = new ArrayList();

        for (int i = 0; i < expectedResults && (i + startIndex.intValue()) <= totalRealmUsers.intValue(); i++) {
            RealmUser realmUser = new RealmUser();
            realmUser.setFirstName("FirstName" + (i + startIndex.intValue()));
            realmUser.setLastName("LastName" + (i + startIndex.intValue()));
            realmUsers.add(realmUser);
        }

        returnedRealmUsers = new Integer( realmUsers.size() );
    }

}
