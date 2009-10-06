/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: WscCreateWizardBean.java,v 1.2 2009-10-06 18:28:03 ggennaro Exp $
 */

package com.sun.identity.admin.model;

import java.io.Serializable;
import java.util.ArrayList;

import javax.faces.model.SelectItem;

import com.icesoft.faces.context.effects.Effect;
import com.iplanet.am.util.SystemProperties;

public class WscCreateWizardBean
        extends WssWizardBean
        implements Serializable
{
    private WssClientProfileBean wscProfileBean;
    private WssClientProfileBean stsProfileBean;
    private ArrayList<SelectItem> wspProfileSuggestions;
    private String stsType;
    private boolean usingSts;
    private boolean usingOurSts;
    private String openssoStsUrl;
    private Effect openssoStsUrlMessageEffect;
    private Effect openssoStsUrlInputEffect;
    private RealmSummary realmSummary;
    private WscCreateProfileNameSummary profileNameSummary;
    private WscCreateUseStsSummary useStsSummary;
    private WscCreateServiceSecuritySummary serviceSecuritySummary;
    private WscCreateSignEncryptSummary signEncryptSummary;


    public WscCreateWizardBean() {
        super();
        initialize();
    }

    @Override
    public void reset() {
        super.reset();
        initialize();
    }

    private void initialize() {
        this.setWscProfileBean(new WssClientProfileBean());
        this.getWscProfileBean().setProfileName(null);
        this.getWscProfileBean().setEndPoint(null);
        this.getWscProfileBean().setUsingMexEndPoint(false);
        this.getWscProfileBean().setMexEndPoint(null);
        this.getWscProfileBean().setRequestSigned(true);
        this.getWscProfileBean().setRequestHeaderEncrypted(false);
        this.getWscProfileBean().setRequestEncrypted(false);
        this.getWscProfileBean().setResponseSignatureVerified(false);
        this.getWscProfileBean().setResponseDecrypted(false);
        this.getWscProfileBean().setEncryptionAlgorithm(EncryptionAlgorithm.AES_128.toInt());
        this.getWscProfileBean().setPrivateKeyAlias("test");
        this.getWscProfileBean().setPublicKeyAlias("test");
        this.getWscProfileBean().setSecurityMechanism(SecurityMechanism.ANONYMOUS.toInt());
        this.getWscProfileBean().setUserNameTokenUserName(null);
        this.getWscProfileBean().setUserNameTokenPassword(null);
        this.getWscProfileBean().setX509TokenSigningReferenceType(X509SigningRefType.DIRECT.toInt());
        this.getWscProfileBean().setKerberosDomain(null);
        this.getWscProfileBean().setKerberosDomainServer(null);
        this.getWscProfileBean().setKerberosServicePrincipal(null);
        this.getWscProfileBean().setKerberosTicketCache(null);

        this.setStsType(SecurityTokenServiceType.OPENSSO.toString());
        this.setUsingSts(true);
        this.setUsingOurSts(true);
        this.setOpenssoStsUrl(SystemProperties.getServerInstanceName());

        this.setStsProfileBean(new WssClientProfileBean());
        this.getStsProfileBean().setEndPoint("http://<sts-host-name:portnumber>/<sts>");
        this.getStsProfileBean().setMexEndPoint("http://<sts-host-name:portnumber>/<sts>/<mex>");
        this.getStsProfileBean().setUsingMexEndPoint(true);
        this.getStsProfileBean().setRequestSigned(true);
        this.getStsProfileBean().setRequestHeaderEncrypted(false);
        this.getStsProfileBean().setRequestEncrypted(false);
        this.getStsProfileBean().setResponseSignatureVerified(false);
        this.getStsProfileBean().setResponseDecrypted(false);
        this.getStsProfileBean().setEncryptionAlgorithm(EncryptionAlgorithm.AES_128.toInt());
        this.getStsProfileBean().setPrivateKeyAlias("test");
        this.getStsProfileBean().setPublicKeyAlias("test");
        this.getStsProfileBean().setSecurityMechanism(SecurityMechanism.ANONYMOUS.toInt());
        this.getStsProfileBean().setUserNameTokenUserName(null);
        this.getStsProfileBean().setUserNameTokenPassword(null);
        this.getStsProfileBean().setX509TokenSigningReferenceType(X509SigningRefType.DIRECT.toInt());
        this.getStsProfileBean().setKerberosDomain(null);
        this.getStsProfileBean().setKerberosDomainServer(null);
        this.getStsProfileBean().setKerberosServicePrincipal(null);
        this.getStsProfileBean().setKerberosTicketCache(null);

        this.setRealmSummary(new RealmSummary());
        this.setProfileNameSummary(new WscCreateProfileNameSummary(this));
        this.setUseStsSummary(new WscCreateUseStsSummary(this));
        this.setServiceSecuritySummary(new WscCreateServiceSecuritySummary(this));
        this.setSignEncryptSummary(new WscCreateSignEncryptSummary(this));
    }

    // Getters / Setters -------------------------------------------------------

    public WssClientProfileBean getWscProfileBean() {
        return wscProfileBean;
    }

    public void setWscProfileBean(WssClientProfileBean wscProfileBean) {
        this.wscProfileBean = wscProfileBean;
    }

    public WssClientProfileBean getStsProfileBean() {
        return stsProfileBean;
    }

    public void setStsProfileBean(WssClientProfileBean stsProfileBean) {
        this.stsProfileBean = stsProfileBean;
    }

    public String getStsType() {
        return stsType;
    }

    public void setStsType(String stsType) {
        this.stsType = stsType;
    }

    public boolean isUsingSts() {
        return usingSts;
    }

    public void setUsingSts(boolean usingSts) {
        this.usingSts = usingSts;
    }

    public boolean isUsingOurSts() {
        return usingOurSts;
    }

    public void setUsingOurSts(boolean usingOurSts) {
        this.usingOurSts = usingOurSts;
    }

    public String getOpenssoStsUrl() {
        return openssoStsUrl;
    }

    public void setOpenssoStsUrl(String openssoStsUrl) {
        this.openssoStsUrl = openssoStsUrl;
    }

    public Effect getOpenssoStsUrlMessageEffect() {
        return openssoStsUrlMessageEffect;
    }

    public void setOpenssoStsUrlMessageEffect(Effect openssoStsUrlMessageEffect) {
        this.openssoStsUrlMessageEffect = openssoStsUrlMessageEffect;
    }

    public Effect getOpenssoStsUrlInputEffect() {
        return openssoStsUrlInputEffect;
    }

    public void setOpenssoStsUrlInputEffect(Effect openssoStsUrlInputEffect) {
        this.openssoStsUrlInputEffect = openssoStsUrlInputEffect;
    }

    public ArrayList<SelectItem> getWspProfileSuggestions() {
        return wspProfileSuggestions;
    }

    public void setWspProfileSuggestions(ArrayList<SelectItem> wspProfileSuggestions) {
        this.wspProfileSuggestions = wspProfileSuggestions;
    }

    public RealmSummary getRealmSummary() {
        return realmSummary;
    }

    public void setRealmSummary(RealmSummary realmSummary) {
        this.realmSummary = realmSummary;
    }

    public WscCreateProfileNameSummary getProfileNameSummary() {
        return profileNameSummary;
    }

    public void setProfileNameSummary(WscCreateProfileNameSummary profileNameSummary) {
        this.profileNameSummary = profileNameSummary;
    }

    public WscCreateUseStsSummary getUseStsSummary() {
        return useStsSummary;
    }

    public void setUseStsSummary(WscCreateUseStsSummary useStsSummary) {
        this.useStsSummary = useStsSummary;
    }

    public WscCreateServiceSecuritySummary getServiceSecuritySummary() {
        return serviceSecuritySummary;
    }

    public void setServiceSecuritySummary(WscCreateServiceSecuritySummary serviceSecuritySummary) {
        this.serviceSecuritySummary = serviceSecuritySummary;
    }

    public WscCreateSignEncryptSummary getSignEncryptSummary() {
        return signEncryptSummary;
    }

    public void setSignEncryptSummary(WscCreateSignEncryptSummary signEncryptSummary) {
        this.signEncryptSummary = signEncryptSummary;
    }


}
