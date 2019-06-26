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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.topic.TypeSupportImpl;

/**
 * Provides generic access to [Copyable]TypeSupport public static methods. 
 * The user is responsible for subclassing AbstractTypeSupportUtil and registering 
 * and instance of that class with TypeSupportUtil in order for it to be able 
 * to instantiate the [Copyable]TypeSupport classes. This mechanism is required
 * due to visibility scoping rules of Eclipse RCP plugins. 
 * @author mallan
 */
public class TypeSupportUtil {
    private static final Logger logger = Logger.getLogger(TypeSupportUtil.class);
    private static final ArrayList<AbstractTypeSupportUtil> s_instances = new ArrayList<AbstractTypeSupportUtil>();

    public static void addImpl(AbstractTypeSupportUtil impl) {
        s_instances.add(impl);
    }

    /**
     * invokes <Copyable>TypeSupport.get_type_name()
     * @param copyable instance corresponding to <copyable implements Copyable>TypeSupport
     * @return type_name string on success, null otherwise
     */
    public static String getTypeNameFor(final Class<? extends Copyable> copyClass) {
        String retVal = null;
        for(AbstractTypeSupportUtil atsu : s_instances) {
            try {
                retVal = atsu.getTypeNameForImpl(copyClass);
                break;
            }
            catch(Throwable t) {
                //logger.debug(" *** "+t.getMessage());
            }
        }
        if(retVal == null) {
            logger.fatal("Could not find TypeSupport class for "+copyClass.getName()+"\n"
                    +"Please verify that TypeSupportUtil has been initialized with a concrete\n"
                    +"implementation (that extends AbstractTypeSupportUtil) that has visibility\n"
                    +"of the "+copyClass.getName()+" idl-generated code.");
        }
        return retVal;
    }

    /**
     * calls classForName on all registered TypeSupportUtil instances in order to locate className
     */
    public static Class classForName(String className) {
        Class retVal = null;
        for(AbstractTypeSupportUtil atsu : s_instances) {
            try {
                retVal = atsu.classForName(className);
                break; // break on the first one that succeeds
            }
            catch(Throwable t) {
                //logger.debug(" *** "+t.getMessage());
            }
        }
        return retVal;
    }

    /**
     * calls classForName on all registered TypeSupportUtil instances in order to locate className
     */
    public static Class getTypeSupportClassFor(Class<? extends Copyable> copyClass) {
        Class retVal = null;
        for(AbstractTypeSupportUtil atsu : s_instances) {
            try {
                retVal = atsu.getTypeSupportClassFor(copyClass);
                break; // break on the first one that succeeds
            }
            catch(Throwable t) {
                //logger.debug(" *** "+t.getMessage());
            }
        }
        return retVal;
    }

    /**
     * invokes <Copyable>TypeSupport.register_type(participant, type_name)
     * @return true on success
     */
    public static boolean registerType(final DomainParticipant participant, final Class<? extends Copyable> copyClass) {
        boolean retVal = false;
        for(AbstractTypeSupportUtil atsu : s_instances) {
            try {
                atsu.registerTypeImpl(participant, copyClass);
                retVal = true;
                break;
            }
            catch(Throwable t) {
                // ignore
            }
        }
        return retVal;
    }

    /**
     * invokes <Copyable>TypeSupport.unregister_type(participant, type_name)
     * @return true on success
     */
    public static boolean unregisterType(final DomainParticipant participant, final Class<? extends Copyable> copyClass) {
        boolean retVal = false;
        for(AbstractTypeSupportUtil atsu : s_instances) {
            try {
                atsu.unregisterTypeImpl(participant, copyClass);
                retVal = true;
                break;
            }
            catch(Throwable t) {
                // ignore
            }
        }
        return retVal;
    }
    
    public static TypeSupportImpl getTypeSupportImpl(final Class<? extends Copyable> copyClass) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        Class typeSupportClass = getTypeSupportClassFor(copyClass);
        final Method method;
        final Class[] params    = new Class[] {};
        method = typeSupportClass.getDeclaredMethod("get_instance", params);
        return (TypeSupportImpl)method.invoke(null, new Object[] {});
        
    }

    /**
     * NOTE: this does not include encapsulation in size calculation
     */
    public static int getMaxSerializedSizeFor(final Class<? extends Copyable> copyClass) {
        try {
            TypeSupportImpl typeSupport = getTypeSupportImpl(copyClass);
            long size = typeSupport.get_serialized_sample_max_size(null, false, (short)0, 0);
            return (int)size;
        }
        catch(Throwable t) {
            logger.debug(t);
        }
        return -1;
    }

    /**
     * NOTE: this does not include encapsulation in size calculation
     */
    public static int getMinSerializedSizeFor(final Class<? extends Copyable> copyClass) {
        try {
            TypeSupportImpl typeSupport = getTypeSupportImpl(copyClass);
            long size = typeSupport.get_serialized_sample_min_size(null, false, (short)0, 0);
            return (int)size;
        }
        catch(Throwable t) {
            logger.debug(t);
        }
        return -1;
    }

    /**
     * NOTE: this does not include encapsulation in size calculation
     */
    public static int getSerializedSize(final Copyable copyable) {
        try {
            TypeSupportImpl typeSupport = getTypeSupportImpl(copyable.getClass());
            long size = typeSupport.get_serialized_sample_size(null, false, (short)0, 0, copyable);
            return (int)size;
        }
        catch(Throwable t) {
            logger.debug(t);
        }
        return -1;
    }

}
