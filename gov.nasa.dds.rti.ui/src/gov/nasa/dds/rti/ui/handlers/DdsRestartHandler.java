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
package gov.nasa.dds.rti.ui.handlers;

import gov.nasa.dds.system.Dds;
import gov.nasa.util.IProgressUpdater;
import gov.nasa.util.ui.MessageBox;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class DdsRestartHandler extends AbstractHandler {
    private static final Logger logger = Logger.getLogger(DdsRestartHandler.class);
    private static int count = 0;

    private static Job s_job = null;

    public class ProgressUpdater implements IProgressUpdater {
        final IProgressMonitor monitor;
        public ProgressUpdater(IProgressMonitor mon) {
            monitor = mon;
            monitor.beginTask("Restart DDS", Dds.getNumRestartSteps());
            monitor.subTask("begin...");
        }

        @Override
        public void updateProgress(String status) {
            monitor.worked(1);
            monitor.subTask(status);
        }
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        synchronized(DdsRestartHandler.class) {
            try {
                // prevent restart jobs from queueing up by delaying restart and ignoring 
                // any restart requests until restart has completed
                if(s_job == null) {
                    s_job = createRestartJob();
                    s_job.setUser(true);
                    s_job.setPriority(Job.INTERACTIVE);
                    s_job.schedule(1000); // delay restart
                }
            }
            catch(Throwable t) {
                throw new ExecutionException("Error restarting DDS", t);
            }
            return null;
        }
    }

    private Job createRestartJob() {
        final Shell shell = Display.getDefault().getActiveShell();
        Job job = new Job("Restart DDS") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                logger.info("DDS Restart #"+ ++count+" began at "+ new Date().toString());
                ProgressUpdater progress = new ProgressUpdater(monitor);
                try {
                    Dds.restart(progress);
                    logger.info("DDS Restart #"+count+" completed at "+ new Date().toString());
                }
                catch(Throwable t) {
                    final String msg = t.getClass().getSimpleName()+":"+t.getMessage()+"\nSee log for more details";
                    logger.error(msg);
                    MessageBox.warn("DDS Restart Failed", msg, shell);
                }
                
                monitor.done();
                // allow new restart jobs
                s_job = null;

                return Status.OK_STATUS;
            }
        };
        return job;
    }
}
