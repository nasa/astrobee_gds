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

import gov.nasa.arc.irg.freeflyer.rapid.FreeFlyerStrings;
import gov.nasa.arc.irg.iss.ui.IssUiActivator;
import gov.nasa.arc.irg.plan.ui.io.WorkbenchConstants;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IActiveAgentSetListener;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.MessageTypeExt;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;
import gov.nasa.rapid.v2.ui.e4.parts.IImageReshower;
import gov.nasa.rapid.v2.ui.e4.parts.ImageSensorViewSize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rapid.IMAGESENSOR_SAMPLE_TOPIC;
import rapid.ImageSensorSample;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_DOCK;
import rapid.ext.astrobee.SETTINGS_CAMERA_NAME_NAV;

/**
 * Has dropdown to select a video source from the Astrobee selected in the Health & Status part
 * 
 * @author ddwheele
 *
 */
public class LiveImagesPart implements IRapidMessageListener, IImageReshower, IActiveAgentSetListener {
	private static final Logger logger = Logger.getLogger(LiveImagesPart.class);
	protected Label         imageLabel    = null;
	protected Label         infoLabel     = null;
	protected Label         resolutionLabel = null;
	protected Label			imageIcon 	  = null;
	protected int           imageCount    = 0;
	protected Image			image, forward, backward;
	protected Object        imageLock     = new Object();
	protected ImageData     imageData     = null;
	protected ImageSensorViewSize viewAreaSize = new ImageSensorViewSize();
	protected Combo topicsCombo;
	protected List<String> topicsList = new ArrayList<String>();
	protected MessageType baseSampleType;
	protected String selectedPartition = "";
	protected MessageType sampleType;
	protected Agent agent = null;
	protected String participantId = Rapid.PrimaryParticipant;
	protected boolean topicsComboPopulated = false;

	protected final String NAV_CAM_NAME = "Navigation";
	protected final String DOCK_CAM_NAME = "Dock";
	
	protected final String FWD_CAM_TOOLTIP = "Forward Facing Camera";
	protected final String BKWD_CAM_TOOLTIP = "Aft Facing Camera";
	 
	protected final String SELECT_CAMERA_STRING = "Select Camera ...";
	protected int leftSideWidth = 200;
	
	protected Vector<CameraTriple> cameraTriples;
	private static SimpleDateFormat dateFormatUTC = new SimpleDateFormat("HH:mm:ss");
	{
		dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	@Inject
	public LiveImagesPart(Composite parent) {
		baseSampleType = MessageType.IMAGESENSOR_SAMPLE_TYPE;
		setupCameraTranslation();
		loadImages();
	}
	
	protected void setupCameraTranslation() {
		cameraTriples = new Vector<CameraTriple>();
		cameraTriples.add(new CameraTriple(IMAGESENSOR_SAMPLE_TOPIC.VALUE+MessageType.topicSeparator+SETTINGS_CAMERA_NAME_NAV.VALUE, NAV_CAM_NAME, null));
		cameraTriples.add(new CameraTriple(IMAGESENSOR_SAMPLE_TOPIC.VALUE+MessageType.topicSeparator+SETTINGS_CAMERA_NAME_DOCK.VALUE, DOCK_CAM_NAME, null));
	}

	@PostConstruct
	public void createPartControl(Composite m_container) {
		m_container.setLayout(new GridLayout(2,false));
		Composite leftSide = new Composite(m_container, SWT.None);
		GridData leftSideGd = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		leftSide.setLayoutData(leftSideGd);
		leftSide.setLayout(new GridLayout(1,true));
	
		Composite rightSide = new Composite(m_container, SWT.None);
		rightSide.setLayout(new GridLayout(1,true));
		GridData rightSideGd = new GridData(SWT.FILL, SWT.FILL, true, true);
		rightSide.setLayoutData(rightSideGd);
		
		createPreTableParts(leftSide);
		
		infoLabel = new Label(leftSide, SWT.None);
		infoLabel.setBackground(ColorProvider.INSTANCE.white);
		GridData gdInfoLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdInfoLabel.grabExcessHorizontalSpace = true;
		gdInfoLabel.horizontalSpan = 2;
		infoLabel.setLayoutData(gdInfoLabel);
		infoLabel.setText("");
		
		resolutionLabel = new Label(leftSide, SWT.None);
		resolutionLabel.setBackground(ColorProvider.INSTANCE.white);
		GridData gdResLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdResLabel.grabExcessHorizontalSpace = true;
		gdResLabel.horizontalSpan = 2;
		resolutionLabel.setLayoutData(gdInfoLabel);
		resolutionLabel.setText("");
		
		imageIcon = new Label(leftSide, SWT.NONE);
		imageIcon.setOrientation(SWT.RIGHT);
		imageIcon.setImage(forward);
		imageIcon.setVisible(false);
		
		imageLabel = new Label(rightSide, SWT.None);
		GridData gdImageLabel = new GridData();
		gdImageLabel.grabExcessHorizontalSpace = true;
		gdImageLabel.horizontalSpan = 4;
		imageLabel.setLayoutData(gdImageLabel);
	
		loadDefaultImage();

		rightSide.pack();
//		m_container.pack();

		viewAreaSize.createSizeListener(this, m_container);
		
		if(agent != null && !topicsComboPopulated) {
			populateTopicsCombo();
		}
		ActiveAgentSet.INSTANCE.addListener(this);
	}

	protected void createPreTableParts(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));
		label.setText("Show Images from Camera");
		topicsCombo = new Combo(container, SWT.READ_ONLY);
		//topicsCombo.setSize(85, 20);
		GridData gdTopicsCombo = new GridData(SWT.FILL, SWT.FILL, true, false);
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
		topicsCombo.setItems(new String[]{ SELECT_CAMERA_STRING });
		topicsCombo.select(0);
//		container.layout(true);
//		container.pack();
	}

	protected void loadImages() {
		forward = IssUiActivator.getImageFromRegistry("forwardCamera");
		backward = IssUiActivator.getImageFromRegistry("backwardCamera");
	}
	
	@PreDestroy
	public void preDestroy() {
		unsubscribe();
	}

	
	// override this to get FreeFlyerMessageTypes
	protected MessageType getValueOf(String typeName) {
		MessageType standardType = MessageType.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		standardType = MessageTypeExt.valueOf(typeName);
		if(standardType != null) {
			return standardType;
		}
		logger.error("I don't know that MessageType: " + typeName);

		return null;
	}

	// override this to get FreeFlyerMessageTypes
	protected MessageType getTheMessageType(String topic) {
		MessageType standardType = MessageType.getTypeFromTopic(topic);
		if(standardType != null) {
			return standardType;
		}
		standardType = MessageTypeExt.getTypeFromTopic(topic);
		if(standardType != null) {
			return standardType;
		}
		logger.error("I don't know that MessageType: " + topic);

		return null;
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object eventObj, Object configObj) {
		if(msgType.equals(getSampleType())) {

			ImageSensorSample imageSample = (ImageSensorSample) eventObj;
			int size = imageSample.data.userData.size();

			byte[] data = imageSample.data.userData.toArrayByte(new byte[size]);
			imageData = new ImageData(new ByteArrayInputStream(data));
			imageCount++;
			setInfoLabel(null);
			setResolutionLabel(imageData.width + " x " + imageData.height);
			reshowImage();
		}
	}

	@Inject @Optional
	public void onAgentSelected(@Named(FreeFlyerStrings.PRIMARY_BEE) Agent a) {
		if(a == null || a.name().equals(selectedPartition)) {
			return;
		}
		unsubscribe();
		selectedPartition = a.name();
		agent = a;
		if(imageIcon != null)
			imageIcon.setVisible(false);
		topicsComboPopulated = populateTopicsCombo();
	}

	/** return true if successful */
	protected boolean populateTopicsCombo() {
		if(topicsCombo == null) {
			return false;
		}
		
		populateTopicsList();
		if(!topicsList.isEmpty()) {
			String subtopicsArray[] = topicsList.toArray(new String[topicsList.size()]);
			if(subtopicsArray.length > 0) {
				topicsCombo.setItems(subtopicsArray);
				topicsCombo.select(0);
				topicsCombo.pack();
			}
		}
		return true;
	}

	protected void populateTopicsList() {
		topicsList.clear();
		topicsList.add(SELECT_CAMERA_STRING);

		for (String topic : DiscoveredAgentRepository.INSTANCE.getTopicsFor(selectedPartition)) {
			// check if the topic matches our base type
			if(topic.contains(baseSampleType.getTopicName())) {
				topicsList.add(getCameraNameFromTopic(topic));
			}
		}
	}
	
	protected String getCameraNameFromTopic(String topic) {
		for(CameraTriple ct : cameraTriples) {
			if(ct.topic.equals(topic)) {
				return ct.name;
			}
		}
		return topic;
	}

	protected String getTopicFromCameraName(String englishName) {
		for(CameraTriple ct : cameraTriples) {
			if(ct.name.equals(englishName)) {
				return ct.topic;
			}
		}
		return englishName;
	}

	protected void loadDefaultImage() {
		URL default_url;
		try {
			default_url = new URL("platform:/plugin/gov.nasa.arc.verve.freeflyer.workbench/resources/CameraIconGrey256.png");

			synchronized(imageLock) {
				URL fileURL = FileLocator.toFileURL(default_url);
				String fileString = fileURL.getPath();
				imageData = new ImageData(fileString);

				if (imageData == null) {
					logger.error("imageData = null");
				}
				image = new Image(null, imageData);
			}
			directShowImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reshowImage() {
		Display.getDefault().asyncExec(new ReshowRunnable());
	}

	protected class ReshowRunnable implements Runnable {
		public void run() {
			if(!imageLabel.isDisposed()) {
				directShowImage();
			}
		}
	}

	// must call this from display thread
	protected void directShowImage() {
		synchronized(imageLock) {
			if(imageData != null) {
				ImageData localImageData = scaleIfNecessary(imageData);

				Image oldImage = image;
				image = new Image(null,localImageData);
				Rectangle rect = image.getBounds();
				if(!imageLabel.isDisposed()) {
					imageLabel.setImage(image);
					imageLabel.setSize(rect.width, rect.height);
				}
				oldImage.dispose();
			}
		}
	}

	private ImageData scaleIfNecessary(ImageData localImageData) {
		int viewW,viewH;
		synchronized(viewAreaSize) {
			viewW = viewAreaSize.width-leftSideWidth;
			viewH = viewAreaSize.height;
		}
		int imageW = localImageData.width;
		int imageH = localImageData.height;
		double scaleW = (double)(viewW-1) / (double)imageW;
		double scaleH = (double)(viewH-1) / (double)imageH;
		double scale = (scaleW < scaleH) ? scaleW : scaleH;
		
		if( scale < 1 ) {
			int sW = (int)(imageW * scale);
			int sH = (int)(imageH * scale);
			localImageData = localImageData.scaledTo(sW, sH);
		}
		return localImageData;
	}

	public void unsubscribe() {
		if(getAgent() != null) {
			logger.debug("unsubscribe from all on "+getAgent().name() + " for AbstractTelemetryTablePart");
			RapidMessageCollector.instance().removeRapidMessageListener(getParticipantId(), getAgent(), this);
		}
	}

	public void subscribe() {
		if(WorkbenchConstants.isFlagPresent(WorkbenchConstants.NO_IMAGES)) {
			return;
		}

		if (getParticipantId() == null || getParticipantId().isEmpty() || getAgent() == null){
			return;
		}
		//			logger.debug("subscribe on "+getAgent().name() + " for view " + getTitle());
		RapidMessageCollector.instance().addRapidMessageListener(getParticipantId(), 
				getAgent(), 
				getSampleType(), 
				this);
		leftSideWidth = infoLabel.getBounds().width + 30;
	}

	/**
	 * throw a message up on the info label
	 * @param msg
	 */
	protected void setInfoLabel(String msg) {
		if(msg == null) {
			msg = "";
		}
		else {
			msg = "- "+msg;
		}
		final String msgString = dateFormatUTC.format(new Date()) + "  Image #"+imageCount+ " " + msg;
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						if(infoLabel != null && !infoLabel.isDisposed()) {
							infoLabel.setText(msgString);
							infoLabel.setToolTipText(msgString);
						}
					}
				}
				);
	}

	protected void setResolutionLabel(String msg) {
		if(msg == null) {
			msg = "";
		}
		
		final String msgString = msg;
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						if(resolutionLabel != null && !resolutionLabel.isDisposed()) {
							resolutionLabel.setText(msgString);
							resolutionLabel.setToolTipText(msgString);
						}
					}
				}
				);
	}

	public Agent getAgent() {
		return agent;
	}

	protected MessageType getSampleType() {
		return sampleType;
	}

	public String getParticipantId() {
		return participantId;
	}
	
	// stores the topic, English name, and icon of a camera
	protected static class CameraTriple {
		final public String topic;
		final public String name;
		final public Image image;
		
		CameraTriple(String topic, String name, Image image) {
			this.topic = topic;
			this.name = name;
			this.image = image;
		}
	}
	
	@Override
	public void activeAgentSetChanged() {
		boolean found = false;
		for(Agent a : ActiveAgentSet.asList()){
			if(a.equals(Agent.SmartDock)){
				continue;
			}
			if(a.name().equals(selectedPartition)){
				found = true;
				break;
			}
		}
		if(!found ){
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
					imageIcon.setVisible(false);
					topicsCombo.clearSelection();
					topicsCombo.removeAll();
				}
			});
		}
	}
	

	@PersistState
	public void close(){
		ActiveAgentSet.INSTANCE.removeListener(this);	
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
