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
package gov.nasa.arc.verve.freeflyer.workbench.widget.helpers;

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedListener;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.SelectedAgentConnectedRegistry;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Automatically disables if DDS dies.  Will enable automatically if compositeEnabled is true.
 */
public class CommandButton extends Composite implements SelectedAgentConnectedListener {

	private static final Logger logger = Logger.getLogger(CommandButton.class);

	protected boolean ddsConnected = false;
	protected boolean compositeEnabled = false;
	protected Button button;
	protected int size = 11;

	public CommandButton(Composite parent, int style) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);
		button = new Button(this, style);

		Font boldFont = null;
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.ENLARGE)) {
			boldFont = new Font(button.getDisplay(), "Enlarged", WorkbenchConstants.enlargedFontSize, SWT.BOLD);
		}else{
			boldFont = FontDescriptor.createFrom(button.getFont()).setStyle(SWT.BOLD).createFont(button.getDisplay());
		}
		
		button.setFont( boldFont );

		listenToCorrectRegistry();
	}

	public void setToolTipText(String text) {
		button.setToolTipText(text);
	}

	protected void listenToCorrectRegistry() {
		SelectedAgentConnectedRegistry.addListener(this);
	}

	protected void removeFromCorrectRegistry() {
		SelectedAgentConnectedRegistry.removeListener(this);
	}

	@Override
	public void dispose() {
		removeFromCorrectRegistry();
	}

	public void setButtonLayoutData(Object layoutData) {
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.ENLARGE)) {
			if(layoutData instanceof GridData) {
				((GridData) layoutData).widthHint = WorkbenchConstants.enlargedWidth;
				((GridData) layoutData).heightHint = WorkbenchConstants.enlargedHeight;
			}
		}
		button.setLayoutData(layoutData);
	}

	public boolean getSelection() {
		return button.getSelection();
	}

	public void setText(String text) {
		button.setText(text);
	}

	public String getText(){
		return button.getText();
	}

	public void addSelectionListener(SelectionListener sl) {
		button.addSelectionListener(sl);
	}

	public void setDisplay() {
		setEnabled(ddsConnected && compositeEnabled);
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(!button.isDisposed()) {
			super.setEnabled(enabled);
			button.setEnabled(enabled);
		}
	}

	public void setCompositeEnabled(boolean enabled) {
		compositeEnabled = enabled;
		setDisplay();
	}

	@Override
	public void onSelectedAgentConnected() {
		Display.getDefault().asyncExec(new Runnable(){
			@Override
			public void run() {
				ddsConnected = true;
				setDisplay();
			}
		});
	}

	@Override
	public void onSelectedAgentDisconnected() {
		Display.getDefault().asyncExec(new Runnable(){

			@Override
			public void run() {
				ddsConnected = false;
				setDisplay();
			}
		});
	}

	@Override
	public String toString() {
		return button.getText();
	}
}
