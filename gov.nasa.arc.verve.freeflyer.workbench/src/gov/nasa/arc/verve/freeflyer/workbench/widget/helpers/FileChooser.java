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

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.util.ui.ColorProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
 
public class FileChooser extends Composite {
 
	protected Text text;
	protected EnlargeableButton button;
	protected String title = null;
	protected String suffix;
	protected IEclipseContext context;
	protected List<FileChooserListener> chooserListeners = new ArrayList<FileChooserListener>();

	public FileChooser(IEclipseContext context, Composite parent, String buttonText, String suffix) {
		super(parent, SWT.NULL);
		this.context = context;
		this.suffix = suffix;
		createContent(buttonText);
	}
 
	public void createContent(String buttonText) {
		setBackground(ColorProvider.INSTANCE.WIDGET_BACKGROUND);
		GridLayout layout = new GridLayout(2, false);
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		button = new EnlargeableButton(this, SWT.NONE);
		button.setText(buttonText);
		button.addSelectionListener(new SelectionListener() {
 
			public void widgetDefaultSelected(SelectionEvent e) {
			}
 
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(button.getShell(),  SWT.OPEN  );
				String[] extension = new String[]{"*." + suffix};
				dlg.setFilterExtensions(extension);
				dlg.setText("Select a Plan");
				String path = dlg.open();
				if (path == null) return;
				text.setText(path);
				updateChooserListeners();
			}
		});

		text = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 200;
		text.setLayoutData(gd);
	}
 
	public String getText() {
		return text.getText();
 
	}
 
	public Text getTextControl() {
		return text;		
	}
 
	public File getFile() {
		String ftext = text.getText();
		if (ftext.length() == 0) return null;
		return new File(ftext);
	}
 
	public String getTitle() {
		return title;
	}
 
	public void setTitle(String title) {
		this.title = title;
	}
	
	protected void updateChooserListeners(){
		for(FileChooserListener l : chooserListeners) {
			if(l != null)
				l.newFileChosen(getText());
		}
	}
	
	public void addChooserListener(FileChooserListener fcl) {
		chooserListeners.add(fcl);
	}
	
	public void removeChooserListener(FileChooserListener fcl) {
		chooserListeners.remove(fcl);
	}
}