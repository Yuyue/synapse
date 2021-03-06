/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.synapse.core.axis2;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.MessageReceiver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.SynapseContext;
import org.apache.synapse.SynapseMessage;

/**
 * This message receiver should be configured in the Axis2 configuration as the
 * default message receiver, which will handle all incoming messages through the
 * synapse mediation
 */
public class SynapseMessageReceiver implements MessageReceiver {

    private static final Log log = LogFactory.getLog(SynapseMessageReceiver.class);

    public void receive(MessageContext mc) throws AxisFault {

        log.debug("Synapse received message");
        SynapseContext synCtx = Axis2SynapseContextFinder.getSynapseContext(mc);
        ////////////////////////////////////////////////////////////////////////
        // SynapseContext is set as a property in MessageContext. This is due
        // use we can expect in ServiceMediatorProcessor and many extensions yet to come
        // So it a mediator uses EnvironmentAware, that mediator will be injected with the correct environment

        ////////////////////////////////////////////////////////////////////////
        SynapseMessage smc = new Axis2SynapseMessage(mc, synCtx);
        synCtx.getSynapseEnvironment().injectMessage(synCtx);

        ///////////////////////////////////////////////////////////////////////
        // Response handling mechanism for 200/202 and 5XX
        // smc.isResponse =true then the response will be handle with 200 OK
        // else, response will be 202 OK without no http body
        // smc.isFaultRespose = true then the response is a fault with 500 Internal Server Error
        if (smc.isResponse()) {
            mc.getOperationContext().setProperty(Constants.RESPONSE_WRITTEN,
                Constants.VALUE_TRUE);
        }
        if (smc.isFaultResponse()) {
            // todo: a good way to inject faultSoapEnv to the Axis2 Transport 
            throw new AxisFault(
                "Synapse Encounters an Error - Please See Log for More Details");
        }
        ///////////////////////////////////////////////////////////////////////
    }
}
