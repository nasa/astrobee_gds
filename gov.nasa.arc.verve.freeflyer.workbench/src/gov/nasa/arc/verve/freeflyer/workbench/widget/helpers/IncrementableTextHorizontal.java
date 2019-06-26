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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class IncrementableTextHorizontal extends IncrementableText {
	protected ExecutorService executor;
	protected Future<Integer> future;
	
	
	public IncrementableTextHorizontal(Composite parent, double initialInput,
			double incrementSize) {
		super(parent, initialInput, incrementSize);
	}
	
	public IncrementableTextHorizontal (Composite parent, double initialInput,
			double incrementSize, int precision, double roundTo) {
		super(parent, initialInput, incrementSize, precision, roundTo);
	}
	
	protected void delayedUpdateBinding() {
		// if one is in progress, return
		if(future != null && !future.isDone()) {
			return;
		}

		// start a timer that waits and then updates
		Callable<Integer> task = () -> {
			try {
				TimeUnit.MILLISECONDS.sleep(800);
				updateBinding();
				return 0;
			}
			catch (InterruptedException e) {
				throw new IllegalStateException("task interrupted", e);
			}
		};

		executor = Executors.newFixedThreadPool(1);
		future = executor.submit(task);
	}

	@Override
	protected void setupTextAndButtons( double initialInput) {
		buttonWidth = 10;
		buttonHeight = 20;
		
		makeLeftButton(textAndArrowsComposite);
		
		text = new Text(textAndArrowsComposite, SWT.RIGHT | SWT.BORDER);
		text.setBounds(buttonWidth, 0, textWidth, buttonHeight);
		text.setText(df.format(initialInput));
		
		text.addListener(SWT.KeyUp, new Listener() {
		      public void handleEvent(Event event) {
		    	 // delayedUpdateBinding();
		      }
		    });
		
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
		
		makeRightButton(textAndArrowsComposite);
	}
	
	protected void makeLeftButton(Composite parent) {
		down = new Button(textAndArrowsComposite, SWT.ARROW | SWT.LEFT);
		down.setBounds(0, 0, buttonWidth, buttonHeight);
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
	
	protected void makeRightButton(Composite parent) {
		up = new Button(textAndArrowsComposite, SWT.ARROW | SWT.RIGHT);
		up.setBounds(textWidth+buttonWidth, 0, buttonWidth, buttonHeight);
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
}
