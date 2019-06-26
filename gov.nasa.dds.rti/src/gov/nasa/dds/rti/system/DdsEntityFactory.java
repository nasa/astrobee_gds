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
import gov.nasa.dds.exception.UncheckedDdsEntityException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.util.IProgressUpdater;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantListener;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;

/**
 * 
 * @author mallan
 *
 */
public class DdsEntityFactory {
    private static final Logger logger = Logger.getLogger(DdsEntityFactory.class);

    protected static String s_defaultParticipant = null;
    protected static final HashMap<String,ParticipantCreator> s_participantCreators = new HashMap<String,ParticipantCreator>();
    protected static final HashMap<String,DomainParticipant>  s_participantMap      = new HashMap<String,DomainParticipant>();
    protected static final HashMap<String,ContainedEntities>  s_containedMap        = new HashMap<String,ContainedEntities>();

    protected static DomainParticipantFactoryConfig s_dpfConfig = null;

    public static synchronized void updateDomainParticipantFactory() {
        if(s_dpfConfig == null) {
            s_dpfConfig = new DomainParticipantFactoryConfig();
        }
        s_dpfConfig.qosUrlGroups               = DdsPreferences.getProfileUrlGroups();
        s_dpfConfig.isIgnoreEnvironmentProfile = DdsPreferences.isIgnoreEnvironmentProfile();
        s_dpfConfig.isIgnoreUserProfile        = DdsPreferences.isIgnoreUserProfile();
        initDomainParticipantFactory(s_dpfConfig);
    }

    /**
     * 
     * @param config
     * @throws UncheckedDdsEntityException if setting the DomainParticipantFactoryQos fails
     */
    public static synchronized void initDomainParticipantFactory(DomainParticipantFactoryConfig config) {
        s_dpfConfig = config;

        DomainParticipantFactoryQos factoryQos = new DomainParticipantFactoryQos();
        DomainParticipantFactory.get_instance().get_qos(factoryQos);

        // disable autoenable so we can use rti transport plugins
        factoryQos.entity_factory.autoenable_created_entities = false;

        factoryQos.profile.ignore_environment_profile = config.isIgnoreEnvironmentProfile;
        factoryQos.profile.ignore_resource_profile    = config.isIgnoreResourceProfile;
        factoryQos.profile.ignore_user_profile        = config.isIgnoreUserProfile;

        // set the profile xml files
        factoryQos.profile.url_profile.clear();
        if(config.qosUrlGroups != null) {
            factoryQos.profile.url_profile.ensureCapacity(config.qosUrlGroups.length);
            for(String urlGroup : config.qosUrlGroups) {
                // check for invalid files because they'll make DomainParticipantFactory puke
                File file = new File(urlGroup);
                if(file.canRead()) {
                    factoryQos.profile.url_profile.add(urlGroup);
                }
                else {
                    logger.warn("urlGroup entry is not a readable file. Ignoring: "+urlGroup);
                }
            }
        }

        try {
            DomainParticipantFactory.get_instance().set_qos(factoryQos);
            DomainParticipantFactory.get_instance().reload_profiles();
        }
        catch(RETCODE_ERROR e) {
            String msg = "Failed to set DomainParticipantFactoryQos, "+e.getClass().getSimpleName();
            logger.fatal(msg, e);
            throw new UncheckedDdsEntityException(msg);
        }
    }

    /**
     * Explicitly set which participant id should be used as default
     * @param participantId
     */
    public static void setDefaultParticipant(String participantId) {
        s_defaultParticipant = participantId;
    }

    /**
     * Unless explicitly set by the user, the default participant will be the
     * first participant created by the application. 
     */
    public static String getDefaultParticipant() {
        return s_defaultParticipant;
    }

    /**
     * Constructs a ParticipantCreator object from the passed parameters and calls addParticipantCreator()
     * @param participantId
     * @param participantName
     * @param domainId
     * @param qosLibrary
     * @param qosProfile
     * @param customize
     * @param dpListener
     * @return
     * @throws DdsEntityCreationException
     */
    public static synchronized DomainParticipant createParticipant(String participantId,
                                                                   String participantName, int domainId, 
                                                                   String qosLibrary, String qosProfile,
                                                                   IParticipantCustomization customize,
                                                                   DomainParticipantListener dpListener) 
                                                                           throws DdsEntityCreationException {
        ParticipantCreator creator = new ParticipantCreator(participantId, 
                                                            participantName, 
                                                            domainId,
                                                            qosLibrary,
                                                            qosProfile, 
                                                            customize, 
                                                            dpListener);
        return addParticipantCreator(creator);                                                  
    }

    /**
     * Add creator to the list of ParticipantCreators, then invoke 
     * creator.create() and return the DomainParticipant. The ParticipantCreator is 
     * retained so it can be invoked on a RtiDds.restart()
     * @param creator
     * @return
     * @throws DdsEntityCreationException
     */
    public static synchronized DomainParticipant addParticipantCreator(ParticipantCreator creator) 
            throws DdsEntityCreationException {
        if(creator.participantId == null) {
            throw new DdsEntityCreationException("Participant name cannot be null");
        }
        if(s_participantCreators.containsKey(creator.participantId)) {
            throw new DdsEntityCreationException("A ParticipantCreator for \""+creator.participantId+"\" already exists in the factory.");
        }
        s_participantCreators.put(creator.participantId, creator);
        return creator.create();
    }

    public static synchronized void removeParticipantCreator(String participantId) {
        s_participantCreators.remove(participantId);
    }

    public static synchronized ParticipantCreator getParticipantCreator(String participantId) {
        return s_participantCreators.get(participantId);
    }

    /** return list of ParticipantCreators */
    public static synchronized ParticipantCreator[] getParticipantCreators() {
        ParticipantCreator[] retVal = s_participantCreators.values().toArray(new ParticipantCreator[0]);
        Arrays.sort(retVal, new ParticipantCreatorOrdinalComparator());
        return retVal;
    }
    public static class ParticipantCreatorDomainIdComparator implements Comparator<ParticipantCreator> {
        @Override
        public int compare(ParticipantCreator arg0, ParticipantCreator arg1) {
            int retVal = arg0.domainId - arg1.domainId;
            if(retVal == 0) { // if same domainId, sort by participantId
                return arg0.participantId.compareTo(arg1.participantId);
            }
            return retVal;
        }
    }
    public static class ParticipantCreatorOrdinalComparator implements Comparator<ParticipantCreator> {
        @Override
        public int compare(ParticipantCreator arg0, ParticipantCreator arg1) {
            int retVal = arg0.ordinal - arg1.ordinal;
            return retVal;
        }
    }

    /**
     * @return 
     */
    public static String getParticipantName(String participantId) {
        ParticipantCreator creator = s_participantCreators.get(participantId);
        if(creator != null) {
            return creator.participantName;
        }
        return null;
    }

    /**
     * @return qosLibrary associated with participantId
     */
    public static String getParticipantQosLibrary(String participantId) {
        ParticipantCreator creator = s_participantCreators.get(participantId);
        if(creator != null) {
            return creator.qosLibrary;
        }
        return null;
    }

    /**
     * package visibility; intended to be called /only/ by RtiDds.restart()
     * @throws DdsEntityCreationException if any participant fails to be created
     */
    static synchronized void createAllPariticpants(IProgressUpdater progress) throws DdsEntityCreationException {
        LinkedList<String> errorMessages = new LinkedList<String>();
        for(ParticipantCreator creator : s_participantCreators.values()) {
            try {
                String msg = "Creating participant \""+creator.participantName+
                        "\" on domain "+creator.domainId+
                        "\" with library \""+creator.qosLibrary+
                        "\" and profile \""+creator.qosProfile+"\"";
                logger.debug(msg);
                if(progress != null) progress.updateProgress(msg);
                creator.create();
            }
            catch (DdsEntityCreationException e) {
                errorMessages.add(e.getMessage());
                logger.error(e.getMessage(), e);
            }
        }
        if(errorMessages.size() > 0) {
            StringBuilder sb = new StringBuilder(errorMessages.size()+" out of "+s_participantCreators.size()+
                    " participants could not be created.\n");
            for(String msg : errorMessages) {
                sb.append("    ").append(msg);
            }
            throw new DdsEntityCreationException(sb.toString());
        }
    }

    static synchronized DomainParticipant createParticipantImpl(String participantId,
                                                                String participantName, 
                                                                int domainId, 
                                                                String qosLibrary, 
                                                                String qosProfile,
                                                                IParticipantCustomization customize,
                                                                DomainParticipantListener dpListener) 
                                                                        throws DdsEntityCreationException {
        try {
            boolean profileComplete = true;
            if(qosLibrary == null || qosLibrary.length() == 0 ||
                    qosProfile == null || qosLibrary.length() == 0) {
                logger.warn("Participant QoS specification is incomplete (qosLibrary=\""+qosLibrary+"\", qosProfile=\""+qosProfile+"\"). Will create with default QoS.");
                profileComplete = false;
            }

            DomainParticipantQos participantQos = new DomainParticipantQos();
            if(profileComplete) {
                DomainParticipantFactory.get_instance().get_participant_qos_from_profile(participantQos, qosLibrary, qosProfile);
            }
            else {
                DomainParticipantFactory.get_instance().get_default_participant_qos(participantQos);
            }
            return createParticipantImpl(participantId, participantName, domainId, participantQos, customize, dpListener);
        }
        catch (Throwable t) {
            throw new DdsEntityCreationException("Failed to create "+participantId+" with name \""+participantName+
                                                 "\"  using library \""+qosLibrary+
                                                 "\" and profile \""+qosProfile+"\"", 
                                                 t);
        }
    }

    /**
     * Package visibility only. The public interface to create a participant is ParticipantCreator
     * @param participantName
     * @param domainId
     * @param qosLibrary
     * @param qosProfile
     * @param customize hook to alter FactoryQos and Participant pre and post creation. 
     * The original FactoryQos will be restored following creation of the DomainParticipant.
     * @param dpListener
     * @return
     * @throws DdsEntityCreationException
     */
    static synchronized DomainParticipant createParticipantImpl(String participantId,
                                                                String participantName, 
                                                                int domainId, 
                                                                DomainParticipantQos participantQos,
                                                                IParticipantCustomization customize,
                                                                DomainParticipantListener dpListener) 
                                                                        throws DdsEntityCreationException {
        if(s_dpfConfig == null) {
            throw new DdsEntityCreationException("DomainParticipantFactory has not been initialized."+
                    "You must call initDomainParticipantFactory before attemting to create a participant.");
        }
        if(participantId == null) {
            throw new DdsEntityCreationException("Participant name cannot be null");
        }
        DomainParticipant participant = s_participantMap.get(participantId);
        if(participant != null) {
            throw new DdsEntityCreationException("A Participant with ID \""+participantId+"\" already exists.");
        }

        try {
            int statusMask = StatusKind.STATUS_MASK_NONE;
            if(dpListener != null) {
                statusMask = StatusKind.STATUS_MASK_ALL;
            }
            DomainParticipantFactoryQos originalFactoryQos = new DomainParticipantFactoryQos();
            DomainParticipantFactoryQos modifiedFactoryQos = new DomainParticipantFactoryQos();
            DomainParticipantFactory.get_instance().get_qos(originalFactoryQos);
            DomainParticipantFactory.get_instance().get_qos(modifiedFactoryQos);
            if(customize != null) {
                customize.customizeFactory(participantId, modifiedFactoryQos);
                DomainParticipantFactory.get_instance().set_qos(modifiedFactoryQos);
            }

            participantQos.participant_name.name = participantName;

            if(customize != null) {
                customize.customizePreCreation(participantId, participantQos);
            }
            participant = DomainParticipantFactory.get_instance().create_participant(domainId, participantQos,
                                                                                     dpListener, statusMask);
            if (participant == null) {
                throw new DdsEntityCreationException("DomainParticipantFactory.create_participant() returned null");
            }

            //-- add flow controllers
            if(s_dpfConfig.flowControllers != null) {
                for(FlowController fc : s_dpfConfig.flowControllers) {
                    participant.create_flowcontroller(fc.name, fc.properties);
                }
            }

            if(customize != null) {
                customize.customizePostCreation(participantId, participant);
                DomainParticipantFactory.get_instance().set_qos(originalFactoryQos);
            }

            //-- enable the participant
            participant.enable();

            addParticipant(participantId, participant);
            if(s_defaultParticipant == null) {
                s_defaultParticipant = participantId;
            }
        } 
        catch (Throwable t) {
            final String msg = String.format("%s thrown while creating \"%s\": %s", t.getClass().getSimpleName(), participantId, t.getMessage());
            throw new DdsEntityCreationException(msg, t);
        }
        return participant;
    }

    protected static synchronized void addParticipant(String participantId, DomainParticipant participant) {
        s_participantMap.put(participantId, participant);
        s_containedMap.put(participantId, new ContainedEntities(participant));
    }

    protected static synchronized void removeParticipant(String participantId) {
        s_participantMap.remove(participantId);
        s_containedMap.remove(participantId);
    }

    /**
     * get an existing participant. 
     * @param participantId
     * @return
     * @throws UncheckedDdsEntityException 
     */
    public static synchronized DomainParticipant getParticipant(String participantId) throws UncheckedDdsEntityException {
        final DomainParticipant retVal = s_participantMap.get(participantId);
        if(retVal == null && !s_participantCreators.containsKey(participantId)) {
            throw new UncheckedDdsEntityException("Invalid participantId: \""+participantId+"\"");
        }
        return retVal;
    }

    public static synchronized void destroyAllParticipants() {
        destroyAllParticipants(null);
    }
    public static synchronized void destroyAllParticipants(IProgressUpdater progress) {
        for(DomainParticipant participant : s_participantMap.values()) {
            destroyParticipant(participant, progress);
            //try { Thread.sleep(250); } catch(Throwable t) { t.printStackTrace(); }
        }
        s_participantMap.clear();
        s_containedMap.clear();
    }

    /**
     * Destroy and remove DomainParticipant corresponding to participantId 
     * @param participantId
     */
    public static synchronized void destroyParticipant(String participantId) {
        DomainParticipant participant = s_participantMap.get(participantId);
        if(participant != null) {
            destroyParticipant(participant, null);
            removeParticipant(participantId);
        }
        else {
            logger.debug("Could not find participant id \""+participantId+"\"");
        }
    }

    /**
     * Destroy and remove DomainParticipant corresponding to participantId 
     * @param participantId
     */
    public static synchronized void destroyParticipant(DomainParticipant participant, IProgressUpdater progress) {
        if(participant != null) {
            DomainParticipantQos participantQos = new DomainParticipantQos();
            participant.get_qos(participantQos);
            String name = participantQos.participant_name.name;
            String msg = "Destroying participant \""+name+"\" and contained entities on domain "+participant.get_domain_id();
            logger.debug(msg);
            if(progress != null) progress.updateProgress(msg);
            try {
                participant.delete_contained_entities();
            }
            catch(Throwable t) {
                logger.error("delete_contained_entities() failed", t);
            }
            try {
                DomainParticipantFactory.get_instance().delete_participant(participant);
            }
            catch(Throwable t) {
                logger.error("delete_contained_entities() failed", t);
            }
        }
    }

    /**
     * @return an array of current participantIds, sorted by their Domain ID
     */
    public static String[] getParticipantIds() {
        ParticipantCreator[] creators = getParticipantCreators();
        String[] retVal = new String[creators.length];
        for(int i = 0; i < creators.length; i++) {
            retVal[i] = creators[i].participantId;
        }
        return retVal;
    }

    /**
     * check whether a string is a valid participant id 
     * @param participantId
     * @return
     */
    public static boolean isValidParticipantId(String participantId) {
        for(String pid : getValidParticipantIds()) {
            if(pid.equals(participantId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns an <i>unsorted</i> collection of all participantIds that have valid
     * DomainParticipants associated with them. Some participantIds may be 
     * disabled (for example, by setting the empty string for the QoS profile)
     */
    public static List<String> getValidParticipantIdList() {
        List<String> valid = new LinkedList<String>();
        try {
            for(ParticipantCreator creator : getParticipantCreators()) {
                if(getParticipant(creator.participantId) != null) {
                    valid.add(creator.participantId);
                }
            }
        } 
        catch(Throwable t) {
            logger.debug("error in getValidParticipantIds", t);
        }
        return valid;
    }

    /**
     * returns a sorted array of all participantIds that have valid
     * DomainParticipants associated with them. Some participantIds may be 
     * disabled (for example, by setting the empty string for the QoS profile)
     */
    public static String[] getValidParticipantIds() {
        List<String> valid = getValidParticipantIdList();
        return valid.toArray(new String[valid.size()]);
    }

    /**
     * @param participantId
     * @return entities contained by DomainParticipant corresponding to participantId
     */
    public static ContainedEntities getContainedEntities(String participantId) {
        return s_containedMap.get(participantId);
    }

    /**
     * Register a DDS type with the DomainParticipant corresponding to participantId. The 
     * @see gov.nasa.dds.rti.util.TypeSupportUtil
     * @param participantId
     * @param typeClass
     */
    public static void registerType(String participantId, final Class<? extends Copyable> typeClass) {
        ContainedEntities ce = s_containedMap.get(participantId);
        ce.registerType(typeClass);
    }

    /**
     * Get topic if it exists, otherwise create it with the default qos
     * @param topicName
     * @param typeName
     * @param participantId
     * @return null of participant is disabled
     * @throws DdsEntityCreationException if error occured during construction
     */
    public static synchronized Topic getTopic(String participantId, String topicName, final Class<? extends Copyable> typeClass) 
            throws DdsEntityCreationException {
        Topic retVal = null;
        DomainParticipant participant = getParticipant(participantId);
        if(participant != null) {
            //-- look up existing topic, wait for 1 millisecond
            retVal = participant.find_topic(topicName, new Duration_t(0,1000000));
            if(retVal == null) { //-- if it doesn't exist, create it
                // ensure type is registered
                registerType(participantId, typeClass);
                // create topic on participant
                final String typeName = TypeSupportUtil.getTypeNameFor(typeClass);
                if(typeName == null) {
                    throw new DdsEntityCreationException("Could not determine type name for "+typeClass.getSimpleName()+". Make sure TypeSupportUtil has been properly initialized.");
                }
                retVal = participant.create_topic(topicName, typeName, 
                                                  DomainParticipant.TOPIC_QOS_DEFAULT,
                                                  null, StatusKind.STATUS_MASK_NONE);
            }
            if(retVal == null) {
                throw new DdsEntityCreationException("Error creating topic \""+topicName+"\"");
            }
        }
        return retVal;
    }

    /**
     * create and return DataWriter
     */
    public static synchronized DataWriter createDataWriter(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final DataWriterQos qos,
                                                           final String partition) throws DdsEntityCreationException {
        return createDataWriter(participantId, topicName, typeClass, qos, partition,
                                null,
                                StatusKind.STATUS_MASK_NONE,
                                true);
    }

    /**
     * @return DataWriter if successfully created. Will return null 
     * if participant is disabled.
     */
    public static synchronized DataWriter createDataWriter(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final DataWriterQos qos,
                                                           final String partition,
                                                           final DataWriterListener listener, 
                                                           final int listenerMask,
                                                           boolean doEnable) throws DdsEntityCreationException {
        if(getParticipant(participantId) == null) {
            return null; // participant is disabled
        }
        DataWriter retVal = null;
        ContainedEntities contained = getContainedEntities(participantId);
        Publisher publisher = contained.getPublisherForPartition(partition);
        try {
            Topic topic = getTopic(participantId, topicName, typeClass);
            retVal = publisher.create_datawriter(topic, qos, listener, listenerMask);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Could not obtain DataWriter for \""+topicName+"\"", e);
        }
        if(retVal == null) {
            throw new DdsEntityCreationException("Could not obtain DataWriter for \""+topicName+"\"");
        }

        if(doEnable) {
            retVal.enable();
        }
        return retVal;
    }

    /**
     * Users should use the signature without the qosLibrary parameter 
     * (i.e. look up library name from ParticipantCreator)
     * @throws DdsEntityCreationException if there was an error 
     * @return DataWriter if successfully created. Will return null 
     * if participant is disabled.
     */
    protected static synchronized DataWriter createDataWriter(final String participantId, 
                                                              final String topicName,  
                                                              final Class<? extends Copyable> typeClass, 
                                                              final String qosLibrary, final String qosProfile, 
                                                              final String partition, 
                                                              final DataWriterListener listener, 
                                                              final int listenerMask,
                                                              boolean doEnable) throws DdsEntityCreationException {
        if(getParticipant(participantId) == null) {
            return null; // participant is disabled
        }
        DataWriter retVal = null;
        ContainedEntities contained = getContainedEntities(participantId);
        Publisher publisher = contained.getPublisherForPartition(partition);
        try {
            Topic topic = getTopic(participantId, topicName, typeClass);
            retVal = publisher.create_datawriter_with_profile(topic, 
                                                              qosLibrary, qosProfile, 
                                                              listener, listenerMask);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Could not obtain DataWriter for \""+topicName+"\"", e);
        }
        if(retVal == null) {
            throw new DdsEntityCreationException("Could not obtain DataWriter for \""+topicName+"\"");
        }

        if(doEnable) {
            retVal.enable();
        }
        return retVal;
    }

    /**
     * 
     * @param topicName
     * @param typeClass
     * @param qosLibrary
     * @param qosProfile
     * @param partition
     * @param listener
     * @param listenerMask
     * @param doEnable enable the writer 
     * @return null if particpantId is disabled
     * @throws DdsEntityCreationException if error occured during creation
     */
    public static synchronized DataWriter createDataWriter(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition, 
                                                           final DataWriterListener listener, 
                                                           final int listenerMask,
                                                           final boolean doEnable) throws DdsEntityCreationException {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            return createDataWriter(participantId, topicName, typeClass, qosLibrary, qosProfile, partition, listener, listenerMask, doEnable);
        }
        return null;
    }

    /**
     * 
     * @param topicName
     * @param typeClass
     * @param qosLibrary
     * @param qosProfile
     * @param partition
     * @param listener
     * @param listenerMask
     * @return null if particpantId is disabled
     * @throws DdsEntityCreationException if error occured during creation
     */
    public static synchronized DataWriter createDataWriter(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition, 
                                                           final DataWriterListener listener, 
                                                           final int listenerMask) throws DdsEntityCreationException {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            return createDataWriter(participantId, topicName, typeClass, qosLibrary, qosProfile, partition, listener, listenerMask, true);
        }
        throw new DdsEntityCreationException("ParticipantCreator for "+participantId+" is null");
    }

    /**
     * 
     * @param participantId
     * @param topicName
     * @param typeClass
     * @param qosProfile
     * @param partition
     * @param listener
     * @return
     * @throws DdsEntityCreationException
     */
    public static synchronized DataWriter createDataWriter(final String participantId,
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition, 
                                                           final DataWriterListener listener) throws DdsEntityCreationException {
        int listenerMask = (listener == null) ? StatusKind.STATUS_MASK_NONE : StatusKind.STATUS_MASK_ALL;
        return createDataWriter(participantId, topicName, typeClass, qosProfile, partition, listener, listenerMask);
    }

    /**
     * 
     * @param participantId
     * @param topicName
     * @param typeClass
     * @param qosProfile
     * @param partition
     * @return
     * @throws DdsEntityCreationException
     */
    public static synchronized DataWriter createDataWriter(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition) throws DdsEntityCreationException {
        return createDataWriter(participantId, topicName, typeClass, qosProfile, partition, null);
    }

    /**
     * create DataReader without using XML qos profile
     */
    public static synchronized DataReader createDataReader(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final DataReaderQos qos, 
                                                           final String partition,
                                                           final DataReaderListener listener) throws DdsEntityCreationException {
        int listenerMask = (listener == null) ? StatusKind.STATUS_MASK_NONE : StatusKind.STATUS_MASK_ALL;
        return createDataReader(participantId, topicName, typeClass, qos, partition, listener, listenerMask);
    }

    /**
     * create DataReader without using XML qos profile
     */
    public static synchronized DataReader createDataReader(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final DataReaderQos qos, 
                                                           final String partition,
                                                           final DataReaderListener listener, 
                                                           final int listenerMask) throws DdsEntityCreationException {

        if(getParticipant(participantId) == null) {
            logger.debug("participant \""+participantId+"\" is not enabled");
            return null; // participant is disabled
        }
        DataReader retVal = null;
        ContainedEntities contained = getContainedEntities(participantId);
        Subscriber subscriber = contained.getSubscriberForPartition(partition);
        try {
            Topic topic = getTopic(participantId, topicName, typeClass);
            retVal = subscriber.create_datareader(topic, qos, listener, listenerMask);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Could not obtain DataReader for \""+topicName+"\"", e);
        }
        if(retVal == null) {
            throw new DdsEntityCreationException("Could not obtain DataReader for \""+topicName+"\"");
        }
        retVal.enable();
        return retVal;
    }

    /**
     * Users should use the signature without
     * the qosLibrary parameter (i.e. look up library name from ParticipantCreator)
     * @throws DdsEntityCreationException
     * @return null if participant is disabled
     */
    protected static synchronized DataReader createDataReader(final String participantId, 
                                                              final String topicName,  
                                                              final Class<? extends Copyable> typeClass, 
                                                              final String qosLibrary, final String qosProfile, 
                                                              final String partition,
                                                              final DataReaderListener listener, 
                                                              final int listenerMask) throws DdsEntityCreationException {

        if(getParticipant(participantId) == null) {
            logger.debug("participant \""+participantId+"\" is not enabled");
            return null; // participant is disabled
        }
        DataReader retVal = null;
        ContainedEntities contained = getContainedEntities(participantId);
        Subscriber subscriber = contained.getSubscriberForPartition(partition);
        try {
            Topic topic = getTopic(participantId, topicName, typeClass);
            retVal = subscriber.create_datareader_with_profile(topic, 
                                                               qosLibrary, qosProfile, 
                                                               listener, listenerMask);
        }
        catch(RETCODE_ERROR e) {
            throw new DdsEntityCreationException("Could not obtain DataReader for \""+topicName+"\" with profile \""+qosProfile+"\" ", e);
        }
        if(retVal == null) {
            throw new DdsEntityCreationException("Could not obtain DataReader for \""+topicName+"\" with profile \""+qosProfile+"\" ");
        }
        retVal.enable();
        return retVal;
    }

    /**
     * 
     * @param participantId
     * @param topicName
     * @param typeClass
     * @param qosProfile
     * @param partition
     * @param listener
     * @param listenerMask
     * @return null if participant is disabled
     * @throws DdsEntityCreationException if error occured during construction
     */
    public static synchronized DataReader createDataReader(final String participantId, 
                                                           final String topicName,  
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition,
                                                           final DataReaderListener listener, 
                                                           final int listenerMask) throws DdsEntityCreationException {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            return createDataReader(participantId, topicName, typeClass, qosLibrary, qosProfile, partition, listener, listenerMask);
        }
        return null;
    }

    /**
     * 
     * @param participantId
     * @param topicName
     * @param typeClass
     * @param qosProfile
     * @param partition
     * @param listener
     * @return
     * @throws DdsEntityCreationException
     */
    public static synchronized DataReader createDataReader(final String participantId, 
                                                           final String topicName, 
                                                           final Class<? extends Copyable> typeClass, 
                                                           final String qosProfile, 
                                                           final String partition,
                                                           final DataReaderListener listener) throws DdsEntityCreationException {
        int listenerMask = (listener == null) ? StatusKind.STATUS_MASK_NONE : StatusKind.STATUS_MASK_ALL;
        return createDataReader(participantId, topicName, typeClass, qosProfile, partition, listener, listenerMask);
    }

    /**
     * @return retVal with updated values, or null if participantId is invalid
     */
    public static DataReaderQos getDataReaderQos(String participantId, String qosProfile, DataReaderQos retVal) {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            DomainParticipantFactory.get_instance().get_datareader_qos_from_profile(retVal, qosLibrary, qosProfile);
            return retVal;
        }
        return null;
    }

    public static DataReaderQos getDefaultDataReaderQos(String participantId, DataReaderQos retVal) {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            getParticipant(participantId).get_default_datareader_qos(retVal);
            return retVal;
        }
        return null;
    }



    /**
     * @return retVal with updated values, or null if participantId is invalid
     */
    public static DataWriterQos getDataWriterQos(String participantId, String qosProfile, DataWriterQos retVal) {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            DomainParticipantFactory.get_instance().get_datawriter_qos_from_profile(retVal, qosLibrary, qosProfile);
            return retVal;
        }
        return null;
    }

    public static DataWriterQos getDefaultDataWriterQos(String participantId, DataWriterQos retVal) {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            getParticipant(participantId).get_default_datawriter_qos(retVal);
            return retVal;
        }
        return null;
    }

    /**
     * @return retVal with updated values, or null if participantId is invalid
     */
    public static DomainParticipantQos getParticipantQos(String participantId, String qosProfile, DomainParticipantQos retVal) {
        ParticipantCreator pc = s_participantCreators.get(participantId);
        if(pc != null) {
            String qosLibrary = pc.qosLibrary;
            DomainParticipantFactory.get_instance().get_participant_qos_from_profile(retVal, qosLibrary, qosProfile);
            return retVal;
        }
        return null;
    }
}


