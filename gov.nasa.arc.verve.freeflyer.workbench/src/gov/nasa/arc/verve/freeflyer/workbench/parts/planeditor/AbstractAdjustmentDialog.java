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
package gov.nasa.arc.verve.freeflyer.workbench.parts.planeditor;

import gov.nasa.arc.irg.plan.model.Sequenceable;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.arc.verve.freeflyer.workbench.helpers.PlaneListenerRegistry;
import gov.nasa.arc.verve.freeflyer.workbench.undo.DelegateCommandStack;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractAdjustmentDialog extends Dialog {
	private Color colorOne = ColorProvider.get(213, 94, 163);
	private Color colorTwo = ColorProvider.get(59, 181, 74);
	private Color colorThree = ColorProvider.get(59, 82, 164);
	
	private Text xInput, yInput, zInput;
	private String[] axisLabels = {"X", "Y", "Z"};
	private Color[] colors = {colorOne, colorTwo, colorThree};
	
	private int span = 8;
	@Inject @Optional
	private Sequenceable selected;
	@Inject @Optional
	protected DelegateCommandStack delegateCommandStack;
	
	public AbstractAdjustmentDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.CLOSE | SWT.MODELESS | SWT.BORDER | SWT.TITLE);
	}
	
	public String getSelectedIndex(int i) {
		if(selected instanceof Station) {
			return Float.toString(selected.getEndPosition().getCoordinates().get(i));
		} else {
			return "0";
		}
	}

	@Override
	public Control createDialogArea(Composite parent) {
		GridLayout glParent = new GridLayout(2, false);
		parent.setLayout(glParent);
		
		Composite topRow = new Composite(parent, SWT.None);
		
		GridLayout gl = new GridLayout(span, false);
		topRow.setLayout(gl);

		xInput = makeAxisInput(topRow, 0);
		yInput = makeAxisInput(topRow, 1);
		zInput = makeAxisInput(topRow, 2);

		Label mLabel = new Label(topRow, SWT.LEFT);
		mLabel.setText("m");
		
		createButtons(parent);	
		
		return parent;
	}
	
	protected abstract void createButtons(Composite parent);
		
	private Text makeAxisInput(Composite parent, int ordinal) {
		Label theLabel = new Label(parent, SWT.LEFT);
		theLabel.setText(axisLabels[ordinal]);
		theLabel.setForeground(colors[ordinal]);
		Text inputText = new Text(parent, SWT.BORDER);
		inputText.setText(getSelectedIndex(ordinal));
		inputText.addListener(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				if (e.character == SWT.CR) {
					 handleEnter();
				}
			}
		});
		return inputText;
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Adjust Repositioning Plane");
	}
	
	protected void handleEnter() {
		float x = Float.valueOf(xInput.getText());
		float y = Float.valueOf(yInput.getText());
		float z = Float.valueOf(zInput.getText());
		
		PlaneListenerRegistry.movePlane(x, y, z);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// They will throw an IllegalStateException if you hit enter - very nasty
	}
}
