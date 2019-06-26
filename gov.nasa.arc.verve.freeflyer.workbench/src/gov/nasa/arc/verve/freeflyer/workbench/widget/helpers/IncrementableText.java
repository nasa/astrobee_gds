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

import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.eclipse.core.databinding.Binding;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class IncrementableText {
	protected Composite textAndArrowsComposite;
	protected Text text;
	protected Button up, down;
	protected int textWidth = 40;
	protected int buttonWidth = 20;
	protected int buttonHeight = 10;
	protected double roundTo = 0.01; // precision to round to
	protected double halfRoundTo = 0.005;
	protected double incrementSize;
	protected double maxValue = 360;
	protected double minValue = -360;
	protected int precision = 2;
	
	protected DecimalFormat df = new DecimalFormat();
    protected Binding binding;
    
	public IncrementableText(Composite parent, double initialInput, double incrementSize) {
		setupCompositeAndIncrementSize(parent, incrementSize);
		setupTextAndButtons(initialInput);
	}
	
	public IncrementableText(Composite parent, double initialInput, double incrementSize, int textWidth) {
		this.textWidth = textWidth;
		setupCompositeAndIncrementSize(parent, incrementSize);
		setupTextAndButtons(initialInput);
	}
	
	public IncrementableText(Composite parent, double initialInput, double incrementSize, int precision, double roundTo) {
		this.roundTo = roundTo;
		this.halfRoundTo = roundTo / 2.0;
		this.precision = precision;
		setupCompositeAndIncrementSize(parent, incrementSize);
		setupTextAndButtons(initialInput);
	}
	
	public void setBinding(Binding bd) {
		binding = bd;
	}
	
	public void updateBinding() {
		 if(binding != null) {
         	binding.updateTargetToModel();
         	//text.setSelection(text.getText().length());
         }
	}
	
	public void setToolTipText(String tooltip) {
		if(text != null) {
			text.setToolTipText("Enter " + tooltip);
		}
	}
	
	public void setArrowToolTipText(String units) {
		if(up != null && down != null) {
			up.setToolTipText("Increase by " + incrementSize + " " + units);
			down.setToolTipText("Decrease by " + incrementSize + " " + units);
		}
	}
	
	public void setLayoutData(Object layoutData) {
		textAndArrowsComposite.setLayoutData(layoutData);
	}
	
	protected void setupCompositeAndIncrementSize(Composite parent, double incrementSize) {
		textAndArrowsComposite = new Composite(parent, SWT.NONE);
		
		this.incrementSize = incrementSize;
		df.setMinimumFractionDigits(2);
	    df.setMaximumFractionDigits(precision);
	    df.setRoundingMode(RoundingMode.HALF_UP);
	}
	
	protected void setupTextAndButtons(double initialInput) {
		text = new Text(textAndArrowsComposite, SWT.RIGHT | SWT.BORDER);
		text.setBounds(0, 0, textWidth, buttonHeight*2);
		setTextString(initialInput);
		text.addListener(SWT.Traverse, new Listener()
		{
			@Override
			public void handleEvent(Event event)
			{
				if(event.detail == SWT.TRAVERSE_RETURN ||
						event.detail == SWT.TRAVERSE_TAB_NEXT ||
						event.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				{
					updateBinding();
				}
			}
		});
		
		makeUpButton(textAndArrowsComposite);
		makeDownButton(textAndArrowsComposite);
	}
	
	public void setAllowableRange(double min, double max) {
		maxValue = max;
		minValue = min;
	}
	
	public void enable() {
		text.setEnabled(true);
		up.setEnabled(true);
		down.setEnabled(true);
	}
	
	public void disable() {
		text.setEnabled(false);
		up.setEnabled(false);
		down.setEnabled(false);
	}
	
	protected void makeUpButton(Composite parent) {
		up = new Button(textAndArrowsComposite, SWT.ARROW | SWT.UP);
		up.setBounds(textWidth, 0, buttonWidth, buttonHeight);
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double num = 0;
				try{
					num = new Double(text.getText()).doubleValue();
				} catch (NumberFormatException nfe) {
					//
				}
				num += incrementSize;
				text.setText(df.format(round(num)));
				text.selectAll();
				
				verifyRange(num);
				
				updateBinding();
			}
		});
	}
	
	protected void makeDownButton(Composite parent) {
		down = new Button(textAndArrowsComposite, SWT.ARROW | SWT.DOWN);
		down.setBounds(textWidth, buttonHeight, buttonWidth, buttonHeight);
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				double num = 0;
				try{
					num = new Double(text.getText()).doubleValue();
				} catch (NumberFormatException nfe) {
					//
				}
				num -= incrementSize;
				text.setText(df.format(round(num)));
				text.selectAll();
				
				verifyRange(num);
				
				updateBinding();
			}
		});
	}
	
	private void verifyRange(double num){
		if(up == null || down == null) {
			return;
		}
		up.setEnabled(true);
		down.setEnabled(true);
		if((num + incrementSize) > maxValue) {
			up.setEnabled(false);
		}
		if((num - incrementSize) < minValue) {
			down.setEnabled(false);
		}
	}
	
	protected double round(double d) {
		int bigD = (int) (d * 100);
		int roundTo100 = (int) (roundTo * 100);
		double halfRoundTo100 =  halfRoundTo * 100;
		
		int remainder = bigD % roundTo100;
		if(0 < remainder) {
			if(remainder < halfRoundTo100) {
				bigD -= remainder;
			} else {
				bigD += (roundTo100 - remainder);
			}
		} else {
			if(remainder < -halfRoundTo100) {
				bigD -= (roundTo100 + remainder);
			} else {
				bigD -= remainder;
			}
		}
		double ret = bigD/100.0;

		return ret;
	}
	
	public void setEnabled(boolean enabled) {
		text.setEnabled(enabled);
		up.setEnabled(enabled);
		down.setEnabled(enabled);
	}
	
	public Text getTextControl() {
		return text;
	}
	
	public void setTextString(double num) {
		text.setText(df.format(num));
		verifyRange(num);
	}
	
	// Trim any whitespace
	public String getTextString() {
		return Double.valueOf(text.getText()).toString();
	}
	
	public double getNumber() {
		return Double.valueOf(text.getText());
	}
	
	public void dispose() {
		textAndArrowsComposite.dispose();
	}
}
