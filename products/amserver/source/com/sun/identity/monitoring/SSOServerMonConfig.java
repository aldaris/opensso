/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2009 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: SSOServerMonConfig.java,v 1.1 2009-06-19 02:23:16 bigfatrat Exp $
 *
 */

package com.sun.identity.monitoring;

import java.util.Hashtable;

public class SSOServerMonConfig {
    int htmlPort;
    int snmpPort;
    int rmiPort;
    boolean monitoringEnabled;
    boolean monHtmlPortEnabled;
    boolean monRmiPortEnabled;
    boolean monSnmpPortEnabled;

    public SSOServerMonConfig() {
    }

    private SSOServerMonConfig (SSOServerMonInfoBuilder asib) {
        htmlPort = asib.htmlPort;
        snmpPort = asib.snmpPort;
        rmiPort = asib.rmiPort;
        monitoringEnabled = asib.monitoringEnabled;
        monHtmlPortEnabled = asib.monHtmlPortEnabled;
        monRmiPortEnabled = asib.monRmiPortEnabled;
        monSnmpPortEnabled = asib.monSnmpPortEnabled;
    }

    public static class SSOServerMonInfoBuilder {
        int htmlPort;
        int snmpPort;
        int rmiPort;
        boolean monitoringEnabled;
        boolean monHtmlPortEnabled;
        boolean monRmiPortEnabled;
        boolean monSnmpPortEnabled;

        public SSOServerMonInfoBuilder(boolean monEnabled) {
            monitoringEnabled = monEnabled;
        }

        public SSOServerMonInfoBuilder htmlPort(int htmlPrt) {
            htmlPort = htmlPrt;
            return this;
        }

        public SSOServerMonInfoBuilder snmpPort (int snmpPrt) {
            snmpPort = snmpPrt;
            return this;
        }

        public SSOServerMonInfoBuilder rmiPort (int rmiPrt) {
            rmiPort = rmiPrt;
            return this;
        }

        public SSOServerMonInfoBuilder monHtmlEnabled (boolean monHtmlEnabled) {
            monHtmlPortEnabled = monHtmlEnabled;
            return this;
        }

        public SSOServerMonInfoBuilder monRmiEnabled (boolean monRmiEnabled) {
            monRmiPortEnabled = monRmiEnabled;
            return this;
        }

        public SSOServerMonInfoBuilder monSnmpEnabled (boolean monSnmpEnabled) {
            monSnmpPortEnabled = monSnmpEnabled;
            return this;
        }

        public SSOServerMonConfig build() {
            return new SSOServerMonConfig (this);
        }
    }
}

