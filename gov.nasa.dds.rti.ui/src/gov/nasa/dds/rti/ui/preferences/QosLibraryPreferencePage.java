/*******************************************************************************
 * Copyright (c) 2013 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration. 
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package gov.nasa.dds.rti.ui.preferences;

import gov.nasa.dds.rti.ui.RtiDdsUiActivator;
import gov.nasa.util.ui.jface.preference.FileListFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.rti.dds.infrastructure.TransportBuiltinKind;

public class QosLibraryPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public QosLibraryPreferencePage() {
		super(GRID);
		setPreferenceStore(RtiDdsUiActivator.getDefault().getPreferenceStore());
		setDescription("Specify locations of Qos Profile Libraries");
	}
	
	@Override
	public void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
        FileListFieldEditor   flfe;
        BooleanFieldEditor    bfe;
		Label                 label;
		
		flfe = new FileListFieldEditor(DdsPreferenceKeys.P_QOS_URL_GROUPS, 
		                               "Qos Library Files", "Qos Library Files", 
		                               DdsPreferenceKeys.LIST_SEPARATOR, 
		                               parent);
		label = flfe.getLabelControl(parent);
		label.setToolTipText("List of QoS profiles to read");
		addField(flfe);
		
        bfe = new BooleanFieldEditor(DdsPreferenceKeys.P_IGNORE_USER_PROFILE, "Ignore user profile", parent);
        bfe.getDescriptionControl(parent).setToolTipText("Ignore USER_QOS_PROFILES.xml in current working directory");
        addField(bfe);
        
        bfe = new BooleanFieldEditor(DdsPreferenceKeys.P_IGNORE_ENV_PROFILE, "Ignore environment profile", parent);
        bfe.getDescriptionControl(parent).setToolTipText("Ignore NDDS_QOS_PROFILES environment variable");
        addField(bfe);
        
        final String SHMEM = TransportBuiltinKind.SHMEM_ALIAS;
        bfe = new BooleanFieldEditor(DdsPreferenceKeys.transportDisabled(SHMEM), "Disable shared memory transport", parent);
        bfe.getDescriptionControl(parent).setToolTipText("Disable the shared memory (shmem) transport");
        addField(bfe);
        
	}

	public void init(IWorkbench workbench) { 
		// do nothing
	}
	
}
