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
 * $Id: SchemaCommand.java,v 1.1 2006-05-31 21:50:05 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.schema;


import com.iplanet.sso.SSOException;
import com.iplanet.sso.SSOToken;
import com.sun.identity.cli.AuthenticatedCommand;
import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IArgument;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.SchemaType;
import com.sun.identity.sm.ServiceSchema;
import com.sun.identity.sm.ServiceSchemaManager;
import java.text.MessageFormat;
import java.util.StringTokenizer;

/**
 * Base class for schema commands.
 */
public class SchemaCommand extends AuthenticatedCommand {
    static final String SCHEMA_TYPE_GLOBAL = "global";
    static final String SCHEMA_TYPE_ORGANIZATION = "organization";
    static final String SCHEMA_TYPE_DYNAMIC = "dynamic";
    static final String SCHEMA_TYPE_USER = "user";
    static final String SCHEMA_TYPE_POLICY = "policy";

    protected ServiceSchemaManager getServiceSchemaManager()
        throws CLIException {
        ServiceSchemaManager mgr = null;
        SSOToken adminSSOToken = getAdminSSOToken();
        String serviceName = getStringOptionValue(IArgument.SERVICE_NAME);

        if (serviceName != null) {
            try {
                mgr = new ServiceSchemaManager(serviceName, adminSSOToken);
            } catch (SSOException e) {
                debugError("SchemaCommand.getServiceSchemaManager", e);
                throw new CLIException(
                    e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            } catch (SMSException e) {
                debugError("SchemaCommand.getServiceSchemaManager", e);
                throw new CLIException(
                    e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
        }
        return mgr;
    }
    
    protected ServiceSchema getServiceSchema()
        throws CLIException {
        String subSchemaName = getStringOptionValue(IArgument.SUBSCHEMA_NAME);
        String schemaType = getStringOptionValue(IArgument.SCHEMA_TYPE);
        ServiceSchema ss = null;
        ServiceSchemaManager ssm = getServiceSchemaManager();

        if ((ssm != null) && (schemaType != null)) {
            
            try {
                ss = ssm.getSchema(getSchemaType(schemaType));

                if (subSchemaName != null) {
                    boolean done = false;
                    StringTokenizer st = new StringTokenizer(subSchemaName,"/");

                    while (st.hasMoreTokens() && !done) {
                        String str = st.nextToken();
                        if (str != null) {
                            ss = ss.getSubSchema(str);
                            if (ss == null) {
                                String[] args = {subSchemaName};
                                throw new CLIException(MessageFormat.format(
                                    getResourceString(
                                        "schema-sub-schema-does-not-exists"),
                                    (Object[])args),
                                    ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
                            }
                        } else {
                            done = true;
                        }
                    }
                }
            } catch (SMSException e) {
                debugError("SchemaCommand.getServiceSchema", e);
                throw new CLIException(
                    e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
            }
        }

        if (ss == null) {
            String[] args = {subSchemaName};
            throw new CLIException(MessageFormat.format(
                getResourceString("schema-sub-schema-does-not-exists"), 
                    (Object[])args),
                ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
        return ss;
    }

    private SchemaType getSchemaType(String schemaTypeName){
        SchemaType schemaType = null;
        if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_GLOBAL)) {
            schemaType = SchemaType.GLOBAL;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_ORGANIZATION)) {
            schemaType = SchemaType.ORGANIZATION;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_DYNAMIC)) {
            schemaType = SchemaType.DYNAMIC;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_USER)) {
            schemaType = SchemaType.USER;
        } else if (schemaTypeName.equalsIgnoreCase(SCHEMA_TYPE_POLICY)) {
            schemaType = SchemaType.POLICY;
        }
        return schemaType;
    }
}
