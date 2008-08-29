/**
 * $Id: UnixHelper.java,v 1.1 2008-08-29 22:06:40 kevinserwin Exp $
 * Copyright © 2005 Sun Microsystems, Inc.  All rights reserved.
 *
 * Sun Microsystems, Inc. has intellectual property rights relating to
 * technology embodied in the product that is described in this document.
 * In particular, and without limitation, these intellectual property rights
 * may include one or more of the U.S. patents listed at
 * http://www.sun.com/patents and one or more additional patents or pending
 * patent applications in the U.S. and in other countries.
 *
 * U.S. Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and applicable
 * provisions of the FAR and its supplements.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.Sun,
 * Sun Microsystems and  the Sun logo are trademarks or registered trademarks
 * of Sun Microsystems, Inc. in the U.S. and other countries.  
 *
 * Copyright © 2005 Sun Microsystems, Inc. Tous droits réservés.
 * Sun Microsystems, Inc. détient les droits de propriété intellectuels relatifs
 * à la technologie incorporée dans le produit qui est décrit dans ce document.
 * En particulier, et ce sans limitation, ces droits de propriété
 * intellectuelle peuvent inclure un ou plus des brevets américains listés
 * à l'adresse http://www.sun.com/patents et un ou les brevets supplémentaires
 * ou les applications de brevet en attente aux Etats - Unis et dans les
 * autres pays.
 *
 * L'utilisation est soumise aux termes du contrat de licence.
 *
 * Cette distribution peut comprendre des composants développés par des
 * tierces parties.
 *
 * Sun,  Sun Microsystems et  le logo Sun sont des marques de fabrique ou des
 * marques déposées de Sun Microsystems, Inc. aux Etats-Unis et dans
 * d'autres pays.
 */



package com.sun.identity.authentication.modules.unix;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import com.sun.identity.authentication.spi.AuthenticationException;
import com.iplanet.am.util.*;

public class UnixHelper {
	protected static final int DAEMON_TIMEOUT_mS = 7500;
	private final int MAXLOOP = 200; // maximum loop allowed for do...while

	private Socket sock = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;
	private static final String charSet = "ISO8859_1";
	public static Debug debug;

	public UnixHelper(int port, String bundleName) throws AuthenticationException
	{
		debug = Debug.getInstance("amUnixHelper");
		debug.message("unix helper...init");
		try {
			sock = new Socket("127.0.0.1", port);
			sock.setSoTimeout(DAEMON_TIMEOUT_mS);
			reader = new BufferedReader(new InputStreamReader(sock.getInputStream(), charSet));
			writer = new PrintWriter (new BufferedWriter (
					new OutputStreamWriter(sock.getOutputStream(), charSet)));
		} catch (UnknownHostException e) {
			throw new AuthenticationException(bundleName, 
			"UnixHelperLocalhost", null);
		} catch (IOException ex) {
			throw new AuthenticationException(bundleName, 
			"UnixHelperIOEx", null);
		}
	}

	protected synchronized int do_write(String cmd)
	{
		writer.println(cmd);		// send the command
		writer.flush();				// flush buffer
		return cmd.length();
	}

	public String do_read (int readsize, ResourceBundle bundle) throws IOException {
		int i;
		char buf[] = new char[254];
		String readstring;

		debug.message("in do_read...");
		try {
			i = reader.read(buf, 0, readsize);
		} catch (IOException ioex) {
			throw ioex;
		}
		readstring = new String (buf);
		try {
			if (!readstring.equals(new String(readstring.getBytes("ASCII"), "ASCII"))) {
				throw new IOException(bundle.getString ("UnixHelperInputNotASCII"));
			}
		} catch (UnsupportedEncodingException ueex) {
			debug.message("Unsupported coding ...");
			throw new IOException(bundle.getString ("UnixHelperInputEncodingException"));
		}
		debug.message("returng... readString... " + readstring);
		return readstring;
	}


	public int configHelper (String helper_port, String helper_timeout,
			String helper_threads, com.iplanet.am.util.Debug debug, ResourceBundle bundle)
	{
		String instring;
		int i;

		//
		//  should get this sequence:
		//  Enter Unix Helper Listen Port [7946]:  
		//  Enter Unix Helper Session Timeout [3]:  
		//  Enter Unix Helper Max Sessions [5]:  
		//  get_config_info: amunixd configured successfully
		//
		
		try {
			instring = do_read(254, bundle);
		} catch (IOException ex) {
			return -1;
		}

		if (instring.startsWith("Enter Unix Helper Listen Port")) {
			i = do_write (helper_port);
		} else {
			return -2;
		}

		try {
			instring = do_read(254, bundle);
		} catch (IOException ex) {
			return -3;
		}

		if (instring.startsWith("Enter Unix Helper Session Timeout")) {
			i = do_write (helper_timeout);
		} else {
			return -4;
		}

		try {
			instring = do_read(254, bundle);
		} catch (IOException ex) {
			return -5;
		}

		if (instring.startsWith("Enter Unix Helper Max Sessions")) {
			i = do_write (helper_threads);
		} else {
			return -6;
		}

		try {
			instring = do_read(254, bundle);
		} catch (IOException ex) {
			return -7;
		}

		if (instring.startsWith("get_config_info: amunixd configured successfully")) {
		} else {
			return -8;
		}

		return 0;
	}

	// first screen of the Unix login
	// authenticate user
	// return result:
	//	0  : authenticate pass
	//	-1 : failed
	// 	k  : goto corresponding next screen
	public int authenticate (String userlogin, 
				 String userpass, 
				 String serviceModule,
				 String clientIPAddr,
				 ResourceBundle bundle) {

		int i, k;		
		String instring;
		final int MAXSCREEN = 1000;
		int maxloop = MAXLOOP;
	
		k = MAXSCREEN;
		if (debug.messageEnabled()) {
		   debug.message("authenticate.....userlogin" + userlogin);
		   debug.message("authenticate.....serviceModule" + serviceModule);
		}
		do {
			instring = "";
			try {
				debug.message("calling do_read");
				instring = do_read(254, bundle);
				debug.message("after do_read");
			} catch (IOException ex) {
				return -1;
			}

			if ( instring.length() == 0 ) {
		 	 	return -1;
			}

			debug.message("Instring is.. : " + instring);
			if (instring.startsWith("Enter Unix login:")) {
				i = do_write (userlogin);
				k = MAXSCREEN;
			} else if (instring.startsWith("Enter password:")) {
				i = do_write (userpass);
				k = MAXSCREEN;
			} else if (instring.startsWith("Enter Service Name :")) {
				debug.message("writing service name");
				i=do_write(serviceModule);
				debug.message("after writing service name" + i);
				k=MAXSCREEN;
			} else if (instring.startsWith("Enter Client IP Address:")) {
				if (clientIPAddr != null) {
				    i = do_write (clientIPAddr);
				} else {
				    i = do_write("0.0.0.0");
				}
				k = MAXSCREEN;
			} else if (instring.startsWith("Authentication passed")){
				k = 0;
			} else if (instring.startsWith("Access denied")) {
				k = -1;
			} else if (instring.startsWith("unknown return code ")) {
				k = -1;
			} else if (instring.startsWith("Processing timed-")) {
				k = -1;
			} else if (instring.startsWith("Processing erro")) {
				k = -1;
			} else if (instring.startsWith("Authentication Failed")) {
				k = -1;
				if (instring.indexOf("Password Expired") != -1) {
				    debug.message ("password expired for " + userlogin);
				    k = 2;
				}
			} else {
				k = MAXSCREEN;
			}

			maxloop--;
			if (maxloop == 0) 
				k = -1;

		} while (k == MAXSCREEN);

		debug.message("returning... k from authenticate" + k);
		return k;
	}

	protected synchronized void destroy(ResourceBundle bundle) {
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
				writer = null;
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
			if (sock != null) {
				sock.close();
				sock = null;
			}
		} catch (IOException e) {
			System.err.println(bundle.getString("UnixDestroyIOEx"));
			System.exit(1);
		} catch (Exception ee) {
			System.err.println(bundle.getString("UnixDestroyEx") + ee.getMessage());
		}
	}
}

