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
package gov.nasa.arc.verve.freeflyer.workbench.widget.helpers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class IncrementableTextHorizontalInt extends IncrementableTextHorizontal {

	public IncrementableTextHorizontalInt(Composite parent, double initialInput,
			double incrementSize) {
		super(parent, initialInput, incrementSize);
		setTextStringInt((int)initialInput);
	}
	
	@Override
	protected void makeLeftButton(Composite parent) {
		down = new Button(textAndArrowsComposite, SWT.ARROW | SWT.LEFT);
		down.setBounds(0, 0, buttonWidth, buttonHeight);
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String value = text.getText();
				int dot = value.indexOf(".");
				if(dot > 0) {
					value = text.getText().substring(0, dot);
				}
				int num = 0;
				try{
					num = new Integer(value).intValue();
				} catch (NumberFormatException nfe) {
					//
				}
				num -= incrementSize;
				text.setText(new Integer(num).toString());
				text.selectAll();
				
				if((num + incrementSize) <= maxValue) {
					up.setEnabled(true);
				}
				if((num - incrementSize) < minValue) {
					down.setEnabled(false);
				}
				
				updateBinding();
			}
		});
	}
	
	@Override
	protected void makeRightButton(Composite parent) {
		up = new Button(textAndArrowsComposite, SWT.ARROW | SWT.RIGHT);
		up.setBounds(textWidth+buttonWidth, 0, buttonWidth, buttonHeight);
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String value = text.getText();
				int dot = value.indexOf(".");
				if(dot > 0) {
					value = text.getText().substring(0, dot);
				}
				int num = 0;
				try{
					num = new Integer(value).intValue();
				} catch (NumberFormatException nfe) {
					//
				}
				num += incrementSize;
				text.setText(new Integer(num).toString());
				text.selectAll();
				
				if((num + incrementSize) > maxValue) {
					up.setEnabled(false);
				}
				if((num - incrementSize) >= minValue) {
					down.setEnabled(true);
				}
				
				updateBinding();
			}
		});
	}
	
	@Override
	public void setArrowToolTipText(String units) {
		if(up != null && down != null) {
			up.setToolTipText("Increase by " + (int)incrementSize + " " + units);
			down.setToolTipText("Decrease by " + (int)incrementSize + " " + units);
		}
	}
	
	public void setTextStringInt(int num) {
		text.setText(((Integer)num).toString());
	}
	
	@Override
	public String getTextString() {
		// Trim any whitespace
		return Integer.valueOf(text.getText()).toString();
	}
	
	@Override
	public double getNumber() {
		return Double.valueOf(text.getText());
	}
}
