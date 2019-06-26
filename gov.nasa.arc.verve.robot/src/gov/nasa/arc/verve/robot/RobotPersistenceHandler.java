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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.robot.persist.RobotPropertiesPersistence;

import java.net.URI;

import org.apache.log4j.Logger;

public class RobotPersistenceHandler implements RobotRegistryListener {
    private static final Logger logger = Logger.getLogger(RobotPersistenceHandler.class);
    private URI     persistencePathUri = null; 
    private boolean doSaveState        = false;

    public RobotPersistenceHandler(URI persistencePathUri) {
        this.persistencePathUri = persistencePathUri;
    }

    public URI getPersistencePathUri() {
        return persistencePathUri;
    }

    public void setPersistencePathUri(URI uri) {
        if(uri == null) {
            logger.warn("URI is null. Persistence path remains as "+persistencePathUri.toASCIIString());
        }
        else {
            persistencePathUri = uri;
        }
    }

    public void setSaveState(boolean doSave) {
        doSaveState = doSave;
    }

    @Override
    public void robotRegistered(String robotName, AbstractRobot robot) {
        if(doSaveState) { // do NOT want memory of where parts were. Will reset from latest telemetry.
            try {
                RobotPropertiesPersistence.read(persistencePathUri, robot);
            }
            catch(Throwable t) {
                logger.error(t);
            }
        }
    }

    @Override
    public void robotDeregistered(String robotName, AbstractRobot robot) {
        if(doSaveState) {
            try {
                RobotPropertiesPersistence.write(persistencePathUri, robot);
            }
            catch(Throwable t) {
                logger.error(t);
            }
        }
    }

}
