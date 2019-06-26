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
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.TabName;
import gov.nasa.arc.verve.freeflyer.workbench.utils.AgentsFromCommandLine;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public abstract class LiveVideoComboPart extends LiveVideo implements IActiveAgentSetListener, IPartListener {
	private static final Logger logger = Logger.getLogger(LiveVideoComboPart.class);
	private WorkbenchMediaPlayer mediaPlayer;
	private final Combo astrobeeCombo;
	private final Composite videoComposite;
	private final String SELECT_STRING = "Select Bee ...";
	private String currentSelect = "";
	private Agent currentAgent = null;
	protected String MY_PARENT_TAB;
	protected String MY_VIDEO_TAB;
	int myVideoRequestCount = 0;
	LiveVideoAgentListener agentVideoPlayingListener;
	EPartService eps;
	protected TabName TAB_NAME;
	public boolean videoPartVisible = true;
	public boolean videoParentVisible = true;
	private boolean noImages = false;
	
	@Inject
	public LiveVideoComboPart(final EPartService eps, final Composite parent, final MApplication application) {
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.NO_IMAGES)) {
			noImages = true;
			videoComposite = null;
			astrobeeCombo = null;
			return;
		}
		
		final GridLayout gridLayout = new GridLayout(2, false);
		parent.setLayout(gridLayout);

		final Label selectLabel = new Label(parent, SWT.None);
		selectLabel.setText("Select Bee");
		
		astrobeeCombo = new Combo(parent, SWT.READ_ONLY);
		
		final String[] agentArray = new String[3];
		for (int i = 0; i < 3; i++) {
			agentArray[i] = AgentsFromCommandLine.INSTANCE.getAgent(i).name();
		}
		populateDropDown();
		astrobeeCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				// find out who is selected
				if (currentSelect.equals(astrobeeCombo.getItem(astrobeeCombo.getSelectionIndex())) || astrobeeCombo.getText().equals(SELECT_STRING)) {
					if(astrobeeCombo.getText().equals(SELECT_STRING)){
						astrobeeCombo.setText(currentSelect);
					}
					return;
				}
				currentSelect = astrobeeCombo.getItem(astrobeeCombo.getSelectionIndex());
				try {
					LiveVideoAgentHolder.releaseAgent(currentAgent,agentVideoPlayingListener,true);
					currentAgent = Agent.valueOf(astrobeeCombo.getText());
				} catch (final Exception e) {
					// don't choke on the Select string
					return;
				}
				
				LiveVideoAgentHolder.selectAgent(currentAgent,agentVideoPlayingListener,true);
				setupAndStartMediaPlayer();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		astrobeeCombo.pack();
		astrobeeCombo.select(0);
		
		final Composite composite = new Composite(parent, SWT.NONE);
		
		composite.setLayout(new GridLayout(1, true));
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		composite.setLayoutData(gd);
		
		
		videoComposite = new Composite(composite, SWT.EMBEDDED);
		
		
		gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = 1;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		
		videoComposite.setLayoutData(gd);
		
		videoComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				// resize video on mac
				if(mediaPlayer != null && videoComposite.getBounds().width != WIDTH){
					mediaPlayer.resize(videoComposite.getBounds().width,videoComposite.getBounds().height);
				}
			}
		});
		videoComposite.pack();
		parent.pack();
		
		configureProperties();
		ActiveAgentSet.INSTANCE.addListener(this);
		eps.addPartListener(this);
		this.eps = eps;
		
		agentVideoPlayingListener = new LiveVideoAgentListener() {
			
			@Override
			public void agentSelected(final Agent a) {
				populateDropDown();
				if(currentAgent == null){
					return;
				}
				
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						final int index = astrobeeCombo.indexOf(a.name());
						if(index > 0){
							astrobeeCombo.remove(index);
						}				
					}
				});
			}
			
			@Override
			public void agentRelease() {
				populateDropDown();
				
				if(currentAgent == null)
					return;
				
				if(videoPartVisible && videoParentVisible){
					final ScheduledThreadPoolExecutor thread = new ScheduledThreadPoolExecutor(1);
					ScheduledFuture<Boolean> schedule = thread.schedule(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							for(MPart part : eps.getParts()){
								if(MY_VIDEO_TAB.equals(part.getElementId()) && part.isVisible()){
									//if(TAB_NAME.matches(eps.getActivePart().getElementId()) && videoPartVisible && videoParentVisible){
										if(!LiveVideoAgentHolder.isAgentSelected(currentAgent) && (mediaPlayer == null || !mediaPlayer.getMediaPlayer().isPlaying())){
											return true;
										}
									//}
								}
							}
							return false;
						}
					}, 1000, TimeUnit.MILLISECONDS);
					Display.getCurrent().asyncExec(new Runnable() {
						
						@Override
						public void run() {
							try {
								if(schedule.get()){
									setupAndStartMediaPlayer();
									LiveVideoAgentHolder.selectAgent(currentAgent, agentVideoPlayingListener,true);
								}
							} catch (Exception e){
								e.printStackTrace();
							}
						}
					});
				}
			}
		};
		LiveVideoAgentHolder.addListener(agentVideoPlayingListener);
	}

	private void setupAndStartMediaPlayer() {
		if (currentAgent == null) {
			return;
		}
		// logger.info("thinking about playing " +
		// PROPERTIES.getProperty(currentAgent.name()+"."+STREAM_URL));
		if(mediaPlayer != null){
			mediaPlayer.getMediaPlayer().stop();
		}else{
			final Frame frame = SWT_AWT.new_Frame(videoComposite);
			mediaPlayer = new WorkbenchMediaPlayer(WIDTH,HEIGHT);
			frame.add(mediaPlayer.getVideoSurface());
			mediaPlayer.getMediaPlayer().setPlaySubItems(true);
			
			//used to resize composite because it first render too big (actualy video dimensions) for the composite 
			videoComposite.setSize(videoComposite.getParent().getBounds().width,videoComposite.getParent().getBounds().height);
		}

		try{
			mediaPlayer.getMediaPlayer().playMedia(PROPERTIES.getProperty(currentAgent.name() + "." + STREAM_URL));//rtsp://184.72.239.149/vod/mp4:BigBuckBunny_115k.mov");
		}catch(final Exception e){
			mediaPlayer.stopStream();
			mediaPlayer = null;
			logger.info("ERROR: Unable to find and play a stream for "+currentAgent.name()+". Try modifying the global.properties file by editing an existing robot's stream or adding a new entry");
		}

//		context.set(FreeFlyerStrings.VIDEO_STREAMING_COUNTER, null);
//		myVideoRequestCount++;
//		context.set(FreeFlyerStrings.VIDEO_STREAMING_COUNTER, new Integer(
//				myVideoRequestCount));
		// logger.info("Starting media player from " +
		// this.getClass().getSimpleName()
		// + " counter = " + myVideoRequestCount);
	}

	private void populateDropDown() {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				astrobeeCombo.setItems(makePartitionList());
				if (astrobeeCombo.getItemCount() == 1 || currentSelect.isEmpty()) {
					astrobeeCombo.select(0);
				} else if (!currentSelect.isEmpty()) {
					boolean found = false;
					for (int i = 0; i < astrobeeCombo.getItemCount(); i++) {
						if (astrobeeCombo.getItem(i).equals(currentSelect)) {
							astrobeeCombo.select(i);
							found = true;
							break;
						}
					}
					if (!found) {
						if(astrobeeCombo.indexOf(SELECT_STRING) != -1)
							astrobeeCombo.remove(SELECT_STRING);
						astrobeeCombo.add(SELECT_STRING, 0);
						astrobeeCombo.select(0);
					}
				}
				astrobeeCombo.pack();
			}
		});
	}

	protected void restartMediaPlayer() {
		if(noImages) {
			return;
		}
		logger.info("Restarting media player from "+ this.getClass().getSimpleName());
		if(mediaPlayer == null && (currentAgent != null && ActiveAgentSet.contains(currentAgent))){
			if(!LiveVideoAgentHolder.isAgentSelected(currentAgent)){
				LiveVideoAgentHolder.selectAgent(currentAgent, agentVideoPlayingListener,true);
				setupAndStartMediaPlayer();
			}
		}
	}

	public String[] makePartitionList() {
		if(noImages) {
			return null;
		}
		final List<Agent> agents = ActiveAgentSet.asList();
		final List<String> agentStrings = new ArrayList<String>();
		for (final Agent a : agents) {
			if (!a.equals(Agent.SmartDock) && (!LiveVideoAgentHolder.isAgentSelected(a) || (currentAgent != null && currentAgent.name().equals(a.name())))) {
				agentStrings.add(a.name());
			}
		}
		agentStrings.remove(SELECT_STRING);
		agentStrings.add(0, SELECT_STRING);
		return agentStrings.toArray(new String[agentStrings.size()]);
	}

	@Override
	public void activeAgentSetChanged() {
		if(noImages) {
			return;
		}
		populateDropDown();
	}

	@PersistState
	public void close() {
		if(noImages) {
			return;
		}
		if (mediaPlayer != null) {
			mediaPlayer.stopStream();
			mediaPlayer = null;
		}
		LiveVideoAgentHolder.removeListener(agentVideoPlayingListener);
		if(currentAgent != null)
			LiveVideoAgentHolder.releaseAgent(currentAgent,agentVideoPlayingListener,true);
		
		ActiveAgentSet.INSTANCE.removeListener(this);
	}

	@Override
	public void partBroughtToTop(final MPart part) {
		if(noImages) {
			return;
		}
		if(MY_VIDEO_TAB.equals(part.getElementId())){
			videoPartVisible = true;
			videoParentVisible = true;
			restartMediaPlayer();
			logger.info("I'm on top!");
		}
		if(MY_PARENT_TAB.equals(part.getElementId())){
			videoParentVisible = true;
		}
	}
	
	@Override
	public void partActivated(final MPart part) {/* */
	}

	@Override
	public void partDeactivated(final MPart part) {/* */
	}

	@Override
	public void partHidden(final MPart part) {
		if(noImages) {
			return;
		}
		if(MY_PARENT_TAB.equals(part.getElementId()) || MY_VIDEO_TAB.equals(part.getElementId())){
			if(MY_VIDEO_TAB.equals(part.getElementId()))
				videoPartVisible = false;
			if(MY_PARENT_TAB.equals(part.getElementId()))
				videoParentVisible = false;
			if(currentAgent != null)
				LiveVideoAgentHolder.releaseAgent(currentAgent,agentVideoPlayingListener,true);
			if(mediaPlayer != null){
				mediaPlayer.stopStream();
				mediaPlayer = null;
			}	
		}
	}

	@Override
	public void partVisible(final MPart part) {
		if(noImages) {
			return;
		}
		//one would think this would get fired when a part is visible but this only checks if the parent part ex. Run Plan tab is visible
		//we need to check for the video tab as well
		//
		//Also there is a bug where this get fired even when a part becomes INVISIBLE/HIDDEN. When a user Switches tabs for a split microsecond 
		//the part becomes visible throwing off this check. We have to have the funky delay thread to make sure the workbench 
		//has fully switched tabs before checking if a part is truly visible
		if(videoPartVisible){
			final ScheduledThreadPoolExecutor thread = new ScheduledThreadPoolExecutor(1);
			final ScheduledFuture<Boolean> schedule = thread.schedule(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if(MY_PARENT_TAB.equals(part.getElementId()) ){
						for(final MPart parts : eps.getParts()){
							if(MY_VIDEO_TAB.equals(parts.getElementId()) && part.isVisible()){
								if(TAB_NAME.matches(eps.getActivePart().getElementId())){
									return true;
								}
							}
						}	
					}
					return false;
				}
			}, 1000, TimeUnit.MILLISECONDS);
			Display.getCurrent().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					try {
						if(schedule.get()){
							restartMediaPlayer();
						}
					} catch (final Exception e){
						e.printStackTrace();
					}
				}
			});
		}
		
	}
	
	@Override
	public void activeAgentAdded(Agent agent, String participantId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void activeAgentRemoved(Agent agent) {
		// TODO Auto-generated method stub
		
	}
}