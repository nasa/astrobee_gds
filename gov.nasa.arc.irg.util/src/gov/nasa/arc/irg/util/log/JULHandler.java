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
package gov.nasa.arc.irg.util.log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Forwards Java Util Logging events to Log4J
 */
public class JULHandler extends Handler {
	
	protected final Logger m_rootLogger;
	protected Level        m_maxLevel = Level.ALL;
	protected static final Map<Level,org.apache.log4j.Level> s_levelMap = new HashMap<Level,org.apache.log4j.Level>(10);
	
	static {
        s_levelMap.put(Level.OFF,     org.apache.log4j.Level.OFF);
        s_levelMap.put(Level.SEVERE,  org.apache.log4j.Level.ERROR);
	    s_levelMap.put(Level.WARNING, org.apache.log4j.Level.WARN);
	    s_levelMap.put(Level.INFO,    org.apache.log4j.Level.INFO);
        s_levelMap.put(Level.CONFIG,  org.apache.log4j.Level.DEBUG);
	    s_levelMap.put(Level.FINE,    org.apache.log4j.Level.TRACE);
	    s_levelMap.put(Level.FINER,   org.apache.log4j.Level.TRACE);
	    s_levelMap.put(Level.FINEST,  org.apache.log4j.Level.TRACE);
	    s_levelMap.put(Level.ALL,     org.apache.log4j.Level.ALL);
	}
	
	public JULHandler(Level initLevel) {
		m_rootLogger = java.util.logging.Logger.getLogger("");
		m_maxLevel = initLevel;
		m_rootLogger.setLevel(initLevel);
		m_rootLogger.addHandler(this);
//		m_rootLogger.info("Connected JUL here");
	}

	@Override
	public void close() throws SecurityException {
		m_rootLogger.removeHandler(this);
	}

	@Override
	public void flush() {
		// ignored
	}
	
	public org.apache.log4j.Level toLog4jLevel(Level level){
	    org.apache.log4j.Level retVal = s_levelMap.get(level);
	    if(retVal == null) {
	        retVal = org.apache.log4j.Level.INFO;
	    }
		return retVal;
	}

	@Override
	public void publish(LogRecord record) {
		if (record.getLevel().intValue() <= m_maxLevel.intValue()){
			org.apache.log4j.Logger apacheLogger = org.apache.log4j.Logger.getLogger(record.getLoggerName());
			apacheLogger.log(record.getSourceClassName(), toLog4jLevel(record.getLevel()), record.getMessage(), record.getThrown());
		}
	}

}
