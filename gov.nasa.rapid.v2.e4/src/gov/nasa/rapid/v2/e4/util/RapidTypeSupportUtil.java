/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.rapid.v2.e4.util;

import gov.nasa.dds.rti.util.AbstractTypeSupportUtil;

import com.rti.dds.infrastructure.Copyable;

public class RapidTypeSupportUtil extends AbstractTypeSupportUtil {
    /**
     * this method has to be implemented in the concrete class in 
     * order for class lookup to succeed
     */
    @Override
    public Class getTypeSupportClassFor(Class<? extends Copyable> copyClass) throws ClassNotFoundException {
        final String typeSuppName = copyClass.getName()+"TypeSupport";
        return Class.forName(typeSuppName);
    }

    @Override
    public Class classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
