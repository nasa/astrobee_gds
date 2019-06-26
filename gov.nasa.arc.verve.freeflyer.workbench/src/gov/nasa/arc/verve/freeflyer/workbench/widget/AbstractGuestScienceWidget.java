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

import gov.nasa.arc.irg.plan.converters.GuestScienceConfigToIntConverter;
import gov.nasa.arc.irg.plan.converters.IntToGuestScienceConfigConverter;
import gov.nasa.arc.irg.plan.ui.io.GuestScienceConfigListLoader;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public abstract class AbstractGuestScienceWidget extends AbstractFreeFlyerWidget {
	
	protected Combo guestScienceCombo;
	
	public AbstractGuestScienceWidget(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	void setupCustomControls() {
		Label l = new Label(this, SWT.NONE);
		l.setText("Guest Science");
		addChildLabel(l);
		
		guestScienceCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		guestScienceCombo.setLayoutData(gdata);
		try {
			guestScienceCombo.setItems(GuestScienceConfigListLoader.getArrayOfConfigNames());	
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Guest Science Config", e.getMessage());
		}
		addChildControl(guestScienceCombo);
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("guestScienceApkGds", guestScienceCombo, new IntToGuestScienceConfigConverter(), new GuestScienceConfigToIntConverter());

		return result;
	}

}
