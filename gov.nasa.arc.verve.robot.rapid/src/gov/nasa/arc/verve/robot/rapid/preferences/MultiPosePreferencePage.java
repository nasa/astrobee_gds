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
import gov.nasa.arc.verve.robot.rapid.VerveRapidRobotPreferences;
import gov.nasa.util.ui.jface.preference.StringListFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class MultiPosePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public MultiPosePreferencePage() {
        super(GRID);
        setPreferenceStore(VerveRapidRobotActivator.getDefault().getPreferenceStore());
        setDescription("");
    }

    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        StringListFieldEditor slfe;

        slfe = new StringListFieldEditor(VerveRapidRobotPreferences.P_MULTIPOSE_TOPICS,
                                         "MultiPose Topics", "MultiPose Topics",
                                         VerveRapidRobotPreferences.LIST_SEPARATOR,
                                         parent);
        addField(slfe);
    }

    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
