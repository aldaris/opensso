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
 * $Id: InfocardStorageImpl.java,v 1.2 2008-02-28 23:31:20 superpat7 Exp $
 *
 * Copyright 2008 Sun Microsystems Inc. All Rights Reserved
 * Portions Copyrighted 2008 Patrick Petit Consulting
 */

package com.identarian.infocard.opensso.db;

import com.identarian.infocard.opensso.exception.InfocardException;
import com.identarian.infocard.opensso.rp.Infocard;
import com.sun.identity.shared.debug.Debug;
import java.sql.*;

public class InfocardStorageImpl implements InfocardStorage {

    // define the driver to use 
    private static final String driver = "org.apache.derby.jdbc.ClientDriver";
    // the database name  
    private static final String dbName = "jdbcInfocardDB";
    // define the Derby connection URL to use 
    private static final String connectionURL = "jdbc:derby://localhost:1527/" + dbName + ";create=true";
    // TODO PPID is used as the key, but we (should?) use the card id instead
    private static final String createString = "create table storecreds (ppid varchar(64) NOT NULL CONSTRAINT ACCOUNT_PK PRIMARY KEY," +
            "userid varchar(64)," +
            "password varchar(64))";
    private static final String searchString = "SELECT * FROM storecreds where ppid = ?";
    private static Debug debug = null;
    private static Connection conn = null;
    private static boolean initialized = false;

    public InfocardStorageImpl() {

        if (debug == null) {
            debug = com.sun.identity.shared.debug.Debug.getInstance(Infocard.amAuthInfocard);
            debug.message("InfocardStorage initialization");
        }
    }

    public void startup()
            throws InfocardException {

        //   Beginning of JDBC code sections   
        //   ## LOAD DRIVER SECTION ##
        if (initialized == false) {
            debug.message("starting up" + dbName + " database...");
            try {
                // Load the Derby driver.
                Class.forName(driver);
                debug.message("driver " + driver + " loaded. ");
            } catch (java.lang.ClassNotFoundException cnfe) {
                debug.error(driver + " class not found", cnfe);
                throw new InfocardException(driver + " class not found", cnfe);
            }

            try {
                // Create (if needed) and connect to the database
                conn = DriverManager.getConnection(connectionURL);
                debug.message("Connected to database " + dbName);
                createStoredCredsTable();
            } catch (SQLException sqle) {
                throw new InfocardException("Failed to load JDBC driver", sqle);
            }
        }
    }

    public void shutdown()
            throws InfocardException {
        if (conn == null) {
            return;
        }

        try {
            conn.close();
            debug.message("database " + dbName + " shutdown");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InfocardException(e);
        }
    }

    public void addCredentials(StoredCredentials creds)
            throws InfocardException {

        if (conn == null) {
            startup();
        }

        Statement s = null;
        try {
            s = conn.createStatement();

            try {

                // store ppid, userID and password
                // TODO: WE SHOULD encrypt the password!
                StringBuffer insert = new StringBuffer("insert into storecreds values ('");
                insert.append(creds.getPpid());
                insert.append("', '");
                insert.append(creds.getUserID());
                insert.append("', '");
                insert.append(creds.getUserPasswd());
                insert.append("')");
                if (debug.messageEnabled()) {
                    debug.message(insert.toString());
                }
                s.execute(insert.toString());
                s.close();
                conn.commit();

            } catch (SQLException e) {
                e.printStackTrace();
                throw new InfocardException("Card creation failed", e);
            } finally {
                s.close();
                conn.commit();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InfocardException(e);
        }
    }

    public StoredCredentials findCredentials(String ppid)
            throws InfocardException {

        StoredCredentials creds = null;

        if (conn == null) {
            startup();
        }

        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(searchString);
            try {

                pstmt.setString(1, ppid);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    creds = new StoredCredentials();
                    creds.setPpid(rs.getString(1));
                    creds.setUserID(rs.getString(2));
                    creds.setUserPasswd(rs.getString(3));
                }
                rs.close();
                pstmt.close();
                conn.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new InfocardException(e);
            } finally {
                pstmt.close();
                conn.commit();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new InfocardException(e);
        }
        return creds;
    }

    //      Check for  STOREDCREDS table
    private static void createStoredCredsTable()
            throws SQLException {

        Statement s = conn.createStatement();
        conn.setAutoCommit(false);

        try {

            ResultSet rs = null;
            rs = s.executeQuery("SELECT init FROM initialized");
            if (rs.next()) {
                if (debug.messageEnabled()) {
                    debug.message(dbName + " is already initialized");
                }
                initialized = true;
            }
        } catch (SQLException sqle) {
            String theError = (sqle).getSQLState();
            //   System.out.println("  Utils GOT:  " + theError);
            /** If table exists will get -  WARNING 02000: No row was found **/
            if (theError.equals("42X05")) { // Table does not exist
                debug.message("creating table " + dbName);
                s.execute(createString);
                s.execute("create table initialized(init int, version int)");
                s.execute("insert into initialized values (1, 1)");
                debug.message("Table " + dbName + "initialized");
                initialized = true;
            } else {
                // unhandled exceptions
                sqle.printStackTrace();
                debug.error("Unhandled SQLException", sqle);
                throw sqle;
            }
        } finally {
            s.close();
            conn.commit();
        }
    }
}
