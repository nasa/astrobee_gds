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
package gov.nasa.rapid.v2.ui.e4.parts;

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.ParticipantCreator;
import gov.nasa.dds.rti.util.DiscoveredParticipants;
import gov.nasa.dds.rti.util.DiscoveredParticipants.ParticipantInfo;
import gov.nasa.dds.system.Dds;
import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IDiscoveredAgentListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Quick and overly simple view to show discovered information A better version of this view would be much appreciated.
 */
public class DiscoveredDdsInfoPart implements IDdsRestartListener, IDiscoveredAgentListener {
    public static String   ID       = DiscoveredDdsInfoPart.class.getName();

    protected Text         m_agentText;
    protected Text         m_partitionText;
    protected Text         m_participantText;

    protected final String indent   = "    ";
    protected final String newline  = "\n";
    protected final String separate = "\n----------------------------------------\n";
    
    Action                 m_refreshAction;
    Action                 m_showTopicsAction;
    Action                 m_showMetatrafficLocatorsAction;
    
    boolean                m_showTopics = false;
    boolean                m_showMetatrafficLocators = false;
    @Inject
    IEventBroker			eventBroker;

    @Inject
    public DiscoveredDdsInfoPart(Composite parent, IEventBroker ieb, IEclipseContext iec) {
    	createPartControl(parent);
    	eventBroker = ieb;
        if(eventBroker != null) {
        	eventBroker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
        }
        iec.set(DiscoveredDdsInfoPart.class, this);
    }

    /**
     * clear all text
     */
    public void clear() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                m_agentText.setText("");
                m_partitionText.setText("");
                m_participantText.setText("");
            }
        });
    }
    
    public void setShowTopics(boolean showTopics) {
    	 m_showTopics = showTopics;
    	 refresh();
    }

//    protected void createActions() {
//        m_refreshAction = new Action() {
//            @Override
//            public void run() {
//                refresh();
//            }
//        };
//        m_refreshAction.setText("Refresh Info");
//        m_refreshAction.setToolTipText("Refresh Discovered Information\n(not all information is automatically updated)");
//        m_refreshAction.setImageDescriptor(ImageRegistryKeeper.getInstance().getImageDescriptorFromRegistry("refresh"));
//
//        m_showTopicsAction = new Action("show topics", Action.AS_CHECK_BOX) {
//            @Override
//            public void run() {
//                m_showTopics = this.isChecked();
//                refresh();
//            }
//        };
//        m_showTopicsAction.setToolTipText("Show publication topics for each partition");
//
//        m_showMetatrafficLocatorsAction = new Action("show metatraffic locators", Action.AS_CHECK_BOX) {
//            @Override
//            public void run() {
//                m_showMetatrafficLocators = this.isChecked();
//                refresh();
//            }
//        };
//    }

//    protected void hookActions() {
//        IActionBars bars = getViewSite().getActionBars();
//        IMenuManager mm = bars.getMenuManager();
//        IToolBarManager tbm = bars.getToolBarManager();
//        mm.add(m_refreshAction);
//        mm.add(m_showTopicsAction);
//        mm.add(m_showMetatrafficLocatorsAction);
//        
//        tbm.add(m_showTopicsAction);
//        tbm.add(m_refreshAction);
//    }

    public void createPartControl(Composite topComposite) {
        GridLayout topLayout;
        GridData gridData;

        topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        topLayout = new GridLayout(2, false);
        topLayout.verticalSpacing = 2;
        topComposite.setLayout(topLayout);

        Label label;

        label = new Label(topComposite, SWT.LEFT);
        label.setText("Discovered Agents: ");
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        label.setLayoutData(gridData);

        label = new Label(topComposite, SWT.LEFT);
        label.setText("Discovered Participants: ");
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        label.setLayoutData(gridData);

        m_agentText = new Text(topComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        m_agentText.setLayoutData(gridData);

        m_participantText = new Text(topComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
        m_participantText.setLayoutData(gridData);

        label = new Label(topComposite, SWT.LEFT);
        label.setText("Discovered Partitions: ");
        gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        label.setLayoutData(gridData);
        m_partitionText = new Text(topComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
        m_partitionText.setLayoutData(gridData);

        topComposite.pack();

//        createActions();
//        hookActions();

        delayedRefresh(1500);
        Dds.addRestartListener(this);
        DiscoveredAgentRepository.INSTANCE.addListener(this);
    }

    @PreDestroy
    public void dispose() {
        try {
            DiscoveredAgentRepository.INSTANCE.removeListener(this);
            Dds.removeRestartListener(this);
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * refresh all text
     */
    public void delayedRefresh(final int millisec) {
        Display.getDefault().asyncExec(new Runnable() { // avoid invalid thread
            // access
            @Override
            public void run() {
                Display.getDefault().timerExec(millisec, new Runnable() {
                    @Override
                    public void run() {
                        refreshAgents();
                        refreshPartitions();
                        refreshParticipants();
                    }
                });
            }
        });
    }

    @Override
    public void newAgentsDiscovered(String participantId, Collection<Agent> agents) {
        // ignore
    	delayedRefresh(500);
    }

    @Override
    public void newPartitionsDiscovered(String participantId, Collection<String> partitions) {
//        System.err.println("newPartitionsDiscovered: " + participantId);
//        for (String str : partitions) {
//            System.err.println("     : " + str);
//        }
        delayedRefresh(500);
    }

    @Override
    public void onDdsAboutToStop() throws Exception {
        clear();
    }

    @Override
    public void onDdsStarted() throws Exception {
        delayedRefresh(100);
    }

    @Override
    public void onDdsStopped() throws Exception {
        // nothing
    }

    /**
     * refresh all text
     */
    public void refresh() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                refreshAgents();
                refreshPartitions();
                refreshParticipants();
            }
        });
    }

    protected String participantString(String participantId) {
        ParticipantCreator pc = DdsEntityFactory.getParticipantCreator(participantId);
        return String.format("%s (domain %d : %s)", participantId, pc.domainId, pc.participantName);
    }

    public void refreshAgents() {
        StringBuilder builder = new StringBuilder();
        for (String participantId : DdsEntityFactory.getValidParticipantIds()) {
            builder.append(participantString(participantId));
            builder.append(separate);
            for (Agent agent : DiscoveredAgentRepository.INSTANCE.getDiscoveredAgents(participantId)) {
                builder.append(indent);
                builder.append(agent.name());
                builder.append(newline);
            }
            builder.append(newline);
        }
        if(!m_agentText.isDisposed())
            m_agentText.setText(builder.toString());
    }

    public void refreshParticipants() {
        StringBuilder builder = new StringBuilder();
        for (String participantId : DdsEntityFactory.getValidParticipantIds()) {
            builder.append(participantString(participantId));
            builder.append(separate);
            Map<String, ParticipantInfo> discovered = DiscoveredParticipants.getDiscoveredParticipants(participantId);
            String[] keys = discovered.keySet().toArray(new String[discovered.keySet().size()]);
            Arrays.sort(keys);
            for (String key : keys) {
                ParticipantInfo pi = discovered.get(key);
                builder.append(indent);
                builder.append(key).append(newline);
                if(true) {
                    builder.append(indent).append(indent).append(indent).append("default locators:\n");
                    for (String locator : pi.locatorStrings) {
                        builder.append(indent).append(indent).append(indent).append(indent);
                        builder.append(locator);
                        builder.append(newline);
                    }
                }
                if(m_showMetatrafficLocators) {
                    builder.append(indent).append(indent).append(indent).append("metatraffic locators:\n");
                    for (String locator : pi.metatrafficStrings) {
                        builder.append(indent).append(indent).append(indent).append(indent);
                        builder.append(locator);
                        builder.append(newline);
                    }
                }
            }
            builder.append(newline);
        }
        if(!m_participantText.isDisposed()) 
            m_participantText.setText(builder.toString());
    }

    public void refreshPartitions() {
        StringBuilder builder = new StringBuilder();
        for (String participantId : DdsEntityFactory.getValidParticipantIds()) {
            builder.append(participantString(participantId));
            builder.append(separate);
            for (String partition : DiscoveredAgentRepository.INSTANCE.getDiscoveredPartitions(participantId)) {
                builder.append(indent);
                builder.append(partition);
                builder.append(newline);
                if(m_showTopics) {
                    String[] topics = DiscoveredAgentRepository.INSTANCE.getTopicsFor(partition);
                    for(String topic : topics) {
                        builder.append(indent).append(indent).append(topic).append(newline);
                    }
                }
            }
            builder.append(newline);
        }
        if(!m_partitionText.isDisposed()) 
            m_partitionText.setText(builder.toString());
    }

    @Focus
    public void setFocus() {
        m_participantText.setFocus();
    }

	@Override
	public void agentsDisappeared(String participantId, Collection<Agent> agents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void partitionsDisappeared(String participantId,
			Collection<String> partitions) {
		// TODO Auto-generated method stub
		
	}

}
