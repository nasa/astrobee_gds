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
package gov.nasa.dds.rti.util;

import com.rti.dds.infrastructure.Locator_t;

public class RtiDdsUtil {
    
    public static String toString(Locator_t locator) {
        byte[] b = locator.address;
        switch(locator.kind) {
        case Locator_t.KIND_SHMEM:
            return "shmem://";
        case Locator_t.KIND_UDPv4:
            return String.format("udpv4://%d.%d.%d.%d:%d", 0xFF&b[12], 0xFF&b[13], 0xFF&b[14], 0xFF&b[15], locator.port);
        case Locator_t.KIND_UDPv6:
            return String.format("udpv6://%h%h%h%h.%h%h%h%h.%h%h%h%h.%h%h%h%h:%d",
                                 b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7],
                                 b[8], b[9], b[10], b[11], b[12], b[13], b[14], b[15], locator.port);
        case Locator_t.KIND_DTLS:
            return String.format("DTLS");
        case Locator_t.KIND_INTRA:
            return String.format("INTRA");
        case Locator_t.KIND_INVALID:
            return String.format("INVALID");
        case Locator_t.KIND_RESERVED:
            return String.format("RESERVED");
        case Locator_t.KIND_TCPV4_LAN:
            return String.format("TCPV4_LAN");
        case Locator_t.KIND_TCPV4_WAN:
            return String.format("TCPV4_WAN");
        case Locator_t.KIND_TLSV4_LAN:
            return String.format("TLSV4_LAN");
        case Locator_t.KIND_TLSV4_WAN:
            return String.format("TLSV4_WAN");
        case Locator_t.KIND_WAN:
            return String.format("WAN");
        }
        return "unknown locator kind";
    }
    
}
