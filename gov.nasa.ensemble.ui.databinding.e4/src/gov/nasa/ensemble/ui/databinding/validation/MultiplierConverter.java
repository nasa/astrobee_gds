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
package gov.nasa.ensemble.ui.databinding.validation;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

/**
 * A simple converter that will multiply or divide a class.
 * This is used by the annotation reader for @Multiplier
 * @author tecohen
 *
 */
@SuppressWarnings("unchecked")
public class MultiplierConverter implements ISimpleConverter {
	final private static Logger logger = Logger.getLogger(MultiplierConverter.class);
	
	double m_multiplier = 1.0;
	double m_inverse = 1.0;
	Class m_modelType;

	public MultiplierConverter(double multiplier, Class modelType){
		m_multiplier = multiplier;
		m_modelType = modelType;
		if (m_multiplier != 0){
			m_inverse = 1.0/multiplier;
		}
	}
	public Class getModelType() {
		return m_modelType;
	}

	public Class getTargetType() {
		return String.class;
	}

	public Object toModel(Object target) {
		if (target != null && target instanceof String){
			try {
				double number = Double.parseDouble((String)target);
				number *= m_inverse;
				
				Method method = m_modelType.getMethod("valueOf", new Class[]{String.class});
				Object result  = method.invoke(null, Double.toString(number));
				return result;
			} catch (SecurityException e) {
				logger.warn(e);
			} catch (NoSuchMethodException e) {
				logger.warn(e);
			} catch (IllegalArgumentException e) {
				logger.warn(e);
			} catch (IllegalAccessException e) {
				logger.warn(e);
			} catch (InvocationTargetException e) {
				logger.warn(e);
			}
		}
		return target;
	}

	public Object toTarget(Object model) {
		if (model != null && model instanceof Number){
			Double number = Double.parseDouble(((Number)model).toString());
			number *= m_multiplier;
			return number.toString();
		}
		return model;
	}

	public double getMultiplier() {
		return m_multiplier;
	}

	public void setMultiplier(double multiplier) {
		m_multiplier = multiplier;
	}

	public double getInverse() {
		return m_inverse;
	}

	public void setInverse(double inverse) {
		m_inverse = inverse;
	}

	public void setModelType(Class modelType) {
		m_modelType = modelType;
	}

}
