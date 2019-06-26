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
package gov.nasa.arc.verve.freeflyer.workbench.parts;

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.verve.freeflyer.workbench.utils.CSVPlanConverter;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CSVConverterPart {
	private static final Logger logger = Logger.getLogger(CSVConverterPart.class);
	
	@Inject @Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;
	
	private IEclipseContext context;
	private EnlargeableButton fileButton, convertButton, cancelButton;
	private Label fileLabel, successLabel;
	private Text nameText;
	
	private File openFile;
	private final String suffix = "csv";
	
	private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
	
	@Inject
	public CSVConverterPart(Composite parent, MApplication app) {
		context = app.getContext();
		parent.setLayout(new GridLayout(1, false));
	
		Group group = new Group(parent, SWT.SHADOW_IN);
		group.setText("CSV to Fplan Converter");
		GridLayout gl = new GridLayout(2, false);
		group.setLayout(gl);
		group.setLayoutData(new GridData(SWT.LEFT, SWT.LEFT, false, false));
		
		initUI(group);
	}
	
	private void initUI(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Click File to select a file to convert.");
		GridData lData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		lData.horizontalSpan = 2;
		label.setLayoutData(lData);
		
		initFileButton(parent);
		fileLabel = new Label(parent, SWT.NONE);
		fileLabel.setText("No file selected.");
		fileLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		
		initNameText(parent);
		initConvertButton(parent);
		initCancelButton(parent);
		initSuccessLabel(parent);
		
		parent.pack();
	}
	
	private void initFileButton(Composite parent) {
		fileButton = new EnlargeableButton(parent, SWT.NONE);
		fileButton.setText("File");
		fileButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		fileButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		fileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dlg = new FileDialog(shell,  SWT.OPEN);
				String[] extension = new String[]{"*." + suffix};
				dlg.setFilterExtensions(extension);
				dlg.setText("Select a CSV");
				String path = dlg.open();
				if (path == null || path.length() == 0) {
					//hit cancel
					return;
				}
				openFile = new File(path);
				fileLabel.setText(findTheShortName(path) + "\t");
				successLabel.setText("Click convert to convert a file.");
				convertButton.setEnabled(true);
				cancelButton.setEnabled(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }			
		});
	}
	
	private void initNameText(Composite parent) {
		nameText = new Text(parent, SWT.NONE);
		GridData nData = new GridData(SWT.LEFT, SWT.LEFT, false, false);
		nameText.setLayoutData(nData);
		
		Label nameLabel = new Label(parent, SWT.NONE);
		nameLabel.setText("Enter new file name.");
		nameLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	}
	
	private void initConvertButton(Composite parent) {
		convertButton = new EnlargeableButton(parent, SWT.NONE);
		convertButton.setText("Convert");

		convertButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		convertButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		convertButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean success = CSVPlanConverter.CSVToPlanFile(openFile, nameText.getText());
				if(success) {
					successLabel.setText("Conversion successful.");
				} else {
					successLabel.setText("Conversion failed.");
				}
				fileLabel.setText("No file selected.");
				convertButton.setEnabled(false);
				cancelButton.setEnabled(false);
				openFile = null;
				nameText.setText("");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }
		});
		convertButton.setEnabled(false);
	}
	
	private void initCancelButton(Composite parent) {
		cancelButton = new EnlargeableButton(parent, SWT.NONE);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.setButtonLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, false));
		cancelButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				nameText.setText("");
				fileLabel.setText("No file selected.");
				openFile = null;
				cancelButton.setEnabled(false);
				convertButton.setEnabled(false);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { }		
		});
		cancelButton.setEnabled(false);
	}
	
	private void initSuccessLabel(Composite parent) {
		successLabel = new Label(parent, SWT.NONE);
		successLabel.setText("Click convert to convert a file.");
		successLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	}
	
	private String findTheShortName(String filepath) {
		String[] pathparts;
		if(IS_WINDOWS) {
			pathparts = filepath.split("\\\\");
		} else {
			pathparts = filepath.split("/");
		}
		int numparts = pathparts.length;
		String filename = pathparts[numparts - 1]; 
		String[] nameParts = filename.split("\\.");
		return nameParts[0];
	}

}
