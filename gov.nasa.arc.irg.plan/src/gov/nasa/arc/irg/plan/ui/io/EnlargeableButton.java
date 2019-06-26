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
package gov.nasa.arc.irg.plan.ui.io;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class EnlargeableButton extends Composite {
	Button button;
	
	public EnlargeableButton(Composite parent, int style) {
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
			//.setHeight(size);
			boldFont = FontDescriptor.createFrom(button.getFont()).setStyle(SWT.BOLD).createFont(button.getDisplay());
		}
		
		button.setFont( boldFont );
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

	public void addSelectionListener(SelectionListener listener) {
		button.addSelectionListener(listener);
	}
	
	public boolean getSelection() {
		return button.getSelection();
	}
	
	public String getText() {
		return button.getText();
	}
	
	public void setText(String text) {
		button.setText(text);
	}
	
	public void setEnabled(boolean enabled) {
		if(!button.isDisposed()) {
			super.setEnabled(enabled);
			button.setEnabled(enabled);
		}
	}
	
	public void setImage(Image image) {
		button.setImage(image);
	}
}
