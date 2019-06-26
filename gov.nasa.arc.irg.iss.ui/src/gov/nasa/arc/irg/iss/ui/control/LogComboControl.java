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
package gov.nasa.arc.irg.iss.ui.control;

import gov.nasa.arc.irg.iss.ui.IssButtonEventLoggingConfigurator;
import gov.nasa.arc.irg.iss.ui.IssFontHelper;
import gov.nasa.arc.irg.iss.ui.IssLogEntryMonitor;
import gov.nasa.arc.irg.iss.ui.view.log.ILogEntryChangedListener;
import gov.nasa.arc.irg.iss.ui.view.log.ILogFileReadListener;
import gov.nasa.arc.irg.iss.ui.view.log.IssLogView;
import gov.nasa.arc.irg.iss.ui.view.log.internal.IssLogEntry;
import gov.nasa.arc.irg.iss.ui.view.log.util.LogViewUtils;
import gov.nasa.arc.irg.util.log.IrgLevel;
import gov.nasa.arc.irg.util.ui.ColorProvider;
import gov.nasa.util.StrUtil;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class LogComboControl extends ControlContribution implements ILogFileReadListener, ILogEntryChangedListener {
	private static final Logger buttonLogger = Logger.getLogger(IssButtonEventLoggingConfigurator.BUTTON_LOGGER_NAME);
	private static final Logger logger = Logger.getLogger(LogComboControl.class);
	
	private static final String BUTTON_LOG_STRING = "Button_log";

	private Composite m_composite; // main composite
	private Combo m_combo;
	private AckButton m_ackButton;

	private Composite m_tipComposite;
	private Label m_tipLabel;
	private Label m_tipContents;
	
	private boolean m_alreadyFailed = false;
	protected boolean m_showLevelInCombo = false;
	
	private static String s_lastTip = "";
	
	public LogComboControl(String id) {
		super(id);
	}
	
	/**
	 * ok weirdly after construction this is disposed and then reconstructed, when we are not running in the workbench.
	 */
	private synchronized void kill() {
		IssLogEntryMonitor.INSTANCE.removeLogFileReadListener(this);
		IssLogEntryMonitor.INSTANCE.removeLogEntryChangedListener(this);
//		if (m_composite != null) {
//			if (!m_composite.isDisposed()){
//				if (Display.getDefault() != null){
//					try {
//						m_composite.dispose();
//					} catch (NullPointerException npe){
//						//gulp
//					}
//				}
//			}
//		}
		m_alreadyFailed = false;
		m_composite = null;
		m_combo = null;
		m_ackButton = null;
		m_tipContents = null;
		m_tipComposite = null;
		m_tipLabel = null;
	}
	

	@Override
	protected Control createControl(Composite parent) {
		if (m_composite == null){
			m_composite = new Composite(parent, SWT.NONE);
			m_composite.addDisposeListener(new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					kill();					
				}
				
			});
			
			GridLayout layout = new GridLayout(2, false);
			layout.marginLeft = 0;
			layout.marginRight = 10;
			m_composite.setLayout(layout);
			GridData gd = new GridData(SWT.LEFT, SWT.TOP, true, false);
			m_composite.setLayoutData(gd);
			
			createCombo(m_composite);
			createAck(m_composite);

			// tip stuff
			m_tipComposite = new Composite(m_composite, SWT.NONE);
			GridLayout layout2 = new GridLayout(2, false);
//			layout2.verticalSpacing = 0;
//			layout2.marginTop = 0;
			m_tipComposite.setLayout(layout2);
			
			GridData gd2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd2.horizontalSpan = 2;
			m_tipComposite.setLayoutData(gd2);

			m_tipLabel = new Label(m_tipComposite, SWT.LEFT);
			m_tipLabel.setText("Tip");
			m_tipLabel.setFont(IssFontHelper.getISSFont());
			
			m_tipContents = new Label(m_tipComposite, SWT.NONE);
			m_tipContents.setBackground(ColorProvider.INSTANCE.white);
			
			//make it bold 'n big
			FontData[] fD = IssFontHelper.getISSFont().getFontData();
			fD[0].setStyle(SWT.BOLD);
			m_tipContents.setFont( new Font(m_tipContents.getDisplay(),fD[0]));
			m_tipContents.setText(s_lastTip);
			
			GridData gd3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd3.horizontalAlignment = SWT.LEFT;
			gd3.widthHint = 600; // IMPORTANT this controls the width of the ENTIRE UNIVERSE.
			m_tipContents.setLayoutData(gd3);
			m_composite.pack();
		}

		IssLogEntryMonitor.INSTANCE.addLogFileReadListener(this);
		IssLogEntryMonitor.INSTANCE.addLogEntryChangedListener(this);
		checkPreProcessedErrors();
		
		return m_composite;
	}
	
	private Composite getControl(CoolBar parent) {
		if (m_composite == null  && !m_alreadyFailed){
			m_composite = (Composite)createControl(parent);
			if (m_composite == null){
				m_alreadyFailed = true;
			}
		}
		return m_composite;
	}

	
	@Override
	public void fill(CoolBar parent, int index) {
		m_composite = getControl(parent);
		if (m_composite != null && !m_composite.isDisposed()){
			CoolItem coolItem = new CoolItem(parent, SWT.NONE);
			coolItem.setControl(m_composite);
			// this is really important.  this sets the width of the control, 
			// which affects the available width for other controls in the coolbar.
			
			Point size = m_composite.computeSize(625, SWT.DEFAULT);
//			Point size = m_composite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			coolItem.setPreferredSize(size);
			parent.pack();

		}
	}
	
	public void updateLogComboAndAckCounter(final String status){
		if (status.contains("Button_event")) return;

		//TODO what if you have an error before the display is up?
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				m_combo.add(status, 0);
				m_combo.select(0);
				
				int itemCount = m_combo.getItemCount();
				if (itemCount<=0) 
					m_ackButton.setEnabled(false);
				else 
					m_ackButton.setEnabled(true);
				m_ackButton.updateAckCounterValue(itemCount);	
				
				m_composite.update();
			}
		});
	}

	public String getSelection() {
		if (m_combo != null && m_combo.getText() != null){
			m_combo.getText();
		}
		return null;
	}

	/**
	 * @param parent
	 * @return
	 */
	private Control createCombo (Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.numColumns = 2;
		top.setLayout(layout);
		
		Label label = new Label(top, SWT.LEFT);
		label.setText("Alert");
		label.setFont(IssFontHelper.getISSFont());
		
		m_combo = new Combo(top, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		FontData[] fD = IssFontHelper.getISSFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		m_combo.setFont( new Font(m_combo.getDisplay(),fD[0]));
		m_combo.setVisibleItemCount(5);

		GridData gd3 = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		// IMPORTANT this controls the width of the ENTIRE UNIVERSE.
		gd3.widthHint = 445; // important this is the width of the combo
		m_combo.setLayoutData(gd3);
		
		return top;
	}
	
	private Control createAck (Composite parent) {
		// the composite for the button and the ack label counter
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		GridLayout buttonLayout = new GridLayout(2, false);
		buttonComposite.setLayout(buttonLayout);
		
		m_ackButton = new AckButton(buttonComposite, SWT.PUSH);
		m_ackButton.setEnabled(true);
		m_ackButton.setText("Ack");
		
		m_ackButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				buttonLogger.log(Level.INFO, LogComboControl.class.getName() + "|"+ " Ack button pressed.");
				logger.info("ACK: " + m_combo.getText());
				String gpsTimeString = LogViewUtils.extractFormattedGPS(m_combo.getText());
				IssLogEntryMonitor.INSTANCE.ackIssLogEntry(gpsTimeString, true);
				clearMessage(gpsTimeString, true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// no impl.
			}
		});
		
		return buttonComposite;
	}
	
	protected void processErrorForCombo(IssLogEntry event) {
		String eventLevelString = m_showLevelInCombo ? LogViewUtils.getEventLevelString(event.getLevel()) : "";
		
		if (!eventLevelString.contains(BUTTON_LOG_STRING)) {
			// add time to the list 
			String gpsTimeString = event.getTime();
			StringBuffer status = new StringBuffer(gpsTimeString);
			status.append("  ");
//			status.append(eventLevelString);
			status.append(event.getDescription());

			if (m_combo.indexOf(status.toString()) < 0) {
				updateLogComboAndAckCounter(status.toString());
			}
		}
	}
	
	protected void checkPreProcessedErrors() {
		List<IssLogEntry> logFileErrors = IssLogView.getPreProcessedErrors();
		if (logFileErrors==null) return;
		for (IssLogEntry cle : logFileErrors) {
			if (cle.getLevel().equals(IrgLevel.ALERT)&&!cle.isAcknowledged()) {
				processErrorForCombo(cle);
			}
		}
	}


//	@Override
//	public void setTip(final String tip) {
//		s_lastTip = tip;
//		Display.getDefault().asyncExec(new Runnable() {
//			@Override
//			public void run() {
//				if (m_tipContents != null && !(m_tipContents.isDisposed())){
//					m_tipContents.setText(tip);
//					m_tipContents.redraw();
//					m_composite.update();
//				}
//			}
//		});
//	}


	/**
	 * clears the message from the combo box and the counter
	 * 
	 * @param gpsTime
	 * @param clear
	 */
	protected void clearMessage(final String gpsTime, final boolean clear) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				// go through the Combo and remove the message with the timestamp
				int idx = 0;
				for (String s : m_combo.getItems()) {
					if (s.startsWith(gpsTime)) {
						// clear it 
						m_combo.remove(idx);
						m_combo.select(0);

						int itemCount = m_combo.getItemCount();
						if (itemCount<=0) 
							m_ackButton.setEnabled(false);
						else 
							m_ackButton.setEnabled(true);
						m_ackButton.updateAckCounterValue(itemCount);
						m_composite.update();
						break;
					}
					idx++;
				}			
			}
		});
	}

	@Override
	public void logFileRead() {
		checkPreProcessedErrors();
	}

	
	protected String getEventLevelString(Level level){
		if (level == null){
			return "";
		}
		if (level.equals(Level.WARN) || level.equals(IrgLevel.ALERT)){
			return "Alert: ";
		}
		String result =  level.toString();
		return StrUtil.upperFirstChar(result, true) + ": ";
	}

	@Override
	public void entryChanged(IssLogEntry entry) {
		//noop
		
	}

	@Override
	public void entryAdded(IssLogEntry entry) {
		if (entry.getLevel().equals(IrgLevel.ALERT)){
			final String levelString = m_showLevelInCombo ? getEventLevelString(entry.getLevel()) : "";
			StringBuffer status = new StringBuffer(entry.getTime());
			status.append("  ");
			status.append(levelString);
			status.append(entry.getDescription());
			updateLogComboAndAckCounter(status.toString());
		}
	}
	
	@Override
	public void update(String id) {
		super.update(id);
		
	}
	
}
