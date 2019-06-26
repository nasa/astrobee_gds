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

import gov.nasa.ensemble.ui.databinding.status.ISimpleStatus;
import gov.nasa.ensemble.ui.databinding.status.SimpleStatus;

/**
 * A simple validator that tests if a number is within limits
 * @author tecohen
 *
 */
public class LimitsValidator implements ISimpleValidator {

	protected double m_max = 0;	// the maximum
	protected double m_min = 0;	// the minimum
	
	protected boolean m_minCanEqual = false;	// if the min value can equal what is set
	protected boolean m_maxCanEqual = false;	// if the max value can equal what is set
	
	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.validation.ISimpleValidator#validate(java.lang.Object)
	 */
	public ISimpleStatus validate(Object object) {
		double number = 0;
		try {
			if (object instanceof String){
				number = Double.parseDouble((String)object);
			} else if (object instanceof Number){
				number = Double.parseDouble(object.toString());
			}
		} catch (NumberFormatException nfe){
			return SimpleStatus.error(nfe);
		}
		if (number < m_min || (!m_minCanEqual && number == m_min)){
			return SimpleStatus.error(number + " is too low; minimum is " + m_min);
		}
		if (number > m_max || (!m_maxCanEqual && number == m_max)){
			return SimpleStatus.error(number + " is too high; maximum is " + m_max);
		}
		return SimpleStatus.OK_STATUS;
	}

	/**
	 * Constructor
	 * @param min
	 * @param max
	 * @param minCanEqual
	 * @param maxCanEqual
	 */
	public LimitsValidator(double min, double max, boolean minCanEqual, boolean maxCanEqual){
		m_min = min;
		m_max = max;
		m_minCanEqual = minCanEqual;
		m_maxCanEqual = maxCanEqual;
	}
	
	/**
	 * @return
	 */
	public double getMax() {
		return m_max;
	}

	/**
	 * @param max
	 */
	public void setMax(double max) {
		m_max = max;
	}

	/**
	 * @return
	 */
	public double getMin() {
		return m_min;
	}

	/**
	 * @param min
	 */
	public void setMin(double min) {
		m_min = min;
	}

	/**
	 * @return
	 */
	public boolean isMinCanEqual() {
		return m_minCanEqual;
	}

	/**
	 * @param minCanEqual
	 */
	public void setMinCanEqual(boolean minCanEqual) {
		m_minCanEqual = minCanEqual;
	}

	/**
	 * @return
	 */
	public boolean isMaxCanEqual() {
		return m_maxCanEqual;
	}

	/**
	 * @param maxCanEqual
	 */
	public void setMaxCanEqual(boolean maxCanEqual) {
		m_maxCanEqual = maxCanEqual;
	}

}
