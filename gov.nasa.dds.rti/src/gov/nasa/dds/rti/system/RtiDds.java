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
package gov.nasa.dds.rti.system;


import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.system.IDds;
import gov.nasa.dds.system.IDdsRestartListener;
import gov.nasa.util.IProgressUpdater;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.rti.dds.domain.DomainParticipantFactory;

public class RtiDds implements IDds {
    private final Logger logger = Logger.getLogger(RtiDds.class);
    private AtomicInteger                  s_restartCount = new AtomicInteger(0);
    //private static final List<IDdsRestartListener> s_restartListeners = new LinkedList<IDdsRestartListener>();
    private final List<WeakReference<IDdsRestartListener>> s_restartListeners = new LinkedList<WeakReference<IDdsRestartListener>>();

    /**
     * Add the restart listener to the list 
     * @param listener
     */
    public synchronized void addRestartListener(final IDdsRestartListener listener) {
        LinkedList<WeakReference<IDdsRestartListener>> removeList = new LinkedList<WeakReference<IDdsRestartListener>>();
        boolean inList = false;
        for(WeakReference<IDdsRestartListener> lref : s_restartListeners) {
            IDdsRestartListener l = lref.get();
            if(l == null)
                removeList.add(lref);
            else {
                if(l == listener) {
                    inList = true;
                    break;
                }
            }
        }
        if(!inList) {
            s_restartListeners.add(new WeakReference<IDdsRestartListener>(listener));
        }
        s_restartListeners.removeAll(removeList);
    }

    public synchronized boolean removeRestartListener(IDdsRestartListener listener) {
        LinkedList<WeakReference<IDdsRestartListener>> removeList = new LinkedList<WeakReference<IDdsRestartListener>>();
        for(WeakReference<IDdsRestartListener> lref : s_restartListeners) {
            if(listener == lref.get()) {
                removeList.add(lref);
                break;
            }
        }
        return s_restartListeners.removeAll(removeList);
    }

    public int getRestartCount() {
        return s_restartCount.get();
    }

    public int getNumRestartSteps() {
        return 8 + 2*DdsEntityFactory.getParticipantCreators().length + 2*s_restartListeners.size();                
    }

    public synchronized boolean restart() throws Exception {
        return restart(null);
    }

    /**
     * restart the DDS subsystem. The following will occur:
     * 1) inform all listeners that DDS is going down. 
     * 2) destroy all participants and contained entities. 
     * 3) inform listeners that DDS has gone down. 
     * 4) update fields in ParticipantCreators from DdsPreferences
     * 5) create new participants. 
     * 6) inform listeners that DDS has been restarted. 
     * @throws Exception 
     */
    public synchronized boolean restart(IProgressUpdater progress) throws Exception {
        boolean stopSuccess = stop(progress);
        boolean startSuccess = start(progress);
        return (stopSuccess && startSuccess);
    }

    public synchronized boolean stop(IProgressUpdater progress) {
        LinkedList<WeakReference<IDdsRestartListener>> removeList = new LinkedList<WeakReference<IDdsRestartListener>>();
        boolean retVal = true;
        if(progress != null) progress.updateProgress("notifying restart listeners that DDS is about to stop...");

        for(WeakReference<IDdsRestartListener> lref : s_restartListeners) {
            final IDdsRestartListener listener = lref.get();
            if(listener == null) {
                removeList.add(lref);
            }
            else {
                try {
                    listener.onDdsAboutToStop();
                }
                catch(Throwable t) {
                    retVal = false;
                    logger.error("exception while notifying "+listener.getClass().getSimpleName()+" that DDS subsystem is about to stop.", t); 
                }
            }
        }    

        if(progress != null) progress.updateProgress("destroying DDS participants...");
        DdsEntityFactory.destroyAllParticipants(progress);
        // pause before wiping out the factory
        try { Thread.sleep(250); } catch(Throwable t) { t.printStackTrace(); }
        // wipe out the participant factory
        DomainParticipantFactory.finalize_instance();

        if(progress != null) progress.updateProgress("notifying restart listeners that DDS has stopped...");
        for(WeakReference<IDdsRestartListener> lref : s_restartListeners) {
            final IDdsRestartListener listener = lref.get();
            if(listener != null) {
                final String listenerName = listener.getClass().getSimpleName();
                if(progress != null) progress.updateProgress("notifying "+listenerName+" that DDS has stopped...");
                try {
                    listener.onDdsStopped();
                }
                catch(Throwable t) {
                    retVal = false;
                    logger.error("exception while notifying "+listenerName+" that DDS subsystem has stopped.", t); 
                }
            }
        }
        
        s_restartListeners.removeAll(removeList);

        return retVal;
    }

    public synchronized boolean start(IProgressUpdater progress) throws Exception {
        LinkedList<WeakReference<IDdsRestartListener>> removeList = new LinkedList<WeakReference<IDdsRestartListener>>();
        boolean retVal = true;
        StringBuilder sb = new StringBuilder("");

        if(progress != null) progress.updateProgress("updating DomainParticipantFactory from preferences...");
        DdsEntityFactory.updateDomainParticipantFactory();

        if(progress != null) progress.updateProgress("updating ParticipantCreator fields from preferences...");

        //-- Update the ParticipantCreator fields before starting
        for(ParticipantCreator creator : DdsEntityFactory.getParticipantCreators() ) {
            creator.domainId   = DdsPreferences.getDomainId  (creator.participantId);
            creator.qosLibrary = DdsPreferences.getQosLibrary(creator.participantId);
            creator.qosProfile = DdsPreferences.getQosProfile(creator.participantId);
        }

        if(progress != null)  progress.updateProgress("creating new participants...");

        // increment restart count after dds stopped
        s_restartCount.addAndGet(1);
        try {
            DdsEntityFactory.createAllPariticpants(progress);
        }
        catch (DdsEntityCreationException e) {
            retVal = false;
            String msg = "DDS Participant Creation Error: "+e.getMessage();
            sb.append(msg).append("\n");
            logger.error(msg, e);
        }

        if(progress != null) progress.updateProgress("notifying restart listeners that DDS has been restarted...");

        for(WeakReference<IDdsRestartListener> lref : s_restartListeners) {
            final IDdsRestartListener listener = lref.get();
            if(listener == null) {
                removeList.add(lref);
            }
            else {
                String listenerName = listener.getClass().getSimpleName();
                if(progress != null) progress.updateProgress("notifying "+listenerName+" that DDS has started...");
                try {
                    listener.onDdsStarted();
                }
                catch(Throwable t) {
                    retVal = false;
                    String msg = "exception while notifying "+listenerName+" that DDS subsystem has started.";
                    sb.append(msg).append("\n");
                    logger.error(msg, t); 
                }
            }
        }
        
        if(progress != null) progress.updateProgress("done.");

        s_restartListeners.removeAll(removeList);
        
        if(!retVal) {
            throw new Exception(sb.toString());
        }
        
        return retVal;
    }

}
