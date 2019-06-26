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
package gov.nasa.ensemble.ui.databinding.databinding;

import gov.nasa.ensemble.ui.databinding.util.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.databinding.observable.value.ValueDiff;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Support undo/redo for pojo objects
 * @author tecohen
 *
 */
@SuppressWarnings("unchecked")
public class UndoablePOJOSetOperation extends AbstractOperation {
	
	final private static Logger logger = Logger.getLogger(UndoablePOJOSetOperation.class);

	public static final String SET = "set";
	
	protected Object m_object;		// the POJO we are editing
	protected String m_feature;		// the name of the property we are modifying
	protected Object m_newValue;	// the new value
	protected Object m_oldValue;	// the old value
	protected Method m_setMethod;	// the set method
	
	public UndoablePOJOSetOperation(Object object, String feature, ValueDiff diff) {
		super("Set " + feature);
		if (object != null && feature != null){
			setObject(object);
			setFeature(feature);
			String property = StringUtil.upperFirstChar(feature, false);
			Object parameterValue = diff.getOldValue();
			// there was an old value
			if (parameterValue == null) {
				parameterValue = object;
			}
			if (parameterValue.getClass() != null && parameterValue.getClass().isPrimitive()){
				try {
					Class oldClass = parameterValue.getClass();
					Class primitiveOldClass = primitiveTypeFor(oldClass);
					if (primitiveOldClass != null){
						m_setMethod = object.getClass().getMethod(SET + property, primitiveOldClass);
					} 
				} catch (SecurityException e1) {
					logger.warn(e1);
				} catch (NoSuchMethodException e1) {
					logger.warn(e1);
				} catch (IllegalArgumentException e1) {
					logger.warn(e1);
				}
			}
			
			try {
				Class oldClass = parameterValue.getClass();
				m_setMethod = object.getClass().getMethod(SET + property, oldClass);
			} catch (SecurityException e) {
				logger.warn(e);
			} catch (NoSuchMethodException e) {
				try {
					Class oldClass = parameterValue.getClass();
					Class primitiveOldClass = primitiveTypeFor(oldClass);
					if (primitiveOldClass != null){
						m_setMethod = object.getClass().getMethod(SET + property, primitiveOldClass);
					} 
				} catch (SecurityException e1) {
					logger.warn(e);
				} catch (NoSuchMethodException e1) {
					logger.warn(e);
				} catch (IllegalArgumentException e1) {
					logger.warn(e);
				}
				
			} catch (IllegalArgumentException e) {
				logger.warn(e);
			}
			
		}
		setOldValue(diff.getOldValue());
		setNewValue(diff.getNewValue());
	}
	
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (m_setMethod == null){
			throw new ExecutionException("No set method for " + m_feature);
		}
		try {
			m_setMethod.invoke(m_object, m_newValue);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ExecutionException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ExecutionException(e.getMessage());
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (m_setMethod == null){
			throw new ExecutionException("No set method for " + m_feature);
		}
		
		try {
			m_setMethod.invoke(m_object, m_newValue);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ExecutionException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ExecutionException(e.getMessage());
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (m_setMethod == null){
			throw new ExecutionException("No set method for " + m_feature);
		}
		
		try {
			m_setMethod.invoke(m_object, m_oldValue);
		} catch (IllegalArgumentException e) {
			throw new ExecutionException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new ExecutionException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new ExecutionException(e.getMessage());
		}
		return Status.OK_STATUS;
	}

	public Object getObject() {
		return m_object;
	}

	public void setObject(Object object) {
		m_object = object;
	}

	public String getFeature() {
		return m_feature;
	}

	public void setFeature(String feature) {
		m_feature = feature;
	}

	public Object getNewValue() {
		return m_newValue;
	}

	public void setNewValue(Object newValue) {
		m_newValue = newValue;
	}

	public Object getOldValue() {
		return m_oldValue;
	}

	public void setOldValue(Object oldValue) {
		m_oldValue = oldValue;
	}
	
	 public static Class primitiveTypeFor(Class wrapper) {
	        if (wrapper == Boolean.class) return Boolean.TYPE;
	        if (wrapper == Byte.class) return Byte.TYPE;
	        if (wrapper == Character.class) return Character.TYPE;
	        if (wrapper == Short.class) return Short.TYPE;
	        if (wrapper == Integer.class) return Integer.TYPE;
	        if (wrapper == Long.class) return Long.TYPE;
	        if (wrapper == Float.class) return Float.TYPE;
	        if (wrapper == Double.class) return Double.TYPE;
	        if (wrapper == Void.class) return Void.TYPE;
	        return null;
	    }
}
