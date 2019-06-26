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

import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class LiveImagesSelectAstrobeePart extends LiveImagesPart implements  IActiveAgentSetListener {
	protected Combo partitionsCombo;
	private String SELECT_STRING = "Select Bee ...";
	
	@Inject
	public LiveImagesSelectAstrobeePart(Composite parent) {
		super(parent);
		ActiveAgentSet.INSTANCE.addListener(this);
	}
	
	protected void createPartitionsCombo(Composite parent) {
		partitionsCombo = new Combo(parent, SWT.READ_ONLY);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 150;
		partitionsCombo.setLayoutData(gd);

		populatePartitionsCombo();
		partitionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					Agent agent = Agent.valueOf(partitionsCombo.getText());
					onAgentSelected(agent);
				}catch(Exception ee){
					//usually mean you selected "Select Astrobee.." which will fail
				}
			}
		});

		parent.layout(true);
		parent.pack();
		parent.update();
	}
	
	protected void createPreTableParts(Composite container) {
		Label agentLabel = new Label(container, SWT.NONE);
		agentLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
		agentLabel.setText("Select Bee");
		
		createPartitionsCombo(container);
		
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
		label.setText("Show Images from Camera");
		topicsCombo = new Combo(container, SWT.READ_ONLY);
		topicsCombo.setSize(85, 20);
		GridData gdTopicsCombo = new GridData( SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdTopicsCombo.horizontalSpan = 1;
		topicsCombo.setLayoutData(gdTopicsCombo);
		topicsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unsubscribe();
				
				if(topicsCombo.getText().equals(NAV_CAM_NAME)){
					imageIcon.setImage(forward);
					imageIcon.setToolTipText(FWD_CAM_TOOLTIP);
					imageIcon.setVisible(true);
				}else if(topicsCombo.getText().equals(DOCK_CAM_NAME)){
					imageIcon.setImage(backward);
					imageIcon.setToolTipText(BKWD_CAM_TOOLTIP);
					imageIcon.setVisible(true);
				} else {
					imageIcon.setToolTipText("");
					imageIcon.setVisible(false);
					return;
				}
				
				String subtopic = getTopicFromCameraName(topicsCombo.getText());
				sampleType = getTheMessageType(subtopic);
				subscribe();
			}
		});
		
		container.layout(true);
		container.pack();
	}
	
	@Override
	public void activeAgentSetChanged() {
		populatePartitionsCombo();
	}
	
	protected void populatePartitionsCombo() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				String namesArray[] = makePartitionList();
				partitionsCombo.setItems(namesArray);
				partitionsCombo.pack(true);
				partitionsCombo.redraw();
				if(agent != null){ //makes agent null
					boolean agentExist = false;
					for(int i = 0; i < namesArray.length;i++){
						if(namesArray[i].equals(agent.name())){
							agentExist = !agentExist;
							break;
						}
					}
				}
				if (agent != null) {
					int index = partitionsCombo.indexOf(selectedPartition);
					if(index == -1) {
						partitionsCombo.select(0);
						topicsCombo.removeAll();
						imageIcon.setVisible(false);
					}
					else 
						partitionsCombo.select(index);
				}
				else{
					partitionsCombo.select(0);
					topicsCombo.removeAll();
					imageIcon.setVisible(false);
				}
			}
		});
	}
	
	public String[] makePartitionList(){
		final List<Agent> agents = ActiveAgentSet.asList();
		final List<String> agentStrings = new ArrayList<String>();
		for(final Agent a : agents){
			if(!a.equals(Agent.SmartDock)) {
				agentStrings.add(a.name());
			};
		}
		agentStrings.remove(SELECT_STRING);
		agentStrings.add(0, SELECT_STRING);
		
		return agentStrings.toArray(new String[agentStrings.size()]);
	}
	
	// do NOT inject into this class
	@Override
	public void onAgentSelected(Agent a) {
		if(a == null || a.name().equals(selectedPartition)) {
			return;
		}
		unsubscribe();
		selectedPartition = a.name();
		agent = a;
		topicsComboPopulated = populateTopicsCombo();
	}
	
	@PersistState
	public void close(){
		ActiveAgentSet.INSTANCE.removeListener(this);
	}

}
