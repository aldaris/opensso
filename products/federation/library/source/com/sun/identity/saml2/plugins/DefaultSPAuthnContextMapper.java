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
 * $Id: DefaultSPAuthnContextMapper.java,v 1.3 2007-10-18 23:53:54 qcheng Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */


package com.sun.identity.saml2.plugins;

import com.sun.identity.saml2.assertion.AuthnContext;
import com.sun.identity.saml2.protocol.ProtocolFactory;
import com.sun.identity.saml2.protocol.RequestedAuthnContext;
import com.sun.identity.saml2.common.SAML2Constants;
import com.sun.identity.saml2.common.SAML2Exception;
import com.sun.identity.saml2.common.SAML2Utils;
import com.sun.identity.saml2.jaxb.entityconfig.SPSSOConfigElement;
import com.sun.identity.saml2.meta.SAML2MetaManager;
import com.sun.identity.saml2.meta.SAML2MetaException;
import com.sun.identity.saml2.meta.SAML2MetaUtils;
import com.sun.identity.saml2.profile.SPCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashSet;

/**
 * The <code>DefaultSPAuthnContextMapper.java</code> class determines
 * the authentication context and the authentication requirements for
 * authentication by the authenticaion authority.
 *
 * This implementation only uses Authentication Class Reference.
 * The Authentication Class Reference can be passed as a query parameter
 * or set in the SP Entity Configuration.
 */

public class DefaultSPAuthnContextMapper implements SPAuthnContextMapper {

    static SAML2MetaManager sm = SAML2Utils.getSAML2MetaManager();;
    static String DEFAULT = "default";

    /**
     * Returns the <code>RequestedAuthnContext</code> object.
     *
     * The RequestedAuthContext is created based on the query parameters
     * AuthnContextClassRef and AuthComparison  in the request
     * and authnContext attribute ,
     * spAuthncontextClassrefMapping, and  authComparison
     * attribute, spAuthncontextComparisonType ,  
     * set in the Service Provider Extended Configuration.
     * If the AuthnContext Class Reference cannot be determined then
     * the default value
     * urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTranstport
     * will be used. AuthnComparsion defaults to "exact" if no value
     * is specified.
     *
     * @param realm  Realm or Organization of the Service Provider.
     * @param hostEntityID Entity ID of the Service Provider.
     * @param paramsMap Map containing key/value pairs of parameters.
     *        The key/value pairs are those accepted during SP SSO
     *        initiation.
     * @throws SAML2Exception if an error occurs.
     */
    public RequestedAuthnContext getRequestedAuthnContext(String realm,
        String hostEntityID, Map paramsMap)
        throws SAML2Exception {

        // Read the AuthnContext Class Reference passed as query string
        // to SP 
        List authContextClassRef =
            (List) paramsMap.get(SAML2Constants.AUTH_CONTEXT_CLASS_REF);
        List authLevelList = 
            ((List)paramsMap.get(SAML2Constants.AUTH_LEVEL));
    
        Integer authLevel=null;
        if (authLevelList != null && !authLevelList.isEmpty()) {
            try { 
                 authLevel = 
                     new Integer((String) authLevelList.iterator().next());
             } catch (NumberFormatException nfe) {
                 if (SAML2Utils.debug.messageEnabled()) {
                     SAML2Utils.debug.message("not a valid integer",nfe);   
                 }
             } catch (Exception e) {
                 if (SAML2Utils.debug.messageEnabled()) {
                     SAML2Utils.debug.message("error getting " 
                          + "integer object",e);
                 }
             }
         }

        if (SAML2Utils.debug.messageEnabled()) {   
            SAML2Utils.debug.message("authLevel in Query:"+ authLevel);
            SAML2Utils.debug.message("authContextClassRef in Query:"+
                                      authContextClassRef);
        }

        Map spConfigMap = getSPConfigAttrs(realm,hostEntityID);
        // Retreived the cached AuthClass Ref / Auth Level Map
        Map authRefMap = Collections.EMPTY_MAP;
        if ((SPCache.authContextHash != null) && 
           (!SPCache.authContextHash.isEmpty())) {
            authRefMap = 
                (Map) SPCache.authContextHash.get(hostEntityID+"|"+realm);
        }
        // Retreive the AuthClass Reference from SP Configuration
        if (authRefMap != null && authRefMap.isEmpty()) {
            authRefMap = (Map) getAuthnCtxFromSPConfig(spConfigMap);
            if (authRefMap != null && !authRefMap.isEmpty()) {
                SPCache.authContextHash.put(hostEntityID+"|"+realm,authRefMap);
            }
        }

         List authCtxList = new ArrayList();

        // create a List of AuthnContext Class Reference
         if (authContextClassRef != null && !authContextClassRef.isEmpty()) {
            Iterator i = authContextClassRef.iterator();
            while (i.hasNext()) {
                String authClassRef = prefixIfRequired((String) i.next());
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("DefaultSPAuthnContextMapper: "
                        + "authClassRef=" + authClassRef);
                }
                authCtxList.add(authClassRef);
            }
         }   
         if (authLevel != null) {
             Set authCtxSet = authRefMap.keySet();
             Iterator i = authCtxSet.iterator();
             while (i.hasNext()) {
                  String className = (String)i.next();
                  if (DEFAULT.equals(className)) {
                      continue;
                  }
                  try {
                      Integer aLevel = 
                        new Integer((String) authRefMap.get(className));
                      if (aLevel != null &&
                          aLevel.intValue() >= authLevel.intValue()) {
                          authCtxList.add(className);
                      }
                  } catch (NumberFormatException nfe) {
                      if (SAML2Utils.debug.messageEnabled()) {
                          SAML2Utils.debug.message("not a valid integer",nfe);
                      }
                  } catch (Exception e) {
                      if (SAML2Utils.debug.messageEnabled()) {
                          SAML2Utils.debug.message("error getting " 
                                    + "integer value ",e);
                      }
                  }
              }
         }

         if ((authCtxList == null || authCtxList.isEmpty()) 
              && (authRefMap != null 
              && !authRefMap.isEmpty())) {   
              Set authCtxSet = authRefMap.keySet();
              Iterator i = authCtxSet.iterator();
              while (i.hasNext()) {
                  String val = (String) i.next();
                  if (val != null && !val.equals(DEFAULT)) {
                      authCtxList.add(val);
                  }
              }
         }

         // if list empty set the default
         if (authCtxList.isEmpty()) {
            authCtxList.add(
                SAML2Constants.CLASSREF_PASSWORD_PROTECTED_TRANSPORT);
         }
        
        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("SPCache.authContextHash is: "
                 + SPCache.authContextHash);
            SAML2Utils.debug.message("authCtxList is: "+ authCtxList);
        }
                
        // Retrieve Auth Comparison from Query parameter
         String authCtxComparison =  getAttrValue(paramsMap,
             SAML2Constants.SP_AUTHCONTEXT_COMPARISON);

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("AuthComparison in Query:"+
                                      authCtxComparison);
        }
         if ((authCtxComparison == null) || 
             !isValidAuthComparison(authCtxComparison)) {
             authCtxComparison = 
                 getAttrValue(spConfigMap,
                 SAML2Constants.SP_AUTHCONTEXT_COMPARISON_TYPE);
         } 

         RequestedAuthnContext reqCtx = 
            ProtocolFactory.getInstance().createRequestedAuthnContext();
         reqCtx.setAuthnContextClassRef(authCtxList);
         reqCtx.setComparison(authCtxComparison);

         return reqCtx;
    }

    /**
     * Returns the auth level for the AuthContext
     *
     * @param reqCtx  the RequestedAuthContext object.
     * @param authnContext  the AuthnContext object.
     * @param realm the realm or organization to 
     *    retreive the authncontext.
     * @param hostEntityID the Service Provider Identity String.
     * @param idpEntityID the Identity Provider Identity String.
     * @return authlevel an integer value.
     * @throws SAML2Exception if there is an error.
     */
    public int getAuthLevel(RequestedAuthnContext reqCtx,
                            AuthnContext authnContext,
                            String realm,
                            String hostEntityID, String idpEntityID) 
                            throws SAML2Exception {

        Map authRefMap = 
                (Map) SPCache.authContextHash.get(hostEntityID+"|"+realm);
        if (authRefMap == null || authRefMap.isEmpty()) {
            authRefMap = getAuthRefMap(realm,hostEntityID);
        }
        int authLevel = 0;

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:hostEntityID:"
                                        + hostEntityID);
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:realm:"
                                        + realm);
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:MAP:"
                                        + authRefMap);
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:HASH:"
                                        + SPCache.authContextHash);
        }
        String authnClassRef = null;
        if (authnContext != null) {
            authnClassRef = authnContext.getAuthnContextClassRef();
        }

        String authLevelStr = null;
        if ((authnClassRef != null) && (authnClassRef.length() > 0)) {
            if ((authRefMap != null) && !authRefMap.isEmpty()) {
                if (authRefMap.containsKey(authnClassRef)) {
                    authLevelStr =  (String) authRefMap.get(authnClassRef);
                } else {
                    authLevelStr = (String)authRefMap.get(DEFAULT);
                }
            }
        } else {
             if ((authRefMap != null) && (!authRefMap.isEmpty())) {
                    authLevelStr = (String)authRefMap.get(DEFAULT);
             }
        }

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("AuthLevel is :" + authLevel);
        }

        if (authLevelStr != null) {
            try {
                authLevel = new Integer(authLevelStr).intValue();
            } catch (Exception e) {
                SAML2Utils.debug.message("Error getting authLevel " 
                                        + "using default");
            }
        }

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:authnClRef:"
                                        + authnClassRef);
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper:authLevel :"
                                        + authLevel);
        }
        return authLevel;
    }

    /* parses the AuthContext attribute to get the Class Reference and
     * authlevel 
     */
    private static Map getAuthnCtxFromSPConfig(Map spConfigMap) {
        List authContextClassRefConfig =
         (List) spConfigMap.get(SAML2Constants.SP_AUTH_CONTEXT_CLASS_REF_ATTR);

        if (SAML2Utils.debug.messageEnabled()) {
            SAML2Utils.debug.message("DefaultSPAuthnContextMapper: List:"
                        +authContextClassRefConfig);
        }
        HashMap authRefMap =  new LinkedHashMap();
        String authLevel = null;

        if (authContextClassRefConfig != null && 
                authContextClassRefConfig.size() != 0) {

            Iterator i = authContextClassRefConfig.iterator();
            while (i.hasNext()) { 
                boolean isDefault = false;
                String authRef = (String)i.next();
                int idx = authRef.indexOf(DEFAULT);
                String authRefVal = authRef;
                if (idx != -1) {
                    authRefVal = authRef.substring(0,idx);
                    isDefault = true;
                }
                StringTokenizer st = new StringTokenizer(authRefVal,"|");
                String authClass = null;
                try {
                    authClass = (String) st.nextToken();
                } catch (Exception e ) {
                    if (SAML2Utils.debug.messageEnabled()) {
                        SAML2Utils.debug.message("AuthnContextClassRef "
                                                 + "not found");
                    }
                }
                try {
                    authLevel = (String) st.nextToken();
                } catch (Exception e ) {
                    if (SAML2Utils.debug.messageEnabled()) {
                        SAML2Utils.debug.message("AuthnContextClassRef  "
                                                 + "not found");
                    }
                }
                if (SAML2Utils.debug.messageEnabled()) {
                    SAML2Utils.debug.message("AuthLevel is :" + authLevel);
                }
                if (authRefVal != null) {
                    if (isDefault && !authRefMap.containsKey(DEFAULT)) {
                        authRefMap.put(DEFAULT, authLevel);
                    }
                    if (authClass != null) {
                        authRefMap.put(prefixIfRequired(authClass), authLevel);
                    }
                }
            }
        }

        return Collections.unmodifiableMap(authRefMap);
    }


    /* checks for validity of authcomparision */
    private static boolean isValidAuthComparison(String authComparison) {

        return authComparison.equals("exact") 
                                || authComparison.equals("maximum") 
                                || authComparison.equals("minimum") 
                                || authComparison.equals("better") ;
   }


    /* returns the SP Entity Config attributes */
    private static Map getSPConfigAttrs(String realm,String hostEntityID) {
        Map spConfigMap = Collections.EMPTY_MAP;
        try {
            if (sm != null) {
                SPSSOConfigElement spEntityCfg =
                            sm.getSPSSOConfig(realm,hostEntityID);
                if (spEntityCfg != null) {
                    spConfigMap = SAML2MetaUtils.getAttributes(spEntityCfg);
                }
            }
        } catch (Exception se) {
            SAML2Utils.debug.message("Error retrieving config",se);
        }
        return spConfigMap;
    }


   /* returns the auth context comparison type value */
   private static String getAttrValue(Map spConfigMap,String attrName) {
        String attrVal = SAML2Constants.SP_AUTHCONTEXT_COMPARISON_TYPE_VALUE;
        List listVal = (List) spConfigMap.get(attrName);
        if (listVal != null && listVal.size() != 0) {
            attrVal= ((String) listVal.iterator().next()).trim();
        }
        return attrVal;
   }

    /* returns a Map with key as the hostEntityID|realm and value the
     * the SP Extended configuration attributes.
     */
    private static Map getAuthRefMap(String realm,String hostEntityID) {
        Map authRefMap = Collections.EMPTY_MAP;
        try {
            SPSSOConfigElement spEntityCfg = 
                        sm.getSPSSOConfig(realm,hostEntityID);
            if (spEntityCfg != null)  {
                Map spConfigMap = SAML2MetaUtils.getAttributes(spEntityCfg);
                authRefMap = (Map) getAuthnCtxFromSPConfig(spConfigMap);
                if (authRefMap != null && !authRefMap.isEmpty()) {
                    SPCache.authContextHash.put(hostEntityID+"|"+realm,
                                                authRefMap);
                }
            }
        } catch (Exception e ) {
            if (SAML2Utils.debug.messageEnabled()) {
                SAML2Utils.debug.message("Error getting SP config : ",e);
            }
        }
        return authRefMap;
    }

    /**
     * Adds prefix to the authn class reference only when there is 
     * no ":" present.
     */ 
    private static String prefixIfRequired(String authClassRef) {
        if ((authClassRef != null) && (authClassRef.indexOf(':') == -1)) {
            return SAML2Constants.AUTH_CTX_PREFIX + authClassRef;
        } else {
            return authClassRef;
        }
    }
 }
