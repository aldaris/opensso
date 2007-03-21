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
 * $Id: DefinitionStubCreator.java,v 1.3 2007-03-21 22:33:44 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.tools;

import com.sun.identity.cli.annotation.DefinitionClassInfo;
import com.sun.identity.cli.annotation.Macro;
import com.sun.identity.cli.annotation.SubCommandInfo;
import com.sun.identity.cli.tools.StubTemplate;
import com.sun.identity.cli.tools.SubCommandStub;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is called by the ant script to generate CLI Definition Stub
 * Class. This is to support JDK 1.4 where annotation support is missing.
 */
public class DefinitionStubCreator {
    private static final String FLD_PRODUCT_NAME = "product";
    private String rbName;
    private String defClassName;
    private List subCommands = new ArrayList();
    
    private DefinitionStubCreator(String className) {
        defClassName = className;
    }
    
    private void createStub(String outFileName, String packageName)
        throws Exception {
        Class defClass = Class.forName(defClassName);
        StubTemplate stub = new StubTemplate();
        getProductName(defClass, stub);
        getCommands(defClass, stub);
        stub.createClassFile(outFileName, packageName);
    }
    
    private void getProductName(Class clazz, StubTemplate stub)
        throws Exception {
        Field pdtField = clazz.getDeclaredField(FLD_PRODUCT_NAME);
        
        if (pdtField != null) {
            DefinitionClassInfo classInfo = pdtField.getAnnotation(
                DefinitionClassInfo.class);
            stub.rbName = classInfo.resourceBundle();
        } else {
            throw new Exception(
                "Incorrect Definiton: missing product field");
        }
    }
    
    private void getCommands(Class clazz, StubTemplate stub)
        throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field fld : fields) {
            SubCommandInfo info = fld.getAnnotation(SubCommandInfo.class);
            
            if (info != null) {
                if ((info.implClassName() == null) ||
                    (info.description() == null)
                    ) {
                    throw new Exception(
                        "Incorrect Definiton: mandatory fields in command");
                }
                
                boolean webSupport = info.webSupport().equals("true");
                List mandatoryOptions = CLIDefinitionGenerator.toList(
                    info.mandatoryOptions());
                List optionalOptions = CLIDefinitionGenerator.toList(
                    info.optionalOptions());
                List optionAliases = CLIDefinitionGenerator.toList(
                    info.optionAliases());
                
                if ((info.macro() != null) && (info.macro().length() > 0)) {
                    Field fldMarco = clazz.getDeclaredField(info.macro());
                    Macro macroInfo =(Macro)fldMarco.getAnnotation(
                        Macro.class);
                    CLIDefinitionGenerator.appendToList(mandatoryOptions,
                        macroInfo.mandatoryOptions());
                    CLIDefinitionGenerator.appendToList(optionalOptions,
                        macroInfo.optionalOptions());
                    CLIDefinitionGenerator.appendToList(optionAliases,
                        macroInfo.optionAliases());
                }
                
                String subcmdName = fld.getName().replace('_', '-');
                
                stub.subCommands.add(new SubCommandStub(subcmdName,
                    info.implClassName(), mandatoryOptions, optionalOptions,
                    optionAliases, webSupport));
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            DefinitionStubCreator creator = new DefinitionStubCreator(args[0]);
            creator.createStub(args[1], args[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
