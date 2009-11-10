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
 * $Id: ParseInputData.java,v 1.1 2009-11-10 08:37:28 mrudul_uchil Exp $
 *
 */

package com.sun.identity.setup;

import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Parse input data and write the parsed data into the required output file.
 */
public class ParseInputData {
    
    private ParseInputData() {
    }
    
    public static void main(String[] args) throws Exception {
        FileOutputStream os = new FileOutputStream(args[1]);
        PrintWriter out = new PrintWriter(os);
        String warFileLocation = args[0];
        String newWarFileLocation = warFileLocation.trim().replace("\\", "/");
        String warFileName = null;
        int index = newWarFileLocation.lastIndexOf("/");
        if (index != -1) {
            int indexWar = newWarFileLocation.indexOf(".war");
            if (indexWar != -1) {
                warFileName = newWarFileLocation.substring(index+1,indexWar);
                out.write("war.exist=yes" + "\n");
            }
        }
        int indexPipe = newWarFileLocation.indexOf("|");
        if (indexPipe != -1) {
            String stagingLocation = newWarFileLocation.substring(0,indexPipe);
            warFileName = newWarFileLocation.substring(indexPipe+1);
            out.write("wss.war.packages.dir=" + stagingLocation + "\n");
        }
        out.write("unsecure.appname=" + warFileName + "\n");
        out.close();
        os.close();
    }

}

