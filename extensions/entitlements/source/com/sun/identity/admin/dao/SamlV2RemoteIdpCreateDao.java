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
 * $Id: SamlV2RemoteIdpCreateDao.java,v 1.1 2009-06-23 05:56:28 babysunil Exp $
 */
package com.sun.identity.admin.dao;
import com.sun.identity.cot.COTException;
import com.sun.identity.workflow.AddProviderToCOT;
import com.sun.identity.workflow.ImportSAML2MetaData;
import com.sun.identity.workflow.WorkflowException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

public class SamlV2RemoteIdpCreateDao
        implements Serializable {

    public SamlV2RemoteIdpCreateDao() {
    }

    public void importSamlv2RemoteIdp(
            String cot,
            String realm,
            String stdMeta) throws RuntimeException {
        String entityId = null;

        if ((stdMeta == null) || (stdMeta.trim().length() == 0)) {
            throw new RuntimeException("meta-data-required");
        }
        if ((realm == null) || (realm.trim().length() == 0)) {
            throw new RuntimeException("meta-data-required");
        }
        String StandardMeta = getContent(stdMeta);

        try {
            String[] results = ImportSAML2MetaData.importData(
                    realm, StandardMeta, null);
            realm = results[0];
            entityId = results[1];
        } catch (WorkflowException e) {
            throw new RuntimeException(e);
        }

        if ((cot != null) && (cot.length() > 0)) {
            try {
                AddProviderToCOT.addToCOT(realm, cot, entityId);
            } catch (COTException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static String getContent(String resName) {
        if (resName.startsWith("http://") ||
                resName.startsWith("https://")) {
            return getWebContent(resName);
        } else {
            return resName;
        }
    }

    private static String getWebContent(String url) {

        try {
            StringBuffer content = new StringBuffer();
            URL urlObj = new URL(url);
            URLConnection conn = urlObj.openConnection();

            if (conn instanceof HttpURLConnection) {
                HttpURLConnection httpConnection = (HttpURLConnection) conn;
                httpConnection.setRequestMethod("GET");
                httpConnection.setDoOutput(true);

                httpConnection.connect();
                int response = httpConnection.getResponseCode();
                InputStream is = httpConnection.getInputStream();
                BufferedReader dataInput = new BufferedReader(
                        new InputStreamReader(is));
                String line = dataInput.readLine();

                while (line != null) {
                    content.append(line).append('\n');
                    line = dataInput.readLine();
                }
            }

            return content.toString();

        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

