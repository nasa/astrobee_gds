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
package gov.nasa.dds.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author mallan
 *
 */
public class NetInterfaceUtil {

    public static class NetInterface {
        public final String name;
        public final String displayName;
        public final String address;
        public NetInterface(String name, String displayName, String address) {
            this.name = name;
            this.displayName = displayName;
            this.address = address;
        }
    }
    
    /**
     * get a list of the network interfaces on the local machine
     * @return
     */
    public static List<String> getNetworkInterfaceNames() {
        LinkedList<String> retVal = new LinkedList<String>();
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while(nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                retVal.add(nif.getName());
            }
        } 
        catch (SocketException e) {
            e.printStackTrace();
        }
        return retVal;
    }
    
    /**
     * get interfaces with v4 addresses
     */
    public static List<NetInterface> getIpv4Interfaces() {
        List<NetInterface> retVal = new LinkedList<NetInterface>();
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while(nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                Enumeration<InetAddress> addrs = nif.getInetAddresses();
                while(addrs.hasMoreElements()) {
                    InetAddress addr = addrs.nextElement();
                    if(addr instanceof Inet4Address) {
                        retVal.add( new NetInterface(nif.getName(), nif.getDisplayName(), addr.getHostAddress()) );
                    }
                }
            }
        } 
        catch (SocketException e) {
            e.printStackTrace();
        }
        return retVal;
    }
    
    public static String getIpv4InterfacesString() {
        StringBuilder builder = new StringBuilder();
        List<NetInterface> interfaces = getIpv4Interfaces();
        for(NetInterface ni : interfaces) {
            builder.append(String.format("%-15s : %-5s [%s]\n", ni.address, ni.name, ni.displayName));
        }
        return builder.toString();
    }

    public static String networkInterfaceInfoString(NetworkInterface nif) {
        StringBuilder builder = new StringBuilder("NetworkInterface:"+nif.getName()+"  ["+nif.getDisplayName()+"]");
        Enumeration<InetAddress> addrs = nif.getInetAddresses();
        while(addrs.hasMoreElements()) {
            InetAddress addr = addrs.nextElement();
            builder.append("\n            addr:"+addr.getHostAddress());
        }
        return builder.toString();
    }

    public static void printNetworkInterfaceInfo() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while(nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();
                System.out.println(networkInterfaceInfoString(nif));
            }
        } 
        catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
