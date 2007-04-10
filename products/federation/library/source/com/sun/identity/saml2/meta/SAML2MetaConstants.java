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
 * $Id: SAML2MetaConstants.java,v 1.3 2007-04-10 06:28:33 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.meta;

/**
 * This interface is used to define the constants used by this SAML2 meta
 * service.
 */
public interface SAML2MetaConstants {
    String SAML2_COT_SERVICE = "sunSAML2COTConfigService";
    
    String SAML2_COT_SERVICE_VERSION = "1.0";
    
    String COT_DESC = "sun-saml2-cot-description";
    
    String COT_STATUS = "sun-saml2-cot-status";
    
    String COT_WRITER_SERVICE = "sun-saml2-writerservice-url";
    
    String COT_READER_SERVICE = "sun-saml2-readerservice-url";

    String COT_TRUSTED_PROVIDERS = "sun-saml2-trusted-providers";

    String ACTIVE= "active"; 
    
    String INACTIVE = "inactive";         
} 
