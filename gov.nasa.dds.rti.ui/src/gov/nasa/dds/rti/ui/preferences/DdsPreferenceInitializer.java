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

import gov.nasa.dds.rti.ui.RtiDdsUiActivator;
import gov.nasa.util.PlatformInfo;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.rti.dds.infrastructure.TransportBuiltinKind;

public class DdsPreferenceInitializer extends AbstractPreferenceInitializer {

    final String initialPeers = "builtin.udpv4://239.255.0.1" +
            DdsPreferenceKeys.LIST_SEPARATOR + "builtin.udpv4://127.0.0.1" +
            DdsPreferenceKeys.LIST_SEPARATOR + "builtin.shmem://";

    /**
     * 
     */
    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = RtiDdsUiActivator.getDefault().getPreferenceStore();
        store.setDefault(DdsPreferenceKeys.P_QOS_URL_GROUPS,      "");
        store.setDefault(DdsPreferenceKeys.P_PEERS_LIST,          initialPeers);
        store.setDefault(DdsPreferenceKeys.P_IPv4_DENY_LIST,      "");
        store.setDefault(DdsPreferenceKeys.P_IGNORE_USER_PROFILE, "true");
        store.setDefault(DdsPreferenceKeys.P_IGNORE_ENV_PROFILE,  "true");
        
        final String SHMEM = TransportBuiltinKind.SHMEM_ALIAS;
        if(PlatformInfo.getOS() == PlatformInfo.OS.Mac) {
            store.setDefault(DdsPreferenceKeys.transportDisabled(SHMEM),  true);
        }
    }
}
