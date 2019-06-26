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
import gov.nasa.dds.util.NetInterfaceUtil;
import gov.nasa.util.ui.jface.preference.InfoTextFieldEditor;
import gov.nasa.util.ui.jface.preference.StringListFieldEditor;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DenyListPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public DenyListPreferencePage() {
        super(GRID);
        setPreferenceStore(RtiDdsUiActivator.getDefault().getPreferenceStore());
        setDescription("Typically, DDS should only use one UDPv4 interface to communicate with remote hosts. "
                + "If multiple interfaces are available (for example, virtual vmware interfaces or multi-homed hosts) "
                + "the middleware may choose the wrong one.\n"
                + "Enter the interfaces below that you do not want DDS to use.");
    }

    @Override
    public void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        StringListFieldEditor slfe;
        InfoTextFieldEditor   tfe;

        slfe = new StringListFieldEditor(DdsPreferenceKeys.P_IPv4_DENY_LIST,
                                         "UDPv4 Denied Addresses List", "Select UDPv4 Deny List",
                                         DdsPreferenceKeys.LIST_SEPARATOR,
                                         parent);
        addField(slfe);

        tfe = new InfoTextFieldEditor("Existing Local Host UDPv4 Interfaces:", parent);
        tfe.setStringValue(NetInterfaceUtil.getIpv4InterfacesString());
        tfe.setFixedWidthFont(true);
        addField(tfe);

    }

    public void init(IWorkbench workbench) { 
        // do nothing
    }

}
