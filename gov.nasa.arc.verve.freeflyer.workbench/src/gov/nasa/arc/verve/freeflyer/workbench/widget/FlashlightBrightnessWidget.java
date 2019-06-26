/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.plan.converters.FlashlightLocationToIntConverter;
import gov.nasa.arc.irg.plan.converters.IntToFlashlightLocationConverter;
import gov.nasa.arc.irg.plan.util.PlanConstants;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class FlashlightBrightnessWidget extends AbstractFreeFlyerWidget {
	private Combo whichCombo;
	protected IncrementableText xText;
	protected List<IncrementableText> m_incrementables;	// list of child controls

	public FlashlightBrightnessWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		m_incrementables = new ArrayList<IncrementableText>();
		
		Label l = new Label(this, SWT.NONE);
		l.setText("Flashlight");
		addChildLabel(l);
		
		whichCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		whichCombo.setLayoutData(gdata);
		whichCombo.setItems(PlanConstants.FLASHLIGHT_LOCATION_STRINGS);
		addChildControl(whichCombo);
	
		Label l2 = new Label(this, SWT.NONE);
		l2.setText("Brightness");
		addChildLabel(l2);
			
		xText = new IncrementableText(this, 1.0, 0.1, 1, 0.1);
		xText.setAllowableRange(0.0, 1.0);
		//GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		//gdata.horizontalSpan = 2;
		//xText.setLayoutData(gdata);
		m_incrementables.add(xText);
		
		Label endSpacer = new Label(this, SWT.None);
		
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("which", whichCombo, new IntToFlashlightLocationConverter(), new FlashlightLocationToIntConverter());
		result &= bind("brightness", xText.getTextControl());

		return result;
	}
	
	@Override
	public void dispose() {
		for(IncrementableText it : m_incrementables) {
			it.dispose();
		}
		super.dispose();
	}

}
