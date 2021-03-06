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
package org.apache.synapse.config.xml;

import org.apache.synapse.api.Mediator;
import org.apache.synapse.mediators.builtin.ValidateMediator;
import org.apache.synapse.SynapseException;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.xpath.AXIOMXPath;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jaxen.JaxenException;

import javax.xml.namespace.QName;

/**
 * Creates a validation mediator from the XML configuration
 *
 * <validate schema="url" [source="xpath"]>
 *   <on-fail>
 *     mediator+
 *   </on-fail>
 * </validate>
 */
public class ValidateMediatorFactory extends AbstractListMediatorFactory {

    private static final Log log = LogFactory.getLog(TransformMediatorFactory.class);
    private static final QName VALIDATE_Q    = new QName(Constants.SYNAPSE_NAMESPACE, "validate");

    public Mediator createMediator(OMElement elem) {

        ValidateMediator validateMediator = new ValidateMediator();
        OMAttribute attSchema = elem.getAttribute(new QName(Constants.NULL_NAMESPACE, "schema"));
        OMAttribute attSource = elem.getAttribute(new QName(Constants.NULL_NAMESPACE, "source"));

        if (attSchema != null) {
            validateMediator.setSchemaUrl(attSchema.getAttributeValue());
        } else {
            String msg = "The 'schema' attribute is required for the validate mediator configuration";
            log.error(msg);
            throw new SynapseException(msg);
        }

        if (attSource != null) {
            try {
                AXIOMXPath xp = new AXIOMXPath(attSource.getAttributeValue());
                validateMediator.setSource(xp);
                addNameSpaces(elem, xp, log);

            } catch (JaxenException e) {
                String msg = "Invalid XPath expression specified for attribute 'source'";
                log.error(msg);
                throw new SynapseException(msg, e);
            }
        }

        OMElement onFail = elem.getFirstElement();
        if (new QName(Constants.SYNAPSE_NAMESPACE, "on-fail").equals(onFail.getQName()) &&
            onFail.getChildElements().hasNext()) {
            super.addChildren(onFail, validateMediator);

        } else {
            String msg = "A non-empty on-fail element is required for the validate mediator";
            log.error(msg);
            throw new SynapseException(msg);
        }

        return validateMediator;
    }

    public QName getTagQName() {
        return VALIDATE_Q;
    }
}
