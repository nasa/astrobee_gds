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
package gov.nasa.arc.ff.ocu.part;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class GpsTimePart {
	private Composite   m_composite;
	private Label m_time;
	private Timer m_timer = new Timer(false); // this is the timer that will run the throttled updates
	private TimerTask m_updateTimerTask = null;   // this is the actual update
		
	private static SimpleDateFormat s_dateFormatUTC = new SimpleDateFormat("ddMMMyy HH:mm:ss");
	{
		s_dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	@Inject 
	public GpsTimePart(Composite parent, Display display) {
		GridLayout gridLayout;
		m_composite = new Composite(parent, SWT.LEFT);

		gridLayout = new GridLayout(3, false);
		m_composite.setLayout(gridLayout);
		
		// GPS time label
		Composite gpsComposite = new Composite(m_composite, SWT.NONE);
		gpsComposite.setLayout(new GridLayout(1,false));
		gpsComposite.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));
		
		Label gpsLabel = new Label(gpsComposite, SWT.NONE);
		gpsLabel.setText("GPS");
		//gpsLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

		m_time = new Label(gpsComposite, SWT.NONE);
		Color white = display.getSystemColor(SWT.COLOR_WHITE);
		m_time.setBackground(white);
		m_time.setText("ddMmmYY HH:MM:SS");

		m_time.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END));

		m_updateTimerTask = new TimerTask() {

			@Override
			public void run() {
				try {
					Display display = Display.getDefault();
					if (display != null && !display.isDisposed()){
						display.asyncExec(new Runnable() {
							public void run() {
								updateTime();
							}
						});
					}
				} catch (SWTException e){
					// hi
				}
			}
		};
		// update time once per second
		m_timer.schedule(m_updateTimerTask, 0, 1000);
	}
	

	protected void updateTime(){
		if (m_time.isDisposed()){
			return;
		}
		m_time.setText(s_dateFormatUTC.format(new Date()));
	}
	
}
