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

import java.text.NumberFormat;
import java.text.ParseException;

import gov.nasa.ensemble.ui.databinding.status.ISimpleStatus;
import gov.nasa.ensemble.ui.databinding.status.SimpleStatus;

public class NumberLimitsValidator<N extends Number> implements ISimpleValidator {
	protected N m_max;	// the maximum
	protected N m_min;	// the minimum
	
	protected boolean m_minCanEqual = false;	// if the min value can equal what is set
	protected boolean m_maxCanEqual = false;	// if the max value can equal what is set
	
	/* (non-Javadoc)
	 * @see gov.nasa.ensemble.ui.databinding.validation.ISimpleValidator#validate(java.lang.Object)
	 */
	public ISimpleStatus validate(Object object) {
		N number;
		try {
			if (object instanceof String){
				try {
					number = (N) NumberFormat.getInstance().parse((String)object);
				} catch (ParseException e) {
					return SimpleStatus.error(e);
				}
			} else if (object instanceof Number){
				number = (N)object;
			} else {
				return SimpleStatus.error("Number is not readable: " + object.toString());
			}
			if (number.doubleValue() < m_min.doubleValue() || (!m_minCanEqual && number == m_min)){
				return SimpleStatus.error(number + " is too low; minimum is " + m_min);
			}
			if (number.doubleValue() > m_max.doubleValue() || (!m_maxCanEqual && number == m_max)){
				return SimpleStatus.error(number + " is too high; maximum is " + m_max);
			}
		} catch (NumberFormatException nfe){
			return SimpleStatus.error(nfe);
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
	public NumberLimitsValidator(N min, N max, boolean minCanEqual, boolean maxCanEqual){
		m_min = min;
		m_max = max;
		m_minCanEqual = minCanEqual;
		m_maxCanEqual = maxCanEqual;
	}
	
	/**
	 * @return
	 */
	public N getMax() {
		return m_max;
	}

	/**
	 * @param max
	 */
	public void setMax(N max) {
		m_max = max;
	}

	/**
	 * @return
	 */
	public N getMin() {
		return m_min;
	}

	/**
	 * @param min
	 */
	public void setMin(N min) {
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
