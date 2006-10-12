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
 * $Id: URLPolicyTaskHandler.java,v 1.2 2006-10-12 06:24:03 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.Manager;
import com.sun.identity.agents.common.SSOValidationResult;
import com.sun.identity.agents.policy.AmWebPolicyManager;
import com.sun.identity.agents.policy.AmWebPolicyResult;
import com.sun.identity.agents.policy.AmWebPolicyResultStatus;
import com.sun.identity.agents.policy.IAmWebPolicy;
import com.sun.identity.agents.util.NameValuePair;
import com.sun.identity.agents.util.StringUtils;
import com.sun.identity.shared.Constants;

/**
 * <p>
 * This task handler provides the necessary functionality to process incoming
 * requests for URL policy enforcement.
 * </p>
 */
public class URLPolicyTaskHandler extends AmFilterTaskHandler
        implements IURLPolicyTaskHandler {
    
    public URLPolicyTaskHandler(Manager manager) {
        super(manager);
    }
    
    public void initialize(ISSOContext context, AmFilterMode mode)
    throws AgentException {
        super.initialize(context, mode);
        setAmWebPolicy(AmWebPolicyManager.getAmWebPolicyInstance());
        initCompositeAdviceFormContent();
    }
    
    /**
     * Processes the incoming requests for URL policies and suggests
     * any necessary action needed in order to enforce such policies.
     *
     * @param ctx the <code>AmFilterRequestContext</code> that carries
     * information about the incoming request and response objects.
     *
     * @return <code>null</code> if no action is necessary, or
     * <code>AmFilterResult</code> object indicating the necessary action in
     * order to enforce URL policies.
     * @throws AgentException if this request cannot be handled by the task
     * handler successfully.
     */
    public AmFilterResult process(AmFilterRequestContext ctx)
    throws AgentException {
        AmFilterResult result = null;
        SSOValidationResult ssoValidationResult = ctx.getSSOValidationResult();
        HttpServletRequest request = ctx.getHttpServletRequest();
        AmWebPolicyResult policyResult =
                getAmWebPolicy().checkPolicyForResource(
                ssoValidationResult.getSSOToken(),
                ctx.getPolicyDestinationURL(), request.getMethod(),
                ssoValidationResult.getClientIPAddress(),
                ssoValidationResult.getClientHostName());
        
        switch(policyResult.getPolicyResultStatus().getIntValue()) {
            
            case AmWebPolicyResultStatus.INT_STATUS_ALLOW:
                if (isLogMessageEnabled()) {
                    logMessage(
                        "URLPolicyTaskHandler: access allowed by AmWebPolicy");
                }
                // Add response attributes
                if (policyResult.getResponseAttributes() != null) {
                    ctx.setPolicyResponseAttributes(
                            policyResult.getResponseAttributes());
                }
                break;
            case AmWebPolicyResultStatus.INT_STATUS_DENY:
                if (isLogMessageEnabled()) {
                    logMessage(
                        "URLPolicyTaskHandler: access denied by AmWebPolicy");
                }
                result = ctx.getBlockAccessResult();
                break;
                
            case AmWebPolicyResultStatus.INT_STATUS_INSUFFICIENT_CREDENTIALS :
                result = getServeDataResult(ctx, policyResult);
                break;
                
            default :
                logError("URLPolicyTaskHandler: Denying access to "
                        + request.getRequestURI()
                        + " due to invalid policy result status: "
                        + policyResult.getPolicyResultStatus());
                
                result = ctx.getBlockAccessResult();
                break;
        }
        
        return result;
    }
    
    public AmFilterResult getServeDataResult(AmFilterRequestContext ctx,
            AmWebPolicyResult policyResult)
            throws AgentException {
        String data = getModifiedCompositeAdviceFormContent(ctx, policyResult);
        if (isLogMessageEnabled()) {
            logMessage("URLPolicyTaskHandler: insufficient credentials - "
                    + "Post advices to Authentication Service "+ data);
        }
        
        return ctx.getServeDataResult(data);
    }
    
    
    private String getModifiedCompositeAdviceFormContent(
            AmFilterRequestContext ctx,
            AmWebPolicyResult amWebPolicyResult) throws AgentException {
        String param = Constants.COMPOSITE_ADVICE;
        String value = "";
        // should return auth url without nvp
        String action = ctx.getAuthRedirectURL(); 
        if (amWebPolicyResult != null && amWebPolicyResult.hasNameValuePairs()) {
            NameValuePair[] nvp = amWebPolicyResult.getNameValuePairs();
            
            if (nvp.length == 1) {
                param = URLEncoder.encode(nvp[0].getName());
                value = URLEncoder.encode(nvp[0].getValue());
            } else {
                throw new AgentException("Advice NVP length more than 1");
            }
        }
        
        StringBuffer buff = new StringBuffer(getCompositeAdviceFormContent());
        StringUtils.replaceString(buff, AM_FILTER_ADVICE_FORM_ACTION, action);
        StringUtils.replaceString(buff, AM_FILTER_ADVICE_NAME, param);
        StringUtils.replaceString(buff, AM_FILTER_ADVICE_VALUE, value);
        
        return buff.toString();
    }
    
    
    private void initCompositeAdviceFormContent() throws AgentException {
        String fileName = getManager().getConfigurationString(
                CONFIG_COMPOSITE_ADVICE_FILENAME,
                DEFAULT_COMPOSITE_ADVICE_FILENAME);
        if (isLogMessageEnabled()) {
            logMessage("URLPolicyTaskHandler: Composite Advice form file is: " 
                    + fileName);
        }
        
        InputStream inStream = null;
        try {
            inStream = ClassLoader.getSystemResourceAsStream(fileName);
        } catch (Exception ex) {
            if (isLogWarningEnabled()) {
                logWarning(
                        "URLPolicyTaskHandler: Exception while trying to get "
                        + "for file " + fileName, ex);
            }
        }
        
        if (inStream == null) {
            try {
                File contentFile = new File(fileName);
                if (!contentFile.exists() || !contentFile.canRead()) {
                    throw new AgentException(
                            "Unable to read composite advice form file "
                            + fileName);
                }
                
                inStream = new FileInputStream(contentFile);
            } catch (Exception ex) {
                logError("URLPolicyTaskHandler: Unable to read composite " 
                        + "advice form file content"
                        + fileName, ex);
                throw new AgentException(
                        "Unable to composite advice form file content "
                        + fileName, ex);
            }
        }
        
        if (inStream == null) {
            throw new AgentException("Unable to read form login content "
                    + fileName);
        }
        
        try {
            BufferedReader reader = 
                    new BufferedReader(new InputStreamReader(inStream));
            StringBuffer contentBuffer = new StringBuffer();
            String       nextLine      = null;
            
            while ( (nextLine = reader.readLine()) != null) {
                contentBuffer.append(nextLine);
                contentBuffer.append(NEW_LINE);
            }
            
            setCompositeAdviceFormContent(contentBuffer.toString());
            if (isLogMessageEnabled()) {
                logMessage("URLPolicyTaskHandler: composite advice content: " +
                        NEW_LINE + _compositeAdviceFormContent);
            }
        } catch (Exception ex) {
            throw new AgentException(
                    "Unable to read composite advice form content "
                    + fileName, ex);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception ex) {
                    logError("Exception while trying to close input stream "
                            + "for composite advice content file " 
                            + fileName, ex);
                }
            }
        }
    }
    
    public void setCompositeAdviceFormContent(String contentString) {
        _compositeAdviceFormContent = contentString;
    }
    
    public String getCompositeAdviceFormContent() {
        return _compositeAdviceFormContent;
    }
    
    /**
     * Returns a String that can be used to identify this task handler
     * @return the name of this task handler
     */
    public String getHandlerName() {
        return AM_FILTER_URL_POLICY_TASK_HANDLER_NAME;
    }
    
    public boolean isActive() {
        return isModeURLPolicyActive();
    }
    
    private void setAmWebPolicy(IAmWebPolicy amWebPolicy) {
        _amWebPolicy = amWebPolicy;
    }
    
    private IAmWebPolicy getAmWebPolicy() {
        return _amWebPolicy;
    }
    
    private IAmWebPolicy _amWebPolicy;
    private String _compositeAdviceFormContent;
}
