/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.apache.synapse.transport.jms;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.description.AxisService;
import org.apache.synapse.transport.testkit.listener.AbstractChannel;
import org.apache.synapse.transport.testkit.name.NameComponent;

public abstract class JMSChannel extends AbstractChannel {
    private final String destinationType;
    protected JMSTestEnvironment env;
    private String destinationName;
    private Destination destination;
    
    public JMSChannel(String destinationType) {
        this.destinationType = destinationType;
    }
    
    @SuppressWarnings("unused")
    private void setUp(JMSTestEnvironment env) throws Exception {
        this.env = env;
        destinationName = "request" + destinationType;
        destination = env.createDestination(destinationType, destinationName);
        env.getContext().bind(destinationName, destination);
    }

    @SuppressWarnings("unused")
    private void tearDown() throws Exception {
        env.getContext().unbind(destinationName);
        destinationName = null;
        destination = null;
    }

    @NameComponent("destType")
    public String getDestinationType() {
        return destinationType;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public Destination getDestination() {
        return destination;
    }

    public int getMessageCount() throws JMSException {
        Connection connection = env.getConnectionFactory(destination).createConnection();
        try {
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            QueueBrowser browser = session.createBrowser((Queue)destination);
            int count = 0;
            for (Enumeration<?> e = browser.getEnumeration(); e.hasMoreElements(); e.nextElement()) {
                count++;
            }
            return count;
        } finally {
            connection.close();
        }
    }

    public EndpointReference getEndpointReference() throws Exception {
        return new EndpointReference("jms:/" + destinationName + "?transport.jms.DestinationType=" + destinationType + "&java.naming.factory.initial=org.mockejb.jndi.MockContextFactory&transport.jms.ConnectionFactoryJNDIName=QueueConnectionFactory");
    }

    @Override
    public void setupService(AxisService service) throws Exception {
        service.addParameter(JMSConstants.CONFAC_PARAM, destinationType);
        service.addParameter(JMSConstants.DEST_PARAM_TYPE, destinationType);
        service.addParameter(JMSConstants.DEST_PARAM, destinationName);
    }
}
