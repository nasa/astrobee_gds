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
import gov.nasa.dds.rti.system.RtiDds;
import gov.nasa.dds.system.Dds;
import gov.nasa.rapid.v2.e4.agent.ActiveAgentSet;
import gov.nasa.rapid.v2.e4.agent.Agent;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.agent.IAgentOfInterestListener;
import gov.nasa.rapid.v2.e4.agent.RapidAgentOfInterest;
import gov.nasa.rapid.v2.e4.message.IRapidMessageListener;
import gov.nasa.rapid.v2.e4.message.MessageType;
import gov.nasa.rapid.v2.e4.message.collector.RapidMessageCollector;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import rapid.ImageSensorSample;
import rapid.MIME_IMAGE_BMP;
import rapid.MIME_IMAGE_GIF;
import rapid.MIME_IMAGE_JPEG;
import rapid.MIME_IMAGE_TIFF;

/**
 * 
 * @author mallan
 *
 */
public class ImageSensorView implements IRapidMessageListener, IAgentOfInterestListener {
    protected final static Logger logger = Logger.getLogger(ImageSensorView.class);
    public static String ID = ImageSensorView.class.getName();

    public final String DEFAULT_CAMERA = "Default";

    protected Image         m_image         = null;
    protected ImageData     m_imageData     = null;
    protected Object        m_imageLock     = new Object();
    protected Label         m_imageLabel    = null;
    protected Label         m_infoLabel     = null;

    protected int           m_numErrors     = 0;
    protected int           m_imageCount    = 0;
    protected boolean       m_autoScale     = true; // scale image to fit widget
    protected boolean		m_autoScaleBig	= false;
    protected ImageSensorViewSize m_viewAreaSize = new ImageSensorViewSize();

    protected String        m_initialImage  = "images/CameraIconGrey256.png";

    protected Map<Agent, String[]> m_cameraNamesMap = new HashMap<Agent, String[]>();
    protected MessageType   m_subscribedType= null;
    protected Object        m_subLock       = new Object();

    protected Button        m_startButton	= null;
    protected Button        m_stopButton	= null;

    protected String        m_participantId;

    protected Agent         m_agent;
    protected String        m_cameraName    = null;
    protected Combo         m_agentCombo   	= null;
    protected Combo         m_cameraCombo	= null;
    protected Combo         m_imageManipulationCombo = null;

    // pin the rover in this view
    protected boolean 		m_pinned = false;
    protected Action 		m_pinAgentAction;
    protected Action 		m_startAction;
    protected Action		m_stopAction;
    protected Action		m_imageFullAction;
    protected Action		m_imageScaleAction;
    protected Action		m_imageScaleActionBigger;
    
    /* Instead of ActionCombo use jface.ComboViewer */
    // protected ParticipantActionCombo m_participantAction;
    protected ComboViewer   m_participantCombo;
    
    

    //private Composite           parentComposite;
    private ScrolledComposite   scrolledComposite;
    protected Composite         topComposite;
    private GridLayout          topLayout;
    private Composite           selectComposite;

    private enum ImageManipulationType {
        DEFAULT, X, Y, XANDY
    }

    private ImageManipulationType imageManipulationType = ImageManipulationType.DEFAULT;

    /**
     * 
     */
    public ImageSensorView() {
        super();
        
        initializeRapid();
        
        try {
        	m_agent = RapidAgentOfInterest.getAgentOfInterest();
        } catch (Throwable e) {
        	if (m_agent == null) {
        		m_agent = Agent.Spheres0;
        	}
        }
        
        String[] participantIDs = DdsEntityFactory.getParticipantIds();
        if (participantIDs.length > 0){
            m_participantId = DdsEntityFactory.getParticipantIds()[0];
        } else {
            logger.error("There are no participants!  Image Sensor View will not work.");
        }
    }
    
    
    public void initializeRapid() {

        //== First, set our DDS implementation
        Dds.setDdsImpl(new RtiDds());
        
        //== Then, kick off agent discovery
        DiscoveredAgentRepository.INSTANCE.getClass();
    }

//    @Override
//    public void createPartControl(Composite parent) {
//        //Map<String,String> options = ViewID.getOptionMap(this);
//        //final String initialCamera = options.get("camera");
//
//        //parentComposite = parent;
//        //GridLayout gridLayout;
//        topComposite = new Composite(parent, SWT.LEFT);
//        topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//        topLayout = new GridLayout();
//        topLayout.verticalSpacing = 2;
//        topLayout.numColumns = 1;
//        topComposite.setLayout(topLayout);
//
//        createSelectionArea();
//
//        scrolledComposite = new ScrolledComposite(topComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//        scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//        m_imageLabel = new Label(scrolledComposite, SWT.NONE);
//        scrolledComposite.setContent(m_imageLabel);
//        loadInitialImage(m_initialImage);
//
//        m_infoLabel = new Label(topComposite, SWT.NONE);
//        m_infoLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
//        GridData cl = new GridData(GridData.FILL_BOTH);
//        cl.grabExcessHorizontalSpace = true;
//        cl.grabExcessVerticalSpace = false;
//        //cl.minimumHeight = 35;
//        //cl.heightHint = 35;
//        m_infoLabel.setLayoutData(cl);
//        m_infoLabel.setText(".");
//
//        topComposite.pack();
//
//        // setup our actions
//        createActions();
//        hookActions();
//
//        if(m_agent != null) {
//            populateCameraCombo();
//        }
//        if(m_cameraName != null && m_cameraName.length() > 1) {
//            setCameraComboRunnable(m_cameraName);
//        }
//
//        m_viewAreaSize.createSizeListener(this, scrolledComposite);
//        RapidAgentOfInterest.addListener(this);
//        
//        initializePreferenceListeners();
//    }

    public void dispose() {
        try {
            RapidAgentOfInterest.removeListener(this);
            if(m_subscribedType != null) {
                try {
                    RapidMessageCollector.instance().removeRapidMessageListener(m_participantId, 
                                                                                m_agent, 
                                                                                m_subscribedType, 
                                                                                this);
                    m_subscribedType = null;
                } 
                catch (Throwable t) {
                    logger.error("unsubscribe error", t);
                }
            }
        }
        catch(Throwable t) {
            t.printStackTrace();
        }
//        finally {
//            super.dispose();
//        }
    }

    /*
     * Create the area in the view that has the combos to select agent and camera
     */
    protected void createSelectionArea() {
        // rover and camera selection grid
        selectComposite = new Composite(topComposite, SWT.LEFT);
        selectComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        GridLayout gridLayout = new GridLayout(3, true);
        gridLayout.marginHeight = 0;
        gridLayout.marginWidth = 0;
        selectComposite.setLayout(gridLayout);

        m_agentCombo	= setupRoverCombo(selectComposite);
        m_cameraCombo	= setupCameraCombo(selectComposite);
        m_imageManipulationCombo = setupImageManipulation(selectComposite);
        selectComposite.pack();
    }

    /**
     * load up a default image
     */
    protected void loadInitialImage(String initialImage) {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());

        if(bundle != null ) {
            Path path = new Path(initialImage);
            URL bndlURL = FileLocator.find(bundle, path, null); 
            if(bndlURL != null) {
                try {
                    URL fileURL = FileLocator.toFileURL(bndlURL);
                    String fileString = fileURL.getPath();

                    synchronized(m_imageLock) {
                        m_imageData = new ImageData(fileString);
                        m_image = new Image(null, m_imageData);
                    }
                    reshowImage();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


//    protected void hookActions() {
//        IActionBars bars = getViewSite().getActionBars();
//        IToolBarManager tbm = bars.getToolBarManager();
//        IMenuManager    mm  = bars.getMenuManager();
//
//        //tbm.add(m_participantAction);
//        tbm.add(m_pinAgentAction);
//        tbm.add(m_stopAction);
//
//        mm.add(m_participantAction);
//        mm.addMenuListener(m_participantAction); // see ParticipantActionCombo.menuAboutToShow() comment
//        mm.add(new Separator());
//        mm.add(m_startAction);
//        mm.add(m_stopAction);
//        mm.add(new Separator());
//        mm.add(m_imageFullAction);
//        mm.add(m_imageScaleAction);
//        mm.add(m_imageScaleActionBigger);
//    }

//    /**
//     * Create Actions
//     */
//    void createActions() {
//        m_participantAction = new ParticipantActionCombo(m_participantId) {
//            @Override
//            public void participantChanged(String newParticipant) {
//                ImageSensorView.this.changeParticipant(newParticipant);
//            }
//        };
//        //-----------------
//        m_startAction = new Action() {
//            @Override
//            public void run() {
//                synchronized(m_subLock) {
//                    m_numErrors = 0;
//                    m_cameraName = m_cameraCombo.getText();
//                    String cameraTopic = IMAGESENSOR_SAMPLE_TOPIC.VALUE;
//                    if(!DEFAULT_CAMERA.equals(m_cameraName)) {
//                        cameraTopic += MessageType.topicSeparator + m_cameraName;
//                    }
//                    if(m_cameraName == null || m_cameraName.length() == 0 || m_cameraName.startsWith("---")) {
//                        setInfoLabel("No camera selected.");
//                        return;
//                    }
//                    if(m_subscribedType != null) {
//                        m_stopAction.run();
//                    }
//
//                    // XXX FIXME : We should not be dynamically adding the MessageType here, but it is convenient until we get this sorted...
//                    MessageType type = MessageType.getTypeFromTopic(cameraTopic);
//                    try {
//                        if(type == null) {
//                            MessageType imageType = MessageType.IMAGESENSOR_SAMPLE_TYPE;
//
//                            type = MessageType.add(MessageType.Category.Sample, 
//                                                   m_cameraName,
//                                                   null, 
//                                                   imageType.getDataTypeClass(), 
//                                                   cameraTopic, 
//                                                   imageType.getQosProfile());
//                        }
//                        RapidMessageCollector.instance().addRapidMessageListener(m_participantId, 
//                                                                                 m_agent, 
//                                                                                 type, 
//                                                                                 ImageSensorView.this);
//                        m_subscribedType = type;
//                        setInfoLabel("Subscribing to "+m_subscribedType+" from "+m_agent.name());
//                    }
//                    catch(Throwable t) {
//                        logger.error("subscription error", t);
//                    }
//
//                    imageManipulationType = ImageManipulationType.valueOf(m_imageManipulationCombo.getText());
//
//                }
//            }
//        };
//        m_startAction.setText("Subscribe");
//        m_startAction.setToolTipText("(re)subscribe to image stream");
//        m_startAction.setImageDescriptor(RapidV2UiActivator.getImageDescriptor("play"));
//
//
//        //-----------------
//        m_stopAction = new Action() {
//            @Override
//            public void run() {
//                synchronized(m_subLock) {
//                    if(m_subscribedType != null) {
//                        setInfoLabel("Unsubscribing from "+m_agent+","+m_subscribedType);
//                        try {
//                            RapidMessageCollector.instance().removeRapidMessageListener(m_participantId, 
//                                                                                        m_agent, 
//                                                                                        m_subscribedType, 
//                                                                                        ImageSensorView.this);
//                            m_subscribedType = null;
//                        } 
//                        catch (Throwable t) {
//                            logger.error("unsubscribe error", t);
//                        }
//                    }
//                    else {
//                        setInfoLabel("Not subscribed");
//                    }
//                }
//                loadInitialImage(m_initialImage);
//            }
//        };
//        m_stopAction.setText("Unsubscribe");
//        m_stopAction.setToolTipText("Unsubscribe from image stream");
//        m_stopAction.setImageDescriptor(RapidV2UiActivator.getImageDescriptor("pause"));
//
//        //-----------------
//        m_imageFullAction = new Action() { 
//            @Override
//            public void run() { m_autoScale = false; m_autoScaleBig = false; reshowImage(); } 
//        };
//        m_imageFullAction.setText("No image scaling");
//
//        //-----------------
//        m_imageScaleAction = new Action() { 
//            @Override
//            public void run() { m_autoScale = true; m_autoScaleBig = false; reshowImage(); } 
//        };
//        m_imageScaleAction.setText("Auto-scale image");
//
//        //-----------------
//        m_imageScaleActionBigger = new Action() { 
//            @Override
//            public void run() { m_autoScale = true; m_autoScaleBig = true; reshowImage(); } 
//        };
//        m_imageScaleActionBigger.setText("Auto-scale image larger");
//
//        //-----------------
//        m_pinAgentAction = new Action("Pin Agent (force this view to keep monitoring the current agent)", IAction.AS_CHECK_BOX) {
//            @Override
//            public void run() {
//                m_pinned = isChecked();
//                if(!m_pinned) {
//                    changeAgent(RapidAgentOfInterest.getAgentOfInterest());
//                }
//            }
//        };
//        m_pinAgentAction.setImageDescriptor(RapidV2UiActivator.getImageDescriptor("pin"));
//        m_pinAgentAction.setChecked(m_pinned);
//    }

    /**
     * 
     */
    public void reshowImage() {
        Display.getDefault().asyncExec(new ReshowRunnable());
    }
    protected class ReshowRunnable implements Runnable {
        public void run() {
            if(!m_imageLabel.isDisposed()) {
                synchronized(m_imageLock) {
                    if(m_imageData != null) {
                        ImageData imageData = scaleIfNecessary(m_imageData);

                        // for sony cams need to flip vertical and horizontal
                        switch (imageManipulationType) {
                        case X:
                            imageData = flip(imageData, false);
                            break;
                        case Y:
                            imageData = flip(imageData, true);
                            break;
                        case XANDY:
                            imageData = flip(imageData, false);
                            imageData = flip(imageData, true);
                            break;
                        case DEFAULT:
                            break;
                        }
                        //                                   imageData = flip(imageData, false);
                        //                                   imageData = flip(imageData, true);


                        Image oldImage = m_image;
                        m_image = new Image(null, imageData);
                        Rectangle rect = m_image.getBounds();
                        if(!m_imageLabel.isDisposed()) {
                            m_imageLabel.setImage(m_image);
                            m_imageLabel.setSize(rect.width, rect.height);
                        }
                        oldImage.dispose();
                    }
                }
            }
        }
    }

    private ImageData scaleIfNecessary(ImageData imageData) {
        if(m_autoScale) {
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
            if( (scale < 1) || m_autoScaleBig) {
                int sW = (int)(imageW * scale);
                int sH = (int)(imageH * scale);
                imageData = imageData.scaledTo(sW, sH);
            }
        }
        return imageData;
    }

    private ImageData flip(final ImageData srcData, boolean vertical) {
        int bytesPerPixel = srcData.bytesPerLine / srcData.width;
        int destBytesPerLine = srcData.width * bytesPerPixel;
        byte[] newData = new byte[srcData.data.length];
        for (int srcY = 0; srcY < srcData.height; srcY++) {
            for (int srcX = 0; srcX < srcData.width; srcX++) {
                int destX = 0, destY = 0, destIndex = 0, srcIndex = 0;
                if (vertical){
                    destX = srcX;
                    destY = srcData.height - srcY - 1;
                } else {
                    destX = srcData.width - srcX - 1;
                    destY = srcY; 
                }
                destIndex = (destY * destBytesPerLine) + (destX * bytesPerPixel);
                srcIndex = (srcY * srcData.bytesPerLine) + (srcX * bytesPerPixel);
                System.arraycopy(srcData.data, srcIndex, newData, destIndex, bytesPerPixel);
            }
        }
        // destBytesPerLine is used as scanlinePad to ensure that no padding is required
        return new ImageData(srcData.width, srcData.height, srcData.depth, srcData.palette, srcData.scanlinePad, newData);
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

    /**
     * 
     * @param index
     */
    void setCameraCombo(int index) {
        try {
            Listener[] la;
            m_cameraCombo.select(index);
            la = m_cameraCombo.getListeners(SWT.Selection);
            for(Listener l : la) {
                TypedListener tl = (TypedListener)l;
                SelectionListener sl = (SelectionListener)tl.getEventListener();
                sl.widgetSelected(null);
            }
        }
        catch(Throwable t) {
            logger.error("setCameraCombo failed.", t);
        }
    }

    /**
     * 
     * @param camera
     */
    void setCameraComboRunnable(final String camera) {
        Display.getDefault().asyncExec(new CameraComboRunnable(camera));
    }
    protected class CameraComboRunnable implements Runnable {
        final String camera;
        CameraComboRunnable(String camera) {
            this.camera = camera;
        }
        public void run() {
            int index = -1;
            if(camera != null) {
                String[] items = m_cameraCombo.getItems();
                for(int i = 0; i < items.length; i++) {
                    if(items[i].equals(camera)) {
                        index = i;
                        break;
                    }
                }
            }
            if(index >= 0) {
                setCameraCombo(index);
            }
        }
    }

//    @Override
//    public void setFocus() {
//        /* noop */
//    }

    //	static final int COMBO_TYPE = SWT.SIMPLE | SWT.READ_ONLY;
    protected static final int COMBO_TYPE = SWT.READ_ONLY;

    /**
     * set up the rover combo box and associated action
     * @param parent
     */
    protected Combo setupRoverCombo(Composite parent) {
        Combo retVal = new Combo(parent, COMBO_TYPE);
        retVal.setToolTipText("Select Agent to Acquire Images From");
        GridData cl = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        cl.minimumWidth = 80;
        cl.grabExcessHorizontalSpace = true;
        retVal.setLayoutData(cl);

        Agent[] knownAgents = new Agent[] {  // default in case ActiveAgentSet hasn't been initialized
                                             Agent.K10Black, Agent.K10Red, Agent.KRex, 
                                             Agent.LerA, Agent.LerASim, 
                                             Agent.AthleteA, Agent.AthleteSim,
                                             Agent.Spheres0
        };
        try {
            if(ActiveAgentSet.size() > 0) {
                knownAgents = ActiveAgentSet.asArray();
            }
        }
        catch(Throwable t) {
            logger.warn("could not get known agents", t);
        }

        for(Agent agent : knownAgents) {
            retVal.add(agent.name());
        }
        if(m_agent != null) {
            int index = retVal.indexOf(m_agent.toString());
            if (index >= 0){
                retVal.select(index);
                populateCameraCombo();
            }
        }
        //-- action on selection --------------------------------------------
        retVal.addSelectionListener(new SelectionListener() {
            protected void selectionChanged() {
                m_stopAction.run();
                try {
                    m_agent = Agent.valueOf(m_agentCombo.getText());
                    if(m_agent != null) {
                        populateCameraCombo();
                    }
                }
                catch(Throwable t) {
                    //ignore
                }
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                selectionChanged();
            }
            public void widgetSelected(SelectionEvent e) {
                selectionChanged();
            }
        });
        return retVal;
    }

    /**
     * 
     * @param parent
     * @return
     */
    protected Combo setupCameraCombo(Composite parent) {
        final Combo retVal =  new Combo(parent, COMBO_TYPE);
        retVal.setToolTipText("Select Camera");
        GridData cl = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        cl.minimumWidth = 80;
        cl.grabExcessHorizontalSpace = false;
        retVal.setLayoutData(cl);

        //-- action on selection ------------------------------------------
        retVal.addSelectionListener(new SelectionListener() {
            protected void selectionChanged() {
                m_startAction.run();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                selectionChanged();
            }
            public void widgetSelected(SelectionEvent e) {
                selectionChanged();
            }
        });
        return retVal;
    }

    protected Combo setupImageManipulation(Composite parent) {
        Combo retVal =  new Combo(parent, COMBO_TYPE);
        retVal.setToolTipText("Image Manipulation");
        GridData cl = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        cl.minimumWidth = 80;
        cl.grabExcessHorizontalSpace = true;
        retVal.setLayoutData(cl);

        //-- action on selection ------------------------------------------
        retVal.addSelectionListener(new SelectionListener() {
            protected void selectionChanged() {
                m_startAction.run();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                selectionChanged();
            }
            public void widgetSelected(SelectionEvent e) {
                selectionChanged();
            }
        });

        retVal.add("DEFAULT");
        retVal.add("X");
        retVal.add("Y");
        retVal.add("XANDY");
        retVal.select(0);

        return retVal;
    }    

    /**
     * @param parent
     */
    Button setupStartButton(Composite parent) {
        Button retVal = new Button(parent, SWT.PUSH);
        retVal.setText("Start");
        GridData cl  = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        cl.grabExcessHorizontalSpace = true;
        retVal.setLayoutData(cl);

        //final KnEventListener thisListener = this;
        //-- action on selection -------------------------------------
        retVal.addSelectionListener(new SelectionListener() {
            protected void selectionChanged() {
                m_startAction.run();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                selectionChanged();
            }
            public void widgetSelected(SelectionEvent e) {
                selectionChanged();
            }
        });
        return retVal;
    }

    /**
     * @param parent
     */
    Button setupStopButton(Composite parent) {
        Button retVal = new Button(parent, SWT.PUSH);
        retVal.setText("Stop");
        GridData cl  = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        cl.grabExcessHorizontalSpace = true;
        retVal.setLayoutData(cl);

        //final KnEventListener thisListener = this;
        //-- action on selection ----------------------------------------
        retVal.addSelectionListener(new SelectionListener() {
            protected void selectionChanged() {
                m_stopAction.run();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                selectionChanged();
            }
            public void widgetSelected(SelectionEvent e) {
                selectionChanged();
            }
        });
        return retVal;
    }

    /**
     * 
     * @param mimeType
     * @return
     */
    public boolean mimeTypeIsViewable(String mimeType) {
        boolean retVal = false;
        if(     mimeType.equals(MIME_IMAGE_BMP.VALUE) ||
                mimeType.equals(MIME_IMAGE_GIF.VALUE) ||
                mimeType.equals(MIME_IMAGE_JPEG.VALUE) ||
                mimeType.equals(MIME_IMAGE_TIFF.VALUE) ) {
            retVal = true;
        }
        return retVal;
    }

    /**
     * Query the CameraManager for the current list of camera names
     * @return
     */
    public void populateCameraCombo() {
        final String[] defaultCameraNames = {"default"};//RapidV2UiPreferences.getDefaultCameraNames();

        if(m_agent != null) { // && !m_cameraNamesMap.containsKey(m_agent)) {
            // TODO: populate camera names (i.e. camera topics)
            m_cameraNamesMap.put(m_agent, defaultCameraNames);
        }

        if (m_cameraCombo != null) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    String lastSelection = m_cameraCombo.getText();
                    if (m_cameraNamesMap.get(m_agent) != null) {
                        m_cameraCombo.setItems(m_cameraNamesMap.get(m_agent));
                        if (lastSelection != null && lastSelection.length() > 0) {
                            int index = m_cameraCombo.indexOf(lastSelection);

                            if (index < 0) index = 0;

                            m_cameraCombo.select(index);
                        }
                    } else {
                        m_cameraCombo.setItems(defaultCameraNames);
                        m_cameraCombo.select(0);
                    }
                }
            });
        }
    }




    /**
     * store thumbnail in m_image (for disposal reasons)
     * @return true on  success
     */
    public boolean imageAcquired(final ImageSensorSample imageSample) {
        boolean retVal = false;
        if(m_image == null) {
            logger.error("m_image is null.");
        }
        else {
            synchronized(m_imageLock) {
                if(mimeTypeIsViewable(imageSample.mimeType)) {
                    m_imageCount++;
                    try{
                        int size = imageSample.data.userData.size();
                        byte[] data = imageSample.data.userData.toArrayByte(new byte[size]);
                        //int size = imageSample.data.userData.size();
                        //byte[] data = imageSample.data.userData.toArrayByte(new byte[size]);
                        m_imageData = new ImageData(new ByteArrayInputStream(data));
                        setInfoLabel(null);
                        reshowImage();
                        retVal = true;
                    }
                    catch(Exception e) {
                        String camera = m_cameraCombo.getText();
                        String msg = m_agent+", "+camera+": error processing image";
                        setInfoLabel(msg);
                        logger.warn(msg, e);
                    }
                }
            }
        }
        return retVal;
    }

    public void changeParticipant(String newParticipant) {
        m_stopAction.run();
        m_participantId = newParticipant;
        m_startAction.run();
    }

    public void changeAgent(final Agent agent) {
        if( m_agent == null || agent != m_agent) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    int index = m_agentCombo.indexOf(agent.name());
                    if (index >= 0){
                        m_cameraName = m_cameraCombo.getText();
                        m_stopAction.run();
                        m_agent = agent;
                        m_agentCombo.select(index);
                        populateCameraCombo();
                        setCameraComboRunnable(m_cameraName);
                    }
                }
            });
        }
    }

    @Override
    public void onRapidMessageReceived(Agent agent, MessageType msgType,
                                       Object eventObj, Object configObj) {
        if(agent == m_agent) {
            final ImageSensorSample sample = (ImageSensorSample)eventObj;
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if( !imageAcquired(sample)) {
                        if(++m_numErrors > 2) {
                            m_stopAction.run();
                        }
                    }
                }
            });
        }
        else {
            logger.error("received "+msgType.name()+" from "+agent.name()+", but should be subscribed to "+m_agent.name());

        }
    }

    public Agent getAgent() {
        return m_agent;
    }
    public String getParticipantId() {
        return m_participantId;
    }

    //-- State Persistence --------------------------------------------------------------
    public static final String SAVED_AGENT       = "agent";
    public static final String SAVED_PINNED      = "pinned";
    public static final String SAVED_PARTICIPANT = "participant";
    public static final String SAVED_CAMERA      = "camera";

//    @Override
//    public void saveState(IMemento memento) {
//        super.saveState(memento);
//        memento.putString (SAVED_AGENT,       getAgent().name());
//        memento.putBoolean(SAVED_PINNED,      m_pinned);
//        memento.putString (SAVED_PARTICIPANT, getParticipantId());
//        if(m_cameraName != null) {
//            memento.putString(SAVED_CAMERA,      m_cameraName);
//        }
//    }
//
//    @Override
//    public void init(IViewSite site, IMemento memento) throws PartInitException {
//        super.init(site, memento);
//        if (memento != null){
//            Boolean pinned = memento.getBoolean(SAVED_PINNED);
//            if(pinned != null) {
//                m_pinned = pinned;
//            }
//            String agentName = memento.getString(SAVED_AGENT);
//            if (agentName != null){
//                try {
//                    Agent foundAgent = Agent.valueOf(agentName);
//                    if (m_pinned && foundAgent != null){
//                        m_agent = foundAgent;
//                    }
//                }
//                catch(IllegalArgumentException iae) {
//                    logger.warn("No agent with name "+agentName);
//                }
//            }
//
//            String participantId = memento.getString(SAVED_PARTICIPANT);
//            if (participantId != null && DdsEntityFactory.getValidParticipantIdList().contains(participantId)){
//                m_participantId = participantId;
//            }
//            
//            String cameraName = memento.getString(SAVED_CAMERA);
//            if(cameraName != null && cameraName.length() > 1) {
//                m_cameraName = cameraName;
//            }
//        }
//    }

    @Override
    public void onAgentOfInterestChanged(Agent agent) {
        if(!m_pinned) {
            changeAgent(agent);
        }
    }
    
//    protected void initializePreferenceListeners() {
//        IPropertyChangeListener listener;
//        listener = new IPropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent event) {
//                if(event.getProperty().equals(CameraPreferenceKeys.DEFAULT_CAMERAS)) {
//                    populateCameraCombo();
//                }
//            }
//        };
//        RapidV2UiActivator.getDefault().getPreferenceStore().addPropertyChangeListener(listener);
//    }


}
