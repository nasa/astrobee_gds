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
import gov.nasa.util.ui.jface.preference.StringListFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PeersListPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PeersListPreferencePage() {
        super(GRID);
        setPreferenceStore(RtiDdsUiActivator.getDefault().getPreferenceStore());
        setDescription("");
    }

    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        StringListFieldEditor slfe;

        slfe = new StringListFieldEditor(DdsPreferenceKeys.P_PEERS_LIST,
                                         "DDS Peers List", "DDS Peers List",
                                         DdsPreferenceKeys.LIST_SEPARATOR,
                                         parent);
        addField(slfe);
    }

    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
