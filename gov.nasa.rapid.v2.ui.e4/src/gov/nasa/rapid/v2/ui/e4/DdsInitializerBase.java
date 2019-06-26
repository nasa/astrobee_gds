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
package gov.nasa.rapid.v2.ui.e4;

import gov.nasa.dds.exception.DdsEntityCreationException;
import gov.nasa.dds.rti.preferences.DdsPreferences;
import gov.nasa.dds.rti.system.DdsEntityFactory;
import gov.nasa.dds.rti.system.DomainParticipantFactoryConfig;
import gov.nasa.dds.rti.system.IParticipantCustomization;
import gov.nasa.dds.rti.system.ParticipantCustomizationAggregate;
import gov.nasa.dds.rti.util.TypeSupportUtil;
import gov.nasa.rapid.v2.e4.Rapid;
import gov.nasa.rapid.v2.e4.agent.DiscoveredAgentRepository;
import gov.nasa.rapid.v2.e4.system.builtin.TopicPublicationDataReaderListener;
import gov.nasa.rapid.v2.e4.system.builtin.TopicPublicationParticipantCustomization;
import gov.nasa.rapid.v2.e4.util.RapidTypeSupportUtil;
import gov.nasa.rapid.v2.ui.e4.preferences.DdsPreferenceKeys;
import gov.nasa.rapid.v2.ui.e4.preferences.DdsPreferencesEclipseUi;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.rti.dds.target.RtiDdsTarget;


/**
 * Base class for initializing DDS. Applications will want to inherit from this class and override the methods to change
 * initialization behavior.
 * 
 * @author mallan
 * 
 */
public class DdsInitializerBase {
	private static final Logger  logger        = Logger.getLogger(DdsInitializerBase.class);
	private static AtomicBoolean s_initialized = new AtomicBoolean(false);
	/**
	 * create a string usable by preferences and preferences ui components from an array of strings
	 * 
	 * @param array
	 * @return
	 */
	@Inject
	public DdsInitializerBase(){
		
	}

	public static String listToPrefString(List<String> list) {
		return arrayToPrefString(list.toArray(new String[0]));
	}

	public static String arrayToPrefString(String[] array) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (i > 0) {
				builder.append(DdsPreferenceKeys.LIST_SEPARATOR);
			}
			builder.append(array[i]);
		}
		return builder.toString();
	}

	protected String[] m_qosFiles  = new String[] { "RAPID_QOS_PROFILES.xml" };

	protected String   m_peersList = "NDDS_DISCOVERY_PEERS";

	private String getPublicAddressFromCommandLine() {
		String[] args = Platform.getCommandLineArgs();
		String flag = "-publicAddress";

		for(int i=0; i<args.length-1; i++) {
			if(args[i].equalsIgnoreCase(flag)) {
				return args[i+1];
			}
		}
		return null;
	}

	/**
	 * Creates the GlobalParticipant by default. To create more participants, override this method.
	 */
	public void createParticipants() throws DdsEntityCreationException {
		int domainId;
		String qosLibrary;
		String qosProfile;
		String participantId;
		String participantName;
		String publicAddress;
		IParticipantCustomization customize;

		// -- Create the PrimaryParticipant participant
		participantId = Rapid.PrimaryParticipant;
		participantName = Rapid.defaultParticipantName();//"FreeFlyerA";//"Spheres0";//
		domainId = DdsPreferences.getDomainId(participantId);
		qosLibrary = DdsPreferences.getQosLibrary(participantId);
		qosProfile = DdsPreferences.getQosProfile(participantId);
		publicAddress = getPublicAddressFromCommandLine();
		customize = new TopicPublicationParticipantCustomization(new TopicPublicationDataReaderListener(
				DiscoveredAgentRepository.INSTANCE));

		if (publicAddress != null) {
			ParticipantCustomizationAggregate pca =
					new ParticipantCustomizationAggregate(customize,
							new PublicAddressParticipantCustomization(publicAddress));
			customize = pca;
		}

		DdsEntityFactory.createParticipant(participantId, participantName, domainId, qosLibrary, qosProfile, customize, null);
		
//		RapidMessageCollector.instance().addRapidMessageListener(m_participantId, m_agent, COMMAND_CONFIG_TYPE, this);
	}

	/**
	 * Given an array of qos filenames, generate a list of full path names for the url group preference. Filenames are relative to
	 * bundle root directory
	 * 
	 * @param bundleId
	 *            bundle ID of the application plugin
	 * @param qosFiles
	 * @return
	 */
	public String getUrlGroupDefault(String bundleId, String[] qosFiles) {
		final StringBuilder urlGroupDefault = new StringBuilder();
		for (String qosFile : qosFiles) {
			File file = resolveDdsFile(bundleId, qosFile);
			if (file != null) {
				urlGroupDefault.append(file.getAbsolutePath() + DdsPreferenceKeys.LIST_SEPARATOR);
			}
		}
		return urlGroupDefault.toString();
	}

	/**
	 * initialize RAPID DDS
	 * 
	 * @param appPrefStore
	 *            preference store
	 * @param appBundleId
	 * @throws Exception
	 */
	@PostConstruct
	//  public void init(String appBundleId) throws Exception {
	public void init() throws Exception {
		//synchronized (s_initialized) {
			if (!s_initialized.get()) {
				
				// == First, initialize preference defaults
				Bundle b = FrameworkUtil.getBundle(getClass()); 
				String appBundleId = b.getSymbolicName();
				initializePreferenceDefaults(appBundleId);

				// == Mandatory DDS and RAPID setup =====================
				setupDdsImplementation();
				setupDdsPreferences();
				setupDdsTypeSupport();
				setupRuntimeMessageTypes();

				// -- Create the default factory configuration
				DomainParticipantFactoryConfig dpfConfig = new DomainParticipantFactoryConfig();
				dpfConfig.qosUrlGroups = DdsPreferences.getProfileUrlGroups();
				dpfConfig.isIgnoreEnvironmentProfile = DdsPreferences.isIgnoreEnvironmentProfile();
				dpfConfig.isIgnoreUserProfile = DdsPreferences.isIgnoreUserProfile();
				DdsEntityFactory.initDomainParticipantFactory(dpfConfig);

				createParticipants();
				s_initialized.set(true);
			}
		//}
	}
	
	private String getDomainIdFromCommandLine() {
		String[] args = Platform.getCommandLineArgs();
		String flag = "-domainId";

		for(int i=0; i<args.length-1; i++) {
			if(args[i].equals(flag)) {
				return args[i+1];
			}
		}
		return "37";
	}

	private List<String> getPeersFromCommandLine() {
		final List<String> peers = new ArrayList<String>();
		final String[] args = Platform.getCommandLineArgs();
		final String flag = "-peer";

		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase(flag)) {
				peers.add(args[++i]);
			}
		}

		return peers;
	}

	/**
	 * Because we want more control over the upstream preferences, we set the defaults at application initialization
	 * 
	 * @param bundleId
	 */
	public void initializePreferenceDefaults(String bundleId) {
		RapidV2UiPreferences.add(DdsPreferenceKeys.domainId(Rapid.PrimaryParticipant), getDomainIdFromCommandLine());
		RapidV2UiPreferences.add(DdsPreferenceKeys.qosLibrary(Rapid.PrimaryParticipant), "RapidQosLibrary");
		RapidV2UiPreferences.add(DdsPreferenceKeys.qosProfile(Rapid.PrimaryParticipant), "RapidDefaultQos");
		RapidV2UiPreferences.add(DdsPreferenceKeys.P_QOS_URL_GROUPS, getUrlGroupDefault(bundleId, m_qosFiles));

		List<String> peers = readPeersFile(bundleId, m_peersList);
		peers.addAll(getPeersFromCommandLine());
		if (peers.size() == 0) {
			peers.addAll(Arrays.asList("builtin.shmem://", "shmem://", "127.0.0.1"));
		}
		RapidV2UiPreferences.add(DdsPreferenceKeys.P_PEERS_LIST, listToPrefString(peers));
	}

	/**
	 * read a peers file and return the peer descriptors
	 * 
	 * @param bundleId
	 * @param filename
	 * @return
	 */
	public List<String> readPeersFile(String bundleId, String filename) {
		ArrayList<String> retVal = new ArrayList<String>();
		InputStream is = null;
		try {
			File peerFile = resolveDdsFile(bundleId, filename);
			if (peerFile != null) {
				is = peerFile.toURI().toURL().openStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				while ((line = br.readLine()) != null) {
					int comment = line.indexOf(';');
					if (comment >= 0) {
						line = line.substring(0, comment);
					}
					line = line.trim();
					if (line.length() > 1) {
						retVal.add(line);
					}
				}
			}
		} catch (Throwable t) {
			logger.error("error reading peers file " + filename, t);
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					logger.error("error closing file", e);
				}
			}
		}
		return retVal;
	}

	/**
	 * resolve a file in the bundle
	 */
	public File resolveDdsFile(String bundleId, String filename) {
		try {
			Bundle bundle = Platform.getBundle(bundleId);
			Path path = new Path(filename);
			URL bndlUrl = FileLocator.find(bundle, path, null);
			if (bndlUrl == null)
				throw new IOException("FileLocator could not find \"" + filename + "\" in " + bundle);
			URL fileUrl = FileLocator.toFileURL(bndlUrl);
			return new File(fileUrl.getFile());
		} catch (Throwable t) {
			logger.error("error resolving file " + filename + ": " + t.getMessage());
		}
		return null;
	}

	/**
	 * load native DDS libraries
	 */
	public void setupDdsImplementation() {
		System.out.println("Loading DDS Libraries...");
		RtiDdsTarget.loadNativeLibraries();
	}

	/**
	 * Set the preferences implementation
	 */
	public void setupDdsPreferences() {
		DdsPreferences.setImpl(new DdsPreferencesEclipseUi());
	}

	/**
	 * Make the RAPID types visible. If the application needs to use non-RAPID DDS types, override this method to add the necessary
	 * TypeSupportUtil implementations
	 */
	public void setupDdsTypeSupport() {
		TypeSupportUtil.addImpl(new RapidTypeSupportUtil());
	}

	/**
	 * If there are any MessageTypes that need to be initialized at runtime
	 * (e.g. cameras with custom qos profiles), set them up here
	 */
	public void setupRuntimeMessageTypes() {
		// empty
	}
}
