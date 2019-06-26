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
package gov.nasa.arc.irg.freeflyer.rapid;

import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.rapid.ui.e4.view.CommandParamGroup;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.Command;
import rapid.CommandDef;
import rapid.CommandDefSeq;
import rapid.DataType;
import rapid.KeyTypePair;
import rapid.KeyTypePairSeq;

public class CommandConfigWidget extends Composite {
	private List<Control> m_controls;
	private Agent m_agent;
	private int m_gridSquares;
	private int m_buttonWidth = 3;
	private String m_paramString = "params";
	private String m_cmdDefString = "commandDef";
	private String m_subsystemString = "subsystem";
	private CommandPublisher m_commandPublisher;
	private String m_defaultNumber = "0.0";

	public CommandConfigWidget(Composite parent, Agent agent, CommandDefSeq cmds, String subsystemName) {
		super(parent, SWT.None);
		
		GridLayout gridLayout = new GridLayout(m_gridSquares, true);
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);
		
		m_gridSquares = 9;
		m_agent = agent;
		m_controls = new ArrayList<Control>();
		m_commandPublisher = CommandPublisher.getInstance(m_agent);
		
		Iterator<CommandDef> cmdIter = cmds.iterator();
		while(cmdIter.hasNext()) {
			Composite innerComposite = new Composite(parent, SWT.None);
			GridDataFactory.fillDefaults().grab(true, true).span(2,1).applyTo(innerComposite);
			GridLayout gl = new GridLayout(m_gridSquares, true);
			innerComposite.setLayout(gl);
			m_controls.add(innerComposite);
			
			Label sep = new Label(innerComposite, SWT.HORIZONTAL | SWT.SEPARATOR);
			GridDataFactory.fillDefaults().grab(true, false).span(m_gridSquares,1).applyTo(sep);
			m_controls.add(sep);

			CommandDef cdef = cmdIter.next();

			final EnlargeableButton b = new EnlargeableButton(innerComposite, SWT.PUSH);
			b.setText(cdef.name);
			GridDataFactory.fillDefaults().grab(true, false).span(m_buttonWidth,1).applyTo(b);
			b.setData(m_cmdDefString, cdef);
			b.setData(m_subsystemString, subsystemName);

			Label spacer1 = new Label(innerComposite, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, false).span(m_gridSquares - m_buttonWidth,1).applyTo(spacer1);
			m_controls.add(spacer1);

			KeyTypePairSeq ktps = cdef.parameters.userData;
			if(ktps.size() > 0) {
				List<CommandParamGroup> params = new ArrayList<CommandParamGroup>();

				Iterator ktIter = ktps.iterator();
				while(ktIter.hasNext()) {

					KeyTypePair ktp = (KeyTypePair)ktIter.next();
					Label key = new Label(innerComposite, SWT.None);
					key.setText(ktp.key);
					GridDataFactory.fillDefaults().align(SWT.END, SWT.BEGINNING).grab(true, false).span(m_buttonWidth,1).applyTo(key);
					m_controls.add(key);

					CommandParamGroup cpg;
					if(ktp.type.equals(DataType.RAPID_VEC3d)) {
						List<Text> in = new ArrayList<Text>();
						Text[] arr = new Text[3];
						
						for(int i=0; i<3; i++) {
							arr[i] =  new Text(innerComposite, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
							
						}
						m_controls.addAll(in);
						cpg = new CommandParamGroup(ktp.key, ktp.type, in);
					}
					else if(ktp.type.equals(DataType.RAPID_MAT33f)) {
						List<Text> in = new ArrayList<Text>();
						Text[] arr = new Text[3];
						
						for(int i=0; i<3; i++) {
							arr[i] =  new Text(innerComposite, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}
						makeButtonSpacer(innerComposite);
						makeButtonSpacer(innerComposite);
						
						for(int i=0; i<3; i++) {
							arr[i] =  new Text(innerComposite, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}
						makeButtonSpacer(innerComposite);
						makeButtonSpacer(innerComposite);
						
						for(int i=0; i<3; i++) {
							arr[i] =  new Text(innerComposite, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}
						
						m_controls.addAll(in);
						cpg = new CommandParamGroup(ktp.key, ktp.type, in);
						
					}
					else {
						Text input = new Text(innerComposite, SWT.BORDER);
						if(ktp.type.equals(DataType.RAPID_BOOL)) {
							input.setText("false");
						}
						else if(ktp.type.equals(DataType.RAPID_STRING)) {
							input.setText("string");
						}
						else {
							input.setText("0");
						}
						GridDataFactory.fillDefaults().grab(true, false).span(m_buttonWidth,1).applyTo(input);
						m_controls.add(input);

						cpg = new CommandParamGroup(ktp.key, ktp.type, input);
					}
					Label type = new Label(innerComposite, SWT.None);
					type.setText(ktp.type.toString());
					GridDataFactory.fillDefaults().grab(true, false).span(m_buttonWidth,1).applyTo(type);
					m_controls.add(type);
					
					params.add(cpg);
				}

				b.setData(m_paramString, params);
			}

			b.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					// create a command
					Command cmd = m_commandPublisher.
							buildCommand((CommandDef)b.getData(m_cmdDefString), 
									(String)b.getData(m_subsystemString));

					// put the parameters in
					Object o = b.getData(m_paramString);
					if(o instanceof List<?>) {
						List<CommandParamGroup> cpgList = (List<CommandParamGroup>)o;
						ParameterList pl = new ParameterList();

						for(CommandParamGroup cpg : cpgList) {
							pl = cpg.setParameterInCommand(pl);

						}
						pl.assign(cmd.arguments.userData);
					}

					// send it
					m_commandPublisher.sendCommand(cmd);
					LogPoster.postToLog(LogEntry.COMMAND, cmd, m_agent.name());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// no-op
				}
			});
			m_controls.add(b);
		}
	}

	private void makeButtonSpacer(Composite parent) {
		Label l = new Label(parent, SWT.None);
		GridDataFactory.fillDefaults().grab(true, false).span(m_buttonWidth,1).applyTo(l);
		m_controls.add(l);
	}
	
	@Override
	public void dispose() {
		ListIterator<Control> cbli = m_controls.listIterator();
		while(cbli.hasNext()) {
			Control c = (Control) cbli.next();
			if(c != null) {
				c.dispose();
			}
		}
		super.dispose();
	}
}
