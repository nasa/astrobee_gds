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
package gov.nasa.arc.irg.freeflyer.rapid.centralized;

import gov.nasa.arc.irg.freeflyer.rapid.parts.CentralizedConfigCommanderPart;
import gov.nasa.arc.irg.plan.ui.io.EnlargeableButton;
import gov.nasa.arc.irg.rapid.ui.e4.view.CommandParamGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import rapid.CommandDef;
import rapid.CommandDefSeq;
import rapid.DataType;
import rapid.KeyTypePair;
import rapid.KeyTypePairSeq;

public class CentralizedCommandConfigWidget extends Composite {
	private List<Control> m_controls;
	private CommandDefSeq m_cmds;
	private int m_gridSquares;
	private int m_buttonWidth = 3;
	private String m_paramString = "params";
	private String m_cmdDefString = "commandDef";
	private String m_subsystemString = "subsystem";
	private String m_defaultNumber = "0.0";
	private CentralizedConfigCommanderPart m_configCommander;

	public CentralizedCommandConfigWidget(Composite parent, CommandDefSeq cmds, 
			int gridSquares, String subsystemName, CentralizedConfigCommanderPart cccp) {
		super(parent, SWT.None);
		m_gridSquares = gridSquares;
		m_cmds = cmds;
		m_controls = new ArrayList<Control>();
		m_configCommander = cccp;

		GridData gdButtons = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdButtons.horizontalSpan = m_buttonWidth;

		GridData gdWholeWidth = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdWholeWidth.horizontalSpan = m_gridSquares;

		GridData gdKeys = new GridData(SWT.END, SWT.FILL, true, false);
		gdKeys.horizontalSpan = m_buttonWidth;

		GridData gdTypes = new GridData(SWT.FILL, SWT.FILL, true, true);
		gdTypes.horizontalSpan = m_buttonWidth;
		gdTypes.heightHint = 20;

		Iterator<CommandDef> cmdIter = cmds.iterator();
		while(cmdIter.hasNext()) {
			Label sep = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
			sep.setLayoutData(gdWholeWidth);
			m_controls.add(sep);

			CommandDef cdef = cmdIter.next();

			final EnlargeableButton b = new EnlargeableButton(parent, SWT.PUSH);
			b.setText(cdef.name);
			b.setLayoutData(gdButtons);
			b.setData(m_cmdDefString, cdef);
			b.setData(m_subsystemString, subsystemName);

			Label spacer1 = new Label(parent, SWT.NONE);
			GridData gdSpacer = new GridData(SWT.FILL, SWT.FILL, true, true);
			gdSpacer.horizontalSpan = m_gridSquares - m_buttonWidth;
			spacer1.setLayoutData(gdSpacer);
			m_controls.add(spacer1);

			KeyTypePairSeq ktps = cdef.parameters.userData;
			if(ktps.size() > 0) {
				List<CommandParamGroup> params = new ArrayList<CommandParamGroup>();

				Iterator ktIter = ktps.iterator();
				while(ktIter.hasNext()) {

					KeyTypePair ktp = (KeyTypePair)ktIter.next();
					Label key = new Label(parent, SWT.None);
					key.setText(ktp.key);
					key.setLayoutData(gdKeys);
					m_controls.add(key);

					CommandParamGroup cpg;
					if(ktp.type.equals(DataType.RAPID_VEC3d)) {
						List<Text> in = new ArrayList<Text>();
						Text[] arr = new Text[3];

						for(int i=0; i<3; i++) {
							arr[i] =  new Text(parent, SWT.BORDER);
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
							arr[i] =  new Text(parent, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}
						makeSpacer(parent, gdButtons);
						makeSpacer(parent, gdButtons);

						for(int i=0; i<3; i++) {
							arr[i] =  new Text(parent, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}
						makeSpacer(parent, gdButtons);
						makeSpacer(parent, gdButtons);

						for(int i=0; i<3; i++) {
							arr[i] =  new Text(parent, SWT.BORDER);
							arr[i].setText(m_defaultNumber);
							in.add(arr[i]);
						}

						m_controls.addAll(in);
						cpg = new CommandParamGroup(ktp.key, ktp.type, in);

					}
					else {
						Text input = new Text(parent, SWT.BORDER);
						if(ktp.type.equals(DataType.RAPID_BOOL)) {
							input.setText("false");
						}
						else if(ktp.type.equals(DataType.RAPID_STRING)) {
							input.setText("string");
						}
						else {
							input.setText("0");
						}
						input.setLayoutData(gdTypes);
						m_controls.add(input);

						cpg = new CommandParamGroup(ktp.key, ktp.type, input);
					}
					Label type = new Label(parent, SWT.None);
					type.setText(ktp.type.toString());
					type.setLayoutData(gdTypes);
					m_controls.add(type);

					params.add(cpg);
				}

				b.setData(m_paramString, params);
			}

			b.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					/*
					 * Instead of this, could call commandRequested() on 
					 * ConfigCommanderPart and have him send it.
					 * That way we don't have to pass agent and LogPoster and
					 * probably even subsystem name through to this class.
					 */
					CommandDef cdef = (CommandDef)b.getData(m_cmdDefString);
					String subsystemName = (String)b.getData(m_subsystemString);
					Object o = b.getData(m_paramString);

					m_configCommander.commandRequested(cdef, subsystemName, o);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// no-op
				}
			});
			m_controls.add(b);
		}
	}

	private void makeSpacer(Composite parent, GridData gd) {
		Label l = new Label(parent, SWT.None);
		l.setLayoutData(gd);
		m_controls.add(l);
	}

	@Override
	public void dispose() {
		ListIterator cbli = m_controls.listIterator();
		while(cbli.hasNext()) {
			Control c = (Control) cbli.next();
			if(c != null) {
				c.dispose();
			}
		}
		super.dispose();
	}
}
