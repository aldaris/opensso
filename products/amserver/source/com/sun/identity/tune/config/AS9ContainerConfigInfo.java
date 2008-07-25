/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems, Inc. All Rights Reserved.
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
 * $Id: AS9ContainerConfigInfo.java,v 1.2 2008-07-25 05:53:45 kanduls Exp $
 */

package com.sun.identity.tune.config;

import com.sun.identity.tune.base.WebContainerConfigInfoBase;
import com.sun.identity.tune.common.FileHandler;
import com.sun.identity.tune.common.AMTuneException;
import com.sun.identity.tune.util.AMTuneUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This class contains the required configuration information for tuning
 * Application Server 9.1
 */
public class AS9ContainerConfigInfo extends WebContainerConfigInfoBase {
    
    private String asAdminUser;
    private String asAdminHost;
    private String asAdminPort;
    private String asAdminSecure;
    private boolean isInteractive;
    private String asAdminTarget;
    private String asAdminDir;
    private String asAdminHttpListener;
    private boolean tuneWebContainerJavaPolicy;
    private StringBuffer asAdminCommonParamsNoTarget;
    private StringBuffer asAdminCommonParams;
    private String asAdminCmd;
    private Map cfgMap;
    private String tempFile;
    private String acceptorThreadParam;
    private String adminPassFile;
    
    /**
     * Creates instance of AS9ContainerConfigInfo
     * @param confRbl Tuning configuration information
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    public AS9ContainerConfigInfo(ResourceBundle confRbl) 
    throws AMTuneException {
        try {
            setWebContainer(AS91_CONTAINER);
            adminPassFile = AMTuneUtil.TMP_DIR + "asadminpass";
            tempFile = AMTuneUtil.TMP_DIR + "cmdoutput";
            setContainerBaseDir(confRbl.getString(CONTAINER_BASE_DIR));
            setContainerInstanceName(
                    confRbl.getString(WEB_CONTAINER_INSTANCE_NAME));
            setContainerInstanceDir(confRbl.getString(CONTAINER_INSTANCE_DIR));
            setASAdminUser(confRbl.getString(ASADMIN_USER));
            setASAdminHost(confRbl.getString(ASADMIN_HOST));
            setASAdminPort(confRbl.getString(ASADMIN_PORT));
            setASAdminSecure(confRbl.getString(ASADMIN_SECURE));
            setIsInteractive(confRbl.getString(ASADMIN_INTERACTIVE));
            setASAdminTarget(confRbl.getString(ASADMIN_TARGET));
            setASAdminDir(confRbl.getString(ASADMIN_DIR));
            setASAdminCmd();
            setASAdminHttpListener(confRbl.getString(ASADMIN_HTTPLISTENER));
            setTuneWebContainerJavaPolicy(
                    confRbl.getString(AMTUNE_WEB_CONTAINER_JAVA_POLICY));
            asAdminCommonParamsNoTarget = new StringBuffer("--user ");
            asAdminCommonParamsNoTarget.append(getASAdminUser());
            asAdminCommonParamsNoTarget.append(" --passwordfile ");
            asAdminCommonParamsNoTarget.append(adminPassFile);
            asAdminCommonParamsNoTarget.append(" --host ");
            asAdminCommonParamsNoTarget.append(getWSAdminHost());
            asAdminCommonParamsNoTarget.append(" --port ");
            asAdminCommonParamsNoTarget.append(getASAdminPort());
            if (getASAdminSecure() != null) {
                asAdminCommonParamsNoTarget.append(" ");
                asAdminCommonParamsNoTarget.append(getASAdminSecure());
            }
            asAdminCommonParamsNoTarget.append(" --interactive=");
            asAdminCommonParamsNoTarget.append(isInteractive());
            asAdminCommonParams = 
                    new StringBuffer(asAdminCommonParamsNoTarget.toString());
            asAdminCommonParams.append(" --target ");
            asAdminCommonParams.append(getASAdminTarget());
            acceptorThreadParam = "server.http-service.http-listener." +
                    getASAdminHttpListener() + ACCEPTOR_THREAD_PARAM;
            writePasswordToFile(confRbl.getString(ASADMIN_PASSWORD));
            checkAppServer64BitEnabled();
            fillCfgMap();
        } catch (Exception ex) {
            pLogger.log(Level.SEVERE, "AS91ContainerConfigInfo", 
                    "Failed to set Appserver 91 configuration information. ");
            throw new AMTuneException(ex.getMessage());
        } finally {
            File tempF = new File(tempFile);
            if (tempF.isFile()) {
                tempF.delete();
            }
        }
    }
    
    /**
     * Writes password to file.
     * @param password
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    protected void writePasswordToFile (String password)
    throws AMTuneException {
        try {
            pLogger.log(Level.FINE, "writePasswordToFile", "Creating AS91 " +
                    "password file.");
            if (password != null && password.trim().length() > 0) {
                File passFile = new File(adminPassFile);
                BufferedWriter pOut =
                        new BufferedWriter(new FileWriter(passFile));
                pOut.write(ASADMIN_PASSWORD_SYNTAX);
                pOut.write(password);
                pOut.flush();
                pOut.close();
            } else {
                mWriter.write(ASADMIN_PASSWORD + " ");
                mWriter.writeLocaleMsg("pt-not-configured");
                mWriter.writelnLocaleMsg("pt-cannot-proceed");
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(" amtune-env");
                throw new AMTuneException("Application Server admin " +
                        "password not set.");
            }
        } catch (Exception ex) {
            pLogger.log(Level.SEVERE, "writePassWordToFile",
                    "Couldn't write password to file. ");
            throw new AMTuneException(ex.getMessage());
        }
    }

    /**
     * Return admin password file path
     */
    public String getAdminPassfilePath() {
        return adminPassFile;
    }
    
    /**
     * This method uses asadmin tool to get the current configuration 
     * information of Application server 9.1
     * 
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void fillCfgMap() 
    throws AMTuneException {
        try {
            StringBuffer getCmd = new StringBuffer(getASAdminCmd());
            getCmd.append("get ");
            getCmd.append(asAdminCommonParamsNoTarget.toString());
            getCmd.append(" ");
            getCmd.append(acceptorThreadParam);
            getCmd.append(" ");
            getCmd.append(COUNT_THREAD_PARAM);
            getCmd.append(" ");
            getCmd.append(QUEUE_SIZE_PARAM);
            StringBuffer resultBuffer = new StringBuffer();
            String reqLine = "";
            cfgMap = new HashMap();
            int retVal = AMTuneUtil.executeCommand(getCmd.toString(), 
                    resultBuffer);
            if (retVal == 0) {
                AMTuneUtil.writeResultBufferToTempFile(resultBuffer,
                        tempFile);
                FileHandler cfgF = new FileHandler(tempFile);
                reqLine =  cfgF.getLine(acceptorThreadParam);
                cfgMap.put(ACCEPTOR_THREAD_PARAM, 
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                
                reqLine = cfgF.getLine(COUNT_THREAD_PARAM);
                cfgMap.put(COUNT_THREAD_PARAM, 
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                
                reqLine = cfgF.getLine(QUEUE_SIZE_PARAM);
                cfgMap.put(QUEUE_SIZE_PARAM, 
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                cfgF.close();
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap",
                        "Error getting acceptor threads, count threads and " +
                        "queue size values. ");
                throw new AMTuneException("Error getting current setting " +
                        "for threads and queue size.");
            }
            String jvmOptParam = getASAdminTarget() + 
                    ".java-config.jvm-options";
            StringBuffer getJvmOptCmd = new StringBuffer(getASAdminCmd());
            getJvmOptCmd.append("get ");
            getJvmOptCmd.append(asAdminCommonParamsNoTarget.toString());
            getJvmOptCmd.append(" ");
            getJvmOptCmd.append(jvmOptParam);
            resultBuffer.setLength(0);
            retVal = AMTuneUtil.executeCommand(getJvmOptCmd.toString(), 
                    resultBuffer);
            List curJVMOptList = new ArrayList();
            if (retVal ==0) {
                String cmdOutput = resultBuffer.toString().
                        replace(jvmOptParam + " = ","");
                StringTokenizer st = new StringTokenizer(cmdOutput, ",");
                while (st.hasMoreTokens()) {
                    String flagStr = st.nextToken();
                    curJVMOptList.add(flagStr);
                }
                cfgMap.put(MIN_HEAP_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        MIN_HEAP_FLAG, true));
                cfgMap.put(MAX_HEAP_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        MAX_HEAP_FLAG, true));
                cfgMap.put(GC_LOG_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList,
                        GC_LOG_FLAG, true));
                if (cmdOutput.indexOf(CLIENT_FLAG) != -1) {
                    cfgMap.put(CLIENT_FLAG, CLIENT_FLAG);
                } else {
                    cfgMap.put(CLIENT_FLAG, "");
                }
                if (cmdOutput.indexOf(SERVER_FLAG) != -1) {
                    cfgMap.put(SERVER_FLAG, SERVER_FLAG);
                } else {
                    cfgMap.put(SERVER_FLAG, "");
                }
                cfgMap.put(STACK_SIZE_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        STACK_SIZE_FLAG, true));
                cfgMap.put(NEW_SIZE_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        NEW_SIZE_FLAG, false));
                cfgMap.put(MAX_NEW_SIZE_FLAG,
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        MAX_NEW_SIZE_FLAG, false));
                cfgMap.put(DISABLE_EXPLICIT_GC_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        DISABLE_EXPLICIT_GC_FLAG, true));
                cfgMap.put(PARALLEL_GC_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        PARALLEL_GC_FLAG, true));
                cfgMap.put(HISTOGRAM_FLAG,
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        HISTOGRAM_FLAG, true));
                cfgMap.put(GC_TIME_STAMP_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        GC_TIME_STAMP_FLAG, true));
                cfgMap.put(MARK_SWEEP_GC_FLAG, 
                        AMTuneUtil.getASJVMOption(curJVMOptList, 
                        MARK_SWEEP_GC_FLAG, true));
                if (isTuneWebContainerJavaPolicy()) {
                    cfgMap.put(JAVA_SECURITY_POLICY, 
                            AMTuneUtil.getASJVMOption(curJVMOptList, 
                            JAVA_SECURITY_POLICY, true));
                }
                if (AMTuneUtil.isNiagara()) {
                    cfgMap.put(PARALLEL_GC_THREADS, 
                            AMTuneUtil.getASJVMOption(curJVMOptList, 
                            PARALLEL_GC_THREADS, true));
                }
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap", 
                        "Error getting Current JVM options ");
                throw new AMTuneException("Error getting current " +
                        "JVM options");
            }
        } catch (Exception ex) {
            pLogger.log(Level.SEVERE, "fillCfgMap", 
                    "Error getting Application server configuration " +
                    "information. " + ex.getMessage());
            throw new AMTuneException(ex.getMessage());
        }
        
    }
    
    /**
     * Set Application Server Administrator user name
     * 
     * @param asAdminUser Application Server Administrator user name
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setASAdminUser(String asAdminUser)
    throws AMTuneException {
        if (asAdminUser != null) {
            this.asAdminUser = asAdminUser.trim();
        } else {
            mWriter.writeLocaleMsg("pt-not-configured");
            mWriter.writelnLocaleMsg("pt-cannot-proceed");
            mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
            mWriter.writeln(ASADMIN_USER);
            throw new AMTuneException("Appserver adminuser is null.");
        }
    }

    /**
     * Return Application Server Administrator user name
     * 
     * @return Application Server Administrator user name
     */
    public String getASAdminUser() {
        return asAdminUser;
    }
    
    /**
     * Set Application Server Administrator Host name
     * 
     * @param asAdminHost Application Server Administrator host name.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setASAdminHost(String asAdminHost) 
    throws AMTuneException {
        if (asAdminHost != null) {
            this.asAdminHost = asAdminHost.trim();
        } else {
            pLogger.log(Level.INFO, "setASAdminHost", "Appserver adminuser" +
                    " host name is null so using localhost. ");
            this.asAdminHost = AMTuneUtil.getHostName();
        }
    }
    
    /**
     * Return Application Server Administrator host name
     * 
     * @return Application Server Administrator host name
     */
    public String getWSAdminHost() {
        return asAdminHost;
    }
    
    
    /**
     * Set Application Server Admin server port
     * 
     * @param asAdminPort Admin server port number.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setASAdminPort(String asAdminPort) 
    throws AMTuneException {
        if (asAdminPort != null && asAdminPort.trim().length() > 0) {
            this.asAdminPort = asAdminPort.trim();
        } else {
             mWriter.writeLocaleMsg("pt-not-configured");
             mWriter.writelnLocaleMsg("pt-cannot-proceed");
             mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
             mWriter.writeln(ASADMIN_PORT);
             throw new AMTuneException("Invalid Application server " +
                     "Admin port.");
        }
    }
    
    /**
     * Return Admin server port number.
     *  
     * @return Admin Server port number.
     */
    public String getASAdminPort() {
        return asAdminPort;
    }
    
    /**
     * Set Admin server secure parameter value.
     * 
     * @param asAdminSecure Admin server secure parameter value.
     */
    private void setASAdminSecure(String asAdminSecure) {
        if (asAdminSecure != null && asAdminSecure.trim().length() > 0) {
            this.asAdminSecure = asAdminSecure.trim();
        } else {
            pLogger.log(Level.INFO, "setAdminSecure", 
                    "Application Server Admin port is not secured. ");
            asAdminSecure = "";
        }
    }
    
    /**
     * Return Application Server secure parameter value.
     * 
     * @return Application Server secure parameter value.
     */
    public String getASAdminSecure() {
        return this.asAdminSecure;
    }
    
    /**
     * Set the value of isInteractive
     * 
     * @param isInteractive 
     */
    private void setIsInteractive(String isInteractive) {
        if (isInteractive != null && isInteractive.trim().length() > 0) {
            this.isInteractive = Boolean.parseBoolean(isInteractive);
        } else {
            pLogger.log(Level.INFO, "setIsInteractive", 
                    "Setting isInteractive to false.");
            this.isInteractive = false;
        }
    }
    
    /**
     * Returns true if asadmin is interactive else false
     * 
     * @return true if interactive.
     */
    public boolean isInteractive() {
        return isInteractive;
    }
    
    /**
     * Administration server target
     * 
     * @param asAdminTarget
     */
    private void setASAdminTarget(String asAdminTarget) {
        if (asAdminTarget != null && asAdminTarget.trim().length() > 0) {
            this.asAdminTarget = asAdminTarget.trim();
        } else {
            pLogger.log(Level.INFO, "setASAdminTarget", 
                    "Using \"server\" as target.");
            this.asAdminTarget = "server";
        }
    }
    /**
     * Return Administration server target
     * 
     * @return Administration server target
     */
    public String getASAdminTarget() {
        return asAdminTarget;
    }
    
    /**
     * Set asadmin tool directory
     * 
     * @param asAdminDir asadmin Directory
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setASAdminDir(String asAdminDir) 
    throws AMTuneException {
        if (asAdminDir != null && asAdminDir.trim().length() > 0) {
            File cmdF = new File(asAdminDir);
            if (cmdF.isDirectory()) {
                this.asAdminDir = asAdminDir.trim();
            } else {
                mWriter.writeLocaleMsg("pt-not-valid-dir");
                mWriter.writeln(" " + asAdminDir);
                mWriter.writelnLocaleMsg("pt-cannot-proceed");
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(ASADMIN_DIR);
                throw new AMTuneException("Invalid asadmin directory.");
            }
        } else {
            mWriter.writeLocaleMsg("pt-not-configured");
            mWriter.writelnLocaleMsg("pt-cannot-proceed");
            mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
            mWriter.writeln(ASADMIN_DIR);
            throw new AMTuneException("Null asadmin directory.");
        }
    }
    
    /**
     * Return asadmin tool directory
     * 
     * @return asadmin tool directory
     */
    public String getASAdminDir() {
        return asAdminDir;
    }
    
    /**
     * Set asadmin tool based on the OS
     * 
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setASAdminCmd() 
    throws AMTuneException {
        if (AMTuneUtil.isWindows()) {
            asAdminCmd = getASAdminDir() + FILE_SEP + "asadmin.bat ";
        } else {
            asAdminCmd = getASAdminDir() + FILE_SEP + "asadmin ";
        }
        File asToolF = new File (asAdminCmd.trim());
        if (!asToolF.isFile()) {
            mWriter.write(asAdminCmd);
            mWriter.writelnLocaleMsg("pt-tool-not-found");
            mWriter.writelnLocaleMsg("pt-cannot-proceed");
            mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
            mWriter.writeln(ASADMIN_DIR);
            throw new AMTuneException("asadmin tool not found.");
        }
    }
    
    /**
     * Return asadmin tool path.
     * @return asadmin tool path
     */
    public String getASAdminCmd() {
        return asAdminCmd;
    }
    
    /**
     * Set Administration server http Listener.
     * 
     * @param asAdminHttpListener 
     */
    private void setASAdminHttpListener(String asAdminHttpListener) {
        if (asAdminHttpListener != null &&
                asAdminHttpListener.trim().length() > 0) {
            this.asAdminHttpListener = asAdminHttpListener.trim();
        } else {
            pLogger.log(Level.SEVERE, "setASAdminHttpListener", 
                    "Using default value \"http-listener-1\" " +
                    "for ASADMIN_HTTPLISTENER ");
            this.asAdminHttpListener = "http-listener-1";
        }
    }
    
    /**
     * Return Administration server http Listener value.
     * 
     * @return Administration server http Listener value.
     */
    public String getASAdminHttpListener() {
        return asAdminHttpListener;
    }
    
    /**
     * Set java security policy
     * 
     * @param tuneWebContainerJavaPolicy
     */
    private void setTuneWebContainerJavaPolicy (
            String tuneWebContainerJavaPolicy) {
        if (tuneWebContainerJavaPolicy != null &&
                tuneWebContainerJavaPolicy.trim().length() > 0) {
            this.tuneWebContainerJavaPolicy = 
                    Boolean.parseBoolean(tuneWebContainerJavaPolicy);
        } else {
            pLogger.log(Level.INFO, "setTuneWebContainerJavaPolicy", 
                    "Using default value false for " + 
                    AMTUNE_WEB_CONTAINER_JAVA_POLICY);
            this.tuneWebContainerJavaPolicy = false;
            
        }
    }
    
    /**
     * Return true if Java security policy is enabled.
     */
    public boolean isTuneWebContainerJavaPolicy() {
        return tuneWebContainerJavaPolicy;
    }
    
    /**
     * Returns current Application server 9.1 configuration information.
     */
    public Map getCurASConfigInfo() {
        return cfgMap;
    }
    
    /**
     * Return Acceptor thread string.
     * @return
     */
    public String getAcceptorThreadString() {
        return acceptorThreadParam;
    }
    
    /**
     * Return asadmin params without target option.
     * @return
     */
    public String getAsAdminCommonParamsNoTarget() {
        return asAdminCommonParamsNoTarget.toString();
    }
    
    /**
     * Return asadmin common params with target option.
     * @return
     */
    public String getAsAdminCommonParams() {
        return asAdminCommonParams.toString();
    }
    
    /**
     * Checks if Web server is using 64 bit JVM
     */
    private void checkAppServer64BitEnabled() 
    throws AMTuneException {
        mWriter.writelnLocaleMsg("pt-app-check-jvmbits");
        StringBuffer jvmcmd = new StringBuffer(getASAdminCmd());
        jvmcmd.append(GENERATE_JVM_REPORT_SUB_CMD);
        jvmcmd.append(" ");
        jvmcmd.append(getAsAdminCommonParamsNoTarget());
        StringBuffer resultBuffer = new StringBuffer();
        int retVal = AMTuneUtil.executeCommand(jvmcmd.toString(), resultBuffer);
        if (resultBuffer.indexOf("Unable to connect to admin-server") != -1) {
            mWriter.writelnLocaleMsg("pt-web-not-running-msg");
            throw new AMTuneException("Unable to connect to appserver.");
        }
        if (retVal == 0) {
            if (resultBuffer.toString().indexOf("sun.arch.data.model = 64") == 
                    -1) {
                setJVM64BitEnabled(false);
            } else {
                setJVM64BitEnabled(true);
            }
        } else {
            pLogger.log(Level.SEVERE, "checkAppServer64BitEnabled",
                    "Error checking jvm bits so using 32 bit. ");
            setJVM64BitEnabled(false);
        }
    }
}
