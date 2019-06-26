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
package gov.nasa.arc.irg.util.ui.browser;

import gov.nasa.arc.irg.util.ui.UtilUIActivator;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SimpleBrowserPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public SimpleBrowserPreferencePage() {
		super(GRID);
		setPreferenceStore(UtilUIActivator.getDefault().getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		StringFieldEditor sfe;
		
		sfe = new StringFieldEditor(
				SimpleBrowserPreferenceKeys.P_BROWSER_HOME_PAGE,
				"Browser Home Page", 
				getFieldEditorParent());
		
		this.addField(sfe);
	}

	public void init(IWorkbench workbench) { /*noop*/ }
	
}
