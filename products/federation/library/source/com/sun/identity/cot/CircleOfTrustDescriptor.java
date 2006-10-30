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
 * $Id: CircleOfTrustDescriptor.java,v 1.1 2006-10-30 23:13:59 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.cot;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import com.sun.identity.shared.validation.ValidationException;
import com.sun.identity.shared.validation.URLValidator;

/**
 * The <code>COTDescriptor</code> class is the representation
 * of the circle of trust configuration.
 */
public class CircleOfTrustDescriptor {
    private String circleOfTrustType = null;
    private String circleOfTrustName = null;
    private String circleOfTrustDescription = null;
    private String circleOfTrustStatus = null;
    private String writerServiceURL = null;
    private String readerServiceURL = null;
    private Set    trustedProviders = null;
    
    /*
     * Private Constructor.
     * This constructor populates object from the attribute
     * Map received from the data store.
     *
     * @param name The name of circle of trust.
     * @param type The circle of trust type.
     * @param attrMap The map which contains attributes of the circle
     *                      of trust.
     * @throws COTException if values in the map are invalid.
     */
    CircleOfTrustDescriptor(String name,String type,Map attrMap)
    throws COTException {
        setCircleOfTrustName(name);
        setCircleOfTrustType(type);
        setCircleOfTrustDescription(COTUtils.getFirstEntry(
                attrMap, COTConstants.COT_DESC));
        setCircleOfTrustType(COTUtils.getFirstEntry(
                attrMap,COTConstants.COT_TYPE));
        setCircleOfTrustStatus(COTUtils.getFirstEntry(
                attrMap, COTConstants.COT_STATUS));
        setWriterServiceURL(COTUtils.getFirstEntry(
                attrMap,COTConstants.COT_WRITER_SERVICE));
        setReaderServiceURL(COTUtils.getFirstEntry(
                attrMap,COTConstants.COT_READER_SERVICE));
        trustedProviders = Collections.unmodifiableSet(
                (Set) attrMap.get(COTConstants.COT_TRUSTED_PROVIDERS));
    }
    
    /**
     * Creates new <code>COTDescriptor</code> instance.
     *
     * @param circleOfTrustName name for the circleOfTrust
     * @param circleOfTrustStatus status of the CircleOfTrust.
     * @throws COTException if <code>circleOfTrustName</code>
     *         or <code>circleOfTrustStatus</code> is invalid.
     */
    public CircleOfTrustDescriptor(String circleOfTrustName,
            String circleOFTrustType,
            String circleOfTrustStatus) throws COTException {
        setCircleOfTrustName(circleOfTrustName);
        setCircleOfTrustType(circleOfTrustType);
        setCircleOfTrustStatus(circleOfTrustStatus);
    }
    
    /**
     * Creates new <code>COTDescriptor</code> instance.
     *
     * @param circleOfTrustName name for the circleOfTrust
     * @param circleOfTrustStatus status of the CircleOfTrust.
     * @param circleOfTrustDescription description for the circleOfTrust.
     * @param circleOfTrustReaderURL the reader url of the CircleOfTrust.
     * @param circleOfTrustWriterURL the writer url of the circleOfTrust.
     * @param circleOfTrustProvider the trusted providers of the CircleOfTrust.
     * @throws COTException if any input parameter is invalid.
     */
    public CircleOfTrustDescriptor(String circleOfTrustName,
            String circleOfTrustType,
            String circleOfTrustStatus,
            String circleOfTrustDescription,
            String circleOfTrustReaderURL,
            String circleOfTrustWriterURL,
            Set circleOfTrustProvider)
            throws COTException {
        setCircleOfTrustName(circleOfTrustName);
        setCircleOfTrustType(circleOfTrustType);
        setCircleOfTrustStatus(circleOfTrustStatus);
        setCircleOfTrustDescription(circleOfTrustDescription);
        if (circleOfTrustReaderURL != null) {
            setReaderServiceURL(circleOfTrustReaderURL);
        }
        if (circleOfTrustWriterURL !=null) {
            setWriterServiceURL(circleOfTrustWriterURL);
        }
        setTrustedProviders(circleOfTrustProvider);
    }
    
    /**
     * Validates and set the circle of trust name.
     */
    private void setCircleOfTrustName(String name) throws COTException {
        if (name == null || name.trim().length() <= 0 ) {
            String[] args = { name };
            throw new COTException("invalidCOTName",args);
        }
        circleOfTrustName = name;
    }
    
    /**
     * Validates and set the circle of trust type.
     */
    private void setCircleOfTrustType(String type) throws COTException {
        if (!COTUtils.isValidCOTType(type)) {
            String[] data = { type };
            throw new COTException("invalidCOTType",data);
        }
        COTUtils.debug.message("Circle of Trust type is : " + type);
        circleOfTrustType=type;
    }
    
    /**
     * Returns name of the Circle of Trust.
     *
     * @return name of the Circle of Trust.
     */
    public String getCircleOfTrustName() {
        return circleOfTrustName;
    }
    
    /**
     * Return the circle of trust type (idff or saml2).
     *
     * @return the circle of turst type (idff or saml2).
     */
    public String getCircleOfTrustType() {
        return circleOfTrustType;
    }
    
    /**
     * Returns status of the Circle of Trust.
     *
     * @return status of the Circle of Trust. It can be
     *         "active" or "inactive"
     */
    public String getCircleOfTrustStatus() {
        return circleOfTrustStatus;
    }
    
    /**
     * Returns description of the Circle of Trust.
     *
     * @return description of the Circle of Trust.
     */
    public String getCircleOfTrustDescription() {
        return circleOfTrustDescription;
    }
    
    /**
     * Returns reader service URL of the Circle of Trust.
     *
     * @return Reader service URL.
     */
    public String getReaderServiceURL() {
        return readerServiceURL;
    }
    
    /**
     * Returns Writer Service URL of the Circle of Trust.
     *
     * @return the Writer service URL.
     */
    public String getWriterServiceURL() {
        return writerServiceURL;
    }
    
    /**
     * Sets description of the Circle of Trust.
     *
     * @param circleOfTrustDescription Description of the Circle of Trust.
     */
    public void setCircleOfTrustDescription(String circleOfTrustDescription) {
        this.circleOfTrustDescription = circleOfTrustDescription;
    }
    
    /**
     * Sets reader service URL.
     *
     * @param readerServiceURL reader service URL.
     * @throws COTException if <code>readerServiceURL</code>
     *         is not an URL.
     */
    public void setReaderServiceURL(String readerServiceURL)
    throws COTException {
        if ((readerServiceURL != null) &&
                (readerServiceURL.trim().length() > 0)){
            URLValidator validator = URLValidator.getInstance();
            try {
                validator.validate(readerServiceURL);
            } catch (ValidationException e) {
                throw new COTException("invalidReaderUrl",null);
            }
        }
        
        this.readerServiceURL = readerServiceURL;
    }
    
    /**
     * Sets writer service URL.
     *
     * @param writerServiceURL writer service URL of the Circle of Trust.
     * @throws COTException if <code>writerServiceURL</code>
     *         is not an URL.
     */
    public void setWriterServiceURL(String writerServiceURL)
    throws COTException {
        if ((writerServiceURL != null) &&
                (writerServiceURL.trim().length() > 0)
                ){
            URLValidator validator = URLValidator.getInstance();
            try {
                validator.validate(writerServiceURL);
            } catch (ValidationException e) {
                throw new COTException("invalidWriterUrl",null);
            }
        }
        
        this.writerServiceURL = writerServiceURL;
    }
    
    /**
     * Sets status of a circle of trust.
     *
     * @param circleOfTrustStatus the circle of trust status. The valid value
     *        for status is "active" or "inactive".
     * @throws COTException if <code>circleOfTrustStatus</code>
     *         is blank or it is not "active" or "inactive"
     */
    public void setCircleOfTrustStatus(String circleOfTrustStatus)
    throws COTException {
        if (circleOfTrustStatus !=null &&
                (circleOfTrustStatus.equalsIgnoreCase(COTConstants.ACTIVE ) ||
                circleOfTrustStatus.equalsIgnoreCase(COTConstants.INACTIVE))) {
            this.circleOfTrustStatus = circleOfTrustStatus;
        } else {
            throw new COTException("invalidCotStatus", null);
        }
    }
    
    /**
     * Sets trusted providers of a circle of trust.
     *
     * @param circleOfTrustProvider A set of trusted providers
     */
    public void setTrustedProviders(Set circleOfTrustProvider) {
        trustedProviders = Collections.unmodifiableSet(circleOfTrustProvider);
    }
    
    /**
     * Returns a set of trusted providers in the circle of trust.
     *
     * @return a set of trusted providers in the circle of trust.
     */
    public Set getTrustedProviders() {
        Set ret = new HashSet();
        if (trustedProviders == null) {
            return null;
        } else {
            ret.addAll(trustedProviders);
        }
        return ret;
    }
    
    /**
     * Adds entity identifier to trusted providers set within the
     * circle of trust.
     *
     * @param entityID the entity id of a provider .
     * @return true if the set did not already contain the entityID.
     */
    public boolean add(String entityID) throws COTException {
        if (!trustedProviders.contains(entityID)) {
            Set ret = new HashSet();
            if (trustedProviders != null) {
                ret.addAll(trustedProviders);
            }
            boolean result = ret.add(entityID);
            trustedProviders = Collections.unmodifiableSet(ret);
            return result;
        } else {
            throw new COTException("entityExistsInCot", null);
        }
    }
    
    /**
     * Removes member from the trusted provider set within this circle
     * of trust.
     *
     * @param entityID The entity id of a provider
     * @return true if the set contained the entityID.
     */
    public boolean remove(String entityID) {
        if (trustedProviders != null && !trustedProviders.contains(entityID)) {
            return true;
        }
        Set ret = new HashSet();
        if (trustedProviders != null) {
            ret.addAll(trustedProviders);
        }
        boolean result = ret.remove(entityID);
        trustedProviders = Collections.unmodifiableSet(ret);
        return result;
    }
    
    /**
     * Returns attributes of this object into a map.
     */
    protected Map getAttributes() {
        Map attrMap = new HashMap();
        COTUtils.fillEntriesInSet(
                attrMap,
                COTConstants.COT_DESC,
                circleOfTrustDescription);
        COTUtils.fillEntriesInSet(
                attrMap,
                COTConstants.COT_TYPE,
                circleOfTrustType);
        COTUtils.fillEntriesInSet(
                attrMap,
                COTConstants.COT_STATUS,
                circleOfTrustStatus);
        COTUtils.fillEntriesInSet(
                attrMap,
                COTConstants.COT_WRITER_SERVICE,
                writerServiceURL);
        COTUtils.fillEntriesInSet(
                attrMap,
                COTConstants.COT_READER_SERVICE,
                readerServiceURL);
        attrMap.put(COTConstants.COT_TRUSTED_PROVIDERS, trustedProviders);
        return attrMap;
    }
}
