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
package gov.nasa.arc.verve.freeflyer.workbench.parts.standard;

import gov.nasa.arc.irg.freeflyer.rapid.ILogPosterListener;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.util.ui.ColorProvider;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class LogButtonBar implements ILogPosterListener{
	private int	numColumns = 20;
	private Label logLabel;
	@Inject
	protected MApplication application;
	private Composite parent;
	private final String INITIAL_TEXT = "";
	private final String TOOLTIP_TEXT = "Latest Message between Astrobee and Control Station";
	
	@Inject 
	public LogButtonBar(Composite parent) {
		this.parent = parent;
		setupLayout();
		
		makeLogLabel();
	
		LogPoster.addListener(this);
	}
	
	@PreDestroy
	public void preDestroy() {
		LogPoster.removeListener(this);
	}
	
	private void makeLogLabel() {
		logLabel = new Label(parent, SWT.LEFT);
		logLabel.setText(INITIAL_TEXT);
		logLabel.setToolTipText(TOOLTIP_TEXT);
		logLabel.setBackground(ColorProvider.INSTANCE.white);
		GridData logLayoutData = new GridData(GridData.FILL_HORIZONTAL);
		logLayoutData.horizontalSpan = numColumns;
		logLabel.setLayoutData(logLayoutData);
	}
	
	private void setupLayout() {
		parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));
		GridLayout gridLayout = new GridLayout(numColumns, true);
		gridLayout.marginHeight = 0;
		//gridLayout.marginWidth = margin;
		parent.setLayout(gridLayout);
	}

	public void postedToLog(String post) {
		Display.getDefault().asyncExec(new Runnable(){
			public void run() {
				logLabel.setText(LogPoster.getLastLogEntry().toString());
			}
		});
		
	}
}
