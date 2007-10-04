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
 * $Id: LEPStart.java,v 1.1 2007-10-04 16:55:29 hengming Exp $
 *
 * Copyright 2007 Sun Microsystems Inc. All Rights Reserved
 */


import java.io.*;
import java.net.*;

public class LEPStart {
   public static void main(String[] args) {
       ServerSocket serverSocket = null;
       Socket clientSocket = null;
       int hport = 8090;
       try {
          hport = Integer.parseInt(args[0]);
       } catch (Exception e) {
          //You might want to catch a NumberFormatException here,
          //or do some more sophisticated command line parsing.
       }
       try {
                serverSocket = new ServerSocket(hport);
       } catch (IOException e) {
                System.out.println("Could not listen on port: " + hport);
                System.exit(-1);
       }
       while(true) {
            clientSocket = null;
            try {
                System.out.println("Accept Requests on port: " + hport);
                clientSocket = serverSocket.accept();     
                LEP lep = new LEP(clientSocket);
                lep.start();
            } catch (IOException e) {
                System.out.println("Accept failed: " + hport);
                System.exit(-1);
            }
        }
   }
}
