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
 * $Id: DBFormatter.java,v 1.3 2006-04-27 07:53:31 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */



package com.sun.identity.log.handlers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import com.sun.identity.log.LogConstants;
import com.sun.identity.log.LogManagerUtil;
import com.sun.identity.log.spi.Debug;
import com.sun.identity.log.spi.ITimestampGenerator;

/**
 * This Formatter provides support for formatting LogRecords that will help
 * Database Logging.
 * <p>
 * Typically this Formatter will be associated with a DBHandler(a handler meant
 * to handle Database logging). <tt> DBFormatter </TT> takes a LogRecord and
 * converts it to a Formatted string which DBHandler can understand.
 *
 */
public class DBFormatter extends Formatter {
    
    private LogManager lmanager = LogManagerUtil.getLogManager();
    private String [] allFields;
    private ITimestampGenerator secureTimestampGenerator;

    private final String NOTAVAIL = "Not Available";
    
    /**
     * Creates <code>DBFormatter</code> object
     */
    public DBFormatter() {
        String timestampGeneratorClass = 
            lmanager.getProperty(LogConstants.SECURE_TIMESTAMP_GENERATOR);
        try {
            Class clz = Class.forName(timestampGeneratorClass);
            secureTimestampGenerator = (ITimestampGenerator)clz.newInstance();
        } catch (ClassNotFoundException cnfe) {
            Debug.error("DBFormatter: TimeStamp Generator Class " +
                "not found", cnfe);
        } catch (InstantiationException ie) {
            Debug.error("DBFormatter: Timestamp Generator Could not " +
                "be Instantiated", ie);
        } catch (IllegalAccessException iae) {
            Debug.error("DBFormatter: Timestamp Generator Could not " +
                "be Instantiated", iae);
        }
    }

    /**
     * Returns the set of all fields converted into a COMMA seperated 
     * string. A typical sql query for logging a record looks like this. <p>
     * insert into table "amSSO_access" (time, data, loginid, domain, level,
     * ipAddress, hostname) values('10:10:10', '10th June, 2002',
     * ..., ..., ...)<p>
     * The getHead method returns back the set of all fields converted into a
     * COMMA seperated string. It is the duty of the formatter to fetch the all
     * field set from the LogManager and convert into a COMMA seperated string.
     * By doing this the handler can be kept independent of the all field and
     * selected field set.
     *
     * @param h The target handler (can be null)
     * @return the set of all fields converted into a COMMA seperated string.
     */
    public String getHead(Handler h) {
        String retString = lmanager.getProperty(LogConstants.ALL_FIELDS);
        if (Debug.messageEnabled()) {
            Debug.message("DBFormatter: Returned String from getHead is " 
                + retString);
        }
        return retString;
    }
    
    /**
     * Returns a null string whenever called.
     * @param h The target handler (can be null)
     * @return a null string whenever called.
     */
    public String getTail(Handler h) {
        return "";
    }
    
    /**
     * Format the given LogRecord and return back a formatted String.
     * <p>
     * The formatted String has the values of the fields which are selected and
     * NULL if any field is not selected. All fields are enclosed in single-
     * quotes.
     * <p>
     * A typical formatted string can be given as follows:
     * '10:10:10', '10th June, 2002', 'NULL', 'NULL',
     * 'Session Created Successfull', 'INFO', 'NULL', 'NULL'
     * <p>
     * This formatted string will be enclosed within braces by Handler to
     * construct the query string.
     * 
     * @param logRecord the log record to be formatted.
     * @return formatted string.
     */
    public String format(java.util.logging.LogRecord logRecord) {
        getAllFields();
        StringBuffer sbuffer = new StringBuffer();
        Map logInfoTable = 
            ((com.sun.identity.log.LogRecord)logRecord).getLogInfoMap();
        Set selectedFields = getSelectedFieldSet();
        String strTime = "";
        if(secureTimestampGenerator != null) {
            strTime = secureTimestampGenerator.getTimestamp();
        }
        
        sbuffer.append("'").append(strTime).append("', ");
        
        /* Need to check for single-quote in the DATA field to be written 
         * to the db
         */
        String tstr = formatMessage(logRecord);
        if ((tstr.length() > 0 ) && (tstr.indexOf("'") != -1)) {
            StringTokenizer tmps = new StringTokenizer(tstr, "'");
            StringBuffer thisfield = new StringBuffer();
            if (Debug.messageEnabled()) {
                Debug.message("DBFormatter:found single-quote in data: " +tstr);
            }
            
            /*
             * Weird cases of "'" at beginning or end of the data
             */
            if (tstr.indexOf("'") == 0) {
                thisfield.append("''");
                if (tmps.hasMoreTokens()) {
                    thisfield.append(tmps.nextToken());
                }
                if (Debug.messageEnabled()) {
                    Debug.message("DBFormatter:thisfield1 = #" + 
                        thisfield.toString() + "#");
                }
            } else {
                if (tmps.hasMoreTokens()) {
                    thisfield.append(tmps.nextToken());
                }
            }
            while (tmps.hasMoreTokens()) {
                thisfield.append("''").append(tmps.nextToken());
                if (Debug.messageEnabled()) {
                    Debug.message("DBFormatter:thisfield2 = #" + 
                                      thisfield.toString() + "#");
                }
            }

            /*
             *  See if it ends in "'"
             */
            if (tstr.indexOf("'", tstr.length()-1) != -1) {
                thisfield.append("''");
            }

            tstr = thisfield.toString();
        }
        sbuffer.append("'").append(tstr).append("', ");
        if (Debug.messageEnabled()) {
            Debug.message("DBFormatter:thisfield3 = #" + sbuffer.toString() 
                          + "#");
        }

        int len = allFields.length;
        for (int i = 2; i < len-1; i ++) { // first 2 fields are compulsory
            if ((logInfoTable != null) &&
            (selectedFields != null) &&
            (selectedFields.contains(allFields[i]))) {
                //
                // if there are any single-quotes in the data, they have to be
                // made double-single-quotes, so it'll pass through sql
                //
                String tempstr = (String)logInfoTable.get(allFields[i]);
                if ((tempstr != null) &&
                    (tempstr.length() > 0) &&
                    (tempstr.indexOf("'") != -1)
                ) {
                    StringTokenizer tmps = new StringTokenizer(tempstr, "'");
                    StringBuffer thisfield = new StringBuffer();
                    if (Debug.messageEnabled()) {
                        Debug.message("DBFormatter:found single-quote in: " 
                                      + tempstr);
                    }
                    //
                    // funky case of "'" at the beginning
                    //
                    if (tempstr.indexOf("'") == 0) {
                        thisfield.append("''");
                        if (tmps.hasMoreTokens()) {
                            thisfield.append(tmps.nextToken());
                        }
                    } else {
                        if (tmps.hasMoreTokens()) {
                            thisfield.append(tmps.nextToken());
                        }
                    }

                    while (tmps.hasMoreTokens()) {
                        thisfield.append("''").append(tmps.nextToken());
                    }
                    //
                    // if string ends in "'"
                    //
                    if (tempstr.indexOf("'", tempstr.length()-1) != -1) {
                        thisfield.append("''");
                    }
                    tempstr = thisfield.toString();
                }
                if (tempstr == null) {
                    tempstr = NOTAVAIL;
                }
                sbuffer.append("'").append(tempstr).append("', ");
            } else {
                sbuffer.append("'" + NOTAVAIL + "'").append(", ");
            }
        }

        if (Debug.messageEnabled()) {
            Debug.message("DBFormatter:format1: sbuffer = " 
                          + sbuffer.toString());
        }

        if (selectedFields.contains(allFields[len-1])) {
            String tmpstr = (String)logInfoTable.get(allFields[len-1]);
            if (tmpstr == null) {
                tmpstr = NOTAVAIL;
            }
            sbuffer.append("'").append(tmpstr).append("'");
        } else {
            sbuffer.append("'" + NOTAVAIL + "'");
        }

        if (Debug.messageEnabled()) {
            Debug.message("DBFormatter:format2: sbuffer = " 
                    + sbuffer.toString());
        }

        return sbuffer.toString();
    }
    
    private void getAllFields() {
        String strAllFields = lmanager.getProperty(LogConstants.ALL_FIELDS);
        StringTokenizer strToken = new StringTokenizer(strAllFields, ", ");
        int count = strToken.countTokens();
        allFields = new String[count];
        count = 0;
        while(strToken.hasMoreElements()) {
            allFields[count++] = strToken.nextToken().trim();
        }
        String temp = "";
        for ( int i = 0; i < count; i ++ ) {
            temp += allFields[i] + "\t";
        }
    }
    
    private Set getSelectedFieldSet() {
        Set selectedFields = new HashSet();
        String strSelectedFields = 
               lmanager.getProperty(LogConstants.LOG_FIELDS);
        StringTokenizer stoken = new StringTokenizer(strSelectedFields, ", ");
        String temp = "", temp1 ="";
        while(stoken.hasMoreElements()) {
            temp1 = stoken.nextToken();
            if (Debug.messageEnabled()) {
                Debug.message("DBFormatter:getSelectedFieldSet: temp1 = " 
                    + temp1);
            }
            selectedFields.add(temp1);
            temp += temp1 + "\t";
        }
        return selectedFields;
    }
}
