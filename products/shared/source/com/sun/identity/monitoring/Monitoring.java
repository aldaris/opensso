/* The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * https://opensso.dev.java.net/public/CDDLv1.0.html or
 * opensso/legal/CDDLv1.0.txt
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at opensso/legal/CDDLv1.0.txt.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * $Id: Monitoring.java,v 1.1 2007-08-28 20:28:59 bigfatrat Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.monitoring;

import java.lang.ClassLoader;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Properties;

import com.sun.cmm.cim.Distribution;
import com.sun.cmm.cim.OperationalStatus;
import com.sun.cmm.CMM_ApplicationSystem;
import com.sun.cmm.j2ee.CMM_J2eeServer;
import com.sun.cmm.relations.CMM_ApplicationSystemDependency;
import com.sun.mfwk.instrum.me.CIM_ManagedElementInstrum;
import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_ApplicationSystemSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ApplicationSystemStatsInstrum;
import com.sun.mfwk.instrum.relations.MfRelationInstrum;
import com.sun.mfwk.instrum.relations.MfRelationInstrumException;
import com.sun.mfwk.instrum.server.MfManagedElementInfo;
import com.sun.mfwk.instrum.server.MfManagedElementServer;
import com.sun.mfwk.instrum.server.MfManagedElementServerException;
import com.sun.mfwk.instrum.server.MfManagedElementServerFactory;
import com.sun.mfwk.instrum.server.MfManagedElementServerProperties;
import com.sun.mfwk.instrum.server.MfManagedElementType;
import com.sun.mfwk.instrum.server.MfRelationInfo;
import com.sun.mfwk.instrum.server.MfRelationType;
import com.sun.mfwk.util.log.MfLogService;

import com.sun.identity.shared.Constants;
import com.sun.identity.shared.configuration.SystemPropertiesManager;
import com.sun.identity.shared.debug.Debug;


/**
 * This class provides the initialization of AM-related managed elements
 * for the JES Monitoring Framework.  It is invoked during container
 * initialization in order to minimize startup latency during "first
 * contact" with the AM server.  It is invoked from
 * WebtopNaming.java:updateNamingTable().
 */

public class Monitoring {

    private static MfManagedElementServer mfMEServer;
    private static MfManagedElementInfo meInfo;
    protected static Debug debug;
    protected static boolean isLocal;

    private static CMM_ApplicationSystemInstrum amAppli;
    private static CMM_ApplicationSystemStatsInstrum amApplstati;
    private static CMM_ApplicationSystemSettingInstrum amApplseti;

    private static CMM_ServiceAccessURIInstrum amJaxrpcUri;

    /*
     *  containers AM Server may be run under.  genAS is generic 3rd party,
     *  such as WebLogic, WebSphere
     */
    private static CMM_ApplicationSystemDependency jesAS; // JES WS
    private static CMM_ApplicationSystemDependency jesWS; // JES AS
    private static CMM_ApplicationSystemDependency genAS; // 3rd party

    static {
        if (debug == null) {
            debug = Debug.getInstance("amMonitoring");
        }
    }

    /**
     *  Monitoring constructor
     */
    private Monitoring()
    {
    }

    /**
     * This method returns either a zero (0) if initialization of
     * the AM server's and services' managed elements has completed
     * successfully, or one (1) if not.
     * @param amServerID The AM server ID
     * @return Success (0) or Failure (1)
     */
    public static int startMonitoring (String amServerID) {
        if (debug.messageEnabled()) {
            debug.message ("Monitoring:startMonitoring: amServerID = " +
                amServerID);
        }

        isLocal = true; // only invoked when serverMode = true

        /*
         *  see if the monitoring framework classes are in the classpath
         *  and loadable
         */
        
//      try {
//          ClassLoader.getSystemClassLoader().loadClass(
//              "com.sun.cmm.CMM_Object");
//      } catch (Exception e) {
//          debug.error ("Monitoring:startMonitoring: " +
//              "Monitoring framework not found - " +
//              e.getMessage() + " - Monitoring is disabled.");
//          return (-2);
//      }
        /*
         *  monitoring framework will throw an undeclared (runtime)
         *  exception if its logging directory is missing, for one, and
         *  causes some problems during container init.  catch it and
         *  leave AM's monitoring uninitialized...
         */
        try {
            mfMEServer =
                MfManagedElementServerFactory.makeManagedElementServer();
        } catch (java.lang.Throwable te) {
            debug.error("Monitoring:framework error:cause =  " +
                te.getCause() + ", message = " + te.getMessage());
            te.printStackTrace();
            return(-1);
        }

        debug.message (
            "Monitoring:startMonitoring:Started Managed Element Server");

        Properties configProperties = new Properties();
        Hashtable context = new Hashtable();

        /*
         *  Add HTML adaptor for debug (hidden property)
         *  and
         *  Change default HTML adaptor port for debug (hidden property)
         *  from 8082 to 3890. (was 3839, but WS7 uses that)
         *  (only for use in non-production testing)
         */

        String tempPort = SystemPropertiesManager.get(
            "com.sun.identity.monitoring.html.port", "3890");
        configProperties.setProperty("ENABLE_HTML_ADAPTOR", "true");
        configProperties.setProperty("HTML_ADAPTOR_PORT", tempPort);

        /*
         *  Change default connector server URL (for instance, for
         *  easy Jconsole connection)
         *  (this might go away for production?)
         */

        String pvtConnSvrURL = SystemPropertiesManager.get(
            MonitoringConstants.AM_PVT_CONN_SVR_URL_KEY_NAME,
            MonitoringConstants.AM_PVT_CONN_SVR_URL_KEY_DFLT_VALUE);

        configProperties.setProperty(
            MfManagedElementServerProperties.PRIVATE_CONNECTOR_SERVER_URL_KEY,
            pvtConnSvrURL);

        //  context properties

        /*
         *  The PRODUCT_PREFIX_CTX_KEY should be Access Manager
         *  instance-specific.  the amServerID passed from
         *  WebtopNaming should suffice.
         */  

        amServerID = MonitoringConstants.AM_PRODUCT_PREFIX_CTX_KEY_NAME +
                amServerID;

        context.put(MfManagedElementServer.PRODUCT_PREFIX_CTX_KEY, amServerID);
        context.put(MfManagedElementServer.PRODUCT_CODE_NAME_CTX_KEY,
            MonitoringConstants.AM_PRODUCT_CODE_NAME);
        context.put(MfManagedElementServer.PRODUCT_NAME_CTX_KEY,
            MonitoringConstants.AM_PRODUCT_NAME);

        /*
         *  get the installdir from AMConfig.properties
         *  com.iplanet.am.installdir entry
         */

        String installDir =
            SystemPropertiesManager.get(Constants.AM_INSTALL_DIR);

        context.put(MfManagedElementServer.PRODUCT_COLLECTIONID_CTX_KEY,
            installDir);

        try {
            mfMEServer.initialize(configProperties, context);
            debug.message (
                "Monitoring:startMonitoring:Initialized Managed Element Svr");

            /*
             *  Start it with an internal private MBeanServer and
             *  internal connector server
             */

            mfMEServer.start();
            debug.message (
                "Monitoring:startMonitoring:Started Managed Element Svr");

            //  Create CMM_ApplicationSystem managed element

            meInfo = mfMEServer.makeManagedElementInfo();
            meInfo.setType(MfManagedElementType.CMM_APPLICATION_SYSTEM);
            meInfo.setName(MonitoringConstants.AM_SERVER_NAME);
            amAppli = (CMM_ApplicationSystemInstrum)
                mfMEServer.createManagedElement(meInfo);


            MfRelationInstrum mRI = null;
            MfRelationInfo relInfo = null;

            // ApplicationSystemStatistics
            meInfo.setType(MfManagedElementType.CMM_APPLICATION_SYSTEM_STATS);
            meInfo.setName(MonitoringConstants.AM_SERVER_STATS_NAME);

            relInfo = mfMEServer.makeRelationInfo();
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                (CIM_ManagedElementInstrum)amAppli,
                relInfo, meInfo);

            amApplstati =
                (CMM_ApplicationSystemStatsInstrum)mRI.getDestination();
            

            // ApplicationSystemSetting
            meInfo.setType(MfManagedElementType.CMM_APPLICATION_SYSTEM_SETTING);
            meInfo.setName(MonitoringConstants.AM_SERVER_SETTING_NAME);

            relInfo = mfMEServer.makeRelationInfo();
            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                (CIM_ManagedElementInstrum)amAppli,
                relInfo, meInfo);

            amApplseti =
                (CMM_ApplicationSystemSettingInstrum)mRI.getDestination();
        
            String proto = SystemPropertiesManager.get(
                Constants.AM_SERVER_PROTOCOL);
            String host = SystemPropertiesManager.get(
                Constants.AM_SERVER_HOST);
            String port = SystemPropertiesManager.get(
                Constants.AM_SERVER_PORT);
            String deploy = SystemPropertiesManager.get(
                Constants.AM_SERVICES_DEPLOYMENT_DESCRIPTOR);
            StringBuffer amURLb = new StringBuffer(proto);
            amURLb.append("://").append(host).append(":").append(port).
                append("/").append(deploy);
            amApplseti.setURL(amURLb.toString());
            

            /*
             *  create CMM_J2eeServer or CMM_ApplicationSystem
             *  element, depending on container.
             */

            relInfo = mfMEServer.makeRelationInfo();
            relInfo.setType(MfRelationType.CMM_APPLICATION_SYSTEM_DEPENDENCY);

            String certdbDir = SystemPropertiesManager.get(
                Constants.AM_ADMIN_CLI_CERTDB_DIR);
            String containerName =
                MonitoringConstants.GENERIC_APPSERVER_PKG_NAME;
            if ((certdbDir != null) && (certdbDir.length() > 0)) {
                if ((certdbDir.indexOf(
                    MonitoringConstants.JES_WEBSERVER_PKG_NAME) != -1) ||
                    (certdbDir.indexOf(
                    MonitoringConstants.JES_WIN_WEBSERVER_PKG_NAME) != -1))
                {  // jes webserver
                    containerName =
                        MonitoringConstants.JES_WEBSERVER_PKG_NAME;
//                    meInfo.setType(MfManagedElementType.CMM_APPLICATION_SYSTEM);
//                    meInfo.setName(containerName);
//                    mRI = mfMEServer.createRelationToNewManagedElement(
//                        amAppli, relInfo, meInfo);
//                    jesWS = (CMM_ApplicationSystemDependency)
//                        mRI.getDestination();

                } else if ((certdbDir.indexOf(
                    MonitoringConstants.JES_APPSERVER_PKG_NAME) != -1) ||
                    (certdbDir.indexOf(
                        MonitoringConstants.JES_WIN_APPSERVER_PKG_NAME) != -1))
                {  // jes appserver
                    containerName =
                        MonitoringConstants.JES_APPSERVER_PKG_NAME;
//                    meInfo.setType(MfManagedElementType.CMM_J2EE_APPLICATION);
//                    meInfo.setName(containerName);
//                    mRI = mfMEServer.createRelationToNewManagedElement(
//                        amAppli, relInfo, meInfo);
//                    jesAS = (CMM_ApplicationSystemDependency)
//                        mRI.getDestination();
                } else {  // generic app server
//                    meInfo.setType(MfManagedElementType.CMM_J2EE_APPLICATION);
//                    meInfo.setName(containerName);
//                    mRI = mfMEServer.createRelationToNewManagedElement(
//                        amAppli, relInfo, meInfo);
//                    genAS = (CMM_ApplicationSystemDependency)
//                        mRI.getDestination();
                }
            }

            if (debug.messageEnabled()) {
                debug.message ("Monitoring:startMonitoring:containerName = " +
                    containerName);
            }

            /*
             *  create CMM_HOSTED_ACCESS_POINT for the jaxrpc
             *  endpoint for the remote/client api.  all services
             *  share it.
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_URI_JAXRPC_NAME);

            relInfo = mfMEServer.makeRelationInfo();
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            MfRelationInstrum am_jaxrpc_uri_rel =
                mfMEServer.createRelationToNewManagedElement(
                    (CIM_ManagedElementInstrum)amAppli,
                    relInfo, meInfo);
            
            //  get created CMM_ServiceAccessURI managed element

            amJaxrpcUri =
                (CMM_ServiceAccessURIInstrum)
                    am_jaxrpc_uri_rel.getDestination();

            //  set the labeled URI

            amJaxrpcUri.setLabeledURI(MonitoringConstants.AM_URI_JAXRPC_STR);


            /*
             *  Create CMM_Service managed element for Authentication
             *  with CMM_HostedService (a containment) dependency
             */

            MonitoringAuth.createAuth(mfMEServer, amAppli);

            /*
             *  Create CMM_Service managed element for Session
             *  with CMM_HostedService (a containment) dependency
             */

            MonitoringSession.createSession(mfMEServer, amAppli);

            debug.message (
                "Monitoring:startMonitoring:Created Session elements");

            MonitoringIdrepo.createIdrepo(mfMEServer, amAppli);

            debug.message (
                "Monitoring:startMonitoring:Created Idrepo elements");

            MonitoringPolicy.createPolicy(mfMEServer, amAppli);

            debug.message (
                "Monitoring:startMonitoring:Created Policy elements");

            MonitoringLogging.createLogging(mfMEServer, amAppli);

            debug.message (
                "Monitoring:startMonitoring:Created Logging elements");


            /*
             *  ===================
             *
             *  do the initialization of amAppli managed element,
             *  then the services' managed elements (?).
             *  don't know if there's a particular order that should
             *  be followed...
             */

            amAppli.setDistribution(Distribution.LOCAL);
            amAppli.setPrimaryOwnerName(MonitoringConstants.AM_OWNER_NAME);

            HashSet amStatus = new HashSet();
            amStatus.add(OperationalStatus.OK);
            amAppli.setOperationalStatus(amStatus);

            amAppli.setDescription(MonitoringConstants.AM_PRODUCT_DESCRIPTION);

            // initialize CMM_ApplicationSystemStats
            
            amApplstati.setInboundAssociations(0);
            amApplstati.setOutboundAssociations(0);
            amApplstati.setRejectedInboundAssociations(0);
            amApplstati.setFailedOutboundAssociations(0);

            // initialize CMM_ApplicationSystemSetting
            
            amApplseti.setDirectoryName("");

            /*
             *  don't know if there's interesting information for
             *  the setPrimaryOwnerContact() and setRoles() calls...
             *
             *
             *  init the services
             */

            debug.message (
                "Monitoring:startMonitoring:Initializing attributes");
            MonitoringAuth.initAttributes();

            MonitoringSession.initAttributes();

            MonitoringPolicy.initAttributes();

            MonitoringLogging.initAttributes();
            debug.message (
                "Monitoring:startMonitoring:Done initializing attributes");


            /*
             *  ===================
             *
             *  the relationships between the Authentication service
             *  and the other services (e.g., Logging, Session, Idrepo, etc.)
             *  assumably get created *after* all the services' managed
             *  elements (CMM_Service) have been created.
             */

            //  these are the ones implemented so far
            CMM_ServiceInstrum authSvc = MonitoringAuth.getSvcAuth();
            CMM_ServiceInstrum logSvc = MonitoringLogging.getSvcLogging();
            CMM_ServiceInstrum sessSvc = MonitoringSession.getSvcSess();
            CMM_ServiceInstrum idrepoSvc = MonitoringIdrepo.getSvcIdrepo();
            CMM_ServiceInstrum policySvc = MonitoringPolicy.getSvcPolicy();

            /*
             *  CMM_ServiceServiceDependency is two-way, so only one
             *  per service pair is necessary.
             */

            relInfo.setType(MfRelationType.CMM_SERVICE_SERVICE_DEPENDENCY);

            if (authSvc != null) {
                if (logSvc != null) {
                    mRI = mfMEServer.createRelation(logSvc, relInfo, authSvc);
                }
                if (sessSvc != null) {
                    mRI = mfMEServer.createRelation(sessSvc, relInfo, authSvc);
                }
                if (policySvc != null) {
                    mRI = mfMEServer.createRelation(policySvc,relInfo,authSvc);
                }
            }
            if (logSvc != null) {
                if (sessSvc != null) {
                    mRI = mfMEServer.createRelation(sessSvc, relInfo,logSvc);
                }
                if (policySvc != null) {
                    mRI = mfMEServer.createRelation(policySvc, relInfo,logSvc);
                }
            }
            if (sessSvc != null) {
                if (policySvc != null) {
                    mRI = mfMEServer.createRelation(policySvc,relInfo,sessSvc);
                }
            }

        } catch (MfManagedElementServerException mmese) {
            debug.error("startMonitoring: " + mmese.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            debug.error("startMonitoring: " + mmeie.getMessage());
        } catch (MfRelationInstrumException mrie) {
            debug.error("startMonitoring: " + mrie.getMessage());
        }

        try {
            mfMEServer.publish();
        } catch (Exception ex) {
            debug.error("Exception received: " + ex.getMessage(), ex);
            return (1);
        }
        return (0);
    }

    /**
     * This method returns the handle to the managed element
     * representing the AM server's sdk JAXRPC endpoint.
     * @return The handle to the JES Monitoring Framework element
     *         for the SDK's JAXRPC endpoint.
     */
    protected static CMM_ServiceAccessURIInstrum getAMJAXRPCURI() {
            return amJaxrpcUri;
    }

    /**
     * This method returns the handle to the managed element
     * representing the AM server.
     * @return The handle to the JES Monitoring Framework element
     *         for the AM server.
     */
    public MfManagedElementServer getMfManagedElementServer() {
        return mfMEServer;
    }

    public static void stopMonitoring() {
        if (debug.messageEnabled()) {
            debug.message("Monitoring:stopMonitoring:mfMEServer active = " +
                (!(mfMEServer == null)));
        }

        if (amAppli != null) {
            HashSet opStat = new HashSet();
            opStat.add(OperationalStatus.STOPPED);
            try {
                amAppli.setOperationalStatus(opStat);
            } catch (MfManagedElementInstrumException meie) {
                debug.error ("Monitoring:setting operational status to STOP: "
                    + meie.getMessage());
            }
        }

        if (mfMEServer != null) {
            try {
                mfMEServer.stop();
            } catch (MfManagedElementServerException mese) {
                debug.error ("Monitoring:error stopping monitoring: " +
                    mese.getMessage());
            }
        }
    }
}

