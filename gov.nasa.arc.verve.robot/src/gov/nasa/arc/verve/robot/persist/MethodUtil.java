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
package gov.nasa.arc.verve.robot.persist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** 
 * FIXME: this class copied here rather than having to depend on ensemble databinding 
 */
@SuppressWarnings("rawtypes")
public class MethodUtil {

    public static final String GET = "get";
    public static final String IS = "is";
    public static final String SET = "set";

    /**
     * Get the setter method for this property for the m_class
     * @param property
     * @param setType
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Method getSetMethod(Class pClass, String property, Class setType) throws SecurityException, NoSuchMethodException{
        if (pClass == null){
            throw new NoSuchMethodException("class is null");
        }
        return pClass.getMethod(SET + upperFirstChar(property, false), setType);
    }

    /**
     * Get the getter method for this property for the m_class
     * @param property
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Method getGetMethod(Class pClass, String property) throws SecurityException, NoSuchMethodException{
        if (pClass == null){
            throw new NoSuchMethodException("class is null");
        }
        return pClass.getMethod(GET + upperFirstChar(property, false), (Class[])null);
    }



    /**
     * Get the is method for this property for the m_class
     * @param property
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Method getIsMethod(Class pClass, String property) throws SecurityException, NoSuchMethodException{
        if (pClass == null){
            throw new NoSuchMethodException("class is null");
        }
        return pClass.getMethod(IS + upperFirstChar(property, false), (Class[]) null);
    }

    /**
     * Get the return type for a class' property method
     * @param pClass
     * @param property
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public static Class getReturnType(Class pClass, String property) throws SecurityException, NoSuchMethodException{
        try {
            Method getMethod = getGetMethod(pClass,property);
            return getMethod.getReturnType();
        } catch (NoSuchMethodException nsme){
            Method isMethod = getIsMethod(pClass, property);
            return isMethod.getReturnType();
        }
    }

    /**
     * Returns true if this return type is simple, ie String, Boolean, Integer, Double, Float, int, double, float ...
     * @param type
     * @return
     */
    @SuppressWarnings("unchecked")
	public static boolean isReturnTypeSimple(Class type){
		if (type == null){
			return false;
		}
		if (type.isPrimitive() || type.isEnum()){
			return true;
		}
		if (type.isAssignableFrom(String.class) ||
			type.isAssignableFrom(Number.class) ||
			type.isAssignableFrom(Character.class) ||
			type.isAssignableFrom(StringBuffer.class)) {
			return true;
		}
		return false;
    }
    
    /*
     * Attempt to convert a string to some class
     * @param string the string to convert
     * @param type the class to convert it to
     * @return null if we had a problem.
     */
    @SuppressWarnings("unchecked")
	public static Object convertStringToType(String string, Class type){
    	try {
	    	if (type.isAssignableFrom(String.class) ){
	    		return string;
	    	} else if (type.isPrimitive()) {
	    		if (type.equals(Boolean.TYPE)){
	    			return Boolean.parseBoolean(string.toLowerCase());
	    		} else if (type.equals(Character.TYPE)){
	    			return string.toCharArray()[0];
	    		} else if (type.equals(Byte.TYPE)){
	    			return Byte.decode(string);
	    		} else if (type.equals(Short.TYPE)){
	    			return Short.decode(string);
	    		} else if (type.equals(Integer.TYPE)){
	    			return Integer.decode(string);
	    		} else if (type.equals(Long.TYPE)){
	    			return Long.decode(string);
	    		} else if (type.equals(Float.TYPE)){
	    			return Float.parseFloat(string);
	    		} else if (type.equals(Double.TYPE)){
	    			return Double.parseDouble(string);
	    		}
	    	} else if (type.equals(Boolean.class)){
				return new Boolean(Boolean.parseBoolean(string.toLowerCase()));
			} else if (type.equals(Character.class)){
				return Character.valueOf(string.charAt(0));
			} else if (type.equals(Byte.class)){
				return Byte.valueOf(string);
			} else if (type.equals(Short.class)){
				return new Short(Short.decode(string));
			} else if (type.equals(Integer.class)){
				return Integer.valueOf(string);
			} else if (type.equals(Long.class)){
				return Long.valueOf(string);
			} else if (type.equals(Float.class)){
				return Float.valueOf(string);
			} else if (type.equals(Double.class)){
				return Double.valueOf(string);
			} else if (type.isEnum()){
				return Enum.valueOf(type, string);
			}
    	} catch (Exception ex){
    		// do nothing
    	}
    	return null;
    }

    /**
     * Get the suffix for an is or get method
     * @param method
     * @return
     */
    public static String getSuffix(Method method){
        if (isGetMethod(method)){
            return method.getName().substring(GET.length());
        } else if (isIsMethod(method)){
            return method.getName().substring(IS.length());
        }
        return method.getName();
    }

    /**
     * Get the suffix for an is, get, or set method name
     * @param methodName
     * @return
     */
    public static String getSuffix(String methodName){
        if (methodName.startsWith(GET)){
            return methodName.substring(GET.length());
        } 
        else if (methodName.startsWith(IS)){
            return methodName.substring(IS.length());
        }
        else if (methodName.startsWith(SET)) {
            return methodName.substring(SET.length());
        }
        return methodName;
    }

    /**
     * @param method
     * @return
     */
    public static boolean isIsMethod(Method method){
        return method.getName().startsWith(IS);
    }

    /**
     * @param method
     * @return
     */
    public static boolean isGetMethod(Method method){
        return method.getName().startsWith(GET);
    }

    /**
     * @param method
     * @return true if method name begins with "is" or "get"
     */
    public static boolean isGetterMethod(Method method){
        final String name = method.getName();
        return name.startsWith(GET) || name.startsWith(IS);
    }

    /**
     * 
     * @param pClass
     * @return a list including this class and all the classes it inherits from up to and not including Object.
     */
    public static List<Class> getAllClasses(Class pClass){
        List<Class> result = new ArrayList<Class>();
        if (!pClass.equals(Object.class)){
            result.add(pClass);
            result.addAll(getAllClasses(pClass.getSuperclass()));
        }
        return result;
    }

    /**
     * Get all the interfaces for a class and its ancestors
     * @param pClass
     * @return
     */
    public static List<Class> getAllInterfaces(Class pClass){
        List<Class> interfaces = new ArrayList<Class>();
        for (Class c : pClass.getInterfaces()) {
            interfaces.add(c);
        }
        if (!pClass.equals(Object.class)){
            interfaces.addAll(getAllInterfaces(pClass.getSuperclass()));
        }
        return interfaces;
    }
    
    /**
     * Returns true if this class conforms to the java bean spec and you can add a property change listener
     * @param pClass
     * @return
     */
    public static boolean isBean(Class pClass){
    	try {
    		pClass.getMethod("addPropertyChangeListener", new Class[] {java.beans.PropertyChangeListener.class});
    		return true;
    	} catch (NoSuchMethodException mnfe){
    		return false;
    	}
    }

    public static String upperFirstChar(String string, boolean lowerTheRest){
        if (string == null){
            return null;
        }
        if (string.length() == 0){
            return "";
        }
        
        StringBuffer result = new StringBuffer();
        result.setLength(1);
        String upperFirst = string.substring(0, 1);
        result.setCharAt(0, upperFirst.toUpperCase().charAt(0));
        
        if (string.length() == 1){
            return result.toString();
        }
        
        String rest;
        if (lowerTheRest) {
            rest = string.substring(1).toLowerCase();
        } else {
            rest = string.substring(1);
        }
        result.append(rest);
        return result.toString();
    }

}
