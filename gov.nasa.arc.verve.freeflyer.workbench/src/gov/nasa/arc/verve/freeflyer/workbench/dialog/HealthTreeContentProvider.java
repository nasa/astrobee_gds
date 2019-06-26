/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.freeflyer.workbench.dialog;

import gov.nasa.arc.irg.freeflyer.rapid.state.AstrobeeStateAdapter.StateTableRow;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class HealthTreeContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<StateTableRow>) inputElement).toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return Collections.EMPTY_LIST.toArray();
	}

	@Override
	public Object getParent(Object element) {
		return Collections.EMPTY_LIST.toArray();
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
}
