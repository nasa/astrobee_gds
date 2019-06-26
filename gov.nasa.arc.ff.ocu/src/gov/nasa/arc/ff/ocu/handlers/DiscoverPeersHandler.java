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
 
package gov.nasa.arc.ff.ocu.handlers;

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.util.ui.MessageBox;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DiscoverPeersHandler {
	
	final int WINDOW_WIDTH = 500;
	final int WINDOW_HEIGHT = 600;

	@Execute
	public void execute(final MApplication application) {
		
		
		try{
			final File file = new File(FileLocator.toFileURL(Platform.getBundle("gov.nasa.rapid.v2.ui.e4").getEntry("NDDS_DISCOVERY_PEERS")).toURI());
			
			final Shell shell = new Shell(Display.getCurrent());
			shell.setBounds(Display.getDefault().getPrimaryMonitor().getBounds().width/2-WINDOW_WIDTH/2, Display.getDefault().getPrimaryMonitor().getBounds().height/2-WINDOW_HEIGHT/2, WINDOW_WIDTH, WINDOW_HEIGHT);
		    shell.setText("NDDS_DISCOVERY_PEERS");
		    shell.setLayout(new GridLayout(2,false));

		    
		    final Text text = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.WRAP);
		    text.setData(text);
		    GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		    data.horizontalSpan = 2;
		    data.grabExcessHorizontalSpace = true;
		    text.setLayoutData(data);
		        
		    try {
				text.setText(FileUtils.readFileToString(file));
			} catch (final IOException e2) {
				text.setText("Error opening file. \n"+e2.getStackTrace());
			}
			
		    data = new GridData();
		    data.horizontalSpan = 1;
		    
		    
		    final EnlargeableButton saveButton = new EnlargeableButton(shell, SWT.NONE);
		    saveButton.setText("Save");
		    saveButton.setData(data);
		    saveButton.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(final SelectionEvent e) {
		    		super.widgetSelected(e);
		    		try {
						FileUtils.writeStringToFile(file, text.getText());
						shell.close();
					} catch (final IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		    	}
			});
		    
		    final EnlargeableButton cancelButton = new EnlargeableButton(shell, SWT.NONE);
		    cancelButton.setText("Cancel");
		    cancelButton.setData(data);
		    cancelButton.addSelectionListener(new SelectionAdapter() {
		    	@Override
		    	public void widgetSelected(final SelectionEvent e) {
		    		super.widgetDefaultSelected(e);
		    		shell.close();
		    	}
			});
		    
		    shell.open();
			while (!shell.isDisposed()) {
		         if (!Display.getCurrent().readAndDispatch()) {
		        	 Display.getCurrent().sleep();
		         }
		      }
		}catch(final Exception e){
			new MessageBox().error("Error", "Could not find NDDS_DISCOVERY_PEERS list.");
			e.printStackTrace();
		}
	}
		
}