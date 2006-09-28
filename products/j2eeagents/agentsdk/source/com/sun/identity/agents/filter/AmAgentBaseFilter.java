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
 * $Id: AmAgentBaseFilter.java,v 1.1 2006-09-28 23:29:01 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.identity.agents.arch.AgentConfiguration;
import com.sun.identity.agents.arch.AgentException;
import com.sun.identity.agents.arch.ISystemAccess;
import com.sun.identity.agents.common.IHttpServletRequestHelper;


/**
 * The base class for agent filter
 */
public abstract class AmAgentBaseFilter implements Filter 
{
    public void doFilter(
            ServletRequest request, ServletResponse response, 
            FilterChain filterChain)
    throws IOException, ServletException 
    {

        HttpServletRequest  httpRequest;
        HttpServletResponse httpResponse;

        try {
            httpRequest  = (HttpServletRequest) request;
            httpResponse = (HttpServletResponse) response;

            IAmFilter filter = getAmFilterInstance(httpRequest);
            
            AmFilterResult result = filter.isAccessAllowed(httpRequest,
                                        httpResponse);

            switch(result.getStatus().getIntValue()) {
            case AmFilterResultStatus.INT_STATUS_CONTINUE :
                allowRequestToContinue(httpRequest, httpResponse,
                                       filterChain, result);
                break;

            case AmFilterResultStatus.INT_STATUS_FORBIDDEN :
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
                break;

            case AmFilterResultStatus.INT_STATUS_REDIRECT :
                httpResponse.sendRedirect(result.getRedirectURL());
                break;

            case AmFilterResultStatus.INT_STATUS_SERVE_DATA :
                sendData(httpResponse, result);
                break;

            default :
                throw new AgentException("Unknown filter result status: "
                        				+ result.getStatus() + ":" 
                        				+ result.getStatus().getIntValue());
            }
        } catch(Exception ex) {
            throw new ServletException(
                "AmAgentFilter: An exception has occured", ex);
        }
    }
    
    public void allowRequestToContinue(
            HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain, AmFilterResult result)
    throws IOException, ServletException 
    {
        IHttpServletRequestHelper helper = result.getRequestHelper();
        HttpServletRequest outgoingRequest = null;
        if(helper != null) {
            outgoingRequest = new AmAgentServletRequest(request, helper);
        } else {
            outgoingRequest = request;
        }
        filterChain.doFilter(outgoingRequest, response);
    }

    public void destroy() {

        // No handling required
    }
    
    public void init(FilterConfig filterConfig) {
        setFilterConfig(filterConfig);
    }
    
    public void setFilterConfig(FilterConfig config) {
        _config = config;
    }
    
    public FilterConfig getFilterConfig() {
        return _config;
    }
    
    protected void sendData(HttpServletResponse response, AmFilterResult result)
    throws IOException 
    {
        PrintWriter out = null;
        try {
            response.setContentType("text/html");
            out = response.getWriter();
            out.print(result.getDataToServe());
            out.flush();
            out.close();
        } catch(IOException ex) {
            throw ex;
        } finally {
            if(out != null) {
                out.close();
            }
        }
    }
    
    protected abstract AmFilterMode getDefaultFilterMode();
    
    protected abstract AmFilterMode[] getAllowedFilterModes();
    
    protected  AmFilterMode verifyFilterMode(AmFilterMode suggestedMode, 
            AmFilterMode globalMode) 
    {
        AmFilterMode result = null;
        AmFilterMode[] allowedModes = getAllowedFilterModes();
        AmFilterMode defaultMode = getDefaultFilterMode();
        
        if (suggestedMode != null) {
            if (isAllowedFilterMode(suggestedMode)) {
                result = suggestedMode;
            }
        }
        
        if (result == null) {
            if (globalMode != null) {
                if (isAllowedFilterMode(globalMode)) {
                    result = globalMode;
                }
            }
        }
        
        if (result == null) {
            result = getDefaultFilterMode();
        }
        
        return result;    
    }
    
    protected AmFilterMode getFilterMode() {
        return _filterMode;
    }
    
    protected String getApplicationName() {
    	return _applicationName;
    }
    
    protected boolean isInitialized() {
    	return _initialized;
    }
    
    private boolean isAllowedFilterMode(AmFilterMode mode) {
        boolean result = false;
        AmFilterMode[] allowedModes = getAllowedFilterModes();
        if (allowedModes == null || allowedModes.length == 0) {
            allowedModes = new AmFilterMode[] { getDefaultFilterMode() };
        }
        for (int i=0; i<allowedModes.length; i++) {
            if (allowedModes[i].equals(mode)) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private synchronized void initializeFilter(HttpServletRequest request) 
    throws AgentException 
    {
        if (!isInitialized()) {
            ISystemAccess sysAccess = AmFilterManager.getSystemAccess();
    	    if (sysAccess != null) {
    	        String applicationName = null;
    	        String contextPath = request.getContextPath();
    		
    	        if (contextPath.trim().length() == 0 ||
    	                contextPath.trim().equals("/")) 
    	        {
    	            applicationName = 
    	                AgentConfiguration.DEFAULT_WEB_APPLICATION_NAME;
    	        } else {
    	            applicationName = contextPath.substring(1);
    	        }
    	    
    	        setApplicationName(applicationName);
    	    
    	        
    	        Map modeMap = sysAccess.getConfigurationMap(
    	                IFilterConfigurationConstants.CONFIG_FILTER_MODE);
    	        String modeString = (String) modeMap.get(applicationName);
    	        AmFilterMode suggestedMode = null;
    	        if (modeString != null && modeString.trim().length() >= 0) {
    	            suggestedMode = AmFilterMode.get(modeString);
    	            if (suggestedMode == null) {
    	                sysAccess.logWarning(
    	                    "AmAgentFilter: Unknown filter mode for "
    	                        + applicationName + ". Using configured mode");
    	            }
    	            sysAccess.logMessage("AmAgentFilter: mode specified for: "
    	                    + applicationName + " is: "
    	                    + modeString + ", instance: " + suggestedMode);
    	        } else {
    	            sysAccess.logMessage("AmAgentFilter: no mode specified for "
    	                    + applicationName);
    	        }
    	        
    	        AmFilterMode actualMode = verifyFilterMode(
    	                	suggestedMode, getGlobalFilterMode(sysAccess));
    	        
    	        sysAccess.logMessage("AmAgentFilter: Filter mode for " 
    	           + applicationName + " set to: "
    	           + ((actualMode==null)?"Configured":actualMode.toString()));
    	     
    	        setFilterMode(actualMode);
    	        markInitialized();
    	    } else {
    	        throw new AgentException(
    	                "AmAgentFilter: Unable to obtain system access");
    	    }
        }
    }  
    
    private AmFilterMode getGlobalFilterMode(ISystemAccess sysAccess) {
        String globalModeString = sysAccess.getConfigurationString(
                IFilterConfigurationConstants.CONFIG_FILTER_MODE);
        
        return AmFilterMode.get(globalModeString);
    }
    
    private IAmFilter getAmFilterInstance(HttpServletRequest request) 
    throws AgentException {
        if (!isInitialized()) {
            initializeFilter(request);
        }
        return AmFilterManager.getAmFilterInstance(getFilterMode());
    }
    
    private void markInitialized() {
    	_initialized = true;
    }

    private void setApplicationName(String applicationName) {
    	_applicationName = applicationName;
    }
    
    private void setFilterMode(AmFilterMode filterMode) {
        _filterMode = filterMode;
    }
    
    private String _applicationName;
    private FilterConfig _config;
    private boolean _initialized;
    private AmFilterMode _filterMode;
}
