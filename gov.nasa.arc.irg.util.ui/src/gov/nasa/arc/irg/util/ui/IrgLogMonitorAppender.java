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
package gov.nasa.arc.irg.util.ui;

import gov.nasa.arc.irg.util.log.IrgLog;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

public class IrgLogMonitorAppender extends AppenderSkeleton {
    public IrgLogMonitorAppender() {
        //
    }

    @Override
    protected void append(LoggingEvent event) {
        Level level    = event.getLevel();
        String message = event.getRenderedMessage();
        Throwable throwable = null;
        ThrowableInformation ti = event.getThrowableInformation();
        if(ti != null) {
            throwable = ti.getThrowable();
        }
        IrgLog.Level irgLevel = IrgLog.Level.Info;
        if     (level.equals(Level.FATAL)) irgLevel = IrgLog.Level.Fatal;
        else if(level.equals(Level.ERROR)) irgLevel = IrgLog.Level.Error;
        else if(level.equals(Level.WARN))  irgLevel = IrgLog.Level.Warn;
        else if(level.equals(Level.INFO))  irgLevel = IrgLog.Level.Info;
        else if(level.equals(Level.DEBUG)) irgLevel = IrgLog.Level.Debug;
        else if(level.equals(Level.TRACE)) irgLevel = IrgLog.Level.Debug;
        IrgLogMonitor.printLogMessage(irgLevel, message, throwable);
    }

    public void close() {
        // do nothing
    }

    public boolean requiresLayout() {
        return false;
    }
}
