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
package gov.nasa.arc.verve.freeflyer.workbench.widget;

import gov.nasa.arc.irg.plan.freeflyer.command.FreeFlyerCommand;
import gov.nasa.arc.irg.plan.model.Station;
import gov.nasa.arc.verve.freeflyer.workbench.undo.DelegateCommandStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

// This is a helper class to make the Command Section in StationAndPointWidget
// because StationAndPointWidget was too long of a class, and because SWT has strange
// bugs that prevent me from splitting this out more cleanly.
public class MakeAddCommandSection {
	private static Logger logger = Logger.getLogger(MakeAddCommandSection.class);

	private List<Class<? extends FreeFlyerCommand>> commandTypes = new ArrayList<Class<? extends FreeFlyerCommand>>();
	private final String getNameName = "getClassNameForWidgetDropdown";
	private final String SELECT_COMMAND = "Select Command";
	
	private static IEclipseContext context;

	protected Composite createCommandArea(StationAndPointWidget parent, Composite container) {
		doSetup();
		
		Composite top = setupTopOfCoordinateArea(container);
		
		Label label3 = new Label(top, SWT.None);
		label3.setText(SELECT_COMMAND);
		parent.addChildLabel(label3);

		Combo commandCombo = new Combo(top, SWT.READ_ONLY);
		commandCombo.setItems(makeCommandArray());
		parent.addChildControl(commandCombo);
		commandCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// for right now, let's just append the command
				if(parent.getModel() instanceof Station) {

					context.get(DelegateCommandStack.class).onAppendCommandCommand(commandCombo.getText());	
				} else {
					logger.error("Non-station being represented by StationWidget");
				}
			}	

			@Override
			public void widgetDefaultSelected(SelectionEvent e) { //
			}
		});
		return top;
	}

	private void doSetup() {
		commandTypes = FreeFlyerCommand.getCommandTypes();
		
		IWorkbench workbench = getService(IWorkbench.class, IWorkbench.class);
		context = workbench.getApplication().getContext();
	}

	private static <T> T getService(Class<T> pClass, Class pContextClass) {
		BundleContext context = FrameworkUtil.getBundle(pContextClass).getBundleContext();
		ServiceReference<T> reference = context.getServiceReference(pClass);
		if(reference == null){
			return null;
		}
		T service = context.getService(reference);
		return service;
	}

	protected String[] makeCommandArray() {
		String[] arr = new String[commandTypes.size()];
		int counter = 0;
		for(Class<? extends FreeFlyerCommand> ffc : commandTypes ) {
			try {
				Method getName = ffc.getMethod( getNameName );
				arr[counter] = (String) getName.invoke(null, (Object[])null);
				counter++;
			} catch (Exception e) {
				logger.error(e);
			}
		}	
		return arr;
	}
	
	protected Composite setupTopOfCoordinateArea(Composite container) {
		Composite top = new Composite(container, SWT.None);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		gd.horizontalSpan = 5;
		top.setLayoutData(gd);
		top.setLayout(new GridLayout(1, false));
		return top;
	}
}
