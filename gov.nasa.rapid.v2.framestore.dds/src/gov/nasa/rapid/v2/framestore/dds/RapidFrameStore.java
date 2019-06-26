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
package gov.nasa.rapid.v2.framestore.dds;

import gov.nasa.rapid.v2.framestore.FrameStoreException;
import gov.nasa.rapid.v2.framestore.tree.FrameStore;
import gov.nasa.rapid.v2.framestore.tree.FrameTreeNode;

import org.apache.log4j.Logger;

public class RapidFrameStore {
    private static final Logger logger = Logger.getLogger(RapidFrameStore.class);
    private static final FrameStore s_frameStore = new FrameStore();

    static {
        try {
            addEarthFrame();
            //addUtmFrames();
        }
        catch(FrameStoreException e) {
            logger.error("static init error", e);
        }
    }

    public static FrameStore instance() {
        return s_frameStore;
    }

    public static FrameStore get() {
    	return instance();
    }

    protected static void addEarthFrame() throws FrameStoreException {
        s_frameStore.add("Earth", null, null);
    }

    protected static void addUtmFrames() throws FrameStoreException {
        FrameTreeNode earth = s_frameStore.lookup("/Earth");
        assert(earth != null);
        for (int zone = 1; zone <= 60; ++zone) {
            for (char band = 'C'; band <= 'X'; ++band) {

                // there is no band I nor O
                if (band == 'I' || band == 'O')
                    continue;
                // X32, X34 & X36 are omitted
                if ((band == 'X') && (zone == 32 || zone == 34 || zone == 36)) {
                    continue;
                }
                // TODO: calculate transform of utmGrid frames
                String gridName;
                gridName = String.format("UtmGrid%02d%c", zone, band);
                s_frameStore.add(gridName, earth, null);
            }
        }
    }


}
