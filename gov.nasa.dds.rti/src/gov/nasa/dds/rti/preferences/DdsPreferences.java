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
package gov.nasa.dds.rti.preferences;

public final class DdsPreferences {
    private static IDdsPreferences s_impl = null;

    public static void setImpl(IDdsPreferences impl) {
        s_impl = impl;
    }

    public static String[] getProfileUrlGroups() {
        return assertImpl().getProfileUrlGroups();
    }

    public static String[] getPeersList() {
        return assertImpl().getPeersList();
    }

    public static String[] getIpv4DenyList() {
        return assertImpl().getIpv4DenyList();
    }

    public static String getIpv4DenyListString() {
        String[] list = getIpv4DenyList();
        StringBuilder builder = new StringBuilder("");
        for(int i = 0; i < list.length; i++) {
            if(i > 0) builder.append(",");
            builder.append(list[i]);
        }
        return builder.toString();
    }

    public static boolean isIgnoreUserProfile() {
        return assertImpl().isIgnoreUserProfile();
    }

    public static boolean isIgnoreEnvironmentProfile() {
        return assertImpl().isIgnoreEnvironmentProfile();
    }

    public static int getDomainId(String participantId) {
        return assertImpl().getDomainId(participantId);
    }

    public static String getQosLibrary(String participantId) {
        return assertImpl().getQosLibrary(participantId);
    }

    public static String getQosProfile(String participantId) {
        return assertImpl().getQosProfile(participantId);
    }

    public static boolean isTransportDisabled(String transportName) {
        return assertImpl().isTransportDisabled(transportName);
    }

    protected static IDdsPreferences assertImpl() {
        if(s_impl != null) {
            return s_impl;
        }
        else {
            String message = "DdsPreferences implementation is null. A class implementing the IDdsPreferences interface must be provided via the setImpl() method.";
            System.err.println(message);
            throw new IllegalStateException(message);
        }
    }

    public static String participantPreferencesString(String participantId) {
        return participantPreferencesString(s_impl, participantId);
    }

    public static String participantPreferencesString(IDdsPreferences prefs, String participantId) {
        StringBuilder builder = new StringBuilder();
        builder.append(participantId+"\n");
        builder.append("                domainId="+prefs.getDomainId(participantId)+"\n");
        builder.append("              qosLibrary="+prefs.getQosLibrary(participantId)+"\n");
        builder.append("              qosProfile="+prefs.getQosProfile(participantId)+"\n");
        builder.append("       ignoreUserProfile="+prefs.isIgnoreUserProfile()+"\n");
        builder.append("ignoreEnvironmentProfile="+prefs.isIgnoreEnvironmentProfile()+"\n");
        builder.append("               urlGroups=");
        for(int i = 0; i < prefs.getProfileUrlGroups().length; i++) {
            if(i > 0) {
                builder.append("                         ");
            }
            final String url = prefs.getProfileUrlGroups()[i];
            builder.append(url).append("\n");
        }
        return builder.toString();
    }

}
