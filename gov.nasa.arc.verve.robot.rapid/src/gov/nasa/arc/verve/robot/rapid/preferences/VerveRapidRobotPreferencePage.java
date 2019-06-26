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
package gov.nasa.arc.verve.robot.rapid.preferences;

import gov.nasa.arc.verve.robot.rapid.VerveRapidRobotActivator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class VerveRapidRobotPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public VerveRapidRobotPreferencePage() {
        super(GRID);
        setPreferenceStore(VerveRapidRobotActivator.getDefault().getPreferenceStore());
    }

    @Override
    public void createFieldEditors() {
//        final Composite parent = getFieldEditorParent();
//        Control label;
//        BooleanFieldEditor bfe;		
//        bfe = new BooleanFieldEditor(VerveRapidRobotPreferenceKeys.P_USE_FALLBACK_SHADERS,
//                                     "Use Fallback Shaders for broader OpenGL compatibilty",
//                                     parent);
//        label = bfe.getDescriptionControl(parent);
//        label.setToolTipText("Some of the shaders used in VERVE require advanced\n"+
//                "OpenGL capabilities that may not be supported on all hardware.\n"+
//                "Select this option to use fallback shaders (where available)\n"+
//                "to increase compatibility with older hardware or bad video drivers.");
//        this.addField(bfe);
    }

    public void init(IWorkbench workbench) { /**/ }

}
