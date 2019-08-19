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

import gov.nasa.arc.irg.plan.converters.IntToTelemetryNameConverter;
import gov.nasa.arc.irg.plan.converters.TelemetryNameToIntConverter;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableTextHorizontalInt;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_COMM_STATUS;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_CPU_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_DISK_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_EKF_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_GNC_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE;
import rapid.ext.astrobee.SETTINGS_TELEMETRY_TYPE_POSITION;

public class SetTelemetryRateWidget extends AbstractFreeFlyerWidget {
	private Combo nameCombo;
	protected IncrementableTextHorizontalInt rateText;
	protected List<IncrementableText> m_incrementables;	// list of child controls

	public SetTelemetryRateWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		m_incrementables = new ArrayList<IncrementableText>();
		
		Label l = new Label(this, SWT.NONE);
		l.setText("Telemetry Name");
		addChildLabel(l);
		
		nameCombo = new Combo(this, SWT.READ_ONLY);
		GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gdata.horizontalSpan = 2;
		nameCombo.setLayoutData(gdata);

		nameCombo.setItems(new String[]{
				SETTINGS_TELEMETRY_TYPE_COMM_STATUS.VALUE,
				SETTINGS_TELEMETRY_TYPE_CPU_STATE.VALUE,
				SETTINGS_TELEMETRY_TYPE_DISK_STATE.VALUE,
				SETTINGS_TELEMETRY_TYPE_EKF_STATE.VALUE,
				SETTINGS_TELEMETRY_TYPE_GNC_STATE.VALUE,
				SETTINGS_TELEMETRY_TYPE_PMC_CMD_STATE.VALUE,
				SETTINGS_TELEMETRY_TYPE_POSITION.VALUE});
		addChildControl(nameCombo);
	
		Label l2 = new Label(this, SWT.NONE);
		l2.setText("Rate");
		addChildLabel(l2);
			
		rateText = new IncrementableTextHorizontalInt(this,5,5);
		rateText.setAllowableRange(0.0, 500);
		//GridData gdata = new GridData(SWT.FILL, SWT.CENTER, true, false);
		//gdata.horizontalSpan = 2;
		//xText.setLayoutData(gdata);
		m_incrementables.add(rateText);
		
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
		result &= bindSpecial("telemetryName", nameCombo, new IntToTelemetryNameConverter(), new TelemetryNameToIntConverter());
		result &= bind("rate", rateText.getTextControl());

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
