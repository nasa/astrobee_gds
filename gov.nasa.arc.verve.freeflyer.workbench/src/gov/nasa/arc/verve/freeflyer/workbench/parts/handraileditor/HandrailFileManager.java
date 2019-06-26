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
package gov.nasa.arc.verve.freeflyer.workbench.parts.handraileditor;

import gov.nasa.arc.irg.plan.freeflyer.config.InertiaConfigList;
import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

//I copied the code from KeepoutFileManager and left the superfluous methods in in case
//later on there is a desire to support them for handrails as well.
public class HandrailFileManager implements PropertyChangeListener {
	private static final Logger logger = Logger.getLogger(HandrailFileManager.class);
	
	@Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;
	
//	private String pluginURL = "platform:/plugin/gov.nasa.arc.verve.freeflyer.workbench/resources/HandrailConfiguration.json";
	
	@Inject
	private IEclipseContext context;
	private final String suffix = "json";
	private boolean handrailDirty = false;
	private boolean handrailOpen = false;
	private HandrailBuilder handrailBuilder;
	
	@Inject
	public HandrailFileManager() {
		
	}
	
	@PostConstruct
	public void setup() {
		context.set(HandrailFileManager.class, this);
		setHandrailClosed();
		String filename = ConfigFileWrangler.getInstance().getHandrailsPath();
		setupOpenHandrail(filename);
	}
	
	private boolean askToSave() {
		if(handrailDirty) {
			// customized MessageDialog with configured buttons
			MessageDialog dialog = new MessageDialog(shell, "Save Handrails?", null,
					"Save handrails before closing?", MessageDialog.QUESTION, new String[] { "Save Handrails",
					"Discard Changes", "Cancel" }, 0);
			int result = dialog.open();

			switch(result) {
			case 0:
				onSaveHandrailCommand();
				break;
			case 1:
				//do nothing
				break;
			case 2:
				return false;
			}
		}
		return true;
	}	
	
	public void onOpenHandrailCommand() {	
		String path = showOpenHandrailDialog();
		
		if(path != null) {
			setupOpenHandrail(path);
		}
	}
	
	public String showOpenHandrailDialog() {
		FileDialog dlg = new FileDialog(shell,  SWT.OPEN  );
		String[] extension = new String[]{"*." + suffix};
		dlg.setFilterExtensions(extension);
		dlg.setText("Select a Handrail");
		String path = dlg.open();
		if (path == null || path.length() == 0) {
			// TODO make an error
			logger.error("Selected keepout was null");
		}
		return path;
	}
	
	public static InertiaConfigList loadFromFile(String filename) throws Exception {
		byte[] jsonData = Files.readAllBytes(Paths.get(filename));
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(jsonData, InertiaConfigList.class);
	}
	
	public void setupOpenHandrail(String filename) {	
//		try {
//			URL url = new URL(filename);
//			handrailBuilder = new HandrailBuilder(url);
//			context.set(ContextNames.HANDRAIL_BUILDER, handrailBuilder);
//			setHandrailOpen();
//			return;
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		File file = new File(filename);
		handrailBuilder = new HandrailBuilder(file);
		context.set(ContextNames.HANDRAIL_BUILDER, handrailBuilder);
		setHandrailOpen();
	}
	
	public void onSaveHandrailCommand() {		
		if(handrailBuilder == null) {
			return;
		}
		
		handrailBuilder.saveToFile();
		setHandrailNotDirty();
	}
	
	public void onCloseHandrailCommand() {
		boolean goAhead = askToSave();
		if(!goAhead) {
			return;
		}
		closeTheHandrail();
	}

	private void closeTheHandrail() {
		context.set(ContextNames.HANDRAIL_BUILDER, null);
		setHandrailNotDirty();
		setHandrailClosed();
	}
	
	public void deleteHandrail() {
		setHandrailNotDirty();
		setHandrailClosed();
	}

	public void setHandrailOpen() {
		handrailOpen = true;
	}

	public void setHandrailClosed() {
		handrailOpen = false;
	}

	public void setHandrailDirty() {
		handrailDirty = true;
		context.set(ContextNames.SAVE_HANDRAILS_ENABLED, handrailDirty);
	}

	public void setHandrailNotDirty() {
		handrailDirty = false;
		context.set(ContextNames.SAVE_HANDRAILS_ENABLED, handrailDirty);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(!handrailDirty) {
			setHandrailDirty();
		}
	}
}
