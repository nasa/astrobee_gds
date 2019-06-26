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
//import gov.nasa.rapid.v2.ui.RapidV2UiActivator;
//import gov.nasa.util.ui.jface.preference.StringListFieldEditor;
//
//import org.eclipse.jface.preference.FieldEditorPreferencePage;
//import org.eclipse.jface.preference.ListEditor;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchPreferencePage;
//
//public class CameraPreferencePage
//	extends FieldEditorPreferencePage
//	implements IWorkbenchPreferencePage {
//
//	public CameraPreferencePage() {
//		super(GRID);
//		setPreferenceStore(RapidV2UiActivator.getDefault().getPreferenceStore());
//		//setDescription("Default Camera Names");
//	}
//	
//	@Override
//	public void createFieldEditors() {
//		ListEditor lfe;
//		
//		lfe = new StringListFieldEditor(CameraPreferenceKeys.DEFAULT_CAMERAS, "Default Camera Names", 
//				"Enter Camera Name", CameraPreferenceKeys.DEFAULT_CAMERA_SEPARATOR, getFieldEditorParent());
//		addField(lfe);
//		
//
//	}
//
//	public void init(IWorkbench workbench) { /* noop */ }
//	
//}
