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

import gov.nasa.arc.irg.plan.converters.IntToPlanPayloadConfigConverter;
import gov.nasa.arc.irg.plan.converters.PlanPayloadConfigToIntConverter;
import gov.nasa.arc.irg.plan.freeflyer.config.PlanPayloadConfig;
import gov.nasa.arc.irg.plan.ui.io.PlanPayloadConfigListLoader;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PowerOnItemWidget extends AbstractFreeFlyerWidget {
	protected Label titleLabel;
	private Combo payloadsCombo;
	protected Label powerLabel;

	public PowerOnItemWidget(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	void setupCustomControls() {
		Label l = new Label(this, SWT.NONE);
		l.setText("Payload");
		addChildLabel(l);

		payloadsCombo = new Combo(this, SWT.READ_ONLY);
		try {
			String[] items = PlanPayloadConfigListLoader.getArrayOfNames();
			payloadsCombo.setItems(items);
			if(items.length > 1) {
				payloadsCombo.select(0);
			}
			payloadsCombo.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					String selectedText = payloadsCombo.getText();
					if(!selectedText.equals("")) {
						try {
							PlanPayloadConfig config = 
									PlanPayloadConfigListLoader.getPlanPayloadConfigFromName(selectedText);
							powerLabel.setText(Double.toString(config.getPower()));
						} catch (Exception ex) {
							String msg = ex.getMessage();
							if (msg == null) {
								msg = "No message available";
							}
							showErrorDialog(shell, "Error Reading Plan Payload Config File", msg);
						}
					}
				}
			});
		} catch (Exception e) {
			showErrorDialog(shell, "Error Reading Plan Payload Config File", e.getMessage());
		}
		addChildControl(payloadsCombo);
		new Label(this, SWT.NONE);

		Label pl = new Label(this, SWT.NONE);
		pl.setText("Power");
		addChildLabel(pl);

		powerLabel = new Label(this, SWT.NONE);
		powerLabel.setText("0.0\t");
		addChildLabel(powerLabel);

		Label powerUnitsLabel = new Label(this, SWT.NONE);
		powerUnitsLabel.setText("W");
		addChildLabel(powerUnitsLabel);
	}

	@Override
	public boolean bindUI(Realm realm) {
		if (getModel() == null){
			setBound(false);
			return false;
		}

		updateNonBoundFields();

		boolean result = true;
		result &= bindSpecial("planPayloadConfig", payloadsCombo, new IntToPlanPayloadConfigConverter(), new PlanPayloadConfigToIntConverter());

		return result;
	}
}
