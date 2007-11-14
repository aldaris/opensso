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
 * $Id: ConnectionFactoryProviderImpl.java,v 1.1 2007-11-14 00:19:59 manish_rustagi Exp $
 *
 * Copyright 2005 Sun Microsystems Inc. All Rights Reserved
 */

package com.iplanet.dpro.session.jmqdb;

import javax.jms.TopicConnectionFactory;
 
/**
 * This class <code>ConnectionFactoryProviderImpl</code> implements 
 * </code> ConnectionFactoryProvider</code> and provides a default 
 * implementation for ConnectionFactoryProvider using sun specifig 
 * configuration
 */
public class ConnectionFactoryProviderImpl implements ConnectionFactoryProvider {

    public com.sun.messaging.TopicConnectionFactory tFactory;
    

    /**
     * Creates a new <code>TopicConnectionFactory</code> instance.
     * 
     * @return a newly created <code>TopicConnectionFactory</code>.
     */
    public TopicConnectionFactory newTopicConnectionFactory(){
        tFactory = new com.sun.messaging.TopicConnectionFactory();
        return tFactory;    
    }

    
    /**
     * Creates a new <code>TopicConnectionFactory</code> instance. The
     * supplied parameters are optionally used to configure the newly created
     * instance using vendor APIs if applicable.
     * 
     * @param brokerAddressList
     *            the list of addresses of the brokers
     * @param reconnectEnabled
     *            a flag indicating if reconnect will be enabled
     * @param flowLimitEnabled
     *            a flag indicating if flow limit will be enabled
     * @param defaultUsername
     *            the default username for establishing connections
     * @param defaultPassword
     *            the default password for establishing connections
     * @return a newly created <code>TopicConnectionFactory</code>.
     * @throws Exception
     *             if an exception occurs during creation or configuration of
     *             the <code>TopicConnectionFactory</code>.
     */
    public TopicConnectionFactory newTopicConnectionFactory(
           String brokerAddressList,boolean reconnectEnabled,
           boolean flowLimitEnabled,String defaultUsername,
           String defaultPassword) throws Exception{
        tFactory = new com.sun.messaging.TopicConnectionFactory();
        com.sun.messaging.ConnectionFactory cf = 
            (com.sun.messaging.ConnectionFactory) tFactory;
        cf.setProperty(com.sun.messaging.ConnectionConfiguration.imqAddressList,
                    brokerAddressList);           
        cf.setProperty(com.sun.messaging.ConnectionConfiguration.imqReconnectEnabled,
                    Boolean.toString(reconnectEnabled));
        cf.setProperty(com.sun.messaging.ConnectionConfiguration.imqConnectionFlowLimitEnabled,
        		Boolean.toString(flowLimitEnabled));
        cf.setProperty(com.sun.messaging.ConnectionConfiguration.imqDefaultUsername,
                    defaultUsername);
        cf.setProperty(com.sun.messaging.ConnectionConfiguration.imqDefaultPassword,
                    defaultPassword);
        return tFactory;                        
    }    
}
