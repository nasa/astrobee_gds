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
package gov.nasa.arc.verve.common.preferences.rcp;

import gov.nasa.arc.verve.common.Activator;
import gov.nasa.arc.verve.common.VervePreferences;
import gov.nasa.util.ui.jface.preference.LabelFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.RenderContext;

/**
 */
public class VervePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public VervePreferencePage() {
        super(GRID);
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
        setDescription("Visual Environment for Remote Virtual Exploration");
    }

    /**
     */
    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        BooleanFieldEditor   bfe;
        IntegerFieldEditor   ife;
        LabelFieldEditor     lfe;
        ComboFieldEditor     cfe;       
        DirectoryFieldEditor dfe;       
        Control              label;

        addField(new LabelFieldEditor("", parent));

        dfe = new DirectoryFieldEditor(VervePreferences.P_VERVE_DATA_DIR, "VERVE Data Directory", parent);
        label   = dfe.getLabelControl(getFieldEditorParent());
        label.setToolTipText("Choose data directory for VERVE.\n"+
                "User data and cached data will be stored in this directory");
        addField(dfe);

        // jeebus, the layout capabilities of these pages is pathetic.
        // add some label spacers so everything isn't crammed at the
        // top. 
        //----------------------------------------------------------------
        for(int i = 0; i < 1; i++) {
            addField(new LabelFieldEditor("", getFieldEditorParent()));
        }
        lfe = new LabelFieldEditor("Compatibility Options:", getFieldEditorParent());
        lfe.setBold(true);
        addField(lfe);

        bfe = new BooleanFieldEditor(VervePreferences.P_USE_FALLBACK_SHADERS,
                                     "Use Fallback Shaders for broader OpenGL compatibilty",
                                     parent);
        label = bfe.getDescriptionControl(parent);
        label.setToolTipText("Some of the shaders used in VERVE require advanced\n"+
                "OpenGL capabilities that may not be supported on all hardware.\n"+
                "Select this option to use fallback shaders (where available)\n"+
                "to increase compatibility with older hardware or bad video drivers.");
        this.addField(bfe);

        addField(new ColorFieldEditor(VervePreferences.P_DEFAULT_BACKGROUND_COLOR, "Default background color", parent));

        ife = new IntegerFieldEditor(VervePreferences.P_TARGET_FRAMERATE, "Target Framerate", parent);
        ife.setValidRange(1, 30);
        ife.getLabelControl(parent).setToolTipText("Set target framerate. Restart after changing this value.");
        addField(ife);

        int maxTex = 32768;
        RenderContext context = ContextManager.getCurrentContext();
        if(context != null) {
            ContextCapabilities caps = context.getCapabilities();
            if(caps != null) {
                maxTex = caps.getMaxTextureSize();
            }
        }
        int sz;
        sz = 1024;
        int numEntries = 1;
        for(int i = 0; i < 6; i++) {
            sz *= 2;
            if(sz <= maxTex)  numEntries++;
            else break;
        }
        String[][] shadowSizeEntries = new String[numEntries][];
        sz = 1024;
        for(int i = 0; i < numEntries; i++) {
            String numStr = Integer.toString(sz);
            shadowSizeEntries[i] = new String[] { numStr, numStr };
            if(sz < maxTex) {
                sz = sz*2;
            }
        }
        cfe = new ComboFieldEditor(VervePreferences.P_SHADOWMAP_SIZE, "Shadow Map Texture Size:", shadowSizeEntries, parent);
        addField(cfe);

        //----------------------------------------------------------------
        for(int i = 0; i < 8; i++) {
            addField(new LabelFieldEditor("", getFieldEditorParent()));
        }
        lfe = new LabelFieldEditor("Debug Options:", getFieldEditorParent());
        lfe.setBold(true);
        addField(lfe);

        bfe = new BooleanFieldEditor(VervePreferences.P_DEBUG_SHOW_BOUNDS_ON_SELECT, 
                                     "Automatically show spatial bounds when selected in Scene Graph view", 
                                     //BooleanFieldEditor.SEPARATE_LABEL, 
                                     BooleanFieldEditor.DEFAULT, 
                                     getFieldEditorParent() );
        addField(bfe);
        bfe = new BooleanFieldEditor(VervePreferences.P_DEBUG_AXIS_USE_BOUND_CENTER, 
                                     "Use bound center instead of spatial origin when drawing debug bounds", 
                                     //BooleanFieldEditor.SEPARATE_LABEL, 
                                     BooleanFieldEditor.DEFAULT, 
                                     getFieldEditorParent() );
        addField(bfe);
    }

    @Override
    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
