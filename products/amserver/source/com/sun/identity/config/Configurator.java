package com.sun.identity.config;

import com.sun.identity.config.pojos.AuthenticationStore;
import com.sun.identity.config.pojos.LDAPStore;
import com.sun.identity.config.pojos.Realm;
import com.sun.identity.config.pojos.RealmRole;

import java.util.List;

/**
 * Interface encapsulating all back-end functionality to support the front-end page operations.
 *
 * @author Les Hazlewood
 */
public interface Configurator {

    boolean isPasswordUpdateRequired();

    /**
     * If <tt>true</tt>, the options.htm page will be cusotmized for a new installation, otherwise it will be
     * customized for an upgrade.
     * @return
     */
    boolean isNewInstall();

    /**
     * Sets the system account password for the given username with the specified password.
     * @param username the system account username for which to set the password.
     * @param password the verified password to associate with the given username.
     */
    void setPassword( String username, String password );

    /**
     * Tests the hostname, port and secure properties of the specified LDAP store configuration - i.e. if they are
     * valid as well as if the host is online and can be accessed.
     *
     * <p>If it is not valid, an exception should be thrown.  That exception's getMessage() will be shown to the user
     * explaining why the host is not valid or online.
     *
     * @param store the store config with the host to test
     */
    void testHost( LDAPStore store );

    /**
     * Tests the Base DN of the specified LDAPStore configuration.
     *
     * <p>If it is not valid or accessible, an exception should be thrown.  That exception's getMessage() will be
     * shown to the user explaining why it is not valid or accessible.
     *
     * @param store the store config with the Base DN to test
     */
    void testBaseDN( LDAPStore store );

    /**
     * Tests the Login ID and credentials of specified LDAPStore configuration.
     *
     * <p>If the id and/or password are not valid, an exception should be thrown.  That exception's getMessage() will
     * be shown to the user explaining why it is not valid or accessible.
     *
     * @param store the store config with the Base DN to test
     */
    void testLoginId( LDAPStore store );

    /**
     * Tests the hostname and port for a user-specified load balancer to ensure it is valid and online/accessible.
     *
     * <p>If it is not valid or unreachable, an exception should be thrown.  That exception's getMessage() will be
     * shown to the user explaining why the host is not valid or online.
     *
     * @param host - the host name of the load balancer to test
     * @param port - the port of the load balancer to test.  If non-positive (i.e. 0 or less), then the user did not
     * specify a port and the argument can be ignored.
     */
    void testLoadBalancer( String host, int port );


    /**
     * When a new instance is deployed, and all defaults will be used, this method executes the default
     * installation configuration (e.g. demo environment);
     */
    void writeConfiguration();

    /**
     * Writes a user-specified custom configuration.  A null value in any argument means that it was not specified by
     * the user during the config process and the system defaults should come in to effect for that argument.
     *
     * @param newInstanceUrl
     * @param configStor
     * @param userStore
     * @param loadBalancerHost
     * @param loadBalancerPort
     */
    void writeConfiguration( String newInstanceUrl, LDAPStore configStor, LDAPStore userStore,
                             String loadBalancerHost, int loadBalancerPort );

    /**
     * Returns a list of URL {@link java.lang.String strings}, one for each each FAM instance whose configuration
     * could be copied over to this instance.  This method supports the first step (of two) in Wireframes
     * Flow 3E.
     *
     * @return a list of URL strings, one for each FAM instance whose config could be copied to this instance.
     */
    List getExistingConfigurations();


    /**
     * Sets this instance's configuration based on the specified URL strings of the corresponding FAM instances.
     * This method supports the second step (of two) in Wireframes Flow 3E.
     *
     * @param configStringUrls URL strings, one for each FAM instance whose config should be copied to this instance.
     */
    void writeConfiguration( List configStringUrls );


    /**
     * Tests that the path specified by the user is accessible and represents a currently on-line other instance in a
     * multi-instance set-up.
     *
     * <p>If it is not valid, an exception should be thrown.  That exception's getMessage() will be shown to the user
     * explaining why the instance url is not valid or online.
     *
     * @param url - user specified new instance url/path to test for online validation.
     */
    void testNewInstanceUrl( String url );

    /**
     * In a multi-instance configuration, this method pushes the configuration to the instance with the specified
     * url/path.
     * @param instanceUrl the user-specified and previously validated url/path to push the configuration to.
     */
    void pushConfiguration( String instanceUrl );

    /**
     * Supports Wireframes "3G Upgrade Case".
     */
    void upgrade();

    /**
     * Supports wireframes "3G Alternate Flow | Co-existence"
     */
    void coexist();

    /**
     * Supports wireframes "3G Alternate Flow | Upgrade from older version."
     */
    void olderUpgrade();

    /**
     * Returns a list of {@link com.sun.identity.config.pojos.Realm} objects.
     * @return the list available Realms
     */
    List getRealms();

    /**
     * Returns a list of {@link com.sun.identity.config.pojos.RealmUser} objects for a specified Realm.
     * @param realm
     * @param filter
     * @return the list Realm Users for the Realm provided as parameter
     */
    List getUsers(Realm realm, String filter);

    /**
     * Returns a list of {@link com.sun.identity.config.pojos.RealmUser} objects with a RealmRole assigned (wich mean they have some specific administrative charge) for a specified Realm and RealmRole.
     * @param realm
     * @param role
     * @return the list Realm users for the Realm and RealmRole provided as parameters
     */
    List getAdministrators(Realm realm, RealmRole role);

    /**
     * Saves the AuthenticationStore object passed as parameter.
     * @param authenticationStore the object to save
     */
    void addAuthenticationStore(AuthenticationStore authenticationStore);

}
