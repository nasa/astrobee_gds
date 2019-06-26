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
package gov.nasa.ensemble.ui.databinding.widgets;

import org.eclipse.swt.widgets.Composite;

/**
 * This is a bindable widget designed to represent a field in another bound widget.
 * As such it should not have a label as the containing widget will provide the label.
 * 
 * AbstractFieldWidgets should be bound to the class via the FieldWidget extension point.
 * @author tecohen
 *
 */
public abstract class AbstractFieldWidget extends AbstractDatabindingWidget {
	
	String m_feature;	// the feature bound to this field widget from the parent widget's class.
	
	/**
	 * @param parent
	 * @param style
	 */
	public AbstractFieldWidget(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param horizontal
	 */
	public AbstractFieldWidget(Composite parent, int style, boolean horizontal) {
		super(parent, style);
	}
	

	/**
	 * @return the feature.  Default is null.
	 */
	public String getFeature() {
		return m_feature;
	}

	/**
	 * Set the feature.
	 * @param feature
	 */
	public void setFeature(String feature) {
		m_feature = feature;
	}
}
