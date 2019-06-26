///*******************************************************************************
// * Copyright (c) 2013 United States Government as represented by the 
// * Administrator of the National Aeronautics and Space Administration. 
// * All rights reserved.
// * 
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//package gov.nasa.rapid.v2.ui.e4.preferences;
//
//import gov.nasa.rapid.v2.agent.ActiveAgentSet;
//import gov.nasa.rapid.v2.agent.Agent;
//import gov.nasa.rapid.v2.ui.RapidV2UiActivator;
//
//import org.eclipse.jface.preference.ComboFieldEditor;
//import org.eclipse.jface.preference.DirectoryFieldEditor;
//import org.eclipse.jface.preference.FieldEditorPreferencePage;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchPreferencePage;
//
//public class RapidPreferencePage
//extends FieldEditorPreferencePage
//implements IWorkbenchPreferencePage {
//
//    public RapidPreferencePage() {
//        super(GRID);
//        setPreferenceStore(RapidV2UiActivator.getDefault().getPreferenceStore());
//    }
//
//    @Override
//    public void createFieldEditors() {
//        Composite              parent = getFieldEditorParent();
//        ComboFieldEditor       cfe;
//        DirectoryFieldEditor   dfe;
////        StringListFieldEditor  slfe;
////        slfe = new StringListFieldEditor(RapidPreferenceKeys.P_AGENT_SET, 
////                                         "Active Agents", 
////                                         "Add Agent to List of Active Agents", 
////                                         DdsPreferenceKeys.LIST_SEPARATOR,
////                                         parent);
////        addField(slfe);
//        
//        Agent[] agents = ActiveAgentSet.asArray();
//        String[][] entries = new String[agents.length][2];
//        for(int i = 0; i < agents.length; i++) {
//            final String agentName = agents[i].name();
//            entries[i][0] = agentName;
//            entries[i][1] = agentName;
//        }
//        cfe = new ComboFieldEditor(RapidPreferenceKeys.P_AGENT_OF_INTEREST, 
//                                   "Default Agent Of Interest", 
//                                   entries, 
//                                   parent);
//        addField(cfe);
//        
//        dfe = new DirectoryFieldEditor(RapidPreferenceKeys.P_DEFAULT_LOG_DIRECTORY,
//                                       "Log File Directory", 
//                                       parent);
//        addField(dfe);
//    }
//
//    public void init(IWorkbench workbench) { /* noop */ }
//
//}
