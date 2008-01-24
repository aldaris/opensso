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
 * $Id: BootstrapData.java,v 1.1 2008-01-24 23:14:14 veiming Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.setup;

import com.iplanet.am.util.SystemProperties;
import com.iplanet.services.ldap.DSConfigMgr;
import com.iplanet.services.ldap.LDAPServiceException;
import com.iplanet.services.util.Crypt;
import com.sun.identity.shared.Constants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class BootstrapData {
    private List data = new ArrayList();

    static final String PROTOCOL = "protocol";
    static final String SERVER_INSTANCE = "serverinstance";
    static final String FF_BASE_DIR = "ffbasedir";
    static final String BASE_DIR = "basedir";
    static final String DS_HOST = "dshost";
    static final String DS_PORT = "dsport";
    static final String DS_PWD = "dspwd";
    static final String DS_MGR = "dsmgr";
    static final String DS_BASE_DN = "dsbasedn";
    static final String PWD = "pwd";
    static final String EMBEDDED_DS = "embeddedds";
    static final String PROT_FILE = "file";
    static final String PROT_LDAP = "ldap";
    static final String PROTOCOL_FILE = "file://";
    static final String PROTOCOL_LDAP = "ldap://";
    private static final String LOCATION = "location";
    private static final String BOOTSTRAPCONFIG = "bootstrapConfig.properties";
    
    private String dsbasedn;
    private String dsameUserPwd;
    private String instanceName;

    /**
     * Creates an instance of this class
     *
     * @param fileName Path of bootstrap file.
     * @throws IOException if cannot read the file.
     */
    public BootstrapData(String fileName) 
        throws IOException {
        readFile(fileName);
    }
    
    public BootstrapData(Map mapConfig)
        throws UnsupportedEncodingException {
        data.add(createBootstrapResource(mapConfig, false));
    }

    /**
     * Returns list of bootstrap data.
     *
     * @return list of bootstrap data.
     */
    public List getData() {
        return data;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getBaseDN() {
        return dsbasedn;
    }

    public String getDsameUserPassword() {
        return dsameUserPwd;
    }

    public void initSMS() 
        throws UnsupportedEncodingException, LDAPServiceException,
        MalformedURLException {
        String serverConfigXML = getServerConfigXML();
        Properties prop = getBootstrapProperties();
        SystemProperties.initializeProperties(prop, true);
        Crypt.reinitialize();
        loadServerConfigXML(serverConfigXML);
        
        startEmbeddedDS();
    }

    private Properties getBootstrapProperties() {
        Properties prop = new Properties();
        try {
            prop.load(
                getClass().getClassLoader().getResourceAsStream(BOOTSTRAPCONFIG)
                );
            for (Enumeration e = prop.propertyNames(); e.hasMoreElements(); ) {
                String name = (String)e.nextElement();
                String property = prop.getProperty(name);
                property = property.replaceAll("@DS_BASE_DN@", dsbasedn);
                prop.setProperty(name, property);
            }
        } catch (IOException e) {
            //ignore because bootstrapConfig.properties is always bundled.
            e.printStackTrace();
        }
        return prop;
    }
    
    private void startEmbeddedDS() 
        throws MalformedURLException, UnsupportedEncodingException {
        for (Iterator i = data.iterator(); i.hasNext();) {
            String info = (String) i.next();

            // need to do this because URL class does not understand ldap://
            if (info.startsWith(BootstrapData.PROTOCOL_LDAP)) {
                info = "http://" + info.substring(7);
            }
            
            URL url = new URL(info);
            Map mapQuery = queryStringToMap(url.getQuery());
            String e = (String)mapQuery.get(EMBEDDED_DS);
            if ((e != null) && (e.length() > 0)) {
                startEmbeddedDS(e);
            }
        }
    }
    
    private static boolean startEmbeddedDS(String odsDir) {
        boolean started = false;
        File odsDirFile = new File(odsDir);

        if (odsDirFile.exists()) {
            if (!EmbeddedOpenDS.isStarted()) {
                try {
                    SetupProgress.reportStart("emb.startemb", null);
                    started = true;
                    EmbeddedOpenDS.startServer(odsDir);
                    SetupProgress.reportEnd("emb.success", null);
                } catch (Exception ex) {
                    //ignore, it maybe started.
                }
            } else {
                started = true;
            }
        }
        return started;
    }
    
    static void loadServerConfigXML(String xml)
        throws LDAPServiceException {
        ByteArrayInputStream bis = null;
        try {
            bis = new ByteArrayInputStream(xml.getBytes());
            DSConfigMgr.initInstance(bis);
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    //ignore
                }
            }
        }
    }
    
    private String getServerConfigXML()
        throws UnsupportedEncodingException, MalformedURLException {
        boolean first = true;
        StringBuffer buff = new StringBuffer();
        buff.append(BOOTSTRAP_SERVER_START_TAG);
        String serverBlob = null;
        StringBuffer serverBuff = new StringBuffer();
        int counter = 1;
        
        for (Iterator i = data.iterator(); i.hasNext(); ) {
            String info = (String)i.next();
            // need to do this because URL class does not understand ldap://
            if (info.startsWith(BootstrapData.PROTOCOL_LDAP)) {
                info = "http://" +  info.substring(7);
            }
            URL url = new URL(info);
            if (first) {
                buff.append(getServerConfigXMLUserBlob(url));
                serverBlob = getServerConfigXMLServerBlob(url);
                first = false;
            }
            serverBuff.append(getServerEntryXMLBlob(url, counter++));
        }

        String servers = serverBlob.replaceAll("@SERVER_ENTRY@",
            serverBuff.toString());

        buff.append(servers);
        buff.append(BOOTSTRAP_SERVER_END_TAG);
        return buff.toString();
    }

    private String getServerConfigXMLUserBlob(URL url)
        throws UnsupportedEncodingException {
        Map mapQuery = queryStringToMap(url.getQuery());
        String dshost = url.getHost();
        String dsport = Integer.toString(url.getPort());
        String dsmgr = (String)mapQuery.get(DS_MGR);
        String dspwd = (String)mapQuery.get(DS_PWD);
        String pwd = (String)mapQuery.get(PWD);

        dsbasedn = (String)mapQuery.get(DS_BASE_DN);
        instanceName = URLDecoder.decode(url.getPath(), "UTF-8");
        dsameUserPwd = (String)mapQuery.get(BootstrapData.PWD);

        if (instanceName.startsWith("/")) {
            instanceName = instanceName.substring(1);
        }

        String template= BOOTSTRAP_SERVER_CONFIG_USER;
        template = template.replaceAll("@" + DS_HOST + "@", dshost);
        template = template.replaceAll("@" + DS_PORT + "@", dsport);
        template = template.replaceAll("@" + DS_MGR + "@", dsmgr);
        template = template.replaceAll(
            "@" + DS_BASE_DN + "@", dsbasedn);
        template = template.replaceAll("@" + DS_PWD + "@", dspwd);
        template = template.replaceAll("@" + PWD + "@", pwd);
        return template;    
    }
    
    private static Map queryStringToMap(String str)
        throws UnsupportedEncodingException {
        Map map = new HashMap();
        StringTokenizer st = new StringTokenizer(str, "&");

        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            int idx = s.indexOf('=');
            map.put(s.substring(0, idx), URLDecoder.decode(
                s.substring(idx +1), "UTF-8"));
        }
        return map;
    }

    private String getServerConfigXMLServerBlob(URL url) 
        throws UnsupportedEncodingException {
        Map mapQuery = queryStringToMap(url.getQuery());
        String pwd = (String)mapQuery.get(BootstrapData.PWD);
        String dshost = url.getHost();
        String dsport = Integer.toString(url.getPort());
        dsbasedn = (String)mapQuery.get(DS_BASE_DN);
        String dsmgr = (String)mapQuery.get(DS_MGR);
        String dspwd = (String)mapQuery.get(DS_PWD);
        String template = BOOTSTRAP_SERVER_CONFIG_LDAP_SVR;
        template = template.replaceAll("@" + DS_HOST + "@", dshost);
        template = template.replaceAll("@" + DS_PORT + "@", dsport);
        template = template.replaceAll("@" + DS_MGR + "@", dsmgr);
        template = template.replaceAll(
            "@" + DS_BASE_DN + "@", dsbasedn);
        template = template.replaceAll("@" + DS_PWD + "@", dspwd);
        template = template.replaceAll("@" + PWD + "@", pwd);
        return template;    
    }

    private String getServerEntryXMLBlob(URL url, int counter) 
        throws UnsupportedEncodingException {
        Map mapQuery = queryStringToMap(url.getQuery());
        String dshost = url.getHost();
        String dsport = Integer.toString(url.getPort());

        String template = BOOTSTRAP_SERVER_CONFIG_LDAP_SVR_ENTRY;
        template = template.replaceAll("@" + DS_HOST + "@", dshost);
        template = template.replaceAll("@" + DS_PORT + "@", dsport);
        template = template.replaceAll("@counter@", Integer.toString(counter));
        return template;
    }


    static String create(
        String bootstrapFile, 
        Map configuration, 
        boolean legacy
    ) throws IOException {
        File btsFile = new File(bootstrapFile);
        if (!btsFile.getParentFile().exists()) {
            btsFile.getParentFile().mkdirs();
        }
        return createBootstrapResource(configuration, legacy);
    }

    static String createBootstrapResource(Map configuration, boolean legacy)
        throws UnsupportedEncodingException
    {
        StringBuffer buff = new StringBuffer();
        String protocol = (String)configuration.get(PROTOCOL);
        String pwd = Crypt.encode((String)configuration.get(PWD),
            Crypt.getHardcodedKeyEncryptor());
        String serverInstance = (String)configuration.get(
            SERVER_INSTANCE);

        if (legacy) {
            buff.append((String)configuration.get(FF_BASE_DIR));
        } else if (protocol.equals(PROTOCOL_FILE)) {
            buff.append(PROTOCOL_FILE);
            buff.append((String)configuration.get(BASE_DIR));
            buff.append("/");
            buff.append(URLEncoder.encode(serverInstance, "UTF-8"));
            buff.append("?");
            buff.append(PWD);
            buff.append("=");
            buff.append(URLEncoder.encode(pwd, "UTF-8"));
            buff.append("&");
            buff.append(FF_BASE_DIR);
            buff.append("=");
            buff.append(URLEncoder.encode(
                (String)configuration.get(FF_BASE_DIR), "UTF-8"));
        } else {
            buff.append(PROTOCOL_LDAP);
            buff.append((String)configuration.get(DS_HOST));
            buff.append(":");
            buff.append((String)configuration.get(DS_PORT));
            buff.append("/");
            buff.append(URLEncoder.encode(serverInstance, "UTF-8"));
            buff.append("?");
            buff.append(PWD);
            buff.append("=");
            buff.append(URLEncoder.encode(pwd, "UTF-8"));
            
            String embeddedDS = (String)configuration.get(EMBEDDED_DS);
            
            if ((embeddedDS != null) && (embeddedDS.length() > 0)) {
                buff.append("&");
                buff.append(EMBEDDED_DS);
                buff.append("=");
                buff.append(URLEncoder.encode(embeddedDS, "UTF-8"));
            }
            buff.append("&");
            buff.append(DS_BASE_DN);
            buff.append("=");
            buff.append(URLEncoder.encode((String)configuration.get(
                DS_BASE_DN), "UTF-8"));
            buff.append("&");
            buff.append(DS_MGR);
            buff.append("=");
            buff.append(URLEncoder.encode((String)configuration.get(
                DS_MGR), "UTF-8")); 
            buff.append("&");
            buff.append(DS_PWD);
            buff.append("=");
            buff.append(URLEncoder.encode(
                Crypt.encode((String)configuration.get(DS_PWD),
                    Crypt.getHardcodedKeyEncryptor()), "UTF-8")); 
        }
        return buff.toString();
    }

    private void readFile(String file) 
        throws IOException
    {
        BufferedReader in = null;
        
        try {
            in = new BufferedReader(new FileReader(file));
            if (in.ready()) {
                String str = in.readLine();

                while (str != null) {
                    str = str.trim();
                    if ((str.length() > 0) && !str.startsWith("#")) {
                        data.add(str);
                    }
                    str = in.readLine();
                }
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private static final String BOOTSTRAP_SERVER_START_TAG =
        "<iPlanetDataAccessLayer>";
    private static final String BOOTSTRAP_SERVER_END_TAG =
        "</iPlanetDataAccessLayer>";
    private static final String BOOTSTRAP_SERVER_CONFIG_USER =
        "<ServerGroup name=\"default\" minConnPool=\"1\" maxConnPool=\"1\">" +
        "<Server name=\"Server1\" host=\"@" + DS_HOST + "@\" " +
        "port=\"@" + DS_PORT + "@\" type=\"SIMPLE\" />" +
        "<User name=\"User1\" type=\"admin\">" +
        "<DirDN>cn=dsameuser,ou=DSAME Users,@" + DS_BASE_DN + "@</DirDN>" +
        "<DirPassword>@" + PWD + "@</DirPassword>" +
        "</User>" +
        "<BaseDN>@" + DS_BASE_DN + "@</BaseDN>" +
        "</ServerGroup>";
    private static final String BOOTSTRAP_SERVER_CONFIG_LDAP_SVR = 
        "<ServerGroup name=\"sms\" " +
             "minConnPool=\"1\" maxConnPool=\"10\">" +
        "@SERVER_ENTRY@" + 
        "<User name=\"User2\" type=\"admin\">" +
        "<DirDN>@" + DS_MGR + "@</DirDN>" +
        "<DirPassword>@" + DS_PWD + "@</DirPassword>" +
        "</User>" +
        "<BaseDN>@" + DS_BASE_DN + "@</BaseDN>" +
        "</ServerGroup>";
    private static final String BOOTSTRAP_SERVER_CONFIG_LDAP_SVR_ENTRY = 
        "<Server name=\"Server@counter@\" host=\"@" + DS_HOST + "@\" " +
        "port=\"@" + DS_PORT + "@\" type=\"SIMPLE\" />";
}

