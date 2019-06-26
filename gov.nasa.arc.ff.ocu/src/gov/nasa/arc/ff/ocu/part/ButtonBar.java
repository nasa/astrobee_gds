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
package gov.nasa.arc.ff.ocu.part;

import gov.nasa.arc.irg.freeflyer.rapid.ILogPosterListener;
import gov.nasa.arc.irg.freeflyer.rapid.LogPoster;
import gov.nasa.arc.irg.util.ui.ColorProvider;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class ButtonBar implements ILogPosterListener {
	protected Button     m_exitButton;
	protected int m_testCounter = 0;
	protected IWorkbench m_workbench;
	protected Composite  m_composite;

	protected ScrolledComposite m_scrollComposite;
    protected Label		 m_dataLabel;
    protected int m_logWidth = 230;
    protected int m_logHeight = 800;
    
    protected static SimpleDateFormat s_dateFormatUTC = new SimpleDateFormat("HH:mm:ss");

	{
		s_dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}

	@Inject @Singleton
	public ButtonBar(Composite parent) {

		GridLayout gridLayout;
		m_composite = new Composite(parent, SWT.LEFT);
		m_composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL));
		gridLayout = new GridLayout(1, true);
		gridLayout.marginHeight = 5;
		gridLayout.marginWidth = 5;
		m_composite.setLayout(gridLayout);

		Label logTitle = new Label(m_composite, SWT.None);
		logTitle.setText("Command Log");

		m_scrollComposite = new ScrolledComposite(m_composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		m_dataLabel = new Label(m_scrollComposite, SWT.NONE);
		m_dataLabel.setSize(m_logWidth, m_logHeight);
		m_dataLabel.setBackground(ColorProvider.INSTANCE.white);

		m_scrollComposite.setContent(m_dataLabel);
		m_scrollComposite.setMinSize(m_logWidth, m_logHeight);
		m_scrollComposite.setBackground(ColorProvider.INSTANCE.white);

		GridData scrollData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		m_scrollComposite.setLayoutData(scrollData);	

		m_exitButton = new Button(m_composite, SWT.NONE);
		m_exitButton.setText("Exit");
		m_exitButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_END));

		m_exitButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				m_workbench.close();
			}
		});
		LogPoster.addListener(this);
	}

	public void postToDataLabel(final String msg){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String previous = m_dataLabel.getText();
				//            	m_dataLabel.setText(s_dateFormatUTC.format(new Date()) + "\t" + msg + "\n" + previous);
				m_dataLabel.setText( msg + "\n" + previous);
				m_dataLabel.getParent().update();
				m_dataLabel.pack();
			}
		});
	}

	@PostConstruct
	public void init(IWorkbench iw) {
		m_workbench = iw;
	}

	public void postedToLog(String post) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				postToDataLabel(post);
			}
		});
	}
}
