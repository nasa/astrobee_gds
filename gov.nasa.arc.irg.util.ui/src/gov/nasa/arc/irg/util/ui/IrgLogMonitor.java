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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

/**
 * A quick and dirty example of how we might do a log4j log monitor.
 * Very static-y... a real implementation should allow multiple, 
 * customizable log views. 
 * 
 * Note that to catch log messages during startup, IrgLogMonitor.addRootContext()
 * should be called immediately after initializing log4j, in the product activator.
 * Otherwise, any log messages that occur prior to activation of this plugin will 
 * not show up in this view
 */
public class IrgLogMonitor extends IrgStatusBase
{
    private static final Logger logger = Logger.getLogger(IrgLogMonitor.class);
    public  static final String ID     = IrgLogMonitor.class.getName();
    
    private static final String ROOT   = "root";

    private static IrgLogMonitor         s_instance        = null;
    private static List<String>          s_contexts        = new LinkedList<String>();
    private static List<Runnable>        s_initialQueue    = new LinkedList<Runnable>();
    private static IrgLogMonitorAppender s_appender        = new IrgLogMonitorAppender();

    private static int s_maxInitialQueueSize = 90;

    public IrgLogMonitor() {
        super();
        // by default, add the root context
        addRootContext();
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        synchronized(IrgLogMonitor.class) {
            if(s_instance == null) {
                s_instance = this;
                // print any messages that arrived before we existed
                for(Runnable runnable : s_initialQueue) {
                    runnable.run();
                }
                s_initialQueue.clear();
            }
            else {
                // this should never happen
                logger.error("Oops, we only support a single log monitor!");
            }
        }
        IActionBars bars = getViewSite().getActionBars();
        Action action = new ShowContextsAction();
        bars.getMenuManager().add(action);
        m_textWidget.setFont(JFaceResources.getTextFont());
    }

    /**
     * print a message directly to IrgLogMonitor
     * @param message
     */
    public static void print(String message) {
        if(s_instance != null) {
            s_instance.postLogMessage(IrgLog.Level.Notice, message, null);
        }
    }

    /** 
     * add root context (i.e. all messages). Can safely be called 
     * multiple times. 
     */
    public static void addRootContext() {
        if(!s_contexts.contains(ROOT)) {
            Logger logger = Logger.getRootLogger();
            logger.addAppender(s_appender);
            s_contexts.add(ROOT);
        }
    }
    public static void removeRootContext() {
        Logger logger = Logger.getRootLogger();
        logger.removeAppender(s_appender);
        s_contexts.remove(ROOT);
    }


    /** 
     * Add a context to monitor. Contexts are package or (full) class names. 
     * By default, the view starts with the root context (i.e. everything)
     * enabled. To narrow the scope only those contexts specified by 
     * addContext(), first call removeRootContext()
     */
    public static void addContext(String context) {
        Logger logger = Logger.getLogger(context);
        logger.addAppender(s_appender);
        s_contexts.add(context);
    }

    /** 
     * remove a context from the monitor. 
     */
    public static void removeContext(String context) {
        Logger logger = Logger.getLogger(context);
        logger.removeAppender(s_appender);
        s_contexts.remove(context);
    }

    /**
     * get a list of the contexts that are currently being monitored
     */
    public static List<String> getContexts() {
        return new ArrayList<String>(s_contexts);
    }

    /**
     * Print a log message. If the view doesn't exist yet, create a 
     * Runnable and queue the call for later. 
     */
    protected static void printLogMessage(final IrgLog.Level level, final String message, final Throwable t) {
        if(s_instance != null) {
            s_instance.postLogMessage(level, message, t);
        }
        else {
            synchronized(IrgLogMonitor.class) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        printLogMessage(level, message, t);
                    }
                };
                s_initialQueue.add(runnable);
                while(s_initialQueue.size() > s_maxInitialQueueSize) {
                    s_initialQueue.remove(0);
                }
            }
        }
    }

    /**
     * 
     */
    public class ShowContextsAction extends Action {
        public ShowContextsAction() {
            setText("Show log contexts");
        }
        @Override
        public void run() {
            List<String> contexts = getContexts();
            print("Contexts: ("+contexts.size()+" total)");
            for(String context : contexts) {
                print("        "+context);
            }
        }
    }


}  
