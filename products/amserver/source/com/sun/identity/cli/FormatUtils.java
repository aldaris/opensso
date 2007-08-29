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
 * $Id: FormatUtils.java,v 1.2 2007-08-29 22:44:47 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli;


import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import com.sun.identity.sm.SchemaType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Utility to format CLI output.
 */
public class FormatUtils {
    public static final String SPACE = "    ";
    public static final String MASKED_PWD = "********";
    private FormatUtils() {
    }

    public static String printServiceNames(
        Set serviceNames,
        String template,
        SSOToken ssoToken
    ) throws SMSException, SSOException {
        StringBuffer buff = new StringBuffer();
        String[] arg = new String[1];
        if (serviceNames != null) {
            for (Iterator i = serviceNames.iterator(); i.hasNext(); ) {
                String serviceName = (String)i.next();
                ServiceSchemaManager mgr = new ServiceSchemaManager(
                    serviceName, ssoToken);
                Set types = mgr.getSchemaTypes();
                if (!types.isEmpty()) {
                    SchemaType type = (SchemaType)types.iterator().next();
                    ServiceSchema schema = mgr.getSchema(type);
                    if (schema != null) {
                        String i18nKey = schema.getI18NKey();
                        if ((i18nKey != null) && (i18nKey.length() > 0)) {
                            arg[0] = serviceName;
                            buff.append(MessageFormat.format(template, 
                                    (Object[])arg))
                                .append("\n");
                        }
                    }
                }
            }
        }
        return buff.toString();
    }
    
    public static String printAttributeValues(
        String template,
        Map attributeValues,
        Set passwords
    ) {
        Map map = new HashMap(attributeValues.size() *2);
        Set setPwd = new HashSet(2);
        setPwd.add(MASKED_PWD);
        for (Iterator i = attributeValues.entrySet().iterator(); i.hasNext(); ){
            Map.Entry entry = (Map.Entry)i.next();
            Object key = entry.getKey();
            if (passwords.contains(key)) {
                map.put(key, setPwd);
            } else {
                map.put(key, entry.getValue());
            }
        }
        return printAttributeValues(template, map);
    }
    
    public static String printAttributeValues(
        String template,
        Map attributeValues
    ) {
        StringBuffer buff = new StringBuffer();
        if (attributeValues != null) {
            String[] args = new String[2];
            for (Iterator i = attributeValues.keySet().iterator(); i.hasNext();
            ) {
                String name = (String)i.next();
                args[0] = name;
                Set values = (Set)attributeValues.get(name);
                if (values.isEmpty()) {
                    args[1] = "";
                    buff.append(
                            MessageFormat.format(template, (Object[])args))
                        .append("\n");
                } else {
                    for (Iterator j = values.iterator(); j.hasNext(); ) {
                        args[1] = (String)j.next();
                        buff.append(MessageFormat.format(
                                template, (Object[])args))
                            .append("\n");
                    }
                }
            }
        }
        return buff.toString();
    }
}
