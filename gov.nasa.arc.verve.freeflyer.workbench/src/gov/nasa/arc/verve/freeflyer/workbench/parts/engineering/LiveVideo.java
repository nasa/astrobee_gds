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
package gov.nasa.arc.verve.freeflyer.workbench.parts.engineering;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

import com.sun.jna.NativeLibrary;

import gov.nasa.arc.irg.plan.ui.io.ConfigFileWrangler;
import uk.co.caprica.vlcj.binding.LibC;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.version.LibVlcVersion;

public class LiveVideo {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	private final String VLCJ_PLUGIN = "org.vlcj";
	private String rawPath;
	
	protected Properties PROPERTIES;
	protected final String STREAM_URL = "vlc.stream.url";
	

	
	public void configureProperties(){
		PROPERTIES = new Properties();
		try {
			PROPERTIES.load(IOUtils.toBufferedInputStream(new FileInputStream(ConfigFileWrangler.getInstance().getGlobalPref())));
		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		//might be ugly but gets the path
		try {
			rawPath = new File(FileLocator.toFileURL(Platform.getBundle(VLCJ_PLUGIN).getEntry("")).toURI()).getAbsolutePath();//Platform.getBundle(VLCJ_PLUGIN).getLocation().replace("reference:file:", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		setupVLC(rawPath);
	}
	
	private void setupVLC(String pluginLocation){
		String LIB_VLC_PATH = "";
	    String PLUGIN_VLC_PATH = "";
		final String os = System.getProperty("os.name");
		if(os.contains("Mac")){
			pluginLocation += "/products/MacOS";
			LIB_VLC_PATH = pluginLocation +"/lib";
			PLUGIN_VLC_PATH = pluginLocation+"/plugins";
			System.out.println("mac vlc path: "+PLUGIN_VLC_PATH);
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), LIB_VLC_PATH);
			if (LibVlcVersion.getVersion().atLeast(LibVlcVersion.LIBVLC_220)) {
	            LibC.INSTANCE.setenv("VLC_PLUGIN_PATH", String.format("%s/../plugins", LIB_VLC_PATH), 1);
	        }
	        System.setProperty("VLC_PLUGIN_PATH",PLUGIN_VLC_PATH);
			return;
		}else if(os.contains("Windows")){
			pluginLocation += "/products/Win";
			LIB_VLC_PATH = pluginLocation +"/lib/vlc";
			PLUGIN_VLC_PATH = LIB_VLC_PATH+"/plugins";
			System.out.println(PLUGIN_VLC_PATH);
		}
		
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),LIB_VLC_PATH);
		System.setProperty("VLC_PLUGIN_PATH",PLUGIN_VLC_PATH);
	}
}
