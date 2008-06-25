/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007 Sun Microsystems Inc. All Rights Reserved
 *
 * The contents of this file are subject to the terms
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
 * $Id: MonitoringSAMLv2.java,v 1.2 2008-06-25 05:52:54 qcheng Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.HashSet;

import com.sun.cmm.cim.OperationalStatus;

import com.sun.mfwk.instrum.me.CMM_ApplicationSystemInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceAccessURIInstrum;
import com.sun.mfwk.instrum.me.CMM_ServiceInstrum;
import com.sun.mfwk.instrum.me.CMM_SWRCacheInstrum;
import com.sun.mfwk.instrum.me.MfManagedElementInstrumException;
import com.sun.mfwk.instrum.me.settings.CMM_SWRCacheSettingInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceAccessURIStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_ServiceStatsInstrum;
import com.sun.mfwk.instrum.me.statistics.CMM_SWRCacheStatsInstrum;
import com.sun.mfwk.instrum.relations.MfRelationInstrum;
import com.sun.mfwk.instrum.relations.MfRelationInstrumException;
import com.sun.mfwk.instrum.server.MfManagedElementInfo;
import com.sun.mfwk.instrum.server.MfManagedElementServer;
import com.sun.mfwk.instrum.server.MfManagedElementServerException;
import com.sun.mfwk.instrum.server.MfManagedElementType;
import com.sun.mfwk.instrum.server.MfRelationInfo;
import com.sun.mfwk.instrum.server.MfRelationType;

import com.sun.identity.monitoring.MonitoringConstants;
import com.sun.identity.monitoring.MonitoringUtils;
import com.sun.identity.shared.debug.Debug;


/**
 * This class provides the initialization of the OpenFAM-related managed
 * elements * specifically associated with the SAML2.0 service for the
 * Proctor Monitoring Framework.  It is invoked during container
 * initialization by the main Monitoring.java module.
 */
public class MonitoringSAMLv2 {

    //  "main" SAMLv2 service
    private static CMM_ServiceInstrum amSvcSAML2;

    //  Services (servlets)
    //  SP servlets
    private static CMM_ServiceInstrum amSAML2SP_IDPMniInit;
    private static CMM_ServiceInstrum amSAML2SP_IDPMniRedirect;
    private static CMM_ServiceInstrum amSAML2SP_SPMniInit;
    private static CMM_ServiceInstrum amSAML2SP_SPMniRedirect;
    private static CMM_ServiceInstrum amSAML2SP_SPWSFederation;
    private static CMM_ServiceInstrum amSAML2SP_SPspssoinit;
    private static CMM_ServiceInstrum amSAML2SP_SPConsumer;
    private static CMM_ServiceInstrum amSAML2SP_SPSPMniSoap;
    private static CMM_ServiceInstrum amSAML2SP_SPIDPMniSoap;
    private static CMM_ServiceInstrum amSAML2SP_SPSPSloInit;
    private static CMM_ServiceInstrum amSAML2SP_SPSPSloRedirect;

    //  IDP servlets
    private static CMM_ServiceInstrum amSAML2IDP_IDPSloSoap;
    private static CMM_ServiceInstrum amSAML2IDP_WSFederation;
    private static CMM_ServiceInstrum amSAML2IDP_idpSSOFederate;
    private static CMM_ServiceInstrum amSAML2IDP_IDPSloInit;
    private static CMM_ServiceInstrum amSAML2IDP_IDPSloRedirect;
    private static CMM_ServiceInstrum amSAML2IDP_idpssoinit;
    private static CMM_ServiceInstrum amSAML2IDP_Consumer;
    private static CMM_ServiceInstrum amSAML2IDP_SPMniRedirect;
    private static CMM_ServiceInstrum amSAML2IDP_IDPMniRedirect;
    private static CMM_ServiceInstrum amSAML2IDP_SPMniSoap;
    private static CMM_ServiceInstrum amSAML2IDP_IDPMniSoap;
    private static CMM_ServiceInstrum amSAML2IDP_SPMniInit;
    private static CMM_ServiceInstrum amSAML2IDP_IDPMniInit;
    private static CMM_ServiceInstrum amSAML2IDP_idpArtifactResolution;

    //  Stats for each servlet
    //  SP servlets' stats
//    private static CMM_ServiceStatsInstrum amSAML2SvcStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_IDPMniInitStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_IDPMniRedirectStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPMniInitStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPMniRedirectStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPWSFederationStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPspssoinitStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPConsumerStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPSPMniSoapStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPIDPMniSoapStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPSPSloInitStats;
    private static CMM_ServiceStatsInstrum amSAML2SP_SPSPSloRedirectStats;

    //  IDP servlets' stats
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPSloSoapStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_WSFederationStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_idpSSOFederateStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPSloInitStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPSloRedirectStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_idpssoinitStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_ConsumerStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_SPMniRedirectStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPMniRedirectStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_SPMniSoapStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPMniSoapStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_SPMniInitStats;
    private static CMM_ServiceStatsInstrum amSAML2IDP_IDPMniInitStats;
    private static CMM_ServiceStatsInstrum
        amSAML2IDP_idpArtifactResolutionStats;

    //  ServiceAccessURIs
    //  SP uris
    private static CMM_ServiceAccessURIInstrum amSAML2SP_IDPMniInitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_IDPMniRedirectURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPMniInitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPMniRedirectURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPWSFederationURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPspssoinitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPConsumerURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPSPMniSoapURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPIDPMniSoapURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPSPSloInitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2SP_SPSPSloRedirectURI;

    //  IDP uris
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPSloSoapURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_WSFederationURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_idpSSOFederateURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPSloInitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPSloRedirectURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_idpssoinitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_ConsumerURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_SPMniRedirectURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPMniRedirectURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_SPMniSoapURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPMniSoapURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_SPMniInitURI;
    private static CMM_ServiceAccessURIInstrum amSAML2IDP_IDPMniInitURI;
    private static CMM_ServiceAccessURIInstrum
        amSAML2IDP_idpArtifactResolutionURI;

    //  ServiceAccessURIStats
    //  SP uris' stats
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_IDPMniInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_IDPMniRedirectURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPMniInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPMniRedirectURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPWSFederationURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPspssoinitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPConsumerURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPSPMniSoapURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPIDPMniSoapURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPSPSloInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2SP_SPSPSloRedirectURIStats;

    //  IDP uris' stats
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPSloSoapURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_WSFederationURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_idpSSOFederateURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPSloInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPSloRedirectURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_idpssoinitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_ConsumerURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_SPMniRedirectURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPMniRedirectURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_SPMniSoapURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPMniSoapURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_SPMniInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_IDPMniInitURIStats;
    private static CMM_ServiceAccessURIStatsInstrum
        amSAML2IDP_idpArtifactResolutionURIStats;


    //  SAML2 Caches
    //  SP caches
    private static CMM_SWRCacheInstrum amSAML2SP_requestHash;
    private static CMM_SWRCacheInstrum amSAML2SP_mniRequestHash;
    private static CMM_SWRCacheInstrum amSAML2SP_relayStateHash;
    private static CMM_SWRCacheInstrum amSAML2SP_fedSessionListsByNameIDInfoKey;
    private static CMM_SWRCacheInstrum amSAML2SP_logoutRequestIDs;
    private static CMM_SWRCacheInstrum amSAML2SP_responseHash;
    private static CMM_SWRCacheInstrum amSAML2SP_authCtxObjHash;
    private static CMM_SWRCacheInstrum amSAML2SP_authContextHash;
    private static CMM_SWRCacheInstrum amSAML2SP_spAccountMapperCache;

    //  IDP caches
    private static CMM_SWRCacheInstrum amSAML2IDP_authnRequestCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_relayStateCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_idpSessionsByIndices;
    private static CMM_SWRCacheInstrum amSAML2IDP_responsesByArtifacts;
    private static CMM_SWRCacheInstrum amSAML2IDP_mniRequestHash;
    private static CMM_SWRCacheInstrum amSAML2IDP_dpAttributeMapperCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_idpAccountMapperCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_idpAuthnContextMapperCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_reponseCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_authnContextCache;
    private static CMM_SWRCacheInstrum amSAML2IDP_oldIDPSessionCache;

    //  Cache Stats
    //  SP caches' stats
    private static CMM_SWRCacheStatsInstrum amSAML2SP_requestHashStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_mniRequestHashStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_relayStateHashStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2SP_fedSessionListsByNameIDInfoKeyStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_logoutRequestIDsStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_responseHashStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_authCtxObjHashStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_authContextHashStats;
    private static CMM_SWRCacheStatsInstrum amSAML2SP_spAccountMapperCacheStats;

    //  IDP caches' stats
    private static CMM_SWRCacheStatsInstrum amSAML2IDP_authnRequestCacheStats;
    private static CMM_SWRCacheStatsInstrum amSAML2IDP_relayStateCacheStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_idpSessionsByIndicesStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_responsesByArtifactsStats;
    private static CMM_SWRCacheStatsInstrum amSAML2IDP_mniRequestHashStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_dpAttributeMapperCacheStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_idpAccountMapperCacheStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_idpAuthnContextMapperCacheStats;
    private static CMM_SWRCacheStatsInstrum amSAML2IDP_reponseCacheStats;
    private static CMM_SWRCacheStatsInstrum amSAML2IDP_authnContextCacheStats;
    private static CMM_SWRCacheStatsInstrum
        amSAML2IDP_oldIDPSessionCacheStats;

    //  Cache Settings
    //  SP caches' settings
    private static CMM_SWRCacheSettingInstrum amSAML2SP_requestHashSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_mniRequestHashSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_relayStateHashSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2SP_fedSessionListsByNameIDInfoKeySetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_logoutRequestIDsSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_responseHashSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_authCtxObjHashSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2SP_authContextHashSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2SP_spAccountMapperCacheSetting;

    //  IDP caches' settings
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_authnRequestCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_relayStateCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_idpSessionsByIndicesSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_responsesByArtifactsSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2IDP_mniRequestHashSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_dpAttributeMapperCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_idpAccountMapperCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_idpAuthnContextMapperCacheSetting;
    private static CMM_SWRCacheSettingInstrum amSAML2IDP_reponseCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_authnContextCacheSetting;
    private static CMM_SWRCacheSettingInstrum
        amSAML2IDP_oldIDPSessionCacheSetting;


    //  MonitoringSAML2 constructor
    private MonitoringSAMLv2()
    {
    }

    /**
     * This method currently returns zero (0) whether or not it has completed
     * initialization of all the SAML2 Service's managed elements.  At
     * some time, there may be an error return value, should some recovery
     * procedure be developed.  This method is invoked by the main
     * initialization module, Monitoring.java.
     * @param mfMEServer The handle to the managed element server for AM
     * @param amAppli The handle to the AM server's managed element
     * @return Success (0) always (for now).
     */
    protected static int createSAMLv2 (MfManagedElementServer mfMEServer,
        CMM_ApplicationSystemInstrum amAppli)
    {
        Monitoring.debug.message ("MonitoringSAMLv2:createSAMLv2");
        try {
            MfManagedElementInfo meInfo =
                mfMEServer.makeManagedElementInfo();
            MfRelationInfo relInfo = mfMEServer.makeRelationInfo();
            MfRelationInstrum mRI = null;

            /*
             *  Create CMM_Service managed element (with a 
             *  CMM_HostedService dependency) for the
             *  SAML2 service, which "represents" the service
             *  that together the servlets provide.   Then
             *  create the CMM_Service managed elements for the
             *  SAML2 servlets with CMM_ServiceComponent (a containment)
             *  dependency.
             *
             *  You can create a new MfManagedElementInfo, or reuse the
             *  previous one and fill every necessary info
             *  (MfManagedElementInfo is purely descriptive)
             */

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2SVC_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_SERVICE);

            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_Service managed element

            amSvcSAML2 = (CMM_ServiceInstrum)mRI.getDestination();

            //  now create the SP servlets' CMM_Service elements
            //  idpMNIRequestInit servlet

            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            //  get created CMM_Service managed element

            amSAML2SP_IDPMniInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpMNIRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_IDPMniRedirect = (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNIRequestInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPMniInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNIRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPMniRedirect = (CMM_ServiceInstrum)mRI.getDestination();

            //  WSFederation servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPWSFederation = (CMM_ServiceInstrum)mRI.getDestination();

            //  spSSOInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_spssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPspssoinit = (CMM_ServiceInstrum)mRI.getDestination();

            //  spAssertionConsumer servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPConsumer = (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNISOAP servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPSPMniSoap = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpMNISOAP servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPIDPMniSoap = (CMM_ServiceInstrum)mRI.getDestination();

            //  spSingleLogoutInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_SPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPSPSloInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  spSingleLogoutRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_SP_SPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2SP_SPSPSloRedirect =
                (CMM_ServiceInstrum)mRI.getDestination();

            //  now create the IDP servlets' CMM_Service elements
            //  IDPSingleLogoutServiceSOAP servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_IDPSloSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPSloSoap = (CMM_ServiceInstrum)mRI.getDestination();

            //  WSFederation servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_WSFederation = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpSSOFederate servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_idpSSOFederate_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_idpSSOFederate =
                (CMM_ServiceInstrum)mRI.getDestination();

            //  idpSingleLogoutInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_IDPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPSloInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpSingleLogoutRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_IDPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPSloRedirect =
                (CMM_ServiceInstrum)mRI.getDestination();

            //  idpssoinit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_idpssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_idpssoinit = (CMM_ServiceInstrum)mRI.getDestination();

            //  spAssertionConsumer servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_Consumer = (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNIRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_SPMniRedirect = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpMNIRedirect servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPMniRedirect =
                (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNISOAP servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_SPMniSoap = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpMNISOAP servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPMniSoap = (CMM_ServiceInstrum)mRI.getDestination();

            //  spMNIRequestInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_SPMniInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpMNIRequestInit servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(MonitoringConstants.AM_SAML2_IDP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_IDPMniInit = (CMM_ServiceInstrum)mRI.getDestination();

            //  idpArtifactResolution servlet
            meInfo.setType(MfManagedElementType.CMM_SERVICE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_idpArtifactResolution_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_COMPONENT);

            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
                      relInfo, meInfo);

            amSAML2IDP_idpArtifactResolution =
                (CMM_ServiceInstrum)mRI.getDestination();

            /*
             *  ===================
             *
             *  now create the ServiceAccessUri's
             *  the SP Uri's:
             *  %protocol://%host:%port/openfam/IDPMniInit/*
             *  %protocol://%host:%port/openfam/IDPMniRedirect/*
             *  %protocol://%host:%port/openfam/SPMniInit/*
             *  %protocol://%host:%port/openfam/SPMniRedirect/*
             *  %protocol://%host:%port/openfam/WSFederationServlet/*
             *  %protocol://%host:%port/openfam/spssoinit
             *  %protocol://%host:%port/openfam/Consumer/*
             *  %protocol://%host:%port/openfam/SPMniSoap/*
             *  %protocol://%host:%port/openfam/IDPMniSoap/*
             *  %protocol://%host:%port/openfam/SPSloInit/*
             *  %protocol://%host:%port/openfam/SPSloRedirect/*
             *
             *  and the IDP Uri's:
             *  %protocol://%host:%port/openfam/IDPSloSoap/*
             *  %protocol://%host:%port/openfam/WSFederationServlet/*
             *  %protocol://%host:%port/openfam/idpSSOFederate/*
             *  %protocol://%host:%port/openfam/IDPSloInit/*
             *  %protocol://%host:%port/openfam/IDPSloRedirect/*
             *  %protocol://%host:%port/openfam/idpssoinit
             *  %protocol://%host:%port/openfam/Consumer/*
             *  %protocol://%host:%port/openfam/SPMniRedirect/*
             *  %protocol://%host:%port/openfam/IDPMniRedirect/*
             *  %protocol://%host:%port/openfam/SPMniSoap/*
             *  %protocol://%host:%port/openfam/IDPMniSoap/*
             *  %protocol://%host:%port/openfam/SPMniInit/*
             *  %protocol://%host:%port/openfam/IDPMniInit/*
             *  %protocol://%host:%port/openfam/idpArtifactResolution/*
             *
             *  map them onto their corresponding servlets
             */

            //  xxxxxx
            //  IS THERE GOING TO BE AN ISSUE WITH "SHARING" OF URIs
            //  BETWEEN SERVLETS?
            //  xxxxxx

            //  SP URIs
            //  %protocol://%host:%port/openfam/IDPMniInit/*

            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(MonitoringConstants.AM_SAML2_URI_SP_IDPMniInit_NAME);

            //  create CMM_HOSTED_ACCESS_POINT relation with amAppli

            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);

            //  Get created CMM_ServiceAccessURI managed element

            amSAML2SP_IDPMniInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();

            //  set the labeled URI

            amSAML2SP_IDPMniInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_IDPMniInit_STR);

            /*
             *  ###########################################################
             *
             *  CMM_ServiceAccessSAPDependency is an association, not a
             *  containnment relationship.  have to do the
             *  createRelation().
             *
             *  ###########################################################
             */ 

            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_IDPMniInit, relInfo,
                    amSAML2SP_IDPMniInitURI);

            //  %protocol://%host:%port/openfam/IDPMniRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_IDPMniRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_IDPMniRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_IDPMniRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_IDPMniRedirect,
                    relInfo, amSAML2SP_IDPMniRedirectURI);

            //  %protocol://%host:%port/openfam/SPMniInit/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPMniInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPMniInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniInit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPMniInit,
                    relInfo, amSAML2SP_SPMniInitURI);

            //  %protocol://%host:%port/openfam/SPMniRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPMniRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPMniRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPMniRedirect,
                    relInfo, amSAML2SP_SPMniRedirectURI);

            //  %protocol://%host:%port/openfam/WSFederationServlet/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPWSFederationURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPWSFederationURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_WSFederation_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPWSFederation,
                    relInfo, amSAML2SP_SPWSFederationURI);

            //  %protocol://%host:%port/openfam/spssoinit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_spssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPspssoinitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPspssoinitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_spssoinit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPspssoinit,
                    relInfo, amSAML2SP_SPspssoinitURI);

            //  %protocol://%host:%port/openfam/Consumer/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPConsumerURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPConsumerURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_Consumer_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPConsumer,
                    relInfo, amSAML2SP_SPConsumerURI);

            //  %protocol://%host:%port/openfam/SPMniSoap/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPSPMniSoapURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPSPMniSoapURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_SPMniSoap_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPSPMniSoap,
                    relInfo, amSAML2SP_SPSPMniSoapURI);

            //  %protocol://%host:%port/openfam/IDPMniSoap/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPIDPMniSoapURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPIDPMniSoapURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPIDPMniSoap,
                    relInfo, amSAML2SP_SPIDPMniSoapURI);

            //  %protocol://%host:%port/openfam/SPSloInit/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_SPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPSPSloInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPSPSloInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_SPSloInit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPSPSloInit,
                    relInfo, amSAML2SP_SPSPSloInitURI);

            //  %protocol://%host:%port/openfam/SPSloRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_SP_SPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_SPSPSloRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2SP_SPSPSloRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_SP_SPSloRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2SP_SPSPSloRedirect,
                    relInfo, amSAML2SP_SPSPSloRedirectURI);

            //  IDP URIs
            //  %protocol://%host:%port/openfam/IDPSloSoap/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloSoap_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPSloSoapURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPSloSoapURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloSoap_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPSloSoap,
                    relInfo, amSAML2IDP_IDPSloSoapURI);

            //  %protocol://%host:%port/openfam/WSFederationServlet/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_WSFederationURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_WSFederationURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_WSFederation_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_WSFederation,
                    relInfo, amSAML2IDP_WSFederationURI);

            //  %protocol://%host:%port/openfam/idpSSOFederate/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_idpSSOFederate_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpSSOFederateURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_idpSSOFederateURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_idpSSOFederate_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_idpSSOFederate,
                    relInfo, amSAML2IDP_idpSSOFederateURI);

            //  %protocol://%host:%port/openfam/IDPSloInit/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPSloInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPSloInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloInit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPSloInit,
                    relInfo, amSAML2IDP_IDPSloInitURI);

            //  %protocol://%host:%port/openfam/IDPSloRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPSloRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPSloRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPSloRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPSloRedirect,
                    relInfo, amSAML2IDP_IDPSloRedirectURI);

            //  %protocol://%host:%port/openfam/idpssoinit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_idpssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpssoinitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_idpssoinitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_idpssoinit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_idpssoinit,
                    relInfo, amSAML2IDP_idpssoinitURI);

            //  %protocol://%host:%port/openfam/Consumer/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_ConsumerURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_ConsumerURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_Consumer_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_Consumer,
                    relInfo, amSAML2IDP_ConsumerURI);

            //  %protocol://%host:%port/openfam/SPMniRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_SPMniRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_SPMniRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_SPMniRedirect,
                    relInfo, amSAML2IDP_SPMniRedirectURI);

            //  %protocol://%host:%port/openfam/IDPMniRedirect/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPMniRedirectURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPMniRedirectURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniRedirect_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPMniRedirect,
                    relInfo, amSAML2IDP_IDPMniRedirectURI);

            //  %protocol://%host:%port/openfam/SPMniSoap/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_SPMniSoapURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_SPMniSoapURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniSoap_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_SPMniSoap,
                    relInfo, amSAML2IDP_SPMniSoapURI);

            //  %protocol://%host:%port/openfam/IDPMniSoap/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPMniSoapURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPMniSoapURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniSoap_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPMniSoap,
                    relInfo, amSAML2IDP_IDPMniSoapURI);

            //  %protocol://%host:%port/openfam/SPMniInit/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_SPMniInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_SPMniInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_SPMniInit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_SPMniInit,
                    relInfo, amSAML2IDP_SPMniInitURI);

            //  %protocol://%host:%port/openfam/IDPMniInit/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_IDPMniInitURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_IDPMniInitURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_IDPMniInit_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_IDPMniInit,
                    relInfo, amSAML2IDP_IDPMniInitURI);

            //  %protocol://%host:%port/openfam/idpArtifactResolution/*
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI);
            meInfo.setName(
              MonitoringConstants.AM_SAML2_URI_IDP_idpArtifactResolution_NAME);
            relInfo.setType(MfRelationType.CMM_HOSTED_ACCESS_POINT);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpArtifactResolutionURI =
                (CMM_ServiceAccessURIInstrum)mRI.getDestination();
            amSAML2IDP_idpArtifactResolutionURI.setLabeledURI(
                MonitoringConstants.AM_SAML2_URI_IDP_idpArtifactResolution_STR);
            relInfo.setType(MfRelationType.CMM_SERVICE_ACCESS_BY_SAP);
            mRI = mfMEServer.createRelation(amSAML2IDP_idpArtifactResolution,
                    relInfo, amSAML2IDP_idpArtifactResolutionURI);

            /*
             *  ###########################################################
             *
             *  create the statistics elements
             *
             *  add in/out stats for SAML2 service
             *    amSAML2SvcStats (xxxxxxx - don't know if necessary yet)
             *    SP servlet stats:
             *      amSAML2SP_IDPMniInitStats
             *      amSAML2SP_IDPMniRedirectStats
             *      amSAML2SP_SPMniInitStats
             *      amSAML2SP_SPMniRedirectStats
             *      amSAML2SP_SPWSFederationStats
             *      amSAML2SP_SPspssoinitStats
             *      amSAML2SP_SPConsumerStats
             *      amSAML2SP_SPSPMniSoapStats
             *      amSAML2SP_SPIDPMniSoapStats
             *      amSAML2SP_SPSPSloInitStats
             *      amSAML2SP_SPSPSloRedirectStats
             *    IDP servlet stats:
             *      amSAML2IDP_IDPSloSoapStats
             *      amSAML2IDP_WSFederationStats
             *      amSAML2IDP_idpSSOFederateStats
             *      amSAML2IDP_IDPSloInitStats
             *      amSAML2IDP_IDPSloRedirectStats
             *      amSAML2IDP_idpssoinitStats
             *      amSAML2IDP_ConsumerStats
             *      amSAML2IDP_SPMniRedirectStats
             *      amSAML2IDP_IDPMniRedirectStats
             *      amSAML2IDP_SPMniSoapStats
             *      amSAML2IDP_IDPMniSoapStats
             *      amSAML2IDP_SPMniInitStats
             *      amSAML2IDP_IDPMniInitStats
             *      amSAML2IDP_idpArtifactResolutionStats
             *
             *  URI stats for
             *    SP URIs:
             *      amSAML2SP_IDPMniInitURI
             *      amSAML2SP_IDPMniRedirectURI
             *      amSAML2SP_SPMniInitURI
             *      amSAML2SP_SPMniRedirectURI
             *      amSAML2SP_SPWSFederationURI
             *      amSAML2SP_SPspssoinitURI
             *      amSAML2SP_SPConsumerURI
             *      amSAML2SP_SPSPMniSoapURI
             *      amSAML2SP_SPIDPMniSoapURI
             *      amSAML2SP_SPSPSloInitURI
             *      amSAML2SP_SPSPSloRedirectURI
             *    IDP URIs:
             *      amSAML2IDP_IDPSloSoapURI
             *      amSAML2IDP_WSFederationURI
             *      amSAML2IDP_idpSSOFederateURI
             *      amSAML2IDP_IDPSloInitURI
             *      amSAML2IDP_IDPSloRedirectURI
             *      amSAML2IDP_idpssoinitURI
             *      amSAML2IDP_ConsumerURI
             *      amSAML2IDP_SPMniRedirectURI
             *      amSAML2IDP_IDPMniRedirectURI
             *      amSAML2IDP_SPMniSoapURI
             *      amSAML2IDP_IDPMniSoapURI
             *      amSAML2IDP_SPMniInitURI
             *      amSAML2IDP_IDPMniInitURI
             *      amSAML2IDP_idpArtifactResolutionURI
             *
             *  ###########################################################
             */

            //  SAML2 Service stats (needed?)
//            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
//            meInfo.setName(MonitoringConstants.AM_SAML2_SVC_STATS_NAME);
//            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
//            mRI = mfMEServer.createRelationToNewManagedElement(amSvcSAML2,
//                    relInfo, meInfo);
//  
//            amSAML2SvcStats = (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  servlet stats
            //  SP servlets
            //  SP_IDPMniInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_IDPMniInit, relInfo, meInfo);
  
            amSAML2SP_IDPMniInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_IDPMniRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_IDPMniRedirect, relInfo, meInfo);
  
            amSAML2SP_IDPMniRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPMniInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPMniInit, relInfo, meInfo);
  
            amSAML2SP_SPMniInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPMniRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPMniRedirect, relInfo, meInfo);
  
            amSAML2SP_SPMniRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPWSFederation
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPWSFederation, relInfo, meInfo);
  
            amSAML2SP_SPWSFederationStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPspssoinit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_spssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPspssoinit, relInfo, meInfo);
  
            amSAML2SP_SPspssoinitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPConsumer
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPConsumer, relInfo, meInfo);
  
            amSAML2SP_SPConsumerStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPSPMniSoap
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPMniSoap, relInfo, meInfo);
  
            amSAML2SP_SPSPMniSoapStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPIDPMniSoap
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPIDPMniSoap, relInfo, meInfo);
  
            amSAML2SP_SPIDPMniSoapStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPSPSloInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPSloInit, relInfo, meInfo);
  
            amSAML2SP_SPSPSloInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  SP_SPSPSloRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPSloRedirect, relInfo, meInfo);
  
            amSAML2SP_SPSPSloRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP servlets
            //  IDP_IDPSloSoap
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloSoap, relInfo, meInfo);
  
            amSAML2IDP_IDPSloSoapStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_WSFederation
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_WSFederation, relInfo, meInfo);
  
            amSAML2IDP_WSFederationStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_idpSSOFederate
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_idpSSOFederate_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_idpSSOFederate, relInfo, meInfo);
  
            amSAML2IDP_idpSSOFederateStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_IDPSloInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloInit, relInfo, meInfo);
  
            amSAML2IDP_IDPSloInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_IDPSloRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloRedirect, relInfo, meInfo);
  
            amSAML2IDP_IDPSloRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_idpssoinit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_idpssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_idpssoinit, relInfo, meInfo);
  
            amSAML2IDP_idpssoinitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_Consumer
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_Consumer, relInfo, meInfo);
  
            amSAML2IDP_ConsumerStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_SPMniRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniRedirect, relInfo, meInfo);
  
            amSAML2IDP_SPMniRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_IDPMniRedirect
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniRedirect, relInfo, meInfo);
  
            amSAML2IDP_IDPMniRedirectStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_SPMniSoap
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniSoap, relInfo, meInfo);
  
            amSAML2IDP_SPMniSoapStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_IDPMniSoap
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniSoap, relInfo, meInfo);
  
            amSAML2IDP_IDPMniSoapStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_SPMniInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniInit, relInfo, meInfo);
  
            amSAML2IDP_SPMniInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_IDPMniInit
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniInit, relInfo, meInfo);
  
            amSAML2IDP_IDPMniInitStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();

            //  IDP_idpArtifactResolution
            meInfo.setType(MfManagedElementType.CMM_SERVICE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SVC_STATS_IDP_idpArtifactResolution_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_idpArtifactResolution, relInfo, meInfo);
  
            amSAML2IDP_idpArtifactResolutionStats =
                (CMM_ServiceStatsInstrum)mRI.getDestination();


            //  SAML2 URI stats
            //  SP URIs
            //  amSAML2SP_IDPMniInitURI
            meInfo.setType(MfManagedElementType.CMM_SERVICE_ACCESS_URI_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_IDPMniInitURI, relInfo, meInfo);

            amSAML2SP_IDPMniInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_IDPMniRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_IDPMniRedirectURI, relInfo, meInfo);

            amSAML2SP_IDPMniRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPMniInitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPMniInitURI, relInfo, meInfo);

            amSAML2SP_SPMniInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPMniRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPMniRedirectURI, relInfo, meInfo);

            amSAML2SP_SPMniRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPWSFederationURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPWSFederationURI, relInfo, meInfo);

            amSAML2SP_SPWSFederationURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPspssoinitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_spssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPspssoinitURI, relInfo, meInfo);

            amSAML2SP_SPspssoinitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPConsumerURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPConsumerURI, relInfo, meInfo);

            amSAML2SP_SPConsumerURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPSPMniSoapURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPMniSoapURI, relInfo, meInfo);

            amSAML2SP_SPSPMniSoapURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPIDPMniSoapURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPIDPMniSoapURI, relInfo, meInfo);

            amSAML2SP_SPIDPMniSoapURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPSPSloInitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPSloInitURI, relInfo, meInfo);

            amSAML2SP_SPSPSloInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2SP_SPSPSloRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_SP_SPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2SP_SPSPSloRedirectURI, relInfo, meInfo);

            amSAML2SP_SPSPSloRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  IDP URIs
            //  amSAML2IDP_IDPSloSoapURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloSoapURI, relInfo, meInfo);

            amSAML2IDP_IDPSloSoapURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_WSFederationURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_WSFederation_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_WSFederationURI, relInfo, meInfo);

            amSAML2IDP_idpSSOFederateURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_IDPSloInitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloInitURI, relInfo, meInfo);

            amSAML2IDP_IDPSloInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_IDPSloRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPSloRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPSloRedirectURI, relInfo, meInfo);

            amSAML2IDP_IDPSloRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_idpssoinitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_idpssoinit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_idpssoinitURI, relInfo, meInfo);

            amSAML2IDP_idpssoinitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_ConsumerURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_Consumer_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_ConsumerURI, relInfo, meInfo);

            amSAML2IDP_ConsumerURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_SPMniRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniRedirectURI, relInfo, meInfo);

            amSAML2IDP_SPMniRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_IDPMniRedirectURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniRedirect_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniRedirectURI, relInfo, meInfo);

            amSAML2IDP_IDPMniRedirectURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_SPMniSoapURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniSoapURI, relInfo, meInfo);

            amSAML2IDP_SPMniSoapURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_IDPMniSoapURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniSoap_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniSoapURI, relInfo, meInfo);

            amSAML2IDP_IDPMniSoapURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_SPMniInitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_SPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_SPMniInitURI, relInfo, meInfo);

            amSAML2IDP_SPMniInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_IDPMniInitURI
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SVC_STATS_IDP_IDPMniInit_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_IDPMniInitURI, relInfo, meInfo);

            amSAML2IDP_IDPMniInitURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();

            //  amSAML2IDP_idpArtifactResolutionURI
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SVC_STATS_IDP_idpArtifactResolution_NAME);
            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                    amSAML2IDP_idpArtifactResolutionURI, relInfo, meInfo);

            amSAML2IDP_idpArtifactResolutionURIStats =
                (CMM_ServiceAccessURIStatsInstrum)mRI.getDestination();


            /*
             *  stats and settings for
             *  SAML2 service's SP and IDP caches
             */
            //  SP Caches
            //  SP_requestHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_REQUESTHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_requestHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_requestHash);

            //  stats and setting for SP_requestHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_REQUESTHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_requestHash, relInfo, meInfo);

            amSAML2SP_requestHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_REQUESTHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_requestHash, relInfo, meInfo);

            amSAML2SP_requestHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_mniRequestHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_MNIREQUESTHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_mniRequestHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_mniRequestHash);

            //  stats and setting for SP_mniRequestHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_MNIREQUESTHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_mniRequestHash, relInfo, meInfo);

            amSAML2SP_mniRequestHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_MNIREQUESTHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_mniRequestHash, relInfo, meInfo);

            amSAML2SP_mniRequestHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_relayStateHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_RELAYSTATEHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_relayStateHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_relayStateHash);

            //  stats and setting for SP_relayStateHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_RELAYSTATEHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_relayStateHash, relInfo, meInfo);

            amSAML2SP_relayStateHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_RELAYSTATEHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_relayStateHash, relInfo, meInfo);

            amSAML2SP_relayStateHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_fedSessionListsByNameIDInfoKey
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_fedSessionListsByNameIDInfoKey =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_fedSessionListsByNameIDInfoKey);

            //  stats and setting for SP_fedSessionListsByNameIDInfoKey's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_fedSessionListsByNameIDInfoKey, relInfo, meInfo);

            amSAML2SP_fedSessionListsByNameIDInfoKeyStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_FEDSESSLISTBYNAME_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_mniRequestHash, relInfo, meInfo);

            amSAML2SP_fedSessionListsByNameIDInfoKeySetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_logoutRequestIDs
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_LOGOUTRQTIDS_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_logoutRequestIDs =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_logoutRequestIDs);

            //  stats and setting for SP_logoutRequestIDs' cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_LOGOUTRQTIDS_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_logoutRequestIDs, relInfo, meInfo);

            amSAML2SP_logoutRequestIDsStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_LOGOUTRQTIDS_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_logoutRequestIDs, relInfo, meInfo);

            amSAML2SP_logoutRequestIDsSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_responseHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_RESPONSEHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_responseHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_responseHash);

            //  stats and setting for SP_responseHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_RESPONSEHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_responseHash, relInfo, meInfo);

            amSAML2SP_responseHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_RESPONSEHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_responseHash, relInfo, meInfo);

            amSAML2SP_responseHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_authCtxObjHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_authCtxObjHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_authCtxObjHash);

            //  stats and setting for SP_authCtxObjHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_authCtxObjHash, relInfo, meInfo);

            amSAML2SP_authCtxObjHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_AUTHCTXOBJHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_authCtxObjHash, relInfo, meInfo);

            amSAML2SP_authCtxObjHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_authContextHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_authContextHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_authContextHash);

            //  stats and setting for SP_authContextHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_authContextHash, relInfo, meInfo);

            amSAML2SP_authContextHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_AUTHCONTEXTHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_authContextHash, relInfo, meInfo);

            amSAML2SP_authContextHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  SP_spAccountMapperCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2SP_spAccountMapperCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2SP_spAccountMapperCache);

            //  stats and setting for SP_spAccountMapperCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_spAccountMapperCache, relInfo, meInfo);

            amSAML2SP_spAccountMapperCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_SP_SPACCOUNTMAPPER_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2SP_spAccountMapperCache, relInfo, meInfo);

            amSAML2SP_spAccountMapperCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP Caches
            //  IDP_authnRequestCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_AUTHNREQUEST_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_authnRequestCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_authnRequestCache);

            //  stats and setting for IDP_authnRequestCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_AUTHNREQUEST_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_authnRequestCache, relInfo, meInfo);

            amSAML2IDP_authnRequestCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_AUTHNREQUEST_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_authnRequestCache, relInfo, meInfo);

            amSAML2IDP_authnRequestCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_relayStateCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_RELAYSTATE_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_relayStateCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_relayStateCache);

            //  stats and setting for IDP_relayStateCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RELAYSTATE_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_relayStateCache, relInfo, meInfo);

            amSAML2IDP_relayStateCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RELAYSTATE_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_relayStateCache, relInfo, meInfo);

            amSAML2IDP_relayStateCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_idpSessionsByIndices
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_IDPSESSBYINDX_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpSessionsByIndices =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_idpSessionsByIndices);

            //  stats and setting for IDP_idpSessionsByIndices's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPSESSBYINDX_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpSessionsByIndices, relInfo, meInfo);

            amSAML2IDP_idpSessionsByIndicesStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPSESSBYINDX_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpSessionsByIndices, relInfo, meInfo);

            amSAML2IDP_idpSessionsByIndicesSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_responsesByArtifacts
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_responsesByArtifacts =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_responsesByArtifacts);

            //  stats and setting for IDP_responsesByArtifacts's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_responsesByArtifacts, relInfo, meInfo);

            amSAML2IDP_responsesByArtifactsStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RSPSBYARTIFACTS_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_responsesByArtifacts, relInfo, meInfo);

            amSAML2IDP_responsesByArtifactsSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_mniRequestHash
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_MNIREQUESTHASH_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_mniRequestHash =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_mniRequestHash);

            //  stats and setting for IDP_mniRequestHash's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_MNIREQUESTHASH_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_mniRequestHash, relInfo, meInfo);

            amSAML2IDP_mniRequestHashStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_MNIREQUESTHASH_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_mniRequestHash, relInfo, meInfo);

            amSAML2IDP_mniRequestHashSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_dpAttributeMapperCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_DPATTRMAPPER_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_dpAttributeMapperCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_dpAttributeMapperCache);

            //  stats and setting for IDP_dpAttributeMapperCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_DPATTRMAPPER_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_dpAttributeMapperCache, relInfo, meInfo);

            amSAML2IDP_dpAttributeMapperCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_DPATTRMAPPER_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_dpAttributeMapperCache, relInfo, meInfo);

            amSAML2IDP_dpAttributeMapperCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_idpAccountMapperCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_IDPACCTMAPPER_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpAccountMapperCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_idpAccountMapperCache);

            //  stats and setting for IDP_idpAccountMapperCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPACCTMAPPER_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpAccountMapperCache, relInfo, meInfo);

            amSAML2IDP_idpAccountMapperCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPACCTMAPPER_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpAccountMapperCache, relInfo, meInfo);

            amSAML2IDP_idpAccountMapperCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_idpAuthnContextMapperCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_idpAuthnContextMapperCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_idpAuthnContextMapperCache);

            //  stats and setting for IDP_idpAuthnContextMapperCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpAuthnContextMapperCache, relInfo, meInfo);

            amSAML2IDP_idpAuthnContextMapperCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_IDPAUTHNCTXMAPPER_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_idpAuthnContextMapperCache, relInfo, meInfo);

            amSAML2IDP_idpAuthnContextMapperCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_reponseCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_RESPONSE_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_reponseCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_reponseCache);

            //  stats and setting for IDP_reponseCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RESPONSE_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_reponseCache, relInfo, meInfo);

            amSAML2IDP_reponseCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_RESPONSE_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_reponseCache, relInfo, meInfo);

            amSAML2IDP_reponseCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_authnContextCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_AUTHNCONTEXT_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_authnContextCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_authnContextCache);

            //  stats and setting for IDP_authnContextCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_AUTHNCONTEXT_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_authnContextCache, relInfo, meInfo);

            amSAML2IDP_authnContextCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_AUTHNCONTEXT_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_authnContextCache, relInfo, meInfo);

            amSAML2IDP_authnContextCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            //  IDP_oldIDPSessionCache
            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE);
            meInfo.setName(
                MonitoringConstants.AM_SAML2_IDP_OLDIDPSESSION_CACHE_NAME);
            relInfo.setType(MfRelationType.CMM_RESOURCE_OF_SYSTEM);
            mRI = mfMEServer.createRelationToNewManagedElement(amAppli,
                    relInfo, meInfo);
            amSAML2IDP_oldIDPSessionCache =
                (CMM_SWRCacheInstrum)mRI.getDestination();
            relInfo.setType(MfRelationType.CMM_SERVICE_RESOURCE);
            mRI = mfMEServer.createRelation(amSvcSAML2, relInfo,
                    amSAML2IDP_oldIDPSessionCache);

            //  stats and setting for IDP_oldIDPSessionCache's cache

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_STATS);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_OLDIDPSESSION_CACHE_STATS_NAME);

            relInfo.setType(MfRelationType.CMM_ELEMENT_STATISTICAL_DATA);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_oldIDPSessionCache, relInfo, meInfo);

            amSAML2IDP_oldIDPSessionCacheStats =
                (CMM_SWRCacheStatsInstrum)mRI.getDestination();

            meInfo.setType(MfManagedElementType.CMM_SWR_CACHE_SETTING);
            meInfo.setName(
                MonitoringConstants.
                    AM_SAML2_IDP_OLDIDPSESSION_CACHE_SETTING_NAME);

            relInfo.setType(MfRelationType.CMM_SCOPED_SETTING);
            mRI = mfMEServer.createRelationToNewManagedElement(
                amSAML2IDP_oldIDPSessionCache, relInfo, meInfo);

            amSAML2IDP_oldIDPSessionCacheSetting =
                (CMM_SWRCacheSettingInstrum)mRI.getDestination();

            // intialize the statistics elements

            initStats();

        } catch (MfManagedElementServerException mmese) {
            Monitoring.debug.error("createSAMLv2: " +
                mmese.getMessage());
        } catch (MfRelationInstrumException mrie) {
            Monitoring.debug.error("createSAMLv2: " +
                mrie.getMessage());
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("createSAMLv2:from initStats(): " +
                mmeie.getMessage());
        }

        return (0);
    }

    /**
     * This method initializes the SAML2 service's attributes
     * in its managed element (when that element gets created).
     */
    public static void initAttributes() {
    }

    /**
     * This method initializes the SAML2 service's statistics
     * in its managed element.
     */
    private static void initStats()
        throws MfManagedElementInstrumException
    {
        //  SP cache stats
        MonitoringUtils.initSWRCacheStats(amSAML2SP_requestHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_mniRequestHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_relayStateHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(
            amSAML2SP_fedSessionListsByNameIDInfoKeyStats, "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_logoutRequestIDsStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_responseHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_authCtxObjHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_authContextHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2SP_spAccountMapperCacheStats,
            "SAML 2");
        // IDP cache stats
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_authnRequestCacheStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_relayStateCacheStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_idpSessionsByIndicesStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_responsesByArtifactsStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_mniRequestHashStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(
            amSAML2IDP_dpAttributeMapperCacheStats, "SAML 2");
        MonitoringUtils.initSWRCacheStats(
            amSAML2IDP_idpAccountMapperCacheStats, "SAML 2");
        MonitoringUtils.initSWRCacheStats(
            amSAML2IDP_idpAuthnContextMapperCacheStats, "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_reponseCacheStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_authnContextCacheStats,
            "SAML 2");
        MonitoringUtils.initSWRCacheStats(amSAML2IDP_oldIDPSessionCacheStats,
            "SAML 2");

        //  SAML2 service stats (if created)
//      MonitoringUtils.initSvcStats(amSAML2SvcStats, "SAML 2");
        //  SP servlet stats
        MonitoringUtils.initSvcStats(amSAML2SP_IDPMniInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_IDPMniRedirectStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPMniInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPMniRedirectStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPWSFederationStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPspssoinitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPConsumerStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPSPMniSoapStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPIDPMniSoapStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPSPSloInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2SP_SPSPSloRedirectStats, "SAML 2");
        //  IDP servlet stats
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPSloSoapStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_WSFederationStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_idpSSOFederateStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPSloInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPSloRedirectStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_idpssoinitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_ConsumerStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_SPMniRedirectStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPMniRedirectStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_SPMniSoapStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPMniSoapStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_SPMniInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_IDPMniInitStats, "SAML 2");
        MonitoringUtils.initSvcStats(amSAML2IDP_idpArtifactResolutionStats,
            "SAML 2");
    }

    /*
     *  don't know if there should be individual methods
     *  for each status type, or make the SAML2 service
     *  import com.sun.cmm.cim.OperationalStatus.  probably
     *  the latter...
     */
    /**
     * This method sets the SAML2 service's operational status
     * in its managed element.
     * @param status The operational status to set the SAML2 service to.
     */
    public static void setStatus(OperationalStatus status) 
    {
        HashSet samlStatus = new HashSet();
        samlStatus.add(status);

        if (amSvcSAML2 == null) {
            return;
        }

        try {
            amSvcSAML2.setOperationalStatus(samlStatus);
            if (amSAML2SP_IDPMniInitURI != null) {
                amSAML2SP_IDPMniInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_IDPMniRedirectURI != null) {
                amSAML2SP_IDPMniRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPMniInitURI != null) {
                amSAML2SP_SPMniInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPMniRedirectURI != null) {
                amSAML2SP_SPMniRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPWSFederationURI != null) {
                amSAML2SP_SPWSFederationURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPspssoinitURI != null) {
                amSAML2SP_SPspssoinitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPConsumerURI != null) {
                amSAML2SP_SPConsumerURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPSPMniSoapURI != null) {
                amSAML2SP_SPSPMniSoapURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPIDPMniSoapURI != null) {
                amSAML2SP_SPIDPMniSoapURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPSPSloInitURI != null) {
                amSAML2SP_SPSPSloInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2SP_SPSPSloRedirectURI != null) {
                amSAML2SP_SPSPSloRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPSloSoapURI != null) {
                amSAML2IDP_IDPSloSoapURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_WSFederationURI != null) {
                amSAML2IDP_WSFederationURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_idpSSOFederateURI != null) {
                amSAML2IDP_idpSSOFederateURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPSloInitURI != null) {
                amSAML2IDP_IDPSloInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPSloRedirectURI != null) {
                amSAML2IDP_IDPSloRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_idpssoinitURI != null) {
                amSAML2IDP_idpssoinitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_ConsumerURI != null) {
                amSAML2IDP_ConsumerURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_SPMniRedirectURI != null) {
                amSAML2IDP_SPMniRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPMniRedirectURI != null) {
                amSAML2IDP_IDPMniRedirectURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_SPMniSoapURI != null) {
                amSAML2IDP_SPMniSoapURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPMniSoapURI != null) {
                amSAML2IDP_IDPMniSoapURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_SPMniInitURI != null) {
                amSAML2IDP_SPMniInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_IDPMniInitURI != null) {
                amSAML2IDP_IDPMniInitURI.setOperationalStatus(samlStatus);
            }
            if (amSAML2IDP_idpArtifactResolutionURI != null) {
                amSAML2IDP_idpArtifactResolutionURI.setOperationalStatus(
                    samlStatus);
            }
            if (Monitoring.debug.messageEnabled()) {
                Monitoring.debug.message("MonitoringSAML2:setStatus = " +
                    status);
            }
        } catch (MfManagedElementInstrumException mmeie) {
            Monitoring.debug.error("MonitoringSAML2:setStatus:error = " +
                mmeie.getMessage());
        }
    }

    /**
     * This method returns the handle to the SAML2 service's statistics
     * managed element.
     * @return the SAML2 service's statistics element handle.
     */
     //  don't know if handles to the service/servlets will be needed
//    public static CMM_ServiceStatsInstrum getSAMLv2SvcStats() {
//        return amSAML2SAMLSvcStats;
//    }

    /**
     * This method returns the handle to the SAML2 service's servlet's
     *  statistics managed element.
     * @return the SAML2 service's SP_IDPMniInit statistics element handle.
     */
     //  don't know if handles to the service/servlets stats will be needed
//    public static CMM_ServiceStatsInstrum getSAMLv2PostProfileStats() {
//        return amSAML2SAMLPostProfileStats;
//    }


    /**
     * This method returns the handle to the SAML2 service's
     * managed element.
     * @return the SAML1.0 service's element handle.
     */
     //  don't know if handles to the service/servlets stats will be needed
    protected static CMM_ServiceInstrum getSvcSAMLv2 () {
        return amSvcSAML2;
    }
}

