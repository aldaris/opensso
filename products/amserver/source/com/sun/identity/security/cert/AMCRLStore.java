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
 * $Id: AMCRLStore.java,v 1.1 2006-01-28 09:28:36 veiming Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.security.cert;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPModification;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPUrl;
import sun.security.x509.CertificateExtensions;
import sun.security.x509.Extension;
import sun.security.x509.GeneralNames;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

import com.iplanet.security.x509.X509CRLExtensionFactory;
import com.iplanet.security.x509.CRLDistributionPoint;
import com.iplanet.security.x509.CRLDistributionPointsExtension;
import com.iplanet.security.x509.IssuingDistributionPoint;
import com.iplanet.security.x509.IssuingDistributionPointExtension;
import com.iplanet.security.x509.X500Name;
import com.sun.identity.authentication.spi.AuthLoginException;
import com.iplanet.am.util.AMURLEncDec;

/**
* The class is used to manage crl store in LDAP server
* This class does get crl and update crl with CRLDistribution
* PointsExtension in client certificate or IssuingDistribution 
* PointExtension in CRL. This class should be used 
* in order to manage CRL store in LDAP
* id-ce-cRLDistributionPoints OBJECT IDENTIFIER ::=  { id-ce 31 }
*
* RLDistributionPoints ::= SEQUENCE SIZE (1..MAX) OF DistributionPoint
*
* DistributionPoint ::= SEQUENCE {
*        distributionPoint       [0]     DistributionPointName OPTIONAL,
*        reasons                 [1]     ReasonFlags OPTIONAL,
*        cRLIssuer               [2]     GeneralNames OPTIONAL }
*
* DistributionPointName ::= CHOICE {
*        fullName                [0]     GeneralNames,
*        nameRelativeToCRLIssuer [1]     RelativeDistinguishedName }
*
* ReasonFlags ::= BIT STRING {
*        unused                  (0),
*        keyCompromise           (1),
*        cACompromise            (2),
*        affiliationChanged      (3),
*        superseded              (4),
*        cessationOfOperation    (5),
*        certificateHold         (6),
*        privilegeWithdrawn      (7),
*        aACompromise            (8) }
* 
*/

public class AMCRLStore extends AMCertStore {

    // In memory CRL cache
    private static Hashtable cachedcrls = new Hashtable();
    private String mCrlAttrName = null;
    private static X509CRLExtensionFactory extFactory 
        = X509CRLExtensionFactory.getInstance();

    /**
     * Class AMCRLStore is special cased CRL store for LDAP.
     * A AMCRLStore instance has to have all the information for ldap 
     * and all the access information for CRLDistributionPointExtension and
     * CRLIssuingDistributionPoint Extension
     *
     * @param   AMLDAPCertStoreParameters param
     */
    public AMCRLStore(AMLDAPCertStoreParameters param) {
        super(param);
    }

    /**
     * Checks certificate and returns corresponding stored CRL in ldap store
     * @param X509Certificate certificate
     */
    public X509CRL getCRL(X509Certificate certificate) throws IOException  {
        LDAPEntry crlEntry = null;
        X509CRL crl = (X509CRL) getCRLFromCache(certificate);
            LDAPConnection ldc = getConnection();

        try {
            if (crl == null) {
                crlEntry = getLdapEntry(ldc);
                crl = getCRLFromEntry(crlEntry);
            }
                
            if (needCRLUpdate(crl)) {
                X509CRL tmpcrl = null;
                IssuingDistributionPointExtension crlIDPExt = getCRLIDPExt(crl);
                CRLDistributionPointsExtension crlDPExt =
                    getCRLDPExt(certificate);
                if ((tmpcrl == null) && (crlIDPExt != null)) {
                   tmpcrl = getUpdateCRLFromCrlIDP(crlIDPExt);
                }
                            
                if ((tmpcrl == null) && (crlDPExt != null)) {
                    tmpcrl = getUpdateCRLFromCrlDP(crlDPExt);
                }

                if (tmpcrl != null) {
                    if (crlEntry == null) {
                        crlEntry = getLdapEntry(ldc);
                    }
                    updateCRL(ldc, crlEntry.getDN().toString(),
                        tmpcrl.getEncoded());
                }
                crl = tmpcrl;
             }
                    
            updateCRLCache(certificate, crl);
        } catch (Exception e) {
            debug.error("Error in getting CRL : " + e.toString());
        } finally {
            try {
                ldc.disconnect();
            } catch (LDAPException e) {}
        }
                
        return crl;
    }

    /**
     * Checks certificate and returns corresponding stored CRL 
     * in cached CRL store
     * @param X509Certificate certificate
     */
    public X509CRL getCRLFromCache(X509Certificate certificate) 
                                          throws IOException  {
        X500Name issuerDN = getIssuerDN(certificate);
         
        X509CRL crl = null;
        
        crl = (X509CRL) cachedcrls.get(issuerDN.toString());
        
        return crl;
    }

    /**
     * Checks certificate and update CRL in cached CRL store
     * @param X509Certificate certificate
     */
    public void updateCRLCache(X509Certificate certificate, X509CRL crl) 
                                         throws IOException  {
        X500Name issuerDN = getIssuerDN(certificate);
                
        if (crl == null) {
            cachedcrls.remove(issuerDN.toString());
        } else {
            cachedcrls.put(issuerDN.toString(), crl);
        }
    }

        
    private X509CRL getCRLFromEntry(LDAPEntry crlEntry) 
        throws AuthLoginException  {
         LDAPAttributeSet attributeSet = crlEntry.getAttributeSet();
         LDAPAttribute crlAttribute = null;
         X509CRL crl = null;
         
         try {
            /*
             * Retrieve the certificate revocation list if available.
             */
                                                                                
            if (mCrlAttrName == null) {
                crlAttribute = attributeSet.getAttribute(
                    "certificaterevocationlist");
                if (crlAttribute == null) {
                    crlAttribute = attributeSet.getAttribute(
                        "certificaterevocationlist;binary");
                    if (crlAttribute == null) {
                        debug.error("No CRL Cache is configured");
                        return null;
                    }
                }

                 mCrlAttrName = crlAttribute.getName();
            } else {
                crlAttribute = attributeSet.getAttribute(mCrlAttrName);
            }
                 
            if (crlAttribute.size() > 1) {
                debug.error("More than one CRL entries are configured");
                return null;
            }
        } catch (Exception e) {
            debug.error("Error in getting Cached CRL");
            return null;
        }

        try {
            Enumeration Crls = crlAttribute.getByteValues();
            byte[] bytes = (byte []) Crls.nextElement();
            cf = CertificateFactory.getInstance("X.509");
            crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            debug.error("Certificate: CertRevoked = ", e);
        }

        return crl;
    }

    /**
     * It checks whether the certificate has CRLDistributionPointsExtension
     * or not. If there is, it returns the extension.
     * @param X509Certificate certificate
     */
    private CRLDistributionPointsExtension getCRLDPExt(
        X509Certificate certificate){
        CRLDistributionPointsExtension dpExt = null;
        CertificateExtensions exts = null;

        if (extFactory != null) {
            try {
                X509CertImpl certImpl = new X509CertImpl(
                    certificate.getEncoded());
                X509CertInfo cinfo = new X509CertInfo(
                    certImpl.getTBSCertificate());
                exts = (CertificateExtensions) cinfo.get(
                    X509CertInfo.EXTENSIONS);
                Enumeration allexts = exts.getElements();
                while ((dpExt == null) && allexts.hasMoreElements()) {
                    Extension ext = (Extension) allexts.nextElement();
                    String oid = ext.getExtensionId().toString();
                    if (oid.equals(CRLDistributionPointsExtension.OID)) {
                        dpExt = extFactory.createCRLDistributionPointsExtension
                                      (Boolean.FALSE, ext.getExtensionValue());
                    }
                }
            } catch (Exception e) {
                debug.error(
                    "Error finding CRL distribution Point configured: ", e);
            }
        }
        
        return dpExt;
    }
    
            
    /**
     * It checks whether the crl has IssuingDistributionPointExtension
     * or not. If there is, it returns the extension.
     * @param X509CRL crl
     */
    private IssuingDistributionPointExtension getCRLIDPExt(X509CRL crl){
        IssuingDistributionPointExtension idpExt = null;
        
        if (extFactory != null) {
            try {
                byte[] ext = 
                  crl.getExtensionValue(IssuingDistributionPointExtension.OID);
                if (ext != null) {
                    idpExt = extFactory.createIssuingDistributionPointExtension(
                        Boolean.FALSE, ext);
                }
            } catch (Exception e) {
                debug.error(
                    "Error finding CRL distribution Point configured: ", e);
            }   
        }
        
        return idpExt;
    }
            
    /**
     * It parses CRLDistributionPointsExtension and returns array of
     * CRLDistributionPoint.
     * @param CRLDistributionPointsExtension dpExt
     */
    private CRLDistributionPoint[] 
                         getCRLdp(CRLDistributionPointsExtension dpExt){
        CRLDistributionPoint[] DPs = null;

        if ((dpExt != null) && (dpExt.getNumPoints() > 0)) {
            DPs = new CRLDistributionPoint[dpExt.getNumPoints()];
            for (int i = 0;i < dpExt.getNumPoints(); i++) {
                DPs[i] = (CRLDistributionPoint)dpExt.getPointAt(i);
            }
        }

        return DPs;
    }
                        
    /**
     * It updates CRL under the dn in the directory server.
     * It retrieves CRL distribution points from the parameter 
     * CRLDistributionPointsExtension dpExt.
     * @param CRLDistributionPointsExtension dpExt
     */
    private synchronized X509CRL 
           getUpdateCRLFromCrlDP(CRLDistributionPointsExtension dpExt) {
        // Get CRL Distribution points
        CRLDistributionPoint[]DPs = getCRLdp(dpExt);
        byte[] Crls = null;
        X509CRL crl = null;
                
        GeneralNames gName = null;
        int numOfDp = 0;
                
        if (DPs == null) {
           return null;
        }
                
        while ((Crls == null) && (numOfDp < DPs.length)) {
            gName = DPs[numOfDp++].getFullName();
            Crls = getCRLsFromGeneralNames(gName);
        }

        if (Crls != null) {
            try {
                crl = (X509CRL) cf.generateCRL(new ByteArrayInputStream(Crls)); 
            } catch (Exception e) {
                debug.error("Error in generating X509CRL" + e.toString());
            }
        }
                                        
        return crl;
    }        

    /**
     * It updates CRL under the dn in the directory server.
     * It retrieves CRL distribution points from the parameter 
     * CRLDistributionPointsExtension dpExt.
     * @param LDAPConnection ldc
     * @param String dn
     * @param CRLDistributionPointsExtension dpExt
     */
    private synchronized X509CRL 
            getUpdateCRLFromCrlIDP(IssuingDistributionPointExtension idpExt) {
        // Get CRL Distribution points
            IssuingDistributionPoint IDP = idpExt.getIssuingDistributionPoint();
        byte[] Crls = null;
        X509CRL crl = null;
        GeneralNames gName = null;
                
        if (IDP != null) {
            gName = IDP.getFullName();
            Crls = getCRLsFromGeneralNames(gName);
        }
                
        if (Crls != null) {
            try {
                crl = (X509CRL)cf.generateCRL(new ByteArrayInputStream(Crls));
            } catch (Exception e) {
                debug.error("Error in generating X509CRL" + e.toString());
            }
        }
                                        
        return crl;
    }        

    private byte[] getCRLsFromGeneralNames(GeneralNames gName) {
            byte[] Crls = null;
        int idx = 0;
        do {
            String uri = gName.get(idx++).toString().trim();
            String protocol = uri.toLowerCase();
            int proto_pos;
            if((proto_pos = protocol.indexOf("http")) == -1) {
                if((proto_pos = protocol.indexOf("https")) == -1) {
                    if((proto_pos = protocol.indexOf("ldap")) == -1) {
                        if((proto_pos = protocol.indexOf("ldaps")) == -1) {
                            continue;
                        }
                    }
                }
            }
        
            uri = uri.substring(proto_pos, uri.length());
            if (debug.messageEnabled()) {
                debug.message("DP Name : " + uri);
            }
            Crls = getCRLByURI(uri);
        } while ((Crls != null) && (idx < gName.size())); 

        return Crls;
    }
    
    /**
     * It replaces attribute value under the DN.
     * It is used to replace old CRL with new one.
     * @param LDAPConnection ldc
     * @param String dn
     * @param LDAPAttribute attr
     */
    private boolean updateCRL(LDAPConnection ldc, String dn, byte[] crls) {
        LDAPAttribute crlAttribute = new LDAPAttribute(mCrlAttrName, crls);
        LDAPModification mod =
                new LDAPModification(LDAPModification.REPLACE, crlAttribute);
        try {
            ldc.modify(dn, mod);
        } catch (LDAPException e){
            debug.error("Error updating CRL Cache : ", e);
            return false;
        }

        return true;
    }        
    
    /**
     * It is checking uri's protocol.
     * Protocol has to be http(s) or ldap.
     * Based on checked protocol, it gets new CRL by invking 
     * getCRLByLdapURI() or getCRLByHttpURI()
     * @param String uri
     */
    private byte[] getCRLByURI(String uri) {
            if (uri == null) {
                    return null;
            }
            
            String protocol = uri.trim().toLowerCase();
        if (protocol.startsWith("http") || protocol.startsWith("https")) {
            return getCRLByHttpURI(uri);
        } else if (protocol.startsWith("ldap") || protocol.startsWith("ldaps")){
            return getCRLByLdapURI(uri);
        }
        
        return null;
    }        

    /**
     * It gets the new CRL from ldap server. 
     * If it is ldap URI, the URI has to be a dn that can be accessed 
     * with ldap anonymous bind. 
     * (example : ldap://server:port/uid=ca,o=company.com) 
     * This dn entry has to have CRL in attribute certificaterevocationlist 
     * or certificaterevocationlist;binary.
     * 
     * @param String uri
     */
    private byte[] getCRLByLdapURI(String uri){
        LDAPUrl url = null;
        LDAPConnection ldc = null;
        byte[] crl = null;

        try {
            url = new LDAPUrl(uri);
            // Check ldap over SSL
            if (url.isSecure()) {
                ldc = new LDAPConnection(storeParam.getSecureSocketFactory());
            } else { // non-ssl
                ldc = new LDAPConnection();
            }
                        
            ldc.connect(url.getHost(), url.getPort(), "", "");
            LDAPSearchResults results = 
            ldc.search(url.getDN().toString(),LDAPConnection.SCOPE_BASE,
                    null,null,false);

            if (results == null || results.hasMoreElements() == false) {
                debug.error(
                    "verifyCertificate - No CRL distribution Point configured");
                return null;
            }

            LDAPEntry entry = results.next();
            LDAPAttributeSet attributeSet = entry.getAttributeSet();

            /* 
            * Retrieve the certificate revocation list if available.
            */
            LDAPAttribute crlAttribute = attributeSet.getAttribute(
                "certificaterevocationlist");
            if (crlAttribute == null) {
                crlAttribute = attributeSet.getAttribute(
                    "certificaterevocationlist;binary");
                if (crlAttribute == null) {
                    debug.error(
                    "verifyCertificate - No CRL distribution Point configured");
                    return null;
                }
            }
            
            crl = (byte[])crlAttribute.getByteValues().nextElement();
            
        } catch (Exception e) {
            debug.error("Error in getting CRL",e);
        } finally {
            try {
                ldc.disconnect();
            } catch (LDAPException e) {}
        }

        return crl;
    }
            
    private byte[] getCRLByHttpURI(String url){
        String argString = "";  //default
        StringBuffer params = null;
        HttpURLConnection con = null;
        byte[] crl = null;

        String uriParamsCRL = storeParam.getURIParams();
                
        try {
            
            if (uriParamsCRL != null) {
                params = new StringBuffer();
                StringTokenizer st1 = new StringTokenizer(uriParamsCRL, ",");
                while (st1.hasMoreTokens()) {
                    String token = st1.nextToken();
                    StringTokenizer st2 = new StringTokenizer(token, "=");
                    if (st2.countTokens() == 2) {
                        String param = st2.nextToken();
                        String value = st2.nextToken();
                        params.append(AMURLEncDec.encode(param) + "=" + 
                            AMURLEncDec.encode(value));
                    } else {
                        continue;
                    }
                    
                    if (st1.hasMoreTokens()) {
                        params.append("&");
                    }                        
                }                    
            }
            
            URL uri = new URL(url); 
            con = (HttpURLConnection) uri.openConnection();
            
            // Prepare for both input and output
            con.setDoOutput( true );
            con.setDoInput( true );
            
            // Turn off Caching
            con.setUseCaches(false);
            
            int requestLength =
                params.toString().trim().getBytes("UTF-8").length;
            con.setRequestProperty ("Content-Length", 
                Integer.toString(requestLength));
                                   
            // Write the arguments as post data
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(params.toString().trim());
            out.flush();
            out.close();

            // Input ...
            InputStream in = con.getInputStream();
            StringBuffer buffer = new StringBuffer();
            int len;
            byte[] buf = new byte[1024];
            while((len = in.read(buf,0,buf.length)) != -1) {
                buffer.append(new String(buf));
            }
            
            crl = buffer.toString().getBytes();
            
        } catch (Exception e) {
            debug.error("Error in getting CRL",e);
        }
        
        return crl;
    }                

    // It returns NextCRLUpdate for current cached CRL
    // It gets CRL from crlAttribue member variable
    private boolean needCRLUpdate(X509CRL crl) {
        Date nextCRLUpdate = null;
        // Check CRLNextUpdate in CRL
        nextCRLUpdate = crl.getNextUpdate(); 

        return ((nextCRLUpdate != null) && nextCRLUpdate.before(new Date()));
    }
        
}
