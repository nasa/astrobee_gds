/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.irg.plan.ui.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

public class ConfigFileWrangler {
	private static ConfigFileWrangler INSTANCE = null;

	private String WORKBENCH_PLUGIN_URL = "platform:/plugin/gov.nasa.arc.verve.freeflyer.workbench/resources";
	private String OCU_PLUGIN_URL = "platform:/plugin/gov.nasa.arc.ff.ocu";
	private String MODELS_PLUGIN_URL = "platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models";
	private String WORLD_MODELS_PLUGIN_URL = "platform:/plugin/gov.nasa.arc.verve.robot.freeflyer/models";

	private static final Logger logger = Logger.getLogger(ConfigFileWrangler.class);

	private static final boolean IS_WINDOWS = System.getProperty( "os.name" ).contains( "indow" );
	private final String JSON_SUFFIX = ".json";
	private String configDirPath, worldConfigDirPath, worldFolderName, worldConfigFolderName;

	private String configFolderName = "ControlStationConfig";

	private final String inertiaConfigName = "AllInertiaConfig.json";
	private final String opLimitsConfigName = "AllOperatingLimitsConfig.json";
	private final String bookmarksName = "BookmarksList.json";
	private final String globalPref = "global.properties";
	private final String cameraConfigName = "GraphicsCameraFovs.json";
	private final String coloredBoxesName = "GraphicsColoredBoxes.json";
	private final String lightsCameraPropertiesName = "GraphicsLightsCamera.properties";
	private final String helpDoc = "help.html";
	private final String helpFolder = "helpfiles";
	private final String handrailsName = "HandrailConfig.json";
	private final String healthAndStatusConfigName = "HealthConfig.txt";
	private final String legalDoc = "legal_notice.html";
	private final String guestScienceConfigName = "PlanEditorGuestScience.json";
	private final String planPayloadConfigName = "PlanEditorPayloadConfig.json";
	private final String cameraPresetName = "PlanEditorSetCameraConfig.json";

	private final String teleopCommandsConfigName = "TeleopCommandConfigurations.json";

	private final String issConfigName = "IssConfiguration.json";
	private final String issConfigReadmeName = "IssConfiguration-README";

	private final String modelsFolderName = "Models";
	private final String astrobeeModelFolderName = "astrobeeModel";

	private final String usLabModelName = "us_lab.dae";
	private final String euLabModelName = "eu_lab.dae";
	private final String jemModelName = "jpm.dae";
	private final String node1ModelName = "node_1.dae";
	private final String node2ModelName = "node_2.dae";
	private final String node3ModelName = "node_3.dae";
	private final String cupolaModelName = "cupola.dae";
	private final String handrailModelName = "new_handle.dae";
	private final String smartDockJsonName = "smart_dock.json";
	private final String smartDockModelName = "smart_dock.dae";
	private final String smartDockTextureName = "smart_dock.png";

	private final String keepoutFolderName = "keepouts";
	private final String keepinFolderName = "keepins";
	private final String dataToDiskFolderName = "DataToDisk";

	private final String[] resourceFilesToCopy = {
			cameraConfigName,
			cameraPresetName,
			guestScienceConfigName,
			healthAndStatusConfigName,
			inertiaConfigName,
			opLimitsConfigName,
			planPayloadConfigName,
			teleopCommandsConfigName
	};

	private final String[] worldResourceFilesToCopy = {
			bookmarksName,
			coloredBoxesName,
			handrailsName,
			issConfigName,
			issConfigReadmeName,
			lightsCameraPropertiesName
	};	

	// for the life of me, I cannot get Eclipse Java to list all
	// the files in a directory
	private final String[] worldTextureFilesToCopy = {
			"Airlock.png.001.png",
			"Airlock_02.png.001.png",
			"Airlock_externalDoor_inside.jpg",
			"ChromeReflection.jpg",
			"Columbus_01.png",
			"Columbus_02.png",
			"Columbus_03.png",
			"Columbus_Bulkhead.png",
			"Cupola_Int_Diffuse.png",
			"Generic_Intersection.png",
			"Generic_Misc_Details.png.001.png",
			"JLP_Int_Racks.png",
			"JLP_Interior.jpg.001.jpg",
			"JPM_Bulkhead.png",
			"JPM_Racks_01.png.002.png",
			"JPM_Racks_02.png",
			"JPM_Racks_03.png",
			"Node1_Bulkhead.png",
			"Node1_Rack.png",
			"Node2_Bulkheads.png",
			"Node2_Interior_Racks.png",
			"Node3_Bulkhead.png",
			"Node3_Rack_01.png",
			"Node3_Rack_02.png",
			"PMM_01.png",
			"USLab_FWD_Bulkhead.png",
			"USLab_Racks_02.png",
			"Untitled.002.png",
			"Untitled.004.png",
			"Untitled.005.png",
			"cupola.blend",
			"eu_lab.blend",
			"jpm.blend",
			"node1.blend",
			"node2.blend",
			"node3.blend",
			"smart_dock.json",
			"smart_dock.png",
			"us_lab.blend"
	};

	private final String[] foldersToCopy = {
			dataToDiskFolderName
	};

	private final String[] worldFoldersToCopy = {
			keepoutFolderName,
			keepinFolderName,
	};

	private final String[] worldModelFilesToCopy = {
			usLabModelName,
			euLabModelName,
			jemModelName,
			node1ModelName,
			node2ModelName,
			node3ModelName,
			cupolaModelName,
			handrailModelName,
			smartDockJsonName,
			smartDockModelName,
			smartDockTextureName,
			smartDockJsonName
	};

	private final URL installUrl;

	public static ConfigFileWrangler getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new ConfigFileWrangler();
		}
		return INSTANCE;
	}

	private ConfigFileWrangler() {
		installUrl = Platform.getInstallLocation().getURL();

		// Do this here so it doesn't fail trying to make ControlStationConfig/world at the same time
		configDirPath = getDirectoryMakeIfNeeded(installUrl, configFolderName);
		worldConfigDirPath = configDirPath;

		worldFolderName = WorkbenchConstants.getWorldFolderName();

		WORLD_MODELS_PLUGIN_URL = MODELS_PLUGIN_URL + File.separator + worldFolderName;
		worldConfigFolderName = configFolderName + File.separator + worldFolderName;
		worldConfigDirPath = getDirectoryMakeIfNeeded(installUrl, worldConfigFolderName);

		copyResourceFiles();
		copyWorldResourceFiles();
		copyGlobalPreferencce();
		for(int i=0; i<foldersToCopy.length; i++) {
			copyJsonFilesFromSubfolder(foldersToCopy[i]);
		}
		for(int i=0; i<worldFoldersToCopy.length; i++) {
			copyWorldJsonFilesFromSubfolder(worldFoldersToCopy[i]);
		}
		copyWorldModelFiles();
		copyWorldTextureFiles();
		copyAstrobeeModelFolder();
		copyHelpFolder();
	}

	public File[] getDataToDiskFiles() {
		return getFilesInFolder(dataToDiskFolderName, JSON_SUFFIX);
	}

	protected void copyResourceFiles() {
		//		configDirPath = getDirectoryMakeIfNeeded(installUrl, configFolderName);
		logger.debug("Configdirpath = " + configDirPath);
		if(configDirPath != null) {
			URL url;
			try {
				for(int i=0; i<resourceFilesToCopy.length; i++) {
					url = new URL( WORKBENCH_PLUGIN_URL + "/" + resourceFilesToCopy[i] );
					final InputStream is = url.openConnection().getInputStream();

					final String outname = configDirPath + File.separator + resourceFilesToCopy[i];
					final Path outFile = Paths.get(outname);
					if(!Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
						Files.copy(is, outFile);
				}

			} catch (final Exception e) {
				logger.error("Failed to copy resource files.", e);
			}
		}
	}

	protected void copyWorldResourceFiles() {
		if(worldConfigDirPath != null) {
			URL url;
			try {
				for(int i=0; i<worldResourceFilesToCopy.length; i++) {
					url = new URL( WORKBENCH_PLUGIN_URL + "/" + worldFolderName + "/" + worldResourceFilesToCopy[i] );
					final InputStream is = url.openConnection().getInputStream();

					final String outname = worldConfigDirPath + File.separator + worldResourceFilesToCopy[i];
					final Path outFile = Paths.get(outname);
					if(!Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
						Files.copy(is, outFile);
				}

			} catch (final Exception e) {
				logger.error("Failed to copy world resource files.", e);
			}
		}
	}

	protected void copyWorldModelFiles() {
		final String modelsPath = getDirectoryMakeIfNeeded(installUrl, worldConfigFolderName + File.separator + modelsFolderName);
		if(modelsPath != null) {
			URL url;
			try {
				for(int i = 0; i < worldModelFilesToCopy.length; i++) {
					url = new URL( WORLD_MODELS_PLUGIN_URL + "/" + worldModelFilesToCopy[i] );
					try {
						final InputStream is = url.openConnection().getInputStream();
						final String outname = modelsPath + File.separator + worldModelFilesToCopy[i];
						final Path outFile = Paths.get(outname);

						if(!Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
							Files.copy(is, outFile);
					} catch(final Exception e1) {
						continue; // don't sweat not having all the modules
					}
				}
			} catch(final Exception e) {
				logger.error("Failed to copy models.", e);
			}
		}
	}

	protected void copyWorldTextureFiles() {
		final String modelsPath = getDirectoryMakeIfNeeded(installUrl, worldConfigFolderName + File.separator + modelsFolderName);
		if(modelsPath != null) {
			URL url;
			try {
				for(int i = 0; i < worldTextureFilesToCopy.length; i++) {
					url = new URL( WORLD_MODELS_PLUGIN_URL + "/" + worldTextureFilesToCopy[i] );
					try {
						final InputStream is = url.openConnection().getInputStream();
						final String outname = modelsPath + File.separator + worldTextureFilesToCopy[i];
						final Path outFile = Paths.get(outname);

						if(!Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
							Files.copy(is, outFile);
					} catch(final Exception e1) {
						continue; // don't sweat not having all the modules
					}
				}
			} catch(final Exception e) {
				logger.error("Failed to copy models.", e);
			}
		}
	}

	protected void copyGlobalPreferencce(){
		URL url;
		try{
			url = new URL(OCU_PLUGIN_URL + "/" + globalPref );
			final InputStream is = url.openConnection().getInputStream();
			final String outName = configDirPath + File.separator + globalPref;
			final Path outFile = Paths.get(outName);
			if(!Files.exists(outFile, LinkOption.NOFOLLOW_LINKS))
				Files.copy(is, outFile);
		} catch(final FileAlreadyExistsException e) {
			//System.out.println("Not overwriting global preferences");
		}catch(final Exception e){
			logger.error("Failed to copy file.", e);
		}
	}

	protected void copyHelpFolder() {
		final String allSuffixes = "";
		final String copiedFolderPath = getDirectoryMakeIfNeeded(installUrl, configFolderName + File.separator + helpFolder);
		final File[] filesToCopy = getFilesFromUrl(OCU_PLUGIN_URL, helpFolder, allSuffixes);

		try {
			for(int i=0; i<filesToCopy.length; i++) {
				final String shortName = filesToCopy[i].getName();
				final String outname = copiedFolderPath + File.separator + shortName;
				final Path outPath = Paths.get(copiedFolderPath + File.separator + shortName);
				final Path inPath = Paths.get(filesToCopy[i].getPath());

				try {
					Files.copy(inPath, outPath);
					logger.debug("Copied file to "+outname);

				} catch(final FileAlreadyExistsException e) {
					//System.out.println("Not overwriting " + outname);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to copy file.", e);
		}
	}

	protected void copyAstrobeeModelFolder() {
		final String allSuffixes = "";
		getDirectoryMakeIfNeeded(installUrl, configFolderName + File.separator + modelsFolderName);

		final String copiedFolderPath = getDirectoryMakeIfNeeded(installUrl, configFolderName + File.separator + modelsFolderName + File.separator + astrobeeModelFolderName);

		File[] filesToCopy = getFilesFromUrl(MODELS_PLUGIN_URL, astrobeeModelFolderName, allSuffixes);

		filesToCopy = discardFilesWithWrongPrefix(filesToCopy, "arm");
		filesToCopy = discardFilesWithWrongPrefix(filesToCopy, "gripper");

		try {
			for(int i=0; i<filesToCopy.length; i++) {
				final String shortName = filesToCopy[i].getName();
				final String outname = copiedFolderPath + File.separator + shortName;
				final Path outPath = Paths.get(copiedFolderPath + File.separator + shortName);
				final Path inPath = Paths.get(filesToCopy[i].getPath());

				try {
					Files.copy(inPath, outPath);
					logger.debug("Copied file to "+outname);

				} catch(final FileAlreadyExistsException e) {
					//System.out.println("Not overwriting " + outname);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to copy file.", e);
		}
	}

	protected void copyJsonFilesFromSubfolder(final String folderToCopyName) {
		String copiedFolderPath = getDirectoryMakeIfNeeded(installUrl, configFolderName + File.separator + folderToCopyName);
		final File[] filesToCopy = getFilesFromUrl(WORKBENCH_PLUGIN_URL, folderToCopyName, JSON_SUFFIX);

		try {
			for(int i=0; i<filesToCopy.length; i++) {
				final String shortName = filesToCopy[i].getName();
				final String outname = copiedFolderPath + File.separator + shortName;
				final Path outPath = Paths.get(copiedFolderPath + File.separator + shortName);
				final Path inPath = Paths.get(filesToCopy[i].getPath());

				try {
					Files.copy(inPath, outPath);
					logger.debug("Copied file to "+outname);

				} catch(final FileAlreadyExistsException e) {
					//System.out.println("Not overwriting " + outname);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to copy file.", e);
		}
	}

	protected void copyWorldJsonFilesFromSubfolder(final String folderToCopyName) {
		String copiedFolderPath = getDirectoryMakeIfNeeded(installUrl, worldConfigFolderName + File.separator + folderToCopyName);
		final File[] filesToCopy = getFilesFromUrl(WORKBENCH_PLUGIN_URL, worldFolderName + "/" + folderToCopyName,
				JSON_SUFFIX);

		try {
			for(int i=0; i<filesToCopy.length; i++) {
				final String shortName = filesToCopy[i].getName();
				final String outname = copiedFolderPath + File.separator + shortName;
				final Path outPath = Paths.get(copiedFolderPath + File.separator + shortName);
				final Path inPath = Paths.get(filesToCopy[i].getPath());

				try {
					Files.copy(inPath, outPath);
					logger.debug("Copied file to "+outname);

				} catch(final FileAlreadyExistsException e) {
					//System.out.println("Not overwriting " + outname);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to copy file.", e);
		}
	}

	public File[] getKeepoutFiles() {
		return getFilesInWorldFolder(keepoutFolderName, JSON_SUFFIX);
	}

	public File[] getKeepinFiles() {
		return getFilesInWorldFolder(keepinFolderName, JSON_SUFFIX);
	}

	private File[] getFilesFromUrl(final String pluginURL, final String foldername, final String suffix) {
		URL folderUrl;
		File[] configureFiles = null;
		try {
			folderUrl = new URL( pluginURL + "/" + foldername );
			final File folder = new File(FileLocator.toFileURL(folderUrl).getPath());
			configureFiles = folder.listFiles();
		} catch (final Throwable t) {
			final IOException e =  new IOException("Failed to load config files from: " + foldername);
			e.initCause(t);
			e.printStackTrace();
		}
		return discardFilesWithWrongSuffix(configureFiles, suffix);
	}

	private File[] getFilesInWorldFolder(final String foldername, final String suffix) {
		File[] candidates = null;
		try {
			final File folder = new File(worldConfigDirPath + File.separator + foldername);
			candidates = folder.listFiles();

		} catch (final Throwable t) {
			final IOException e =  new IOException("Failed to load config files from: " + foldername);
			e.initCause(t);
			e.printStackTrace();
		}
		return discardFilesWithWrongSuffix(candidates, suffix);
	}

	private File[] getFilesInFolder(final String foldername, final String suffix) {
		File[] candidates = null;
		try {
			final File folder = new File(configDirPath + File.separator + foldername);
			candidates = folder.listFiles();

		} catch (final Throwable t) {
			final IOException e =  new IOException("Failed to load config files from: " + foldername);
			e.initCause(t);
			e.printStackTrace();
		}
		return discardFilesWithWrongSuffix(candidates, suffix);
	}

	private File[] discardFilesWithWrongSuffix(final File[] candidates, final String suffix) {
		final Vector<File> ret = new Vector<File>();
		for(final File f : candidates) {
			if(!f.isHidden() && f.getName().endsWith(suffix)) {
				ret.add(f);
			}
		}
		final File[] array = new File[ret.size()];
		return ret.toArray(array);
	}

	private File[] discardFilesWithWrongPrefix(final File[] candidates, final String prefix) {
		final Vector<File> ret = new Vector<File>();
		for(final File f : candidates) {
			if(!f.getName().startsWith(prefix)) {
				ret.add(f);
			}
		}
		final File[] array = new File[ret.size()];
		return ret.toArray(array);
	}

	private String getDirectoryMakeIfNeeded(final URL installURL, final String directoryPath) {
		String configDir;
		try {
			String osAppropriatePath;
			if(IS_WINDOWS) {
				configDir = FileLocator.toFileURL(installURL).getPath() + directoryPath;
				osAppropriatePath = configDir.substring(1).replace('/', '\\');		
			} else {
				osAppropriatePath = FileLocator.toFileURL(installURL).getPath() + directoryPath;
			}

			final File file = new File(osAppropriatePath);
			if (!file.exists()) {
				if (file.mkdir()) {
					//					System.out.println("*****Created directory: " + osAppropriatePath);
					return osAppropriatePath;
				} else {
					logger.error("Failed to create directory!");
					return null;
				}
			} else {
				return osAppropriatePath;
			}

		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getAstrobeeModelsPath(){
		return configDirPath + File.separator + modelsFolderName + File.separator + astrobeeModelFolderName;
	}

	public String getBookmarksPath() {
		return worldConfigDirPath + File.separator + bookmarksName;
	}

	public String getCameraConfigPath() {
		return configDirPath + File.separator + cameraConfigName;
	}

	public String getCameraPresetsListPath() {
		return configDirPath + File.separator + cameraPresetName;
	}

	public String getColoredBoxesPath() {
		return worldConfigDirPath + File.separator + coloredBoxesName;
	}

	public String getGlobalPref(){
		return configDirPath + File.separator + globalPref;
	}

	public String getHelpDocUrlString() {
		return configDirPath + File.separator + helpFolder + File.separator + helpDoc;
	}

	public String getGuestScienceConfigPath(){
		return configDirPath + File.separator + guestScienceConfigName;
	}

	public String getHandrailsPath() {
		return worldConfigDirPath + File.separator + handrailsName;
	}

	public String getHealthAndStatusConfigPath() {
		return configDirPath + File.separator + healthAndStatusConfigName;
	}

	public String getKeepoutsFolderPath() {
		return worldConfigDirPath + File.separator + keepoutFolderName;
	}

	public String getInertiaConfigPath() {
		return configDirPath + File.separator + inertiaConfigName;
	}

	public String getIssConfigurationPath() {
		return worldConfigDirPath + File.separator + issConfigName;
	}

	public String getIssModelsPath(){
		return worldConfigDirPath + File.separator + modelsFolderName;
	}
	
	public String getLegalDocUrlString() {
		return configDirPath + File.separator + helpFolder + File.separator + legalDoc;
	}

	public String getLightsCameraPath(){
		return worldConfigDirPath + File.separator + lightsCameraPropertiesName;
	}

	public String getOperatingLimitsConfigPath() {
		return configDirPath + File.separator + opLimitsConfigName;
	}

	public String getPayloadConfigPath() {
		return configDirPath + File.separator + planPayloadConfigName;
	}

	public String getTeleopCommandsConfigPath() {
		return configDirPath + File.separator + teleopCommandsConfigName;
	}

	public String getConfigDirPath() {
		return configDirPath;
	}
}
