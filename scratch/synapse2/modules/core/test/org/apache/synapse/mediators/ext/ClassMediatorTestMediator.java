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
package org.apache.synapse.mediators.ext;

import org.apache.synapse.api.Mediator;
import org.apache.synapse.SynapseContext;

/**
 * Since the class mediator always "instantiates" a new instance of a class
 * use a static member variable just to test this.. This class is not nice.. :-)
 * but does what is expected... :-(
 */
public class ClassMediatorTestMediator implements Mediator {

    public static boolean invoked = false;

    public static String testProp = null;

    public boolean mediate(SynapseContext synCtx) {
        invoked = true;
        return false;
    }

    public String getType() {
        return null;
    }

    public void setTestProp(String s) {
        testProp = s;
    }

    public String getTestProp() {
        return testProp;
    }
}