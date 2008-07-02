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
 * $Id: WebContainerConfigInfoBase.java,v 1.1 2008-07-02 18:44:02 kanduls Exp $
 */

package com.sun.identity.tune.base;

import com.sun.identity.tune.common.MessageWriter;
import com.sun.identity.tune.common.AMTuneException;
import com.sun.identity.tune.common.AMTuneLogger;
import com.sun.identity.tune.constants.WebContainerConstants;
import com.sun.identity.tune.util.AMTuneUtil;
import java.io.File;
import java.util.logging.Level;

/**
 * This contains all the common properties for Web Server and Application server
 * 
 */
public abstract class WebContainerConfigInfoBase implements 
        WebContainerConstants {
    private String hostName;
    private String containerBaseDir;
    private String containerInstanceName;
    private String containerInstanceDir;
    private String webContainer;
    protected AMTuneLogger pLogger;
    protected MessageWriter mWriter;
    protected boolean isJVM64Bit;
    
    /**
     * Constructs new WebContainerConfigInfoBase object.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    public WebContainerConfigInfoBase() 
    throws AMTuneException {
        isJVM64Bit = false;
        hostName = AMTuneUtil.getHostName();
        pLogger = AMTuneLogger.getLoggerInst();
        mWriter = MessageWriter.getInstance();
        
    }
    
    /**
     * Set Container Base directory
     * @param containerBaseDir container base directory.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    protected void setContainerBaseDir(String containerBaseDir) 
    throws AMTuneException {
        if (containerBaseDir != null && containerBaseDir.trim().length() > 0) {
            File instDir = new File(containerBaseDir);
            if (!instDir.isDirectory()) {
                mWriter.writeLocaleMsg("pt-web-conf-dir-not-found");
                mWriter.writelnLocaleMsg("pt-cannot-proceed");
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(CONTAINER_BASE_DIR);
                throw new AMTuneException("Invalid web container base " +
                        "directory");
            } else {
                this.containerBaseDir = containerBaseDir.trim();
            }
        } else {
            pLogger.log(Level.SEVERE, "setContainerBaseDir",
                    "Please check the value for " + CONTAINER_BASE_DIR);
            throw new AMTuneException("Invalid web container base " +
                    "directory");
        }
    }
    
    /**
     * Return container base directory.
     * @return container base directory.
     */
    public String getContainerBaseDir() {
        return containerBaseDir;
    }
    
    /**
     * Set container instance name.
     * @param containerInstanceName container instance name.
     */
    protected void setContainerInstanceName(String containerInstanceName) {
        if (containerInstanceName != null && 
                containerInstanceName.length() > 0) {
            this.containerInstanceName = containerInstanceName.trim();
        } else {
            containerInstanceName = AMTuneUtil.getHostName();
        }
    }
    
    /**
     * Return container instance name.
     * @return container instance name.
     */
    public String getContainerInstanceName() {
        return containerInstanceName;
    }
    
    /**
     * Set container instance directory.
     * @param containerInstanceDir container instance directory.
     * @throws com.sun.identity.tune.common.AMTuneException
     */
    protected void setContainerInstanceDir(String containerInstanceDir) 
    throws AMTuneException {
            if (containerInstanceDir != null &&
                containerInstanceDir.trim().length() > 0) {
            File instDir = new File(containerInstanceDir);
            if (!instDir.isDirectory()) {
                mWriter.writeLocaleMsg("pt-web-inst-dir-not-found");
                mWriter.writelnLocaleMsg("pt-cannot-proceed");
                mWriter.writeLocaleMsg("pt-conf-parm-cust-msg");
                mWriter.writeln(CONTAINER_INSTANCE_DIR);
                throw new AMTuneException("Invalid WebContainer Instance " +
                        "directory.");
            } else {
                this.containerInstanceDir = containerInstanceDir.trim();
            }
        } else {
            pLogger.log(Level.SEVERE, "setContainerInstanceDir",
                    "Please check the value for " + CONTAINER_INSTANCE_DIR);
            throw new AMTuneException("Invalid web container instance " +
                    "directory");
        }
    }
    
    /**
     * Return container instance directory.
     * @return container instance directory.
     */
    public String getContainerInstanceDir() {
        return containerInstanceDir;
    }
    
    /**
     * Set Web container type.
     * @param webContainer
     */
    protected void setWebContainer(String webContainer) {
        this.webContainer = webContainer;
    }
    
    /**
     * Return Web Container type.
     * @return webContainer type.
     */
    public String getWebContainer() {
        return webContainer;
    }
    
    /**
     * set true if jvm is 64 bit.
     * @param jvm64bitEnabled true if jvm is 64 bit.
     */
    protected void setJVM64BitEnabled(boolean jvm64bitEnabled) {
        this.isJVM64Bit = jvm64bitEnabled;
    }
    
    /**
     * Return true if jvm is 64 bit enabled.
     * @return
     */
    public boolean isJVM64Bit() {
        return isJVM64Bit;
    }
}
