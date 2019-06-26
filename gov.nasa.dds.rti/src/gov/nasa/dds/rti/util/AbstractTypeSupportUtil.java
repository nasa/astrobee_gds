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
package gov.nasa.dds.rti.util;


import gov.nasa.dds.exception.UncheckedTypeSupportException;

import java.lang.reflect.Method;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Copyable;

/**
 * Provides generic access to <Copyable>TypeSupport public static methods. 
 * Because of the visibility scope of plugins, class lookup has to happen 
 * downstream from the package containing the class to be instantiated. So
 * we inherit this class in the package that has the generated DDS code
 * and register it with the TypeSupportUtil class. 
 * @author mallan
 */
public abstract class AbstractTypeSupportUtil {
    
    /**
     * invokes <Copyable>TypeSupport.get_type_name()
     * @param copyable instance corresponding to <copyable implements Copyable>TypeSupport
     * @return type_name string
     * @throws UncheckedTypeSupportException
     */
    public String getTypeNameForImpl(final Class<? extends Copyable> copyClass) throws UncheckedTypeSupportException {
        try {
            final Class  typeSuppClass = getTypeSupportClassFor(copyClass);
            final Method method;
            final Class[] params    = new Class[] {};
            method = typeSuppClass.getDeclaredMethod("get_type_name", params );
            return (String)method.invoke(null, new Object[] {});
        } 
        catch (Throwable t) {
            throw new UncheckedTypeSupportException(this.getClass().getSimpleName()+" could not get type_name for "+copyClass.getSimpleName(), t);
        }
    }
    
    /**
     * invokes <Copyable>TypeSupport.register_type(participant, type_name)
     * @throws UncheckedTypeSupportException
     */
    public void registerTypeImpl(final DomainParticipant participant, final Class<? extends Copyable> copyClass) 
    throws UncheckedTypeSupportException  {
        regMethod("register_type", participant, copyClass);
    }

    /**
     * invokes <Copyable>TypeSupport.unregister_type(participant, type_name)
     * @throws UncheckedTypeSupportException
     */
    public void unregisterTypeImpl(final DomainParticipant participant, final Class<? extends Copyable> copyClass) 
    throws UncheckedTypeSupportException  {
        regMethod("unregister_type", participant, copyClass);
    }
    
    private void regMethod(final String methodName, final DomainParticipant participant, final Class<? extends Copyable> copyClass) 
    throws UncheckedTypeSupportException  {
        try {
            final String type_name = getTypeNameForImpl(copyClass);
            final Class  typeSuppClass = getTypeSupportClassFor(copyClass);
            final Method method;
            final Class[] params = new Class[] { DomainParticipant.class, String.class };
            final Object[] args  = new Object[] { participant, type_name };
            method = typeSuppClass.getDeclaredMethod(methodName, params );
            method.invoke(null, args);
        }
        catch(Throwable t) {
            throw new UncheckedTypeSupportException("Error invoking "+methodName+" for "+copyClass.getSimpleName(), t);
        }

    }

    /**
     * This method *must* be implemented in the concrete class in order
     * for Class.forName to succeed when used in Eclipse RCP plugins due
     * to class scoping rules. This is an inconvenience that we put up with 
     * to get around using any Eclipse RCP specific classes to do the class lookup. 
     * We do not use the OSGi DynamicImport-Package directive as it is very 
     * heavy-weight and its use is discouraged.
     * In the future, we may use Eclipse-Buddy policies to manage class loading.
     * 
     * The method body should look like:<br>
     * <code>
     *    final String typeSuppName = copyClass.getName()+"TypeSupport"; <br>
     *    return Class.forName(typeSuppName); <br>
     * </code>
     * @param copyClass
     * @return
     * @throws ClassNotFoundException
     */
    public abstract Class getTypeSupportClassFor(Class<? extends Copyable> copyClass) throws ClassNotFoundException;
//    public Class getTypeSupportClassFor(Class<? extends Copyable> copyClass) throws ClassNotFoundException {
//        final String typeSuppName = copyClass.getName()+"TypeSupport";
//        return Class.forName(typeSuppName);
//    }
    
    /**
     * This method *must* be implemented in the concrete class in order
     * for Class.forName to succeed when used in Eclipse RCP plugins due
     * to class scoping rules. This is an inconvenience that we put up with 
     * to get around using any Eclipse RCP specific classes to do the class lookup. 
     * We do not use the OSGi DynamicImport-Package directive as it is very 
     * heavy-weight and its use is discouraged.
     * In the future, we may use Eclipse-Buddy policies to manage class loading.
     * 
     * The method body should look like:<br>
     * <code>
     *    return Class.forName(className); <br>
     * </code>
     * @param className
     * @return
     */
    public abstract Class classForName(String className) throws ClassNotFoundException;
}
