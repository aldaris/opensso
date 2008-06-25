/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2006 Sun Microsystems Inc. All Rights Reserved
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
 * $Id: StubTemplate.java,v 1.5 2008-06-25 05:42:24 qcheng Exp $
 *
 */

package com.sun.identity.cli.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is responsible for generating a Stub Java Class which
 * contains CLI Definition. This is to support JDK 1.4 where there
 * are no annotation support.
 */
public class StubTemplate {
    public String logName;
    public String rbName;
    public List subCommands =new ArrayList();

    /**
     * Creates the CLI Definition Stub Java Class.
     *
     * @param fileName Physical file name to write the code to.
     * @param packageName Name of the Java package.
     */
    public void createClassFile(String fileName, String packageName) {
        int idx = fileName.lastIndexOf('.');
        String className = fileName.substring(0, idx);
        idx = className .lastIndexOf('/');
        if (idx != -1) {
            className = className .substring(idx +1);
        }
        String code = TEMPLATE.replaceAll("@className@", className);
        code = code.replaceAll("@packageName@", packageName);
        code = code.replaceAll("@resourcebundle@", rbName);
        code = code.replaceAll("@logname@", logName);
        
        StringBuffer buff = new StringBuffer();
        for (Iterator i = subCommands.iterator(); i.hasNext(); ) {
            SubCommandStub stub = (SubCommandStub) i.next();
            buff.append(stub.generateJavaStatement());
        }
        
        code = code.replaceAll("@subcommand@", buff.toString());
        
        try {
            File outFile = new File(fileName);
            outFile.createNewFile();
            FileOutputStream fStream = new FileOutputStream(outFile);
            PrintStream pStream = new PrintStream(fStream);
            pStream.print(code);
            pStream.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
   
    private static final String TEMPLATE = 
        "/* This is an generated source.\n" + 
        "See com.sun.identity.cli.definition.StubTemplate." +
        " */\n" +
        "\n" +
        "package @packageName@;\n" +
        "\n" +
        "import com.sun.identity.cli.stubs.ICLIStub;\n" +
        "import com.sun.identity.cli.stubs.SubCommandStub;\n" +
        "import java.util.ArrayList;\n" +
        "import java.util.List;\n" +
        "\n" +
        "\n" +
        "public class @className@ implements ICLIStub {\n" +
        "    public String rbName;\n" +
        "    public String logName;\n" +
        "    public List subCommands = new ArrayList();\n" +
        "\n" +
        "    public @className@() {\n" +
        "        rbName = \"@resourcebundle@\";\n" +
        "        logName = \"@logname@\";\n" +
        "        @subcommand@" +
        "    }\n" +
        "\n" +
        "    public String getResourceBundleName() {\n" +
        "        return rbName;\n" +
        "    }\n" +
        "\n" +
        "    public String getLogName() {\n" +
        "        return logName;\n" +
        "    }\n" +
        "\n" +
        "    public List getSubCommandStubs() {\n" +
        "        return subCommands;\n" +
        "    }\n" +
        "\n" +
        "    public void addSubCommand(SubCommandStub stub) {\n" +
        "        subCommands.add(stub);\n" +
        "    }\n" +
        "\n" +
        "}\n" +
        "\n";
}
