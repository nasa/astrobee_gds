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
import gov.nasa.arc.verve.freeflyer.workbench.utils.GuiUtils;
import gov.nasa.rapid.v2.e4.agent.Agent;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class HealthTreeLabelProvider implements ITableLabelProvider, ITableColorProvider {

	private Color orange = new Color(Display.getCurrent(), 255, 165, 0);
	protected Color cyan = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	protected boolean commConnected = false;
	protected Agent selectedAgent = null;

	public HealthTreeLabelProvider() {
	}
	
	public HealthTreeLabelProvider(boolean commConnected) {
		acceptCommConnected(commConnected);
	}
	
	public void acceptCommConnected(boolean connected) {
		commConnected = connected;
		if(!commConnected) {
			selectedAgent = null;
		}
	}

	public void onAgentSelected(Agent a) {
		selectedAgent = a;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		StateTableRow stRow;
		if (element instanceof StateTableRow) {
			stRow = (StateTableRow)element;
			if(!stRow.showBecauseHighPriority()) {
				return null;
			}
		} else {
			return "-";
		}
		switch (columnIndex) {
		case 0:
			return stRow.getLabel();
		case 1:
			if(stRow.getVerbatim()) {
				return stRow.getValue();
			}
			return GuiUtils.toTitleCase(stRow.getValue());
		}
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		if (element instanceof StateTableRow) {
			StateTableRow stRow = (StateTableRow)element;
			if(!stRow.showBecauseHighPriority()) {
				return null;
			}
			
			switch (columnIndex) {
			case 1:
				//			if(!commConnected || (selectedAgent == null)) {
				if(!commConnected ) {
					return cyan;
				}
			}
			if (stRow.colorOrangeBecauseFault()) {
				return orange;
			}
		}	
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
		//
	}

	public void dispose() {
		//
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		//
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		if (element instanceof StateTableRow) {
			if (columnIndex == 1) {
				if (((StateTableRow) element).getValue().equals("Fault")) {
					return orange;
				}
			}
		}
		return null;
	}
}
