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
 * $Id: WeblogicHomeDirValidator.java,v 1.1 2007-08-07 01:47:48 sean_brydon Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.agents.tools.weblogic.v10;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import com.sun.identity.install.tools.configurator.IStateAccess;
import com.sun.identity.install.tools.configurator.InstallException;
import com.sun.identity.install.tools.configurator.ValidationResult;
import com.sun.identity.install.tools.configurator.ValidationResultStatus;
import com.sun.identity.install.tools.configurator.ValidatorBase;
import com.sun.identity.install.tools.util.Debug;
import com.sun.identity.install.tools.util.FileUtils;
import com.sun.identity.install.tools.util.LocalizedMessage;
import com.sun.identity.install.tools.configurator.InstallConstants;
import com.sun.identity.install.tools.configurator.InstallException;

/**
 * This task validates Weblogic's home directory.
 */
public class WeblogicHomeDirValidator extends ValidatorBase
        implements InstallConstants,IConfigKeys {
    
    public WeblogicHomeDirValidator() throws InstallException {
        super();
    }
    
    
    /**
     * Method isHomeDirValid
     *
     *
     * @param homeDir
     * @param props
     * @param IStateAccess
     *
     * @return ValidationResult
     *
     */
    public ValidationResult isHomeDirValid(String homeDir, Map props,
            IStateAccess state) {
        
        ValidationResultStatus validRes = ValidationResultStatus.STATUS_FAILED;
        LocalizedMessage returnMessage = null;
        
        Debug.log("WeblogicHomeDirValidator:isHomeDirValid()");
        if ((homeDir != null) && (homeDir.trim().length() >= 0)) {
            File homeDirFile = new File(homeDir);
            if (homeDirFile.exists() && homeDirFile.isDirectory() &&
                    homeDirFile.canRead()) {
                Debug.log("WeblogicHomeDirValidator:isHomeDirValid() - "
                        + homeDir + " is valid");
                returnMessage = LocalizedMessage.get(
                        LOC_VA_MSG_WL_VAL_WEBLOGIC_HOME_DIR,
                        STR_WL_GROUP,new Object[] {homeDir});
                validRes = ValidationResultStatus.STATUS_SUCCESS;
            }
        }
        
        if (validRes.getIntValue() ==
                ValidationResultStatus.INT_STATUS_FAILED) {
            returnMessage =
                    LocalizedMessage.get(LOC_VA_WRN_WL_INVAL_WEBLOGIC_HOME_DIR,
                    STR_WL_GROUP, new Object[] {homeDir});
        }
        
        Debug.log("WeblogicHomeDirValidator: Is Home Directory " +
                homeDir + " valid ? " + validRes.isSuccessful());
        
        return new ValidationResult(validRes,null,returnMessage);
    }
    
    /**
     * Method isWeblogicVersionValid
     *
     *
     * @param dir
     * @param props
     * @param IStateAccess
     *
     * @return ValidationResult
     *
     */
    public ValidationResult isWeblogicVersionValid(String homeDir, Map props,
            IStateAccess state) {
        
        ValidationResultStatus validRes = ValidationResultStatus.STATUS_FAILED;
        LocalizedMessage returnMessage = null;
        
        Debug.log("WeblogicHomeDirValidator.isWeblogicVersionValid(...)");
        ArrayList versionInfo = getWeblogicVersion(homeDir);
        String majorVersion = (String)versionInfo.get(0);
        String minorVersion = (String)versionInfo.get(1);
        Debug.log("WeblogicHomeDirValidator: WebLogic Version = " +
                majorVersion);
        
        //Sean commented out, added string for version 10
        //if ((majorVersion != null) &&
        //        (majorVersion.length() > 0) &&
        //        (majorVersion.equals(STR_WEBLOGIC_9_VERSION))) 
        if ((majorVersion != null) &&
                (majorVersion.length() > 0) &&
                (majorVersion.equals(STR_WEBLOGIC_10_VERSION))) 
       {
            returnMessage = LocalizedMessage.get(
                    LOC_VA_MSG_WL_VAL_WEBLOGIC_VERSION,
                    STR_WL_GROUP,new Object[] {majorVersion});
            state.put(STR_KEY_WL_MAJOR_VERSION, majorVersion);
            
            validRes = ValidationResultStatus.STATUS_SUCCESS;
        }
        
        if (validRes.getIntValue() ==
                ValidationResultStatus.INT_STATUS_FAILED) {
            returnMessage =
                    LocalizedMessage.get(LOC_VA_WRN_WL_INVAL_WEBLOGIC_VERSION,
                    STR_WL_GROUP, new Object[] {majorVersion});
        }
        
        Debug.log("WeblogicHomeDirValidator: Is a supported version ? "
                + validRes.isSuccessful());
        
        return new ValidationResult(validRes,null,returnMessage);
    }
    
    /**
     * log if the domain type entered by user is portal or server.
     *
     *
     * @param isPortal
     * @param props
     * @param IStateAccess
     *
     * @return ValidationResult
     *
     */
    public ValidationResult isWeblogicServerPortal(String isPortal, Map props,
            IStateAccess state) {
        
        ValidationResultStatus validRes = ValidationResultStatus.STATUS_SUCCESS;
        LocalizedMessage returnMessage = null;
        String userEnterDomain = null;
        
        if (isPortal.equalsIgnoreCase(STR_TRUE_VALUE)) {
            userEnterDomain = STR_PORTAL_DOMAIN_TYPE;
        } else {
            userEnterDomain = STR_SERVER_DOMAIN_TYPE;
        }

        state.put(STR_KEY_WL_DOMAIN, userEnterDomain);
        
        Debug.log("WeblogicHomeDirValidator.isWeblogicServerPortal() - " +
                    "User Entered domain type is:" + userEnterDomain);
    
        return new ValidationResult(validRes, null, returnMessage);
    }
    
    /**
     * return the WL server version
     * @param homeDir Home Directory for WebLogic
     * @return ArrayList version info, Major/Minor
     */
    public ArrayList getWeblogicVersion(String homeDir){
        
        String majorVersion = null;
        String minorVersion = null;
        ArrayList verInfo = new ArrayList(2);
        
        try {
            File wlHomeDir =
                    new File(homeDir + FILE_SEP + STR_SERVER_LEAF +
                    FILE_SEP + STR_LIB_LEAF);
            String jarFile = wlHomeDir + FILE_SEP + STR_WEBLOGIC_JAR;
            //java -classpath <WL_HOME>/server/lib/weblogic.jar weblogic.version
            String[] commandArray =
            { STR_JAVA_EXE, STR_CLASSPATH, jarFile, STR_WEBLOGIC_VERSION};
            StringBuffer output   = new StringBuffer(256);
            executeCommand( commandArray, null,output);
            String temp = output.toString();
            Debug.log("WeblogicHomeDirValidator.getWeblogicVersion() command " +
                    "returned version name =" + temp);
            if (temp != null) {
                // major version
                majorVersion ="10.0";
                        //Sean -comment out logic for now, hard code
                        //temp.substring(STR_VERSION_PREFIX.length()+1,
                        //STR_VERSION_PREFIX.length()+4);
                // @todo minor version - there no minor version for 9.2,
                // still keep this info for future.
                minorVersion = "0";
                         //Sean -comment out logic for now, hard code
                        //temp.substring(STR_VERSION_PREFIX.length()+7,
                        //STR_VERSION_PREFIX.length()+8);
                
                if (majorVersion != null) {
                    verInfo.add(majorVersion.trim());
                }
                if (minorVersion != null) {
                    verInfo.add(minorVersion.trim());
                }
            }
        } catch (Exception ex) {
            Debug.log("WeblogicHomeDirValidator.getWeblogicVersion() threw " +
                    " exception :" + ex);
        } catch(Throwable t) {
            Debug.log("WeblogicHomeDirvalidator.getWeblogicVersion() threw " +
                    " Throwable : " + t);
        }
        
        Debug.log(
                "WeblogicHomeDirValidator.getWeblogicVersion() - Major version "
                + majorVersion);
        
        
        return verInfo;
        
    }
    
    
    
    /**
     * Method executeCommand
     *
     *
     * @param commandArray
     * @param environment
     * @param resultBuffer
     *
     * @return
     *
     */
    private int executeCommand(String[] commandArray,
            String[] environment,
            StringBuffer resultBuffer) {
        
        int status;
        BufferedReader reader = null;
        
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(commandArray, environment);
            String  line;
            reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            
            if(resultBuffer != null) {
                resultBuffer.setLength(0);
                for(line = reader.readLine(); line != null;
                line = reader.readLine()) {
                    resultBuffer.append(line).append('\n');
                }
            } else {
                line = reader.readLine();
                while(line != null) {
                    line = reader.readLine();
                }
            }
            
            status = process.waitFor();
        } catch(InterruptedException exc) {
            throw new RuntimeException(
                    "WeblogicHomeDirValidator.executeCommand(...) error " +
                    "waiting for "
                    + commandArray[0]);
        } catch(IOException exc) {
            throw new RuntimeException(
                    "WeblogicHomeDirValidator.executeCommand(...) : " +
                    "error executing " + commandArray[0]);
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch(IOException exc) {
                    Debug.log("WeblogicHomeDirValidator.executeCommand(...): " +
                            "Error executing java runtime command", exc);
                }
            }
        }
        return status;
    }
    
    
    public void initializeValidatorMap() throws InstallException {
        
        Class[] paramObjs = {String.class,Map.class,IStateAccess.class};
        
        try {
            getValidatorMap().put("VALID_WL_HOME_DIR",
                    this.getClass().getMethod("isHomeDirValid",paramObjs));
            getValidatorMap().put("VALID_WL_VERSION",
                    this.getClass().getMethod("isWeblogicVersionValid",
                    paramObjs));
            getValidatorMap().put("VALID_WL_DOMAIN",
                    this.getClass().getMethod("isWeblogicServerPortal",
                    paramObjs));
            
        } catch (NoSuchMethodException nsme) {
            Debug.log("WeblogicHomeDirValidator: NoSuchMethodException " +
                    "thrown while loading method :",nsme);
            throw new InstallException(LocalizedMessage.get(
                    LOC_VA_ERR_VAL_METHOD_NOT_FOUND),nsme);
        } catch (SecurityException se){
            Debug.log("WeblogicHomeDirValidator: SecurityException thrown "
                    + "while loading method :",se);
            throw new InstallException(LocalizedMessage.get(
                    LOC_VA_ERR_VAL_METHOD_NOT_FOUND),se);
        } catch (Exception ex){
            Debug.log("WeblogicHomeDirValidator: Exception thrown while " +
                    "loading method :",ex);
            throw new InstallException(LocalizedMessage.get(
                    LOC_VA_ERR_VAL_METHOD_NOT_FOUND),ex);
        }
    }
    
    /** Hashmap of Validator names and integers */
    Map validMap = new HashMap();
    
    /*
     * Localized constants
     */
    public static String LOC_VA_MSG_WL_VAL_WEBLOGIC_HOME_DIR =
            "VA_MSG_WL_VAL_WEBLOGIC_HOME_DIR";
    public static String LOC_VA_WRN_WL_INVAL_WEBLOGIC_HOME_DIR =
            "VA_WRN_WL_INVAL_WEBLOGIC_HOME_DIR";
    
    public static String LOC_VA_MSG_WL_VAL_WEBLOGIC_VERSION =
            "VA_MSG_WL_VAL_WEBLOGIC_VERSION";
    public static String LOC_VA_WRN_WL_INVAL_WEBLOGIC_VERSION =
            "VA_WRN_WL_INVAL_WEBLOGIC_VERSION";
    
    public static String STR_JAVA_EXE = System.getProperty("java.home")
    + FILE_SEP + "bin" + FILE_SEP + "java";
    
}
