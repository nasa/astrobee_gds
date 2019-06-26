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
package gov.nasa.dds.rti.ui.preferences;

import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.ui.handlers.DdsRestartHandler;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class DdsRestartPropertyChangeListener implements IPropertyChangeListener {
    private static final Logger logger = Logger.getLogger(DdsRestartPropertyChangeListener.class);

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        boolean restartNeeded = false;
        final String property = event.getProperty();
        for(String pid : DdsEntityFactory.getParticipantIds()) {
            if(property.equals(DdsPreferenceKeys.domainId(pid)) 
                    || property.equals(DdsPreferenceKeys.qosLibrary(pid)) 
                    || property.equals(DdsPreferenceKeys.qosProfile(pid)) ) {
                restartNeeded = true;
                break;
            }
        }
        if(!restartNeeded) {
            if(property.equals(DdsPreferenceKeys.P_QOS_URL_GROUPS) 
                    || property.equals(DdsPreferenceKeys.P_PEERS_LIST)
                    || property.equals(DdsPreferenceKeys.P_IGNORE_ENV_PROFILE)
                    || property.equals(DdsPreferenceKeys.P_IGNORE_USER_PROFILE)) {
                restartNeeded = true;
            }
        }

        if(restartNeeded) {
            //logger.debug("** DDS RESTART TRIGGERED BY PROPERTY: "+property);
            //logger.debug("** Old value: \""+event.getOldValue().toString()+"\"");
            //logger.debug("** New value: \""+event.getNewValue().toString()+"\"");
            try {
                new DdsRestartHandler().execute(null);
            } 
            catch (ExecutionException e) {
                logger.error("Error restarting DDS", e);
            }
        }
    }

}
