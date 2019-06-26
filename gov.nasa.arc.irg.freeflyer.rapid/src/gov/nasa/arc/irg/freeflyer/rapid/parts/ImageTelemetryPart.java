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
package gov.nasa.arc.irg.freeflyer.rapid.parts;

import gov.nasa.arc.irg.rapid.ui.e4.MessageBundle;
import gov.nasa.arc.irg.rapid.ui.e4.view.AbstractTelemetryPart;
import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.ui.e4.parts.IImageReshower;
import gov.nasa.rapid.v2.ui.e4.parts.ImageSensorViewSize;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
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

import rapid.ImageSensorSample;

public class ImageTelemetryPart extends AbstractTelemetryPart implements IImageReshower {
	private static final Logger logger = Logger.getLogger(ImageTelemetryPart.class);
	protected Composite 	m_container;
	protected Label         m_imageLabel    = null;
	protected Label         m_infoLabel     = null;
	protected int           m_imageCount    = 0;
	protected Image			m_image;
	protected Object        m_imageLock     = new Object();
	protected ImageData     m_imageData     = null;
	protected ImageSensorViewSize m_viewAreaSize = new ImageSensorViewSize();

	@Inject
	public ImageTelemetryPart(Composite parent) {
		m_container = new Composite(parent, SWT.NONE);
		topicsComboHorizontalSpan = 3;
		createPartControl(m_container);
		baseSampleType = MessageType.IMAGESENSOR_SAMPLE_TYPE;
		// what is MessageType.IMAGESENSOR_STATE_TYPE;???
		//		baseConfigType = MessageType.AGENT_CONFIG_TYPE;
	}

	@Override
	@PreDestroy
	public void preDestroy() {
		unsubscribe();
	}
	
	protected void loadDefaultImage() {
		URL default_url;
		try {
			default_url = new URL("platform:/plugin/gov.nasa.arc.irg.freeflyer.rapid/resources/CameraIconGrey256.png");

			synchronized(m_imageLock) {
				URL fileURL = FileLocator.toFileURL(default_url);
				String fileString = fileURL.getPath();
				m_imageData = new ImageData(fileString);

				if (m_imageData == null) {
					logger.error("m_imageData = null");
				}
				m_image = new Image(null, m_imageData);
			}
			directShowImage();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRapidMessageReceived(Agent agent, MessageType msgType,
			Object eventObj, Object configObj) {
		if (msgType.equals(getConfigType())){
			if (eventObj != null && eventObj.getClass().equals(getConfigClass())){
				setConfigObject(eventObj);
			}
		} else {

			if(msgType.equals(getSampleType())) {

				if (configObj != null && configObj.getClass().equals(getConfigClass())){
					setConfigObject(configObj);
				}

				//------- this used to be in call method
				ImageSensorSample imageSample = (ImageSensorSample) eventObj;
				int size = imageSample.data.userData.size();

				byte[] data = imageSample.data.userData.toArrayByte(new byte[size]);
				m_imageData = new ImageData(new ByteArrayInputStream(data));
				m_imageCount++;
				setInfoLabel(null);
				reshowImage();
				//-------
			}
		}
	}

	@Override
	protected Object getConfigObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setConfigObject(Object config) {
		// TODO Auto-generated method stub

	}

	@Override
	protected Class getConfigClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getMementoKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected NameValue[] getNameValues(Object input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean configIdMatchesSampleId(Object configObj, Object eventObj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void onSubtopicSelected(String subtopic) {
		// TODO Auto-generated method stub

	}

	@Override
	public void createPartControl(Composite parent) {
		m_container.setLayout(new GridLayout(4,false));
		createPreTableParts(m_container);

		m_infoLabel = new Label(m_container, SWT.None);
		m_infoLabel.setBackground(ColorProvider.INSTANCE.white);
		GridData gdInfoLabel = new GridData(SWT.FILL, SWT.FILL, true, false);
		gdInfoLabel.grabExcessHorizontalSpace = true;
		gdInfoLabel.horizontalSpan = 4;
		m_infoLabel.setLayoutData(gdInfoLabel);
		m_infoLabel.setText("Info Label");

		m_imageLabel = new Label(m_container, SWT.None);
		GridData gdImageLabel = new GridData();
		gdImageLabel.grabExcessHorizontalSpace = true;
		gdImageLabel.horizontalSpan = 4;
		m_imageLabel.setLayoutData(gdImageLabel);
		loadDefaultImage();



		m_container.pack();

		m_viewAreaSize.createSizeListener(this, m_container);

		onCreatePartControlComplete();
	}
	
	@Override
	protected void createPreTableParts(Composite container) {
		GridData gdOneWide = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);

		partitionsCombo = new Combo(container, SWT.READ_ONLY);
		partitionsCombo.setSize(85, 20);
		populatePartitionsCombo();
		partitionsCombo.setLayoutData(gdOneWide);
		partitionsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedPartition = partitionsCombo.getText();
				agent = Agent.valueOf(selectedPartition);
				populateSubtopicsCombo();
			}
		});

		topicsCombo = new Combo(container, SWT.READ_ONLY);
		topicsCombo.setSize(85, 20);
		GridData gdTopicsCombo = new GridData(SWT.BEGINNING, SWT.BEGINNING, true, false);
		gdTopicsCombo.horizontalSpan = topicsComboHorizontalSpan;
		topicsCombo.setLayoutData(gdTopicsCombo);
		topicsCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				unsubscribe();
				String subtopic = topicsCombo.getText();
				sampleType = getTheMessageType(subtopic);
				if(sampleType != null) {
					configType = getValueOf(sampleType.getConfigName());
				}
				subscribe();
				onSubtopicSelected(subtopic);
			}
		});
		container.layout(true);
		container.pack();
	}

	@Override
	public Boolean call() throws Exception {
		MessageBundle latestEvent = null;
		if (getAgent() != null){
			latestEvent = getLastMessageToProcess(getSampleType());
		}

		// There is no config to match so jump straight in
		if(latestEvent != null && latestEvent.eventObj instanceof ImageSensorSample) {

			ImageSensorSample imageSample = (ImageSensorSample) latestEvent.eventObj;
			int size = imageSample.data.userData.size();

			byte[] data = imageSample.data.userData.toArrayByte(new byte[size]);
			m_imageData = new ImageData(new ByteArrayInputStream(data));
			m_imageCount++;
			setInfoLabel(null);
			reshowImage();
			return true;
		}

		
		return Boolean.FALSE;
	}
	
	public void reshowImage() {
		Display.getDefault().asyncExec(new ReshowRunnable());
	}
	
	protected class ReshowRunnable implements Runnable {
		public void run() {
			if(!m_imageLabel.isDisposed()) {
				directShowImage();
			}
		}
	}

	// must call this from display thread
	protected void directShowImage() {
		synchronized(m_imageLock) {
			if(m_imageData != null) {
				ImageData imageData = scaleIfNecessary(m_imageData);

				Image oldImage = m_image;
				m_image = new Image(null,imageData);
				Rectangle rect = m_image.getBounds();
				if(!m_imageLabel.isDisposed()) {
					m_imageLabel.setImage(m_image);
					m_imageLabel.setSize(rect.width, rect.height);
				}
				oldImage.dispose();
			}
		}
	}

	private ImageData scaleIfNecessary(ImageData imageData) {
		int viewW,viewH;
		synchronized(m_viewAreaSize) {
			viewW = m_viewAreaSize.width;
			viewH = m_viewAreaSize.height;
		}
		int imageW = imageData.width;
		int imageH = imageData.height;
		double scaleW = (double)(viewW-1) / (double)imageW;
		double scaleH = (double)(viewH-1) / (double)imageH;
		double scale = (scaleW < scaleH) ? scaleW : scaleH;
		if( scale < 1 ) {
			int sW = (int)(imageW * scale);
			int sH = (int)(imageH * scale);
			imageData = imageData.scaledTo(sW, sH);
		}
		return imageData;
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
		final String msgString = "Image #"+m_imageCount+ " " + msg;
		Display.getDefault().asyncExec(
				new Runnable() {
					public void run() {
						if(m_infoLabel != null && !m_infoLabel.isDisposed()) {
							m_infoLabel.setText(msgString);
							m_infoLabel.setToolTipText(msgString);
						}
					}
				}
				);
	}
}
