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
package gov.nasa.arc.verve.common.scenario.preferences;

import gov.nasa.arc.verve.common.scenario.ScenarioActivator;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.common.ardor3d.shape.grid.GridTexture;
import gov.nasa.util.ui.jface.preference.DoubleFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FlatGridPreferencePage
extends FieldEditorPreferencePage
implements IWorkbenchPreferencePage {

    public FlatGridPreferencePage() {
        super(GRID);
        setPreferenceStore(ScenarioActivator.getDefault().getPreferenceStore());
    }

    @Override
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();

        int num = GridTexture.Style.values().length;
        String[][] gridEntries = new String[num][2];
        for(GridTexture.Style style : GridTexture.Style.values()) {
            gridEntries[style.ordinal()][0] = style.toString();
            gridEntries[style.ordinal()][1] = style.toString();
        }
        //addField(new StringFieldEditor(ScenarioPreferenceKeys.P_SITE_FRAME_OFFSET, "Site Offset (easting,northing,altitude):", parent));

        for(int i = 0; i < ScenarioPreferences.getNumFlatGrids(); i++) {
            addField(new BooleanFieldEditor(ScenarioPreferenceKeys.gridEnabled(i),	"Enable grid "+i, 					parent));
            addField(new DoubleFieldEditor (ScenarioPreferenceKeys.gridSize(i),	"Grid "+i+" Size (meters) :",		parent));
            addField(new StringFieldEditor (ScenarioPreferenceKeys.gridOffset(i),	"Grid "+i+": Grid Offset (meters) :",	parent));
            addField(new ComboFieldEditor  (ScenarioPreferenceKeys.gridGridTextureFile(i),"Grid "+i+": Grid Lines Texture : ", gridEntries, parent));
            //addField(new StringFieldEditor (ScenarioPreferenceKeys.gridBaseTextureFile(i),"Grid "+i+": Base Map Texture : ",   parent));
            addField(new ColorFieldEditor  (ScenarioPreferenceKeys.gridColor(i),   "Grid "+i+" Base color :",			parent));
        }

        addField(new BooleanFieldEditor(ScenarioPreferenceKeys.P_GRID_DEPTH_WRITE, 
                                        "Enable depth buffer writes", parent));

    }

    @Override
    public void init(IWorkbench workbench) { /* noop */ }

}
