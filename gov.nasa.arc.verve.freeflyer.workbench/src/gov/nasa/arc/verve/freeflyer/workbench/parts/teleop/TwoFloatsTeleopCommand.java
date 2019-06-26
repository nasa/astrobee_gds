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
package gov.nasa.arc.verve.freeflyer.workbench.parts.teleop;

import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.CommandButton;
import gov.nasa.arc.verve.freeflyer.workbench.widget.helpers.IncrementableText;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TwoFloatsTeleopCommand extends AbstractTeleopCommandConfig {
	protected String label2;
	protected String paramName1;
	protected String paramName2;
	protected float increment;
	protected float[] range1;
	protected float[] range2;
	protected String units1;
	protected String units2;
	protected double DEG_TO_RAD = Math.PI / 180.0;
	
	protected IncrementableText firstText, secondText;

	public TwoFloatsTeleopCommand() {
		// for JSON
	}
	
	@Override
	public void createWidget(Composite parent) {
		createNameLabel(parent);
		createFirstInput(parent);
		createSendButton(parent);

		createSecondLabel(parent);
		createSecondInput(parent);
		new Label(parent, SWT.None);
	}

	protected void createFirstInput(Composite parent) {
		Composite inner = new Composite(parent, SWT.None);
		GridLayout gl = new GridLayout(2, false);
		inner.setLayout(gl);
		
		GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		inner.setLayoutData(gd);
		
		firstText = new IncrementableText(inner, 0, increment);
		firstText.setAllowableRange(range1[0], range1[1]);
		
		Label l = new Label(inner, SWT.NONE);
		l.setText(units1);
	}
	
	protected void createSecondInput(Composite parent) {
		Composite inner = new Composite(parent, SWT.None);
		GridLayout gl = new GridLayout(2, false);
		inner.setLayout(gl);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true,false).applyTo(inner);
		
		secondText = new IncrementableText(inner, 0, increment);
		secondText.setAllowableRange(range2[0], range2[1]);
		
		Label l = new Label(inner, SWT.NONE);
		l.setText(units2);
	}
	
	@Override
	protected void createSendButton(Composite parent) {
		button = new CommandButton(parent, SWT.NONE);
		button.setText(buttonText);
		
		button.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		button.setButtonLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				float firstNum = (float) (firstText.getNumber() * DEG_TO_RAD);
				float secondNum = (float) (secondText.getNumber() * DEG_TO_RAD);
				
				commandPublisher.sendGenericTwoFloatsCommand(command, subsystem, paramName1, firstNum, paramName2, secondNum);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)  {/**/}
		});
	}
	
	protected void createSecondLabel(Composite parent) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(label2);
	}

	public String getParamName1() {
		return paramName1;
	}

	public void setParamName1(String paramName1) {
		this.paramName1 = paramName1;
	}

	public String getParamName2() {
		return paramName2;
	}

	public void setParamName2(String paramName2) {
		this.paramName2 = paramName2;
	}

	public float getIncrement() {
		return increment;
	}

	public void setIncrement(float increment) {
		this.increment = increment;
	}

	public float[] getRange1() {
		return range1;
	}

	public void setRange1(float[] range1) {
		if(range1.length != 2) {
			throw new IllegalArgumentException("Range1 must be [min,max]");
		}
		this.range1 = range1;
	}

	public float[] getRange2() {
		return range2;
	}

	public void setRange2(float[] range2) {
		if(range2.length != 2) {
			throw new IllegalArgumentException("Range2 must be [min,max]");
		}
		this.range2 = range2;
	}

	public String getLabel2() {
		return label2;
	}

	public void setLabel2(String label2) {
		this.label2 = label2;
	}

	public String getUnits1() {
		return units1;
	}

	public void setUnits1(String units1) {
		this.units1 = units1;
	}

	public String getUnits2() {
		return units2;
	}

	public void setUnits2(String units2) {
		this.units2 = units2;
	}
}
