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
package gov.nasa.arc.irg.util.bean;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

// Minimal JavaBeans support
public abstract class AbstractModelObject implements Cloneable, IHasPropertyChangeListeners {
	//private Logger logger = Logger.getLogger(AbstractModelObject.class);

	private PropertyChangeSupport m_propertyChangeSupport = new PropertyChangeSupport(this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		PropertyChangeListener[] existing = m_propertyChangeSupport.getPropertyChangeListeners();
		for (PropertyChangeListener l : existing){
			if (l.equals(listener)){
//				logger.warn("Trying to add property change listener more than once " + this.getClass().getSimpleName() + " : " + listener.toString());
				return;
			}
		}
		m_propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		PropertyChangeListener[] existing = m_propertyChangeSupport.getPropertyChangeListeners(propertyName);
		for (PropertyChangeListener l : existing){
			if (l.equals(listener)){
//				logger.warn("Trying to add property change listener more than once " + this.getClass().getSimpleName() + " : " + propertyName + " : " + listener.toString());
				return;
			}
		}
		m_propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		m_propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		m_propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//		String newstring = "";
//		if (newValue != null){
//			newstring = newValue.toString();
//		}
//		logger.info("PROP CHANGE " + this.getClass().getSimpleName() + ":" + propertyName + ": " + newstring);
		m_propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		AbstractModelObject newObject =  (AbstractModelObject)super.clone();
		newObject.m_propertyChangeSupport = new PropertyChangeSupport(newObject);
		return newObject;
	}
}
