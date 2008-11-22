/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: ToolCLISave.java,v 1.1 2008-11-22 02:19:55 ak138937 Exp $
 *
 */

package com.sun.identity.diagnostic.base.core.ui.cli;

import java.io.FileWriter;
import java.util.ArrayList;
import java.io.File;

public class ToolCLISave {
    
    /** Creates a new instance of ToolCLISave*/
    public ToolCLISave() {
    }
    
    /**
     * Formats and saves the file with the specified name
     *
     * @param fName Name of the file be saved as.
     * @param params ArrayList containing the contents to save.
     *
     */
    public void saveToFile(String fName, ArrayList params) throws Exception {
        FileWriter fw = new FileWriter(new File(fName));
        fw.write(formatInput(params));
        fw.flush();
        fw.close();
    }
    
    private String formatInput(ArrayList params) {
        StringBuffer buff = new StringBuffer();
        buff.append("========================\n")
        .append("Status: \n")
        .append(params.get(0))
        .append("\n========================\n")
        .append("\n========================\n")
        .append("Message: \n")
        .append(params.get(1))
        .append("\n========================\n")
        .append("Error: \n")
        .append(params.get(2))
        .append("\n========================\n")
        .append("Warning: \n")
        .append(params.get(3))
        .append("\n========================\n")
        .append("\n");
        return buff.toString();
    }
}


