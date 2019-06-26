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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.verve.freeflyer.workbench.parts.liveTelemetryView.TabName;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;

import java.awt.Frame;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.IPartListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public abstract class LiveVideoPart extends LiveVideo implements IActiveAgentSetListener, IPartListener {
	private static final Logger logger = Logger.getLogger(LiveVideoPart.class);
	private final Composite videoComposite;
	private WorkbenchMediaPlayer mediaPlayer = null;
	private Agent currentAgent = null;
	protected String MY_PARENT_TAB;
	protected String MY_VIDEO_TAB;
	int myVideoRequestCount = 0;
	LiveVideoAgentListener agentVideoPlayingListener;
	EPartService eps;
	protected TabName TAB_NAME;
	private boolean videoPartVisible = false;
	private boolean videoParentVisible = false;
	private boolean noImages = false;
	
	@Inject
	public LiveVideoPart(EPartService eps, final Composite parent, MApplication application) {
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.NO_IMAGES)) {
			noImages = true;
			videoComposite = null;
			return;
		}
		videoComposite = new Composite(parent, SWT.EMBEDDED);
		videoComposite.setLayout(new GridLayout(1, true));
		// trying to resize video on mac
		videoComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				if(mediaPlayer != null && videoComposite.getBounds().width != WIDTH){
					mediaPlayer.resize(videoComposite.getBounds().width,videoComposite.getBounds().height);
				}
			}
		});
		configureProperties();
		ActiveAgentSet.INSTANCE.addListener(this);
		eps.addPartListener(this);
		this.eps = eps;
		agentVideoPlayingListener = new LiveVideoAgentListener() {
			
			@Override
			public void agentSelected(Agent a) {
				
			}
			
			@Override
			public void agentRelease() {
				if(currentAgent == null)
					return;
				
				if(videoParentVisible && videoPartVisible){
					final ScheduledThreadPoolExecutor thread = new ScheduledThreadPoolExecutor(1);
					ScheduledFuture<Boolean> schedule = thread.schedule(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							for(MPart part : eps.getParts()){
								if(MY_VIDEO_TAB.equals(part.getElementId()) && part.isVisible()){
									//if(TAB_NAME.matches(eps.getActivePart().getElementId())){
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

	@Inject
	@Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) final Agent a) {
		if(noImages) {
			return;
		}
		logger.info("onAgentSelected() got " + a.name());
		if (a == null || a.equals(currentAgent) || !ActiveAgentSet.contains(a)) {
			return;
		}
		
		if(currentAgent != null)
			LiveVideoAgentHolder.releaseAgent(currentAgent,agentVideoPlayingListener,videoParentVisible);
		
		currentAgent = a;
		if(!LiveVideoAgentHolder.isAgentSelected(a) && videoParentVisible){
			LiveVideoAgentHolder.selectAgent(a,agentVideoPlayingListener,videoParentVisible);
			setupAndStartMediaPlayer();
		}
		else if(mediaPlayer != null){
			mediaPlayer.stopStream();
			mediaPlayer = null;	
		}
	}

	private void setupAndStartMediaPlayer() {
		if (currentAgent == null) {
			return;
		}
		if(mediaPlayer != null){
			mediaPlayer.getMediaPlayer().stop();
		}else{
			final Frame frame = SWT_AWT.new_Frame(videoComposite);
			mediaPlayer = new WorkbenchMediaPlayer(WIDTH, HEIGHT);
			frame.add(mediaPlayer.getVideoSurface());
			mediaPlayer.getMediaPlayer().setPlaySubItems(true);
			
			//used to resize composite because it first render too big (actualy video dimensions) for the composite 
			videoComposite.setSize(videoComposite.getParent().getBounds().width,videoComposite.getParent().getBounds().height);
		}
		
		try{
			mediaPlayer.getMediaPlayer().playMedia(PROPERTIES.getProperty(currentAgent.name() + "." + STREAM_URL));
		}catch(Exception e){
			mediaPlayer.stopStream();
			mediaPlayer = null;
			logger.info("ERROR: Unable to find and play a stream for "+currentAgent.name()+". Try modifying the global.properties file by editing an existing robot's stream or adding a new entry");
		}
			
		//only works on windows	
//		}else{
//			if (mediaPlayer != null) {
//				mediaPlayer.getMediaPlayer().stop();
//			} else {
//				Frame frame = SWT_AWT.new_Frame(videoComposite);
//				mediaPlayer = new EmbeddedMediaPlayerComponent();
//				frame.add(mediaPlayer);
//				mediaPlayer.getMediaPlayer().addMediaPlayerEventListener(
//						new MediaPlayerEventAdapter() {
//							@Override
//							public void buffering(final MediaPlayer mediaPlayer,
//									final float newCache) {
//								logger.info("Buffering " + newCache);
//							}
//						});
//				videoComposite.setSize(WIDTH, HEIGHT);
//				mediaPlayer.getMediaPlayer().setPlaySubItems(true);// .playMedia(rawPath
//																	// +
//																	// "nasa.avi");
//			}
//			mediaPlayer.getMediaPlayer().playMedia(PROPERTIES.getProperty(currentAgent.name() + "." + STREAM_URL));
//		}
//		
//		context.set(FreeFlyerStrings.VIDEO_STREAMING_COUNTER, null);
//		myVideoRequestCount++;
//		context.set(FreeFlyerStrings.VIDEO_STREAMING_COUNTER, new Integer(
//				myVideoRequestCount));
//		logger.info("Starting media player from "
//				+ this.getClass().getSimpleName() + " counter = "
//				+ myVideoRequestCount);
	}

	@Override
	public void activeAgentSetChanged() {
		if(noImages) {
			return;
		}
		boolean found = false;
		for (Agent a : ActiveAgentSet.asList()) {
			if (a.equals(Agent.SmartDock)) {
				continue;
			}
			if (a.equals(currentAgent)) {
				found = true;
				restartMediaPlayer(); // autoplay again if we were disconnected
				break;
			}
		}
		if (!found){
			if(mediaPlayer != null) {
				mediaPlayer.getMediaPlayer().stop(); // stop the player if our guy died
			}
		}
	}

	protected void restartMediaPlayer() {
		if(noImages) {
			return;
		}
		if(mediaPlayer == null && (currentAgent != null && ActiveAgentSet.contains(currentAgent))){
			if(!LiveVideoAgentHolder.isAgentSelected(currentAgent)){
				//LiveVideoAgentHolder.selectAgent(currentAgent, agentVideoPlayingListener,true);
				setupAndStartMediaPlayer();
			}
		}
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
		ActiveAgentSet.INSTANCE.removeListener(this);
		LiveVideoAgentHolder.removeListener(agentVideoPlayingListener);
	}

	@Override
	public void partBroughtToTop(MPart part) {
		if(noImages) {
			return;
		}
		if(MY_VIDEO_TAB.equals(part.getElementId())){
			videoPartVisible = true;
			videoParentVisible = true;
			restartMediaPlayer();
		}
	}
	
	@Override
	public void partActivated(MPart part) {/* */}

	@Override
	public void partDeactivated(MPart part) {/* */}

	@Override
	public void partHidden(MPart part) {/* */
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
	public void partVisible(MPart part) {
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
			ScheduledFuture<Boolean> schedule = thread.schedule(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					if(MY_PARENT_TAB.equals(part.getElementId()) ){
						for(MPart parts : eps.getParts()){
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
							videoParentVisible = true;
							restartMediaPlayer();
						}
					} catch (Exception e){
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