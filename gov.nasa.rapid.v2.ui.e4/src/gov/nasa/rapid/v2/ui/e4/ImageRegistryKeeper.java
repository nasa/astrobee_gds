package gov.nasa.rapid.v2.ui.e4;

import java.net.URL;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class ImageRegistryKeeper {
	final private static Logger logger = Logger.getLogger(ImageRegistryKeeper.class);
	protected static ImageRegistryKeeper s_instance;
	protected ImageRegistry reg;
	
	/**
	 * FYI all the images are null ... there is no icons folder in this plugin (oops)
	 */
	public static ImageRegistryKeeper getInstance() {
		if(s_instance == null) {
			logger.error("ImageRegistryKeeper was not instantiated");
		}
		return s_instance;
	}
	
    @Inject
    public ImageRegistryKeeper() {
    	s_instance = this;
    	s_instance.initialize();
    }
	
	protected void initialize() {
		/*
	     * There must be an SWT Display created in the current 
	     * thread before calling this method.
	     */
		
		// alternatively, look at e4.tools ResourcePool
		reg = JFaceResources.getImageRegistry();
		
        reg.put("refresh",				getImageDescriptorFromPath("icons/arrow_refresh.png"));
        reg.put("camera_mount",       	getImageDescriptorFromPath("icons/camera_mount.png"));
        reg.put("camera_pan_tilt",      getImageDescriptorFromPath("icons/camera_pan_tilt.png"));
        reg.put("camera_unmount",       getImageDescriptorFromPath("icons/camera_unmount.png"));
        reg.put("pause",       			getImageDescriptorFromPath("icons/pause.png"));
        reg.put("pin-big",       		getImageDescriptorFromPath("icons/pin-big.png"));
        reg.put("pin",       			getImageDescriptorFromPath("icons/pin.png"));
        reg.put("play",       			getImageDescriptorFromPath("icons/play.png"));
        reg.put("rapid",       			getImageDescriptorFromPath("icons/rapid-gears-16"));
	}

	/**
	 * Return the image descriptor created from the path found in the bundle 
	 * @param imageName relative to the plugin path
	 * @return the imagedescriptor, null if none found
	 */
	protected ImageDescriptor getImageDescriptorFromPath( String imageName) {
		Bundle b = FrameworkUtil.getBundle(getClass());  
		URL url = FileLocator.find(b, new Path(imageName), null);
		return ImageDescriptor.createFromURL(url);
	}

	/**
	 * Shortcut for getting image descriptor.
	 * @param key
	 * @return
	 */
	public ImageDescriptor getImageDescriptorFromRegistry(String key){
		return reg.getDescriptor(key);
	}

	/**
	 * Gets the image via the image registry
	 * @param key
	 * @return null if not found.
	 */
	public Image getImageFromRegistry(String key){
		return reg.get(key);
	}

}
