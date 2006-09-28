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
 * $Id: GetUniversalIdHandler.java,v 1.1 2006-09-28 23:42:18 huacui Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.install.handler;

/**
 * @author krishc
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

import com.sun.identity.install.tools.admin.ICommonToolsConstants;
import com.sun.identity.install.tools.admin.IToolsOptionHandler;
import com.sun.identity.install.tools.util.Console;
import com.sun.identity.install.tools.util.Debug;
import com.sun.identity.install.tools.util.LocalizedMessage;

import com.sun.identity.idm.AMIdentity;
import com.sun.identity.idm.IdType;
import com.sun.identity.idm.IdUtils;

import java.util.ArrayList;

/**
 * This class gets the universal Id of an identity
 */
public class GetUniversalIdHandler implements IToolsOptionHandler, 
                    ICommonToolsConstants {

    public boolean checkArguments(ArrayList arguments) {
        
        boolean result = true;
        
        if (arguments.size() == 3) {
            String userName = (String) arguments.get(0);
            String idType = (String) arguments.get(1);
            String realmName = (String) arguments.get(2);
        
            if ((userName != null) && (idType != null) &&
                (realmName != null)) { 
                if (convert2IdType(idType) != null) {
                    Debug.log("GetUniversalIdHandler.checkArguments: valid args");
                } else {
                    result = false;
                    Console.println();
                    Console.println(LocalizedMessage.get(
                        LOC_HR_ERR_INVALID_ID_TYPE));
                    Console.println(); 
                }  
            } else {
                result = false;
                Console.println();
                Console.println(LocalizedMessage.get(LOC_HR_ERR_GET_UUID_ARGS));
                Console.println();
            }
        } else {
            result = false;
            Console.println();
            Console.println(LocalizedMessage.get(
                        LOC_HR_ERR_GET_UUID_WRONG_NUM_ARGS));
            Console.println();
        }
        
        return result;
    }

    public void handleRequest(ArrayList arguments) {
        
        boolean result = false;
        String userName = (String) arguments.get(0);
        String idType = (String) arguments.get(1);
        String realmName = (String) arguments.get(2);
        
        try {  
            // Make a local call to create AMIdentity object
            AMIdentity idet = new AMIdentity(null,userName,
                convert2IdType(idType),realmName,null);
            Console.println();
            Console.println(LocalizedMessage.get(LOC_HR_MSG_UNIVERAL_ID),
                new Object[] { idet.getUniversalId() });
            result = true;
        } catch (Exception ex) {
            Debug.log("GetUniversalIdHandler.handleRequest: "
                    + "failed with exception", ex);
        }          
        
        if (!result) {
            Console.println();
            Console.println(LocalizedMessage.get(LOC_HR_ERR_GET_UUID_FAILED));
        }
    }
    
    public void displayHelp() {
        Console.println();        
        Console.println(LocalizedMessage.get(LOC_HR_MSG_GET_UUID_USAGE_DESC));        
        Console.println();
        Console.println(LocalizedMessage.get(LOC_HR_MSG_GET_UUID_USAGE_HELP));
        Console.println();
    }
    
    private IdType convert2IdType(String identType) {
        
        IdType type = null;
        
        if (identType.equalsIgnoreCase("AGENT")) {
            type = IdType.AGENT;
        } else if (identType.equalsIgnoreCase("FILTEREDROLE")) {
            type = IdType.FILTEREDROLE;
        } else if (identType.equalsIgnoreCase("GROUP")) {
            type = IdType.GROUP;
        } else if (identType.equalsIgnoreCase("ROLE")) {
            type = IdType.ROLE;
        } else if (identType.equalsIgnoreCase("USER")) {
            type = IdType.USER;
        } else {
            Debug.log("GetUniversalIdHandler.convert2IdType : Invalid Identity "
                + "type = "+ identType);
            type = null;
        }
        
        return type;
    }
  
    public static final String LOC_HR_ERR_GET_UUID_ARGS= 
        "HR_ERR_GET_UUID_ARGS";
    
    public static final String LOC_HR_ERR_GET_UUID_WRONG_NUM_ARGS= 
        "HR_ERR_GET_UUID_WRONG_NUM_ARGS";
    
    public static final String LOC_HR_ERR_GET_UUID_FAILED=
        "HR_ERR_GET_UUID_FAILED";
    
    public static final String LOC_HR_MSG_GET_UUID_USAGE_DESC=
        "HR_MSG_GET_UUID_USAGE_DESC";
    
    public static final String LOC_HR_MSG_GET_UUID_USAGE_HELP=
        "HR_MSG_GET_UUID_USAGE_HELP";
    
    public static final String LOC_HR_MSG_UNIVERAL_ID = 
        "HR_MSG_GET_UUID";
    
    public static final String LOC_HR_ERR_INVALID_ID_TYPE =
        "HR_ERR_INVALID_ID_TYPE";
    
}
