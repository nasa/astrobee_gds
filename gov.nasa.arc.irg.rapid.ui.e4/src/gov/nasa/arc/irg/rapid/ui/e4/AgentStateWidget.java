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
package gov.nasa.arc.irg.rapid.ui.e4;

import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.rapid.v2.e4.message.MessageType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import rapid.AgentConfig;
import rapid.AgentState;
import rapid.DataType;
import rapid.KeyTypeValueTriple;
import rapid.Mat33f;
import rapid.ParameterUnion;
import rapid.Vec3d;
public class AgentStateWidget extends Composite {
	private static final Logger logger = Logger.getLogger(AgentStateWidget.class);
	protected List<Control> m_controls = new ArrayList<Control>(); // for disposal
	private List<List<Label>> m_displaysList = new ArrayList<List<Label>>(); // in order list of all data fields for display
	protected MessageType m_messageType; // so we can check we got the right thing to update
	protected ScrolledComposite m_scrollComposite;
	protected Composite m_scrollInner; 
	
	public AgentStateWidget(Composite parent, AgentConfig ac, MessageType msgType) {
		super(parent, SWT.NONE);
		if(msgType == null) {
			logger.error("MessageType cannot be null!");
		}
		m_messageType = msgType;

		init(parent, ac);
	}
	
	protected void init(Composite parent, AgentConfig ac) {
		GridData gdKeys = new GridData(SWT.FILL, SWT.FILL, false, false);
		gdKeys.horizontalSpan = 1;

		GridData gdSingleValue = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		gdSingleValue.horizontalSpan = 3;
		gdSingleValue.heightHint = 18;

		GridData gdWholeWidth = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdWholeWidth.horizontalSpan = 4;

		Iterator<KeyTypeValueTriple> valIter = ac.valueKeys.userData.iterator();
		Label sep = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		sep.setLayoutData(gdWholeWidth);
		m_controls.add(sep);

		m_scrollComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gdScrollComposite = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdScrollComposite.horizontalSpan = 4;
		m_scrollComposite.setLayoutData(gdScrollComposite);
		m_scrollComposite.setLayout(new GridLayout(1,false));
		
		m_scrollInner = new Composite(m_scrollComposite, SWT.NONE);
		m_scrollInner.setLayout(new GridLayout(4,false));
	
		m_scrollInner.setLayoutData(gdWholeWidth);
		m_scrollComposite.setContent(m_scrollInner);
		int maxKeyLength = 0;
		while(valIter.hasNext()) {
			// most things will just have a label and a box for the value
			KeyTypeValueTriple element = valIter.next();
			Label key = new Label(m_scrollInner, SWT.None);
			key.setText(element.key);
			if(element.key.length() > maxKeyLength) {
				maxKeyLength = element.key.length();
			}
			
			key.setLayoutData(gdKeys);
			m_controls.add(key);
			
			List<Label> values = new ArrayList<Label>();
			Label val;

			if(element.value.discriminator().equals(DataType.RAPID_MAT33f)) {
				for(int j=0; j<3; j++) {
					for(int i=0; i<3; i++) {
						val = new Label(m_scrollInner, SWT.NONE);
						val.setText(" matrix ");
						val.setBackground(ColorProvider.INSTANCE.white);
						values.add(val);
					}
					if(j<2) {
						new Label(m_scrollInner, SWT.NONE);
					}
				}
			}
			else if(element.value.discriminator().equals(DataType.RAPID_VEC3d)) {

				for(int i=0; i<3; i++) {
					val = new Label(m_scrollInner, SWT.NONE);
					val.setText(" vector ");
					val.setBackground(ColorProvider.INSTANCE.white);
					values.add(val);
				}
			}
			else {
				// it just needs one box
				val = new Label(m_scrollInner, SWT.None);
				val.setText(element.value.discriminator().name());
				val.setLayoutData(gdSingleValue);
				val.setBackground(ColorProvider.INSTANCE.white);
				val.setSize(100,20);
				values.add(val);
			}
			m_displaysList.add(values);
			m_controls.addAll(values);
		}
		
		m_scrollComposite.setSize(1000, 1000);
		
		m_scrollInner.layout();
		m_scrollInner.pack();
		m_scrollInner.update();
		parent.layout();
		parent.pack();
		parent.update();
	}

	public void updateData(MessageType msgType, Object obj) {
		if(m_displaysList == null || !m_messageType.equals(msgType) 
				|| !(obj instanceof AgentState)) {
			return;
		}
		AgentState as = (AgentState) obj;

		Iterator<ParameterUnion> values = as.values.userData.iterator();
		Iterator<List<Label>> lblsIter = m_displaysList.iterator();
		while(values.hasNext()) {
			ParameterUnion val = values.next();
			if(lblsIter.hasNext()) {
				List<Label> lbls = lblsIter.next();
				if(val.discriminator().equals(DataType.RAPID_MAT33f)) {
					// weird case deal with it later
					Mat33f mat = val.mat33f();
					for(int i=0; i<9; i++) {
						lbls.get(i).setText(Double.toString(mat.userData[i]));
					}
				}
				else if(val.discriminator().equals(DataType.RAPID_VEC3d)) {
					Vec3d vec = val.vec3d();
					for(int i=0; i<3; i++) {
						lbls.get(i).setText(Double.toString(vec.userData[i]));
					}
				}
				else if(val.discriminator().equals(DataType.RAPID_STRING)) {
					lbls.get(0).setText(val.s());
				}
				else if(val.discriminator().equals(DataType.RAPID_INT)) {
					lbls.get(0).setText(Integer.toString(val.i()));
				}
				else if(val.discriminator().equals(DataType.RAPID_LONGLONG)) {
					lbls.get(0).setText(Long.toString(val.ll()));
				}
				else if(val.discriminator().equals(DataType.RAPID_FLOAT)) {
					lbls.get(0).setText(Float.toString(val.f()));
				}
				else if(val.discriminator().equals(DataType.RAPID_DOUBLE)) {
					lbls.get(0).setText(Double.toString(val.d()));
				}
				else if(val.discriminator().equals(DataType.RAPID_BOOL)) {
					lbls.get(0).setText(Boolean.toString(val.b()));
				}
			}
		}
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
