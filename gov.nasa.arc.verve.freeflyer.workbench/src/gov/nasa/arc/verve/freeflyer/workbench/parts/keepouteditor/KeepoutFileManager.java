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

package gov.nasa.arc.verve.freeflyer.workbench.parts.keepouteditor;

import gov.nasa.arc.verve.robot.freeflyer.utils.ContextNames;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;


public class KeepoutFileManager implements PropertyChangeListener {
	private static final Logger logger = Logger.getLogger(KeepoutFileManager.class);
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	public Shell shell;
	@Inject
	private IEclipseContext context;
	protected final String suffix = "json";
	private boolean keepoutDirty = false;
	private boolean keepoutOpen = false;
	protected KeepoutBuilder keepoutBuilder; // the specific keepout builder for this editor

	@Inject
	public KeepoutFileManager() {
	}
	
	@PostConstruct
	public void setup() {
		context.set(KeepoutFileManager.class, this);
		setKeepoutClosed();
	}

	private boolean askToSave() {
		if(keepoutDirty) {
			// customized MessageDialog with configured buttons
			MessageDialog dialog = new MessageDialog(shell, "Save Keepout?", null,
					"Save keepout before closing?", MessageDialog.QUESTION, new String[] { "Save Keepout",
					"Discard Changes", "Cancel" }, 0);
			int result = dialog.open();

			switch(result) {
			case 0:
				onSaveKeepoutCommand();
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

	public void onNewKeepoutCommand() {
		String path = showNewKeepoutDialog();

		if(path != null) {
			setupNewKeepout(path);
		}
	}
	
	public String showNewKeepoutDialog() {
		FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		String[] allowedExtension = new String[]{"*." + suffix};
		dlg.setFilterExtensions(allowedExtension);
		dlg.setText("Enter New Keepout Name");
		
		String path = dlg.open();
		return path;
	}
	

	public void setupNewKeepout(String filename) {	
		keepoutBuilder = constructNewKeepout(filename);
		keepoutBuilder.saveToFile();
		context.set(ContextNames.KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR, keepoutBuilder);
		setKeepoutOpen();
	}
	
	private KeepoutBuilder constructNewKeepout(String filename) {
		File file = new File(filename);
		
		KeepoutBuilder builder = new KeepoutBuilder(file);
		builder.setDateCreated(Instant.now().getEpochSecond() + "");
		builder.setDateModified(Instant.now().getEpochSecond() + "");
		builder.setName(file.getName().replace(".json", ""));
		builder.setNotes("Don't go here.");
		builder.setSafe("false");
		builder.setKeepouts(new ArrayList<KeepoutBox>());
		
		return builder;
	}
	
	public void onOpenKeepoutCommand() {	
		String path = showOpenKeepoutDialog();
		
		if(path != null) {
			setupOpenKeepout(path);
		}
	}
	
	public String showOpenKeepoutDialog() {
		FileDialog dlg = new FileDialog(shell,  SWT.OPEN  );
		String[] extension = new String[]{"*." + suffix};
		dlg.setFilterExtensions(extension);
		dlg.setText("Select a Keepout");
		String path = dlg.open();
		if (path == null || path.length() == 0) {
			// TODO make an error
			logger.error("Selected keepout was null");
		}
		return path;
	}
	
	public void setupOpenKeepout(String filename) {	
		File file = new File(filename);
		keepoutBuilder = new KeepoutBuilder(file);
		context.set(ContextNames.KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR, keepoutBuilder);
		setKeepoutOpen();
	}
	
	public void onSaveKeepoutCommand() {		
		if(keepoutBuilder == null) {
			return;
		}
		
		keepoutBuilder.saveToFile();
		setKeepoutNotDirty();
		KeepoutModelingNode.getStaticInstance().onDeselect();
	}
	
	public void onCloseKeepoutCommand() {
		boolean goAhead = askToSave();
		if(!goAhead) {
			return;
		}
		closeTheKeepout();
	}

	private void closeTheKeepout() {
		context.set(ContextNames.KEEPOUT_BUILDER_FOR_KEEPOUT_EDITOR, null);
		setKeepoutNotDirty();
		setKeepoutClosed();
	}
	
	public void deleteKeepout() {
		setKeepoutNotDirty();
		setKeepoutClosed();
	}

	public void setKeepoutOpen() {
		keepoutOpen = true;
		context.set(ContextNames.CLOSE_KEEPOUT_ENABLED, keepoutOpen);
		context.set(ContextNames.OPEN_KEEPOUT_ENABLED, !keepoutOpen);
		context.set(ContextNames.NEW_KEEPOUT_ENABLED, !keepoutOpen);
	}

	public void setKeepoutClosed() {
		keepoutOpen = false;
		context.set(ContextNames.CLOSE_KEEPOUT_ENABLED, keepoutOpen);
		context.set(ContextNames.OPEN_KEEPOUT_ENABLED, !keepoutOpen);
		context.set(ContextNames.NEW_KEEPOUT_ENABLED, !keepoutOpen);
	}

	public void setKeepoutDirty() {
		keepoutDirty = true;
		context.set(ContextNames.SAVE_KEEPOUT_ENABLED, keepoutDirty);
	}

	public void setKeepoutNotDirty() {
		keepoutDirty = false;
		context.set(ContextNames.SAVE_KEEPOUT_ENABLED, keepoutDirty);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		setKeepoutDirty();
	}
}
