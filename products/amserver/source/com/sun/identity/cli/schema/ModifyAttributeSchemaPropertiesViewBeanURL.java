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
 * $Id: ModifyAttributeSchemaPropertiesViewBeanURL.java,v 1.1 2006-05-31 21:50:02 veiming Exp $
 *
 * Copyright 2006 Sun Microsystems Inc. All Rights Reserved
 */

package com.sun.identity.cli.schema;


import com.sun.identity.cli.CLIException;
import com.sun.identity.cli.ExitCodes;
import com.sun.identity.cli.IArgument;
import com.sun.identity.cli.IOutput;
import com.sun.identity.cli.LogWriter;
import com.sun.identity.cli.RequestContext;
import com.iplanet.sso.SSOException;
import com.sun.identity.sm.AttributeSchema;
import com.sun.identity.sm.SMSException;
import com.sun.identity.sm.ServiceSchema;
import java.text.MessageFormat;
import java.util.logging.Level;

/**
 * Modifies attribute schema's properties view bean URL.
 */
public class ModifyAttributeSchemaPropertiesViewBeanURL extends SchemaCommand {
    static final String ARGUMENT_URL = "url";
    
    /**
     * Services a Commandline Request.
     *
     * @param rc Request Context.
     * @throw CLIException if the request cannot serviced.
     */
    public void handleRequest(RequestContext rc) 
        throws CLIException {
        super.handleRequest(rc);
        ldapLogin();

        String schemaType = getStringOptionValue(IArgument.SCHEMA_TYPE);
        String serviceName = getStringOptionValue(IArgument.SERVICE_NAME);
        String subSchemaName = getStringOptionValue(IArgument.SUBSCHEMA_NAME);
        String attributeSchemaName = getStringOptionValue(
            IArgument.ATTRIBUTE_SCHEMA);
        String url = getStringOptionValue(ARGUMENT_URL);
        
        ServiceSchema ss = getServiceSchema();
        IOutput outputWriter = getOutputWriter();

        try {
            String[] params = {serviceName, schemaType, subSchemaName,
                attributeSchemaName, url};
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "ATTEMPT_MODIFY_ATTRIBUTE_SCHEMA_PROPERTIES_VIEW_BEAN_URL", 
                params);
            AttributeSchema attrSchema = ss.getAttributeSchema(
                attributeSchemaName);
            attrSchema.setPropertiesViewBeanUR(url);
            writeLog(LogWriter.LOG_ACCESS, Level.INFO,
                "SUCCEED_MODIFY_ATTRIBUTE_SCHEMA_PROPERTIES_VIEW_BEAN_URL", 
                params);
            outputWriter.printlnMessage(MessageFormat.format(
                getResourceString(
                "attribute-schema-modify-properties-view-bean-url-key-succeed"),
                (Object[])params));
        } catch (SSOException e) {
            String[] args = {serviceName, schemaType, subSchemaName,
                attributeSchemaName, url, e.getMessage()};
            debugError(
                "ModifyAttributeSchemaPropertiesViewBeanURL.handleRequest",e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_MODIFY_ATTRIBUTE_SCHEMA_PROPERTIES_VIEW_BEAN_URL",args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        } catch (SMSException e) {
            String[] args = {serviceName, schemaType, subSchemaName,
                attributeSchemaName, url, e.getMessage()};
            debugError(
                "ModifyAttributeSchemaPropertiesViewBeanURL.handleRequest",e);
            writeLog(LogWriter.LOG_ERROR, Level.INFO,
                "FAILED_MODIFY_ATTRIBUTE_SCHEMA_PROPERTIES_VIEW_BEAN_URL",args);
            throw new CLIException(e, ExitCodes.REQUEST_CANNOT_BE_PROCESSED);
        }
    }
}
