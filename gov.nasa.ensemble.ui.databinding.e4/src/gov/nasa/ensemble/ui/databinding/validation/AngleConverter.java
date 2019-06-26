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

import gov.nasa.ensemble.ui.databinding.widgets.customization.annotations.Angle.AngleType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

@SuppressWarnings("unchecked")
public class AngleConverter implements ISimpleConverter {
	
	final private static Logger logger = Logger.getLogger(AngleConverter.class);
	
	AngleType m_displayType;
	AngleType m_storedType;
	Class m_modelType;
	
	public AngleConverter(AngleType displayType, AngleType storedType, Class modelType){
		m_displayType = displayType;
		m_storedType = storedType;
		m_modelType = modelType;
	}
	
	public Class getModelType() {
		return m_modelType;
	}

	public Class getTargetType() {
		return String.class;
	}

	public Object toModel(Object target) {
		if (target != null && target instanceof String){
			String sTarget = (String)target;
			if (sTarget.length() == 0){
				return new Double(0);
			}
			try {
				double number = Double.parseDouble((String)target);
				
				// convert the model to the display type
				if (m_storedType.equals(AngleType.DEGREES)){
					number = Math.toDegrees(number);
				} else {
					number = Math.toRadians(number);
				}
				
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
			// convert the model to the display type
			if (m_displayType.equals(AngleType.DEGREES)){
				number = Math.toDegrees(number);
			} else {
				number = Math.toRadians(number);
			}
			return number.toString();
		}
		return model;
	}

	public AngleType getDisplayType() {
		return m_displayType;
	}

	public void setDisplayType(AngleType displayType) {
		m_displayType = displayType;
	}

	public AngleType getStoredType() {
		return m_storedType;
	}

	public void setStoredType(AngleType storedType) {
		m_storedType = storedType;
	}

	public void setModelType(Class modelType) {
		m_modelType = modelType;
	}

}
