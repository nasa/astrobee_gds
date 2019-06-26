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
package gov.nasa.util.ui;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * LastPath is used to store the last path selected in a
 * file browser dialog. It is keyed on class name - typically
 * the class which is opening the file dialog. 
 */
public class LastPath {
	@SuppressWarnings("unchecked")
	public static String get(Class clazz) {
		String key = "LastDir."+clazz.getName();
		IPreferenceStore prefStore = NasaUtilUiActivator.getDefault().getPreferenceStore();
		if(!prefStore.contains(key) ){
			prefStore.setDefault(key, System.getProperty("user.home")); 
		}
		return prefStore.getString(key);
	}
	/** keyed on object class */
	public static String get(Object obj) {
		return get(obj.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public static void set(Class clazz, String dir) {
		String key = "LastDir."+clazz.getName();
		IPreferenceStore prefStore = NasaUtilUiActivator.getDefault().getPreferenceStore();
		prefStore.setValue(key, dir);
	}
	public static void set(Object obj, String dir) {
		set(obj.getClass(), dir);
	}
}
