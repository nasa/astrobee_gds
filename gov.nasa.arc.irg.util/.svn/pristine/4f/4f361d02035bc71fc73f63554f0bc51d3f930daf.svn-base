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

import java.util.ArrayList;

import org.apache.log4j.Logger;

/**
 * 
 * @author mallan
 *
 */
public class IrgLogBuffer implements IrgLog 
{
    public class StringLevel {
        public IrgLog.Level 	level;
        public String			str;

        StringLevel(IrgLog.Level l, String inStr) {
            level = l;
            str = inStr;
        }
    }

    public ArrayList<StringLevel> m_buffer = new ArrayList<StringLevel>();
    public Logger 		trace = null;	
    protected boolean	m_doTrace = true;
    protected boolean	m_doBuff  = true;
    protected int		m_maxBuff = 300;

    /**
     * Create with identifier as per log4j standard
     * @param logName
     */
    public IrgLogBuffer(String logName) {
        this(logName, true, true);
    }

    /**
     * Create with identifier as per log4j standard
     * @param logName
     */
    public IrgLogBuffer(String logName, boolean doBuffer, boolean doTrace) {
        m_doBuff = doBuffer;
        m_doTrace = doTrace;
        trace = Logger.getLogger(logName);
    }

    @Deprecated
    public void logDebug(String msg) {
        addMsg(IrgLog.Level.Debug, msg, null);
    }

    @Deprecated
    public void logInfo(String msg) {
        addMsg(IrgLog.Level.Info, msg, null);
    }

    @Deprecated
    public void logWarn(String msg) {
        addMsg(IrgLog.Level.Warn, msg, null);
    }

    @Deprecated
    public void logError(String msg) {
        addMsg(IrgLog.Level.Error, msg, null);
    }

    @Deprecated
    public void logFatal(String msg) {
        addMsg(IrgLog.Level.Fatal, msg, null);
    }

    public void log(IrgLog.Level level, String msg) { addMsg(level, msg, null); }

    public void debug(String msg){	addMsg(IrgLog.Level.Debug, msg, null);	}
    public void info(String msg) {	addMsg(IrgLog.Level.Info,  msg, null);	}
    public void warn(String msg) {	addMsg(IrgLog.Level.Warn,  msg, null);	}
    public void error(String msg){	addMsg(IrgLog.Level.Error, msg, null);	}
    public void fatal(String msg){	addMsg(IrgLog.Level.Fatal, msg, null);	}

    public void debug(String msg, Throwable e)	{	addMsg(IrgLog.Level.Debug, msg, e);	}
    public void info(String msg, Throwable e)	{	addMsg(IrgLog.Level.Info,  msg, e);	}
    public void warn(String msg, Throwable e)	{	addMsg(IrgLog.Level.Warn,  msg, e);	}
    public void error(String msg, Throwable e)	{	addMsg(IrgLog.Level.Error, msg, e);	}
    public void fatal(String msg, Throwable e)	{	addMsg(IrgLog.Level.Fatal, msg, e);	}

    protected synchronized void addMsg(IrgLog.Level level, String msg, Throwable e) {
        if(m_doBuff) {
            if(e == null) {
                m_buffer.add(new StringLevel(level, msg));
            }
            else {
                m_buffer.add(new StringLevel(level, msg + " : " + e.getMessage()) );
            }
            // make sure we don't keep chewing up memory of Log is not set to view. 
            while(m_buffer.size() > m_maxBuff)
                m_buffer.remove(0);
        }
        if(true) {
            if(e == null) {
                doTrace(level, msg);
            }
            else {
                doTrace(level, msg, e);
            }
        }
    }

    protected void doTrace(IrgLog.Level level, String msg) {
        if(m_doTrace) {
            switch(level) {
            case Debug:	trace.debug(msg); break;
            case Info:	trace.info(msg); break;
            case Warn:	trace.warn(msg); break;
            case Error:	trace.error(msg); break;
            case Fatal:	trace.fatal(msg); break;
            default:	trace.info(msg); break;
            }
        }
    }

    protected void doTrace(IrgLog.Level level, String msg, Throwable e) {
        if(m_doTrace) {
            switch(level) {
            case Debug:	trace.debug(msg, e); break;
            case Info:	trace.info (msg, e); break;
            case Warn:	trace.warn (msg, e); break;
            case Error:	trace.error(msg, e); break;
            case Fatal:	trace.fatal(msg, e); break;
            default:	trace.info (msg, e); break;
            }
        }
    }
    /**
     * XXX UNIMPLEMENTED
     */
    public void replace(IrgLog newLog) {
        // TODO Auto-generated method stub

    }

    public void enableTrace(boolean status) {
        m_doTrace = status;
    }

}
