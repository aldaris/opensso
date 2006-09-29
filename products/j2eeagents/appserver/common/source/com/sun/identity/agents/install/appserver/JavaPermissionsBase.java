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
 * $Id: JavaPermissionsBase.java,v 1.1 2006-09-29 00:11:15 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.install.appserver;

import com.sun.identity.install.tools.configurator.IStateAccess;
import com.sun.identity.install.tools.configurator.InstallConstants;
import com.sun.identity.install.tools.util.ConfigUtil;
import com.sun.identity.install.tools.util.Debug;
import com.sun.identity.install.tools.util.FileEditor;
import com.sun.identity.install.tools.util.FileUtils;
import com.sun.identity.install.tools.util.DeletePattern;


/**
 * The class grants or removes Java Permissions to agent
 */
public class JavaPermissionsBase implements InstallConstants {
    
    public JavaPermissionsBase() {
        StringBuffer sb = new StringBuffer();
        sb.append(LINE_SEP);
        sb.append("grant codeBase \"file:").append(ConfigUtil.getLibPath());
        sb.append("/*\" {").append(LINE_SEP);
        sb.append("       permission java.security.AllPermission;");
        sb.append(LINE_SEP);
        sb.append("};");
        setPermissions(sb.toString());
    }
    
    public boolean addToServerPolicy(IStateAccess stateAccess) {
        boolean status = false;        
        String serverPolicyFile = getServerPolicyFile(stateAccess); 
        try {
            FileUtils.appendDataToFile(serverPolicyFile, getPermissions());
            status = true;
        } catch (Exception e) {
            Debug.log("JavaPermissionsBase.addToServerPolicy() - Error " +
                "occurred while adding Agent Realm to '" + serverPolicyFile + 
                "'. ", e);
        }
    
        return status;
    }
    
    public boolean removeFromServerPolicy(IStateAccess stateAccess) {
        // Remove the lines with the match patterns from the login conf file
        String serverPolicyFileName = getServerPolicyFile(stateAccess);        
        FileEditor fileEditor = new FileEditor(serverPolicyFileName);
        
        boolean status = false;
        try {
            DeletePattern pattern = new DeletePattern(ConfigUtil.getLibPath(),
                DeletePattern.INT_MATCH_OCCURRANCE, 2);
            status = fileEditor.deleteLines(pattern);
        } catch (Exception e) {            
            Debug.log("JavaPermissionsBase.removeFromServerPolicy() - " + 
                "Exception occurred while removing the Agent Realm from " +
                "file '" + serverPolicyFileName + "'. ", e);            
        }       
        return status;
    }
    
    public String getServerPolicyFile(IStateAccess stateAccess) {
        return (String) stateAccess.get(STR_AS70_SERVER_POLICY_FILE_KEY);
    }
        
    private String getPermissions() {
        return _javaPermissions;
    }
    
    private void setPermissions(String javaPermissions) {
        _javaPermissions = javaPermissions;
    }
    
    public static final String STR_AS70_SERVER_POLICY_FILE_KEY = 
        "AS_SERVER_POLICY_FILE";    
    private String _javaPermissions;
}
