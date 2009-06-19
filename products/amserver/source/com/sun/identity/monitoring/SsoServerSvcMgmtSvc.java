package com.sun.identity.monitoring;

//
// Generated by mibgen version 5.1 (05/20/05) when compiling SUN-OPENSSO-SERVER-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;
import com.sun.management.snmp.SnmpString;
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "SsoServerSvcMgmtSvc" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.
 */
public class SsoServerSvcMgmtSvc implements SsoServerSvcMgmtSvcMBean, Serializable {

    /**
     * Variable for storing the value of "SsoServerSvcMgmtRepositorySSL".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.6".
     */
    protected String SsoServerSvcMgmtRepositorySSL = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsoServerSvcMgmtRepositoryOrgDN".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.5".
     */
    protected String SsoServerSvcMgmtRepositoryOrgDN = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsoServerSvcMgmtRepositoryBindDN".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.4".
     */
    protected String SsoServerSvcMgmtRepositoryBindDN = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsoServerSvcMgmtRepositoryHostPort".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.3".
     */
    protected String SsoServerSvcMgmtRepositoryHostPort = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsoServerSvcMgmtRepositoryType".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.2".
     */
    protected String SsoServerSvcMgmtRepositoryType = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "SsoServerSvcMgmtStatus".
     * The variable is identified by: "1.3.6.1.4.1.42.2.230.3.1.1.2.1.14.1".
     */
    protected String SsoServerSvcMgmtStatus = new String("JDMK 5.1");


    /**
     * Constructor for the "SsoServerSvcMgmtSvc" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public SsoServerSvcMgmtSvc(SnmpMib myMib) {
    }


    /**
     * Constructor for the "SsoServerSvcMgmtSvc" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public SsoServerSvcMgmtSvc(SnmpMib myMib, MBeanServer server) {
    }

    /**
     * Getter for the "SsoServerSvcMgmtRepositorySSL" variable.
     */
    public String getSsoServerSvcMgmtRepositorySSL() throws SnmpStatusException {
        return SsoServerSvcMgmtRepositorySSL;
    }

    /**
     * Getter for the "SsoServerSvcMgmtRepositoryOrgDN" variable.
     */
    public String getSsoServerSvcMgmtRepositoryOrgDN() throws SnmpStatusException {
        return SsoServerSvcMgmtRepositoryOrgDN;
    }

    /**
     * Getter for the "SsoServerSvcMgmtRepositoryBindDN" variable.
     */
    public String getSsoServerSvcMgmtRepositoryBindDN() throws SnmpStatusException {
        return SsoServerSvcMgmtRepositoryBindDN;
    }

    /**
     * Getter for the "SsoServerSvcMgmtRepositoryHostPort" variable.
     */
    public String getSsoServerSvcMgmtRepositoryHostPort() throws SnmpStatusException {
        return SsoServerSvcMgmtRepositoryHostPort;
    }

    /**
     * Getter for the "SsoServerSvcMgmtRepositoryType" variable.
     */
    public String getSsoServerSvcMgmtRepositoryType() throws SnmpStatusException {
        return SsoServerSvcMgmtRepositoryType;
    }

    /**
     * Getter for the "SsoServerSvcMgmtStatus" variable.
     */
    public String getSsoServerSvcMgmtStatus() throws SnmpStatusException {
        return SsoServerSvcMgmtStatus;
    }

}