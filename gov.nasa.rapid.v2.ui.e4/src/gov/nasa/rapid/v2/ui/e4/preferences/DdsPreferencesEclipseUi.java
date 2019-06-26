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
package gov.nasa.rapid.v2.ui.e4.preferences;

import gov.nasa.dds.rti.preferences.IDdsPreferences;
import gov.nasa.rapid.v2.ui.e4.RapidV2UiPreferences;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DdsPreferencesEclipseUi implements IDdsPreferences {
	private static String prefString(String str) {
		return RapidV2UiPreferences.get(str);
	}
    private static boolean prefBool(String str) {
        return Boolean.valueOf(RapidV2UiPreferences.get(str));
    }
    private static int prefInt(String str) {
        return Integer.valueOf(RapidV2UiPreferences.get(str));
    }
	
    @Override
    public String[] getProfileUrlGroups() {
        return parseStringList(prefString(DdsPreferenceKeys.P_QOS_URL_GROUPS));
    }
    
    @Override
    public String[] getPeersList() {
        return parseStringList(prefString(DdsPreferenceKeys.P_PEERS_LIST));
    }
    
    @Override
    public String[] getIpv4DenyList() {
        return parseStringList(prefString(DdsPreferenceKeys.P_IPv4_DENY_LIST));
    }
    
    protected String[] parseStringList(String stringList) {
        StringTokenizer st = new StringTokenizer(stringList, DdsPreferenceKeys.LIST_SEPARATOR);
        ArrayList<String> strings = new ArrayList<String>();
        while (st.hasMoreElements()) {
            strings.add(st.nextToken().trim());
        }
        return strings.toArray(new String[strings.size()]);
    }

    @Override
    public boolean isIgnoreUserProfile() {
        return prefBool(DdsPreferenceKeys.P_IGNORE_USER_PROFILE);
    }
    @Override
    public boolean isIgnoreEnvironmentProfile() {
        return prefBool(DdsPreferenceKeys.P_IGNORE_ENV_PROFILE);
    }
    @Override
    public int getDomainId(String participantId) {
        return prefInt(DdsPreferenceKeys.domainId(participantId));
    }
    @Override
    public String getQosLibrary(String participantId) {
        return prefString(DdsPreferenceKeys.qosLibrary(participantId));
    }
    @Override
    public String getQosProfile(String participantId) {
        return prefString(DdsPreferenceKeys.qosProfile(participantId));
    }
    @Override
    public boolean isTransportDisabled(String transportName) {
        return prefBool(DdsPreferenceKeys.transportDisabled(transportName));
    }
}
