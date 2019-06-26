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
package gov.nasa.arc.irg.iss.ui.dialog;

import gov.nasa.arc.irg.iss.ui.IssUiActivator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author tecohen
 *
 */
public class NetworkStatusDialog extends TitleAreaDialog {

	private static final Logger logger = Logger.getLogger(NetworkStatusDialog.class);
    protected final int			m_domainId = 0;
	public static final String YES = new String("Yes");
	public static final String NO = new String("No");
	public static final String CONNECTED = new String("Connected");
	public static final String NOT_CONNECTED = new String("Not Connected");
	public static final String NONE = new String("None");

	protected Label m_networkImg, m_networkLabel, m_networkValue;
	protected Label m_localIpImg, m_localIpLabel, m_localIpValue;
	protected Image m_successImg;
	protected Image m_failureImg;
	protected boolean m_itsAMatch = false;
	
	protected Timer m_timer = new Timer(false); // this is the timer that will check for network connection
	protected TimerTask m_updateTimerTask = null;   // actual task to check for network connection
	
	
	public NetworkStatusDialog(Shell parentShell) {
		super(parentShell);
	}
	
	public Control createDialogArea(Composite parent) {
		Composite contentArea = (Composite)(super.createDialogArea(parent));
		
		Composite child = new Composite(contentArea, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 10;
		gridLayout.marginWidth = 5;
		child.setLayout(gridLayout);
		
		m_successImg = IssUiActivator.getImageFromRegistry("success_16");
		m_failureImg = IssUiActivator.getImageFromRegistry("fail_16");

		//NETWORK
		GridData gd0 = new GridData();
		gd0.verticalAlignment = SWT.TOP;
		gd0.horizontalAlignment = SWT.RIGHT;
		m_networkImg = new Label(child, SWT.NONE);
		m_networkImg.setImage(m_failureImg);
		m_networkImg.setLayoutData(gd0);
		
		m_networkLabel = new Label(child, SWT.NONE);
		m_networkLabel.setText("Network ");
		m_networkLabel.setLayoutData(gd0);
		
		m_networkValue = new Label(child, SWT.NONE);
		m_networkValue.setText(NOT_CONNECTED);
		Display display = Display.getDefault();
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		m_networkValue.setBackground(white);
		GridData gd1 = new GridData();
		gd1.widthHint = 150;
		m_networkValue.setLayoutData(gd1);

		//LOCAL IP ADDRESS
		m_localIpImg = new Label(child, SWT.NONE);
		m_localIpImg.setText("\t");
		m_localIpImg.setLayoutData(gd0);

		m_localIpLabel = new Label(child, SWT.NONE);
		m_localIpLabel.setText("Local IP ");
		m_localIpLabel.setLayoutData(gd0);
		
		m_localIpValue = new Label(child, SWT.NONE);
		m_localIpValue.setBackground(white);
		m_localIpValue.setText("Unknown");
		GridData gd2 = new GridData();
		gd2.widthHint = 150;
		gd2.heightHint = 60;
		m_localIpValue.setLayoutData(gd2);
		
		updateLocalIpData();
		
		m_updateTimerTask = new TimerTask() {

			@Override
			public void run() {
				 try {
	                    Display display = Display.getDefault();
	                    if (display != null && !display.isDisposed()){
	                        display.asyncExec(new Runnable() {
	                            public void run() {
	                            	updateLocalIpData();
	                            }
	                        });
	                    }
	                } catch (SWTException e){
	                    
	                }
			}
			
		};
		// update time once per minute
		m_timer.schedule(m_updateTimerTask, 0, 60000);	
		
		return contentArea;
	}

	private void updateLocalIpData() {
		if (m_networkValue.isDisposed()){
			return;
		}
		try{
			String myAddress = "";

			NetworkInterface ni = null;
			try {
				// linux
				ni = NetworkInterface.getByName("eth0");
			} catch (SocketException se){
			}
			
			if (ni == null){
				try {
					// osx
					ni = NetworkInterface.getByName("en0");
				} catch (SocketException se2){
				}
			}
			
			if (ni == null){
				try {
					//windows
					ni = NetworkInterface.getByName("ethernet0");
				} catch (SocketException se3){
				}
			}
			if (ni != null){
				for (InetAddress addr : Collections.list(ni.getInetAddresses())) {
					if (addr instanceof Inet4Address){
						myAddress = addr.getHostAddress();
						break;
					}
				}
			}
			for (NetworkInterface i : Collections.list(NetworkInterface.getNetworkInterfaces())) {
				//eth0 or ethx or ethernetx
				if (i.getDisplayName().startsWith("e")){
//					System.out.println(i.getDisplayName());
					for (InetAddress addr : Collections.list(i.getInetAddresses())) {
//						System.out.println(addr.getHostAddress());
						if (addr instanceof Inet4Address){
							if (myAddress.indexOf(addr.getHostAddress()) < 0){
								if (!myAddress.isEmpty()){
									myAddress += "\n";
								}
								myAddress += addr.getHostAddress();
							}
						}
					}
				}
			}

			if (!myAddress.isEmpty()){
				m_networkValue.setText(CONNECTED);
				m_networkImg.setImage(m_successImg);
				
				System.out.println(myAddress);
				m_localIpValue.setText(myAddress);
				m_localIpImg.setImage(m_successImg);
				return;
			}
			
		}catch (Exception e){
			logger.error("Network Disconnected", e);
		}
		m_networkValue.setText(NOT_CONNECTED);
		m_networkImg.setImage(m_failureImg);
		m_localIpValue.setText(NONE);
		m_localIpImg.setImage(m_failureImg);
		logger.error("No network connection.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CLOSE_ID, IDialogConstants.CLOSE_LABEL, true);
	}
	
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.CLOSE_ID == buttonId) {
			cancelPressed();
		}
	}
	
	@Override
	protected boolean isResizable() {
		return false;
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(280, 220);
	}
	

}
