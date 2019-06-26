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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantListener;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.TransportBuiltinKind;

/**
 * ParticipantCreator holds the parameters needed to construct a DomainParticipant.
 * The parameters are retained so the participant can be recreated on a DDS restart.
 * @author mallan
 *
 */
public class ParticipantCreator {
    private static final Logger logger = Logger.getLogger(ParticipantCreator.class);
    private static int count = 0;
    public final int   ordinal;

    // immutable properties
    public final String participantId;
    public final String participantName;
    public final IParticipantCustomization customize;
    public final DomainParticipantListener dpListener;

    // mutable properties (for restart)
    public int    domainId;
    public String qosLibrary;
    public String qosProfile;

    /**
     * 
     * @param participantId   application identifier for the participant 
     * @param participantName
     * @param domainId
     * @param qosLibrary
     * @param qosProfile
     * @param customize
     * @param dpListener
     */
    public ParticipantCreator(final String participantId,
                              final String participantName, final int domainId, 
                              final String qosLibrary, final String qosProfile,
                              final IParticipantCustomization customize,
                              final DomainParticipantListener dpListener) {
        this.ordinal         = count++;
        this.participantId   = participantId;
        this.participantName = participantName;
        this.domainId        = domainId;
        this.qosLibrary      = qosLibrary;
        this.qosProfile      = qosProfile;
        this.customize       = customize;
        this.dpListener      = dpListener;
    }

    /**
     * 
     * @return
     * @throws DdsEntityCreationException
     */
    DomainParticipant create() throws DdsEntityCreationException {
        try {
            if( (qosLibrary == null || qosLibrary.length() == 0) ||
                    (qosProfile == null || qosProfile.length() == 0) ) {
                logger.debug("Invalid profile ("+qosLibrary+"::"+qosProfile+"); "+participantId+" participant will not be created.");
                return null;
            }
            DomainParticipantQos participantQos = new DomainParticipantQos();
            DomainParticipantFactory.get_instance().get_participant_qos_from_profile(participantQos, qosLibrary, qosProfile);

            // clear out the multicast_receive_addresses to mimic the behavior of NDDS_DISCOVERY_PEERS:
            // http://community.rti.com/docs/html/api_java/group__NDDS__DISCOVERY__PEERS.html
            participantQos.discovery.multicast_receive_addresses.clear();

            // set discovery peers from preferences
            participantQos.discovery.initial_peers.clear();
            for(String peer : DdsPreferences.getPeersList()) {
                participantQos.discovery.initial_peers.add(peer);
                if(!peer.contains("shmem")) {
                    try {
                        if(peer.startsWith("builtin.udpv")) {
                            peer = peer.substring(16);
                        }
                        InetAddress addr = InetAddress.getByName(peer);
                        if(addr.isMulticastAddress()) {
                            // if we have multicast addresses, add them to the discovery policy
                            participantQos.discovery.multicast_receive_addresses.add(peer);
                        }
                    }
                    catch (UnknownHostException e) {
                        // ignore
                    }
                }
            }

            // if we have a deny list, replace the one from the qos
            if(DdsPreferences.getIpv4DenyList() != null && DdsPreferences.getIpv4DenyList().length > 0) {
                PropertyQosStore qosStore = new PropertyQosStore(participantQos.property.value);
                String value = DdsPreferences.getIpv4DenyListString();
                qosStore.put("dds.transport.UDPv4.builtin.parent.deny_interfaces", value);
                qosStore.put("dds.transport.UDPv4.builtin.parent.deny_multicast_interfaces", value);
                qosStore.assign(participantQos.property.value);
            }

            // disable any transports that are explicitly disabled by preferences
            int mask = participantQos.transport_builtin.mask;
            if(DdsPreferences.isTransportDisabled(TransportBuiltinKind.SHMEM_ALIAS)) {
                mask = mask & ~TransportBuiltinKind.SHMEM;
            }
            //if(DdsPreferences.isIgnoreTransport(TransportBuiltinKind.UDPv4_ALIAS)) {
            //    mask = mask & ~TransportBuiltinKind.UDPv4;
            //}
            //if(DdsPreferences.isIgnoreTransport(TransportBuiltinKind.UDPv6_ALIAS)) {
            //    mask = mask & ~TransportBuiltinKind.UDPv6;
            //}
            participantQos.transport_builtin.mask = mask;

            DomainParticipant retVal = DdsEntityFactory.createParticipantImpl(participantId, 
                                                                              participantName, 
                                                                              domainId, 
                                                                              participantQos, 
                                                                              customize, 
                                                                              dpListener);

            return retVal;
        }
        catch(Throwable t) {
            final String msg = String.format("Failed to create participant \"%s\" - %s: %s",
                                             participantName, t.getClass().getSimpleName(), t.getMessage());
            throw new DdsEntityCreationException(msg, t);
        }
    }

}
