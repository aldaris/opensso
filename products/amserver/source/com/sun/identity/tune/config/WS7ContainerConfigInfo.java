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
 * $Id: WS7ContainerConfigInfo.java,v 1.1 2008-07-02 18:48:45 kanduls Exp $
 */

package com.sun.identity.tune.config;

import com.sun.identity.tune.base.WebContainerConfigInfoBase;
import com.sun.identity.tune.common.FileHandler;
import com.sun.identity.tune.common.AMTuneException;
import com.sun.identity.tune.util.AMTuneUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * The <code>WS7ContainerConfigInfo<\code> extends WebContainerConfigInfoBase
 * and contains WEbserver 7 configuration information.
 *
 */
public class WS7ContainerConfigInfo extends WebContainerConfigInfoBase {

    private String wsAdminDir;
    private String wsAdminCmd;
    private String wsAdminHost;
    private String wsAdminUser;
    private String wsAdminPort;
    private boolean isAdminPortSecure;
    private String wsAdminConfig;
    private String wsAdminHttpListener;
    private String adminPassFile;
    private String wsadmCommonParamsNoConfig;
    private String wsAdminCommonParams;
    private Map cfgMap;
    private String tempFile;

    /**
     * Constructs the object
     *
     * @param confRbl
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    public WS7ContainerConfigInfo(ResourceBundle confRbl)
    throws AMTuneException {
        try {
            setWebContainer(WS7_CONTAINER);
            adminPassFile = AMTuneUtil.TMP_DIR + "wsadminpass";
            tempFile = AMTuneUtil.TMP_DIR + "cmdoutput";
            setContainerBaseDir(confRbl.getString(CONTAINER_BASE_DIR));
            setContainerInstanceName(
                    confRbl.getString(WEB_CONTAINER_INSTANCE_NAME));
            setContainerInstanceDir(confRbl.getString(CONTAINER_INSTANCE_DIR));
            setWSAdminDir(confRbl.getString(WSADMIN_DIR));
            setWSAdminCmd();
            setWSAdminUser(confRbl.getString(WSADMIN_USER));
            writePasswordToFile(confRbl.getString(WSADMIN_PASSWORD));
            setWSAdminHost(confRbl.getString(WSADMIN_HOST));
            setWSAdminPort(confRbl.getString(WSADMIN_PORT));
            setWSAdminSecure(confRbl.getString(WSADMIN_SECURE));
            setWSAdminConfig(confRbl.getString(WSADMIN_CONFIG));
            setWSAdminHTTPListener(confRbl.getString(WSADMIN_HTTPLISTENER));
            wsadmCommonParamsNoConfig = " --user=" + getWSAdminUser() +
                    " --password-file=" + adminPassFile + " --host=" +
                    getWSAdminHost() + " --port=" + getWSAdminPort() + 
                    " --ssl=" + isAdminPortSecure;
            wsAdminCommonParams = wsadmCommonParamsNoConfig + " --config=" +
                    getWSAdminConfig();
            validateWSConfig();
            validateWSHttpListener();
            checkWebContainer64BitEnabled();
            fillCfgMap();
        } catch (Exception ex) {
           pLogger.log(Level.SEVERE, "WS7ContainerConfigInfo",
                   "Failed to set webserver configuration information. ");
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
            pLogger.log(Level.FINE, "writePasswordToFile", "Creating WS7 " +
                    "password file.");
            if (password != null && password.trim().length() > 0) {
                File passFile = new File(adminPassFile);
                BufferedWriter pOut =
                        new BufferedWriter(new FileWriter(passFile));
                pOut.write(WS7ADMIN_PASSWORD_SYNTAX);
                pOut.write(password);
                pOut.flush();
                pOut.close();
            } else {
                mWriter.writeLocaleMsg("pt-not-configured");
                mWriter.writelnLocaleMsg("pt-cannot-proceed");
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(WSADMIN_PASSWORD);
                throw new AMTuneException("Webserver admin " +
                        "password not set.");
            }
        } catch (Exception ex) {
            pLogger.log(Level.SEVERE, "writePassWordToFile",
                    "Couldn't write password to file. ");
            throw new AMTuneException(ex.getMessage());
        }
    }

    
    public String getWSAdminPassFilePath() {
        return adminPassFile;
    }

    protected void setContainerInstanceName(String containerInstanceName) {
        if ((containerInstanceName == null) ||
                (containerInstanceName != null &&
                (containerInstanceName.trim().length() == 0))) {
            super.setContainerInstanceName(AMTuneUtil.getHostName());
        } else {
            super.setContainerInstanceName(containerInstanceName.trim());
        }
    }

    protected void setContainerInstanceDir(String containerInstanceDir)
    throws AMTuneException {
        if (containerInstanceDir == null) {
            super.setContainerInstanceDir(getContainerBaseDir() +
                    FILE_SEP + "https-" + getContainerInstanceName());
        } else {
            super.setContainerInstanceDir(containerInstanceDir);
        }
    }

    private void setWSAdminDir(String wsAdminDir)
    throws AMTuneException {
        File wsDir = new File(wsAdminDir);
        if (wsAdminDir != null && wsDir.isDirectory()) {
            this.wsAdminDir = wsAdminDir.trim();
        } else {
            mWriter.writeLocaleMsg("pt-not-configured");
            mWriter.writelnLocaleMsg("pt-cannot-proceed");
            mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
            mWriter.writeln(WSADMIN_DIR);
            throw new AMTuneException("WS7 installation location is wrong.");
        }
    }

    public String getWSAdminDir() {
        return wsAdminDir;
    }

    private void setWSAdminCmd()
    throws AMTuneException {
        if (AMTuneUtil.isWindows2003()) {
            wsAdminCmd = wsAdminDir + FILE_SEP + "wadm.bat ";
        } else {
            wsAdminCmd = wsAdminDir + FILE_SEP + "wadm ";
        }
        File cmdFile = new File(wsAdminCmd.trim());
        if (cmdFile != null && !cmdFile.isFile()) {
            mWriter.write(wsAdminCmd);
            mWriter.writeLocaleMsg("pt-tool-not-found");
            mWriter.writeLocaleMsg("pt-cannot-proceed");
            throw new AMTuneException("Couldn't find wadm file.");
        }
    }

    public String getWSAdminCmd() {
        return wsAdminCmd;
    }

    

    private void setWSAdminHost(String wsAdminHost) {
        if (wsAdminHost != null) {
            this.wsAdminHost = wsAdminHost.trim();
        } else {
            this.wsAdminHost = AMTuneUtil.getHostName();
        }
    }

    public String getWSAdminHost() {
        return wsAdminHost;
    }

    private void setWSAdminSecure(String wsAdminSecure) {
        if (wsAdminSecure != null &&
                wsAdminSecure.indexOf("--ssl=true") != -1) {
            isAdminPortSecure = true;
        } else {
            isAdminPortSecure = false;
        }
    }

    public boolean isAdminPortSecure() {
        return isAdminPortSecure;
    }

    private void setWSAdminConfig(String wsAdminConfig) {
        if (wsAdminConfig == null) {
            this.wsAdminConfig = getContainerInstanceName();
        } else {
            this.wsAdminConfig = wsAdminConfig;
        }
    }

    public String getWSAdminConfig() {
        return wsAdminConfig;
    }

    private void setWSAdminHTTPListener(String wsAdminHTTPListener)
    throws AMTuneException {
        if (wsAdminHTTPListener != null &&
                wsAdminHTTPListener.trim().length() > 0) {
            this.wsAdminHttpListener = wsAdminHTTPListener;
        } else {
            mWriter.writeLocaleMsg("pt-not-configured");
            mWriter.writelnLocaleMsg("pt-cannot-proceed");
            mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
            mWriter.writeln(WSADMIN_HTTPLISTENER + " ");
            throw new AMTuneException("WebServer http Listener is null.");
        }
    }

    public String getWSAdminHttpListener() {
        return wsAdminHttpListener;
    }

    /**
     * Validates Web server Configuration
     *
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void validateWSConfig()
    throws AMTuneException {
        String resultCmd = getWSAdminCmd() + "list-configs" +
                wsadmCommonParamsNoConfig;
        StringBuffer resultBuffer = new StringBuffer();
        int retVal = AMTuneUtil.executeCommand(resultCmd, resultBuffer);
        if (retVal == 0) {
            if (resultBuffer.toString().indexOf(getWSAdminConfig()) == -1) {
                mWriter.writeLocaleMsg("pt-web-conf-error");
                mWriter.write(WSADMIN_CONFIG + " ");
                mWriter.writelnLocaleMsg("pt-web-invalid");
                mWriter.writeLocaleMsg("pt-web-cur-wadm-settings");
                mWriter.writeln(getWSAdminConfig());
                mWriter.writeLocaleMsg("pt-web-cur-configs");
                mWriter.writeln(resultBuffer.toString());
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(WSADMIN_CONFIG);
                throw new AMTuneException("Web server config is wrong");
            } else {
                pLogger.log(Level.INFO, "validateWSConfig", "Validated WS " +
                        "config " + resultBuffer.toString());
            }
        } else {
            mWriter.writelnLocaleMsg("pt-web-wadm-conf-error");
            mWriter.writeln(resultBuffer.toString());
            throw new AMTuneException(resultBuffer.toString());
        }
    }

    /**
     * Validates HTTP listener
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void validateWSHttpListener()
    throws AMTuneException {
        String resultCmd = getWSAdminCmd() + "list-http-listeners" +
                wsAdminCommonParams;
        StringBuffer resultBuffer = new StringBuffer();
        int retVal = AMTuneUtil.executeCommand(resultCmd, resultBuffer);
        if (retVal == 0) {
            if (resultBuffer.toString().indexOf(
                    getWSAdminHttpListener()) == -1) {
                mWriter.writeLocaleMsg("pt-web-cur-http-listener-msg");
                mWriter.writeln(getWSAdminHttpListener());
                mWriter.writeLocaleMsg("pt-web-cur-listeners");
                mWriter.writeln(resultBuffer.toString());
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(WSADMIN_HTTPLISTENER);
                throw new AMTuneException("Web server Http-Listener " +
                        "is not valid" );
            } else {
                pLogger.log(Level.INFO, "validateWSHttpListener",
                        "Validated WS httplistener" + resultBuffer.toString());
            }
        } else {
            mWriter.writelnLocaleMsg("pt-web-wadm-httplistener-error");
            mWriter.writeln(resultBuffer.toString());
            throw new AMTuneException(resultBuffer.toString());
        }
    }

    /**
     * Checks if Web server is using 64 bit JVM
     */
    private void checkWebContainer64BitEnabled() {
        mWriter.writelnLocaleMsg("pt-web-check-jvmbits");
        String jvmcmd = getWSAdminCmd() + "get-config-prop" +
                wsAdminCommonParams + " platform";
        StringBuffer resultBuffer = new StringBuffer();
        int retVal = AMTuneUtil.executeCommand(jvmcmd, resultBuffer);
        if (retVal == 0) {
            if (resultBuffer.toString().indexOf("64") == -1) {
                setJVM64BitEnabled(false);
            } else {
                setJVM64BitEnabled(true);
            }
        } else {
            pLogger.log(Level.SEVERE, "checkWebContainer64BitEnabled",
                    "Error checking jvm bits so using 32 bit. " +
                    resultBuffer.toString());
            setJVM64BitEnabled(false);
        }
    }

    /**
     * Calculates the Web server7 tuning parameters
     *
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void fillCfgMap()
    throws AMTuneException {
        try {
            pLogger.log(Level.INFO, "filCfgMap", "Getting server " +
                    "configuration information.");
            String propCmd = getWSAdminCmd() + "get-thread-pool-prop" +
                    wsAdminCommonParams;
            String httpPropCmd = getWSAdminCmd() + "get-http-listener-prop" +
                    wsAdminCommonParams + " --http-listener=" +
                    getWSAdminHttpListener();
            String statsPropCmd = getWSAdminCmd() + "get-stats-prop" +
                    wsAdminCommonParams;
            String jvmPropCmd = getWSAdminCmd() + "get-jvm-prop" +
                    wsAdminCommonParams;
            String listJvmOptions = getWSAdminCmd() + "list-jvm-options" +
                    wsAdminCommonParams;
            StringBuffer resultBuffer = new StringBuffer();
            String reqLine = "";
            cfgMap = new HashMap();
            int retVal = AMTuneUtil.executeCommand(propCmd, resultBuffer);
            if (retVal == 0) {
                AMTuneUtil.writeResultBufferToTempFile(resultBuffer,
                        tempFile);
                FileHandler cfgF = new FileHandler(tempFile);
                reqLine = cfgF.getLine(MIN_THREADS);
                cfgMap.put(MIN_THREADS,
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                reqLine = cfgF.getLine(MAX_THREADS);
                cfgMap.put(MAX_THREADS,
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                reqLine = cfgF.getLine(QUEUE_SIZE);
                cfgMap.put(QUEUE_SIZE,
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                reqLine = cfgF.getLine(STACK_SIZE);
                cfgMap.put(STACK_SIZE,
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                cfgF.close();
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap",
                        "Error getting get-thread-pool-prop configuration " +
                        "information. " + resultBuffer.toString());
                throw new AMTuneException("Error getting thread pool prop.");
            }
            resultBuffer.setLength(0);
            retVal = AMTuneUtil.executeCommand(httpPropCmd, resultBuffer);
            if (retVal == 0) {
                AMTuneUtil.writeResultBufferToTempFile(resultBuffer,
                        tempFile);
                FileHandler cfgF = new FileHandler(tempFile);
                reqLine = cfgF.getLine(ACCEPTOR_THREADS);
                cfgMap.put(ACCEPTOR_THREADS,
                        AMTuneUtil.getLastToken(reqLine, PARAM_VAL_DELIM));
                cfgF.close();
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap",
                        "Error getting get-http-listener-prop configuration " +
                        "information. " + resultBuffer.toString());
                throw new AMTuneException("Error getting http listener prop");
            }
            resultBuffer.setLength(0);
            retVal = AMTuneUtil.executeCommand(statsPropCmd, resultBuffer);
            if (retVal == 0) {
                AMTuneUtil.writeResultBufferToTempFile(resultBuffer,
                        tempFile);
                FileHandler cfgF = new FileHandler(tempFile);
                reqLine = cfgF.getLine(ENABLED);
                cfgMap.put(ENABLED, AMTuneUtil.getLastToken(reqLine, 
                        PARAM_VAL_DELIM));
                cfgF.close();
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap",
                        "Error getting get-stats-prop configuration " +
                        "information. " + resultBuffer.toString());
                throw new AMTuneException("Error getting stats prop.");
            }
            resultBuffer.setLength(0);
             retVal = AMTuneUtil.executeCommand(listJvmOptions, resultBuffer);
            if (retVal == 0) {
                AMTuneUtil.writeResultBufferToTempFile(resultBuffer,
                        tempFile);
                FileHandler cfgF = new FileHandler(tempFile);
                reqLine = cfgF.getLine(MIN_HEAP_FLAG);
                StringTokenizer st = new StringTokenizer(reqLine, " ");
                st.hasMoreElements();
                cfgMap.put(MIN_HEAP_FLAG, st.nextToken());
                st.hasMoreTokens();
                cfgMap.put(MAX_HEAP_FLAG, st.nextToken());
                reqLine = cfgF.getLine(GC_LOG_FLAG);

                cfgMap.put(GC_LOG_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(SERVER_FLAG);
                cfgMap.put(SERVER_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(STACK_SIZE_FLAG);
                cfgMap.put(STACK_SIZE_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(NEW_SIZE_FLAG);
                cfgMap.put(NEW_SIZE_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(MAX_NEW_SIZE_FLAG);
                cfgMap.put(MAX_NEW_SIZE_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(DISABLE_EXPLICIT_GC_FLAG);
                cfgMap.put(DISABLE_EXPLICIT_GC_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(PARALLEL_GC_FLAG);
                cfgMap.put(PARALLEL_GC_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(HISTOGRAM_FLAG);
                cfgMap.put(HISTOGRAM_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(GC_TIME_STAMP_FLAG);
                cfgMap.put(GC_TIME_STAMP_FLAG, reqLine.trim());
                reqLine = cfgF.getLine(MARK_SWEEP_GC_FLAG);
                cfgMap.put(MARK_SWEEP_GC_FLAG, reqLine.trim());
                if (AMTuneUtil.isNiagara()) {
                    reqLine = cfgF.getLine(PARALLEL_GC_THREADS);
                    cfgMap.put(PARALLEL_GC_THREADS, 
                            AMTuneUtil.getLastToken(reqLine, "="));
                }
                cfgF.close();
            } else {
                pLogger.log(Level.SEVERE, "fillCfgMap",
                        "Error getting list-jvm-options configuration " +
                        "information. " + resultBuffer.toString());
                throw new AMTuneException("Error gettig list jvm options.");
            }
        } catch (Exception ex) {
            pLogger.log(Level.SEVERE, "fillCfgMap", "Error getting " +
                    "server config information.");
            throw new AMTuneException(ex.getMessage());
        }
    }

    /**
     * Return wsadm command parameters without config option
     * @return wsadm command parameters without config option
     */
    public String getWSAdmCommonParamsNoConfig() {
        return wsadmCommonParamsNoConfig;
    }

    /**
     * Return wsadm command parameters with config option
     * @return wsadm command parameters with config option
     */
    public String getWSAdminCommonParams() {
        return wsAdminCommonParams;
    }

    /**
     * Return Web Server 7 configuration map
     * @return Web Server 7 configuration map
     */
    public Map getServerCfgMap() {
        return cfgMap;
    }
    
    /**
     * Set WebServer Admin server port
     * 
     * @param wsAdminPort Admin server port number.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setWSAdminPort(String wsAdminPort) 
    throws AMTuneException {
        if (wsAdminPort != null && wsAdminPort.trim().length() > 0) {
            this.wsAdminPort = wsAdminPort.trim();
        } else {
             mWriter.writeLocaleMsg("pt-not-configured");
             mWriter.writelnLocaleMsg("pt-cannot-proceed");
             mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
             mWriter.writeln(WSADMIN_PORT);
             throw new AMTuneException("Invalid Web server Admin port.");
        }
    }
    
    /**
     * Return Admin server port number.
     *  
     * @return Admin Server port number.
     */
    public String getWSAdminPort() {
        return wsAdminPort;
    }
    
    /**
     * Set WebServer Administrator user name
     * 
     * @param wsAdminUser Administrator user name.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    private void setWSAdminUser(String wsAdminUser) 
    throws AMTuneException {
        if (wsAdminUser != null && wsAdminUser.trim().length() > 0) {
            this.wsAdminUser = wsAdminUser.trim();
        } else {
             mWriter.writeLocaleMsg("pt-not-configured");
             mWriter.writelnLocaleMsg("pt-cannot-proceed");
             mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
             mWriter.writeln(WSADMIN_USER);
             throw new AMTuneException("Invalid Web server Admin User.");
        }
    }
    
    /**
     * Return Administrator User Name.
     *  
     * @return Administrator User name.
     */
    public String getWSAdminUser() {
        return wsAdminUser;
    }
}
