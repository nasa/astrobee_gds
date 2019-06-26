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

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ColorSelectorWidget extends AbstractFieldWidget {
	
	protected ColorSelector m_colorSelector;

	public ColorSelectorWidget(Composite parent, int style) {
		super(parent, style);
		createControls(this, false);
	}

	public ColorSelectorWidget(Composite parent, int style, boolean horizontal) {
		super(parent, style);
		createControls(this, horizontal);
	}
	
	/**
	 * Actually create the UI components
	 * @param parent
	 */
	public void createControls(Composite container, boolean horizontal) {

		final GridLayout gridLayout = new GridLayout(1, false);
		setLayout(gridLayout);

		m_colorSelector = new ColorSelector(container);
		addChildControl(m_colorSelector.getButton());
	}

	

	@Override
	public boolean bindUI(Realm realm) {
		setRealm(realm);
		
		if (getModel() == null){
			setBound(false);
			return false;
		}
		
		boolean worked = bind(getFeature(), m_colorSelector);
		setBound(worked);
		return worked;
	}

}
