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
package gov.nasa.arc.verve.common.ardor3d.preferences;

import gov.nasa.arc.verve.common.ardor3d.VerveArdor3dActivator;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 */
public class CameraPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public CameraPreferencePage() {
        super(GRID);
        setPreferenceStore(VerveArdor3dActivator.getDefault().getPreferenceStore());
    }

    /**
     */
    @Override
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();                
        BooleanFieldEditor 	bfe = null;
        ComboFieldEditor    cfe = null;
        Control				control;

        bfe		= new BooleanFieldEditor(CameraPreferenceKeys.P_NADIR_SNAP, "Snap Camera to Nadir Mode ", parent);
        // getLabelControl doesn't work for BooleanFieldEditors
        control	= bfe.getDescriptionControl(parent);
        control.setToolTipText("If the Follow Cam is moved close to nadir pointing, \nautomatically switch to Nadir Cam");
        addField(bfe);

        String entries[][] = new String[][] {{"None",  "0" },
                                             {  "2X",  "2" },
                                             {  "4X",  "4" },
                                             {  "8X",  "8" },
                                             { "16X", "16" }};
        cfe = new ComboFieldEditor(CameraPreferenceKeys.P_ANTIALIASING, "Full Screen Antialiasing:", entries, parent);
        control = cfe.getLabelControl(parent);
        control.setToolTipText("Number of samples for full screen antialiasing (FSAA).\nChanges to FSAA will not take effect until \nthe application is restarted.");
        addField(cfe);

    }

    @Override
    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
