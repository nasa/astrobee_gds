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
package gov.nasa.arc.irg.util.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * A singleton time simulator to be used to drive any simulation, ie ephemeris.
 * Ensure you set it with the timezone, date and time you are interested in.
 * 
 * @author tecohen
 *
 */
public class TimeSimulator {
	private static final Logger logger = Logger.getLogger(TimeSimulator.class);
	
	public static final TimeSimulator INSTANCE = new TimeSimulator(); // the singleton
	
	protected float m_speed = 1.0f;	// speed to run simulation
	protected int m_updateMilliseconds = 500;	// how often to update the simulation time in milliseconds
	
	protected Calendar m_initialTime;	// when to start
	protected Calendar m_currentSimulationTime;	// current time
	
	protected boolean m_running = Boolean.FALSE;
	
	protected Timer m_timer = new Timer(false); // this is the timer that will call to update the simulation time

	
	/**
	 * constructor
	 */
	protected TimeSimulator(){
		m_initialTime = Calendar.getInstance();
	}

	public float getSpeed() {
		return m_speed;
	}

	public void setSpeed(float speed) {
		m_speed = speed;
	}
	
	public float getUpdateSeconds() {
		return (float)m_updateMilliseconds / 1000;
	}
	
	public int getUpdateMilliseconds() {
		return m_updateMilliseconds;
	}

	public void setUpdateSeconds(float updateSeconds) {
		m_updateMilliseconds = Math.round(1000 *updateSeconds);
	}

	/**
	 * If it is not already running, start the simulation
	 * @throws Exception
	 */
	public synchronized void start() throws Exception{
		if (!isRunning()){
			if (m_currentSimulationTime == null){
				m_currentSimulationTime = (Calendar)m_initialTime.clone();
			}
			m_timer = new Timer(false);
			m_timer.schedule(makeUpdateTask(), 0, getUpdateMilliseconds());
			setRunning(true);
		} else {
			throw new Exception("Simulation already running");
		}
	}
	
	/**
	 * Stop the simulation if it is running
	 * @throws Exception
	 */
	public synchronized void stop() throws Exception{
		if (isRunning()){
			m_timer.cancel();
			setRunning(false);
			m_timer = null;
		} else {
			throw new Exception("Simulation was not running");
		}
	}
	
	/**
	 * Reset the simulation to the start time, restart if already running
	 * @throws Exception
	 */
	public synchronized void reset() throws Exception{
		m_currentSimulationTime = (Calendar)m_initialTime.clone();
	}
	
	public Calendar getInitialTime() {
		return m_initialTime;
	}

	public synchronized void setInitialTime(Calendar initialTime) {
		m_initialTime = initialTime;
	}

	public boolean isRunning() {
		return m_running;
	}

	private void setRunning(boolean running) {
		m_running = running;
	}
	
	/**
	 * Get the current simulated time as a calendar
	 * @return
	 */
	public Calendar getSimulationCalendar() {
		return m_currentSimulationTime;
	}
	DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
	
	protected TimerTask makeUpdateTask() {
		return new TimerTask() {

			@Override
			public void run() {
				// do the actual update
				m_currentSimulationTime.add(Calendar.MILLISECOND, Math.round(getUpdateMilliseconds() * getSpeed()));
//				logger.info("TIME CHANGE " + TIME_FORMAT.format(m_currentSimulationTime.getTime()));
			}
			
		};
	}
	
	public void setInitialHours(int hours){
		getInitialTime().set(Calendar.HOUR_OF_DAY, hours);
	}
	
	public void setInitialMinutes(int minutes){
		getInitialTime().set(Calendar.MINUTE, minutes);
	}
	
	public void setInitialSeconds(int seconds){
		getInitialTime().set(Calendar.SECOND, seconds);
	}
	
	public void setInitialYear(int year){
		getInitialTime().set(Calendar.YEAR, year);
	}
	
	public void setInitialMonth(int month){
		getInitialTime().set(Calendar.MONTH, month);
	}
	
	public void setInitialDay(int day){
		getInitialTime().set(Calendar.DAY_OF_MONTH, day);
	}
	
	public void setInitialTimeZone(TimeZone zone){
		getInitialTime().setTimeZone(zone);
	}
	
	public void setInitialTime(Date date){
		getInitialTime().setTime(date);
	}
	
	

}
