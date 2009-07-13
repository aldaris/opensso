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
 * $Id:
 *
 * Copyright 2009 Sun Microsystems Inc. All Rights Reserved
 */
package com.sun.identity.qatest.qaweb.common;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;

/**
 * This class contains all commonly used methods.
 */
public class QawebCommon {

    /**
     * Creates File from a map of properties and a map of comments.
     * It writes the comments and the corresponding property key and value.
     */
    public void createFileFromMap(Map properties, Map comments, String fileName)
            throws Exception {
        StringBuffer buffer = new StringBuffer();
        for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            for (Iterator j = comments.entrySet().iterator(); j.hasNext();) {
                Map.Entry entryComments = (Map.Entry) j.next();
                if (entry.getKey().equals(entryComments.getKey())) {
                    if (entryComments.getValue().toString().length() != 0) {
                        if (entryComments.getValue().toString().length() > 80) {
                            int noOfloops = entryComments.getValue().
                                    toString().length() / 80;
                            int n = 0;
                            int p = 80;
                            for (int m = 0; m <= noOfloops; m++) {
                                if (m == noOfloops) {
                                    String temp = entryComments.getValue().
                                            toString().substring(n);
                                    buffer.append("#");
                                    buffer.append(temp);
                                    buffer.append("\n");
                                } else {
                                    String temp = entryComments.getValue().
                                            toString().substring(n, p);
                                    buffer.append("#");
                                    buffer.append(temp);
                                    n = n + 80;
                                    p = p + 80;
                                    buffer.append("\n");
                                }
                            }
                        } else {
                            buffer.append("#");
                            buffer.append(entryComments.getValue().toString());
                            buffer.append("\n");
                        }
                    }
                }
            }
            buffer.append("\n");
            String valueString = entry.getValue().toString();
            buffer.append(entry.getKey());
            buffer.append("=");
            if (valueString.length() != 0) {
                buffer.append(valueString.substring(0, valueString.length()));
                buffer.append("\n");
            } else {
                buffer.append("\n");
            }
            buffer.append("\n");
        }
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
        out.write(buffer.toString());
        out.close();
    }

    /**
     * Gets a Map object from a  property file.
     */
    public Map getMapFromProperties(String propName, String str, Set set)
            throws Exception {
        Map map = new HashMap();
        FileInputStream fis = new FileInputStream(propName);
        Properties prop = new Properties();
        prop.load(fis);
        for (Enumeration e = prop.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = (String) prop.get(key);
            if (set != null) {
                if (!set.contains(key)) {
                    if (str != null) {
                        if (key.indexOf(str) != -1) {
                            map.put(key, value);
                        }
                    } else {
                        map.put(key, value);
                    }
                }
            } else {
                if (str != null) {
                    if (key.indexOf(str + ".") != -1) {
                        map.put(key, value);
                    }
                } else {
                    map.put(key, value);
                }
            }
        }
        return (map);
    }

    /**
     * Gets a Map object from a  property file.
     */
    public Map getMapFromProperties(String propName)
            throws Exception {
        return (getMapFromProperties(propName, null, null));
    }

    /**
     * This method verifies if the server details provided by the user are for a
     * preconfigured server or are the details of new server configuration.
     */
    public String configureProduct(String serverURL, String username,
            String password)
            throws Exception {
        WebClient webclient = new WebClient();
        String configError = null;
        serverURL = serverURL.substring(0, serverURL.indexOf("/namingservice"));
        String strURL = serverURL + "/config/options.htm";
        URL url = new URL(strURL);
        HtmlPage page = null;
        int pageIter = 0;
        try {
            /* THIS WHILE IS WRITTEN BECUASE IT TAKES SOME TIME FOR INITIAL
            CONFIGURATOR PAGE TO LOAD AND WEBCLIENT CALL DOES NOT WAIT
            FOR SUCH A DURATION.
             */
            while (page == null && pageIter <= 30) {
                try {
                    page = (HtmlPage) webclient.getPage(url);
                    Thread.sleep(10000);
                    pageIter++;
                } catch (com.gargoylesoftware.htmlunit.ScriptException e) {
                    e.printStackTrace();
                    configError = "URL is not reachable";
                    return configError;
                }
            }
        } catch (com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException e)
        {
            e.printStackTrace();
            configError = "URL is not reachable";
            return configError;
        }
        if (pageIter > 30) {
            configError = "URL is not reachable";
            return configError;
        }
        if (getHtmlPageStringIndex(page, "Not Found") != -1) {
            configError = "URL is not reachable";
            return configError;
        }
        if (getHtmlPageStringIndex(page, "configuration") != -1) {
            return configError;
        //New Configuration...move ahead
        } else {
            WebClient webClient = new WebClient();
            String resource = serverURL + "/UI/Login";
            HtmlPage page1 = consoleLogin(webClient, resource, username,
                    password);
            int iIdx = getHtmlPageStringIndex(page1, "Authentication Failed");
            if (iIdx != -1) {
                configError = "Authentication Failed";
            }
            return configError;
        }
    }

    /**
     * Checks whether the string exists on the page
     */
    protected int getHtmlPageStringIndex(
            HtmlPage page,
            String searchStr)
            throws Exception {
        String strPage;
        try {
            strPage = page.asXml();
        } catch (java.lang.NullPointerException npe) {
            npe.printStackTrace();
            return 0;
        }
        int iIdx = strPage.indexOf(searchStr);
        return iIdx;
    }

    /**
     * This method validates amadmin userid and password.
     */
    protected HtmlPage consoleLogin(
            WebClient webclient,
            String amUrl,
            String amadmUser,
            String amadmPassword)
            throws Exception {
        HtmlPage page = (HtmlPage) webclient.getPage(amUrl);
        HtmlForm form = page.getFormByName("Login");
        HtmlHiddenInput txt1 =
                (HtmlHiddenInput) form.getInputByName("IDToken1");
        txt1.setValueAttribute(amadmUser);
        HtmlHiddenInput txt2 =
                (HtmlHiddenInput) form.getInputByName("IDToken2");
        txt2.setValueAttribute(amadmPassword);
        page = (HtmlPage) form.submit();
        return (page);
    }

    /**
     * Validates the LDAP Connection.
     */
    public String validateSMHost(String host, int port, String bindDN,
            String bindPwd, String rootSuffix) {
        LDAPConnection ld = null;
        String ldapError = null;
        try {
            ld = new LDAPConnection();
            ld.setConnectTimeout(300);
            ld.connect(3, host, port, bindDN, bindPwd);
            String filter = "cn=" + "\"" + rootSuffix + "\"";
            String[] attrs = {""};
            ld.search(rootSuffix, LDAPConnection.SCOPE_BASE, filter,
                    attrs, false);
        } catch (LDAPException lex) {
            switch (lex.getLDAPResultCode()) {
                case LDAPException.CONNECT_ERROR:
                    ldapError = "CONNECT_ERROR";
                    break;
                case LDAPException.SERVER_DOWN:
                    ldapError = "LDAP SERVER DOWN";
                    break;
                case LDAPException.INVALID_DN_SYNTAX:
                    ldapError = "LDAP INVALID DN";
                    break;
                case LDAPException.NO_SUCH_OBJECT:
                    ldapError = "LDAP NO SUCH OBJECT";
                    break;
                case LDAPException.INVALID_CREDENTIALS:
                    ldapError = "LDAP INVALID CREDENTIALS";
                    break;
                case LDAPException.UNWILLING_TO_PERFORM:
                    ldapError = "LDAP UNWILLING";
                    break;
                case LDAPException.INAPPROPRIATE_AUTHENTICATION:
                    ldapError = "LDAP INAPPROPRIATE";
                    break;
                case LDAPException.CONSTRAINT_VIOLATION:
                    ldapError = "LDAP CONSTRAINT";
                    break;
                default:
                    ldapError = "CANNOT CONNECT TO SM DATASTORE";
            }
        } catch (Exception e) {
            e.printStackTrace();
            ldapError = "CANNOT CONNECT TO SM DATASTORE";
        } finally {
            if (ld != null) {
                try {
                    ld.disconnect();
                } catch (LDAPException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return ldapError;
    }

    /**
     * Validates If the port is being used or is free.
     */
    public boolean canUseAsPort(String hostname, int port)
            throws Exception {
        boolean canUseAsPort = false;
        ServerSocket serverSocket = null;
        try {
            InetSocketAddress socketAddress =
                    new InetSocketAddress(hostname, port);
            serverSocket = new ServerSocket();
            serverSocket.bind(socketAddress);
            canUseAsPort = true;
            serverSocket.close();
            Socket s = null;
            try {
                s = new Socket();
                s.connect(socketAddress, 1000);
                canUseAsPort = false;
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (s != null) {
                    try {
                        s.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            canUseAsPort = false;
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return canUseAsPort;
    }

    /**
     * This method creates a test database and returns connection for this
     * database.
     * @param dbUserName Database user Name.
     * @param password Database user Password.
     * @param driver Database Driver to be used.
     * @param connectionURL connectionURL of the database.
     */
    public static String getConnection(String dbUserName, String password,
            String driver, String connectionURL) {
        try {

           int lastIdx = connectionURL.lastIndexOf("/");
            String reqDBURL= connectionURL.substring(0, lastIdx);
            Class.forName(driver);
            Connection con = DriverManager.getConnection(reqDBURL,
                    dbUserName, password);

            con.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Database couldnot be connected";
        }
        return null;
    }


    

    /**
     * Deletes the database
     * @param con Connection to the database.
     * @param dataBaseName Name of the database to be deleted.
     * @return true if the database deleted.
     */
    public static boolean deleteDB(Connection con, String dataBaseName)
    throws Exception {
        try {
            Statement createSt = con.createStatement();

            return createSt.execute("DROP DATABASE " + dataBaseName);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * This method verifies if the URL is accessible
     */
    public String accessURL(String webURL) {
            String urlAccessError = null;
        try {
            WebClient webclient = new WebClient();
            URL url = new URL(webURL);
            HtmlPage page = null;
            int pageIter = 0;

            /* THIS WHILE IS WRITTEN BECUASE IT TAKES SOME TIME FOR INITIAL
            CONFIGURATOR PAGE TO LOAD AND WEBCLIENT CALL DOES NOT WAIT
            FOR SUCH A DURATION.
             */
            while (page == null && pageIter <= 30) {
                try {
                    page = (HtmlPage) webclient.getPage(url);
                    Thread.sleep(10000);
                    pageIter++;
                } catch (com.gargoylesoftware.htmlunit.ScriptException e) {
                    e.printStackTrace();
                   // urlAccessError = "URL is not reachable";
                    return e.getMessage();
                }
            }

            if (pageIter > 30) {
                urlAccessError = "URL is not reachable";
                return urlAccessError;
            }

            if (getHtmlPageStringIndex(page, "Not Found") != -1) {
                urlAccessError = "URL is not reachable";
                return urlAccessError;
            }
        } catch (com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException e)
        {
            e.printStackTrace();
            urlAccessError = "URL is not reachable";
            return e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            urlAccessError = "URL is not reachable";
            return e.getMessage();
        }
        return null;

    }

    /**
     * This method validates if the file or directory exists
     */
    public String isFileExists(String FileName){
        File file = new File(FileName);
         if (!file.exists()) {             
             return "File Not Reachable" ;
         }else {             
        return null;
         }
    }

    /**
     * This method validates if the file or directory is writable
     */
    public String isFileWritable(String FileName){
        File file = new File(FileName);
         if (!file.canWrite()) {
             return "File Not Writable" ;
         }else {
        return null;
         }
    }

}
