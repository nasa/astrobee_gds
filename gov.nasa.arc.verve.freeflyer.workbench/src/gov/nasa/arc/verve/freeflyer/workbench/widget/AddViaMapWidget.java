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

import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class AddViaMapWidget extends AbstractDatabindingWidget {
	private Label titleLabel;
	private IncrementableText zText;
	
	public AddViaMapWidget(Composite parent, int style) {
		super(parent, style);
		GuiUtils.giveGridLayout(this, 3);

		setupTitleData();
		setupIncrementableText();
	}
	
	private void setupIncrementableText() {
		new Label(this, SWT.NONE).setText("Plane Adjustment: Z");
		zText = new IncrementableText(this, SWT.NONE, .1);
	}
	
	protected void setupTitleData() {
		GridData titleData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		titleData.horizontalSpan = 3;

		titleLabel = new Label(this, SWT.BORDER);
		titleLabel.setLayoutData(titleData);

		Font bigFont = GuiUtils.makeBigFont(this, titleLabel, 14);
		titleLabel.setFont( bigFont );
		titleLabel.setText("Add Station via 3D Mode");
	}
	
	@Override
	public void dispose() {
		zText.dispose();
		super.dispose();
	}
	
	@Override
	public boolean bindUI(Realm realm) {
		boolean result = true;
		result &= bind("z", zText.getTextControl());
		setBound(result);
		return result;
	}
	
	@Override
	public void setModel(Object obj) {
		unbindUI();
		m_model = obj;
		bindUI(getRealm());
	}

}
