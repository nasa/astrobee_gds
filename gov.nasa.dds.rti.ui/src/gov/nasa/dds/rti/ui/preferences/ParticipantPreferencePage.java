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

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.ParticipantCreator;
import gov.nasa.dds.rti.ui.RtiDdsUiActivator;
import gov.nasa.util.ui.jface.preference.LabelFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ParticipantPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ParticipantPreferencePage() {
        super(GRID);
        setPreferenceStore(RtiDdsUiActivator.getDefault().getPreferenceStore());
        setDescription("");
    }

    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        LabelFieldEditor    lfe;
        IntegerFieldEditor  ife;
        StringFieldEditor   sfe;

        ParticipantCreator[] creators = DdsEntityFactory.getParticipantCreators();
        for(ParticipantCreator pc : creators) {
            final String pid = pc.participantId;
            lfe = new LabelFieldEditor(pid, parent);
            lfe.setBold(true);
            addField(lfe);
            
            lfe = new LabelFieldEditor(pc.participantName, parent);
            lfe.setBold(false);
            addField(lfe);

            ife = new IntegerFieldEditor(DdsPreferenceKeys.domainId(pid), "Domain id", parent);
            ife.setValidRange(0, 99);
            addField(ife);

            sfe = new StringFieldEditor(DdsPreferenceKeys.qosLibrary(pid), "Qos Library", parent);
            addField(sfe);

            sfe = new StringFieldEditor(DdsPreferenceKeys.qosProfile(pid), "Qos Profile", parent);
            addField(sfe);
        }
    }

    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
