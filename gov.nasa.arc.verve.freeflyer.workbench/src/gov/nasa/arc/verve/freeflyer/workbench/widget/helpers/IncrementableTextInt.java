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

import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class IncrementableTextInt extends IncrementableText {

	public IncrementableTextInt(Composite parent, double initialInput,
			double incrementSize) {
		super(parent, initialInput, incrementSize, 0, 1);
	}
	
	@Override
	protected void makeUpButton(Composite parent) {
		up = new Button(textAndArrowsComposite, SWT.ARROW | SWT.UP);
		up.setBounds(textWidth, 0, buttonWidth, buttonHeight);
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int num = new Integer(scrubText(text.getText())).intValue();
				num += incrementSize;
				text.setText(new Integer(num).toString());
				text.selectAll();
				
				if((num + incrementSize) > maxValue) {
					up.setEnabled(false);
				}
				if((num - incrementSize) >= minValue) {
					down.setEnabled(true);
				}
			}
		});
	}
	
	/** return String with no decimal places or letters */
	protected String scrubText(String in) {
		if(Pattern.matches("^-?\\d*(\\.\\d+)?$", in) == true) {
			if(in.contains(".")) {
				String[] split = in.split("\\.");
				if(split[0].length() > 0) {
					return split[0];
				} else {
					return "0";
				}
			}
			
			return in;
		}
		return "0";
	}
	
	
	@Override
	protected void makeDownButton(Composite parent) {
		down = new Button(textAndArrowsComposite, SWT.ARROW | SWT.DOWN);
		down.setBounds(textWidth, buttonHeight, buttonWidth, buttonHeight);
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int num = new Integer(scrubText(text.getText())).intValue();
				num -= incrementSize;
				text.setText(new Integer(num).toString());
				text.selectAll();
				
				if((num + incrementSize) <= maxValue) {
					up.setEnabled(true);
				}
				if((num - incrementSize) < minValue) {
					down.setEnabled(false);
				}
			}
		});
	}
	
	@Override
	public void setTextString(double num) {
		setTextStringInt((int)num);
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
		return Integer.valueOf(text.getText());
	}
}
