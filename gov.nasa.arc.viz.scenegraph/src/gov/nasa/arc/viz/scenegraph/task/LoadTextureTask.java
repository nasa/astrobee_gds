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
package gov.nasa.arc.viz.scenegraph.task;

import gov.nasa.util.ZipUtil;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.Callable;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.util.TextureManager;
import com.ardor3d.util.resource.URLResourceSource;

/**
 * Load a texture
 * @author mallan
 */
public class LoadTextureTask implements Callable<Texture> {
    final URL texUrl;
    URI sourceURI = null; // if the texture URL differs from the source for the image ...
    final Texture.MinificationFilter minFilter;
    final Texture.MagnificationFilter magFilter;
    final Texture.WrapMode wrapMode;
    final TextureStoreFormat imgFormat;
    final float anisotropy;
    int sleeptime = 0;

    static final TextureStoreFormat defaultFormat = TextureStoreFormat.GuessCompressedFormat;

    /**
     * 
     * @param texUrl
     * @param minFilter
     */
    public LoadTextureTask(URL texUrl, 
            Texture.MinificationFilter minFilter) {
        this(texUrl, minFilter, Texture.MagnificationFilter.Bilinear,
                Texture.WrapMode.EdgeClamp, defaultFormat, 0, 0);
    }

    /**
     * 
     * @param texUrl
     * @param minFilter
     * @param wrapMode
     * @param anisotropy
     */
    public LoadTextureTask(URL texUrl, 
            Texture.MinificationFilter minFilter,
            Texture.WrapMode wrapMode, float anisotropy) {
        this(texUrl, minFilter, Texture.MagnificationFilter.Bilinear,
                wrapMode, defaultFormat, anisotropy, 0);
    }

    public LoadTextureTask(URL texUrl, 
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter,
            Texture.WrapMode wrapMode,
            TextureStoreFormat imgFormat,
            float anisotropy) {
        this(texUrl, minFilter, magFilter, wrapMode, imgFormat, anisotropy, 0);
    }

    /**
     * 
     * @param texUrl
     * @param minFilter
     * @param magFilter
     * @param wrapMode
     * @param imgFormat
     * @param anisotropy
     */
    public LoadTextureTask(URL texUrl, 
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter,
            Texture.WrapMode wrapMode,
            TextureStoreFormat imgFormat,
            float anisotropy,
            int sleeptime) {
        this.texUrl     = texUrl;
        this.minFilter  = minFilter;
        this.magFilter  = magFilter;
        this.wrapMode   = wrapMode;
        this.imgFormat  = imgFormat;
        this.anisotropy = anisotropy;
        this.sleeptime  = sleeptime;
    }
    
    /**
     * 
     * @param texUrl
     * @param sourceURI the URI of the (zipped) file where the texture is coming from
     * @param minFilter
     * @param magFilter
     * @param wrapMode
     * @param imgFormat
     * @param anisotropy
     */
    public LoadTextureTask(URL texUrl, 
    		URI sourceURI,
            Texture.MinificationFilter minFilter,
            Texture.MagnificationFilter magFilter,
            Texture.WrapMode wrapMode,
            TextureStoreFormat imgFormat,
            float anisotropy,
            int sleeptime) {
        this.texUrl     = texUrl;
        this.minFilter  = minFilter;
        this.magFilter  = magFilter;
        this.wrapMode   = wrapMode;
        this.imgFormat  = imgFormat;
        this.anisotropy = anisotropy;
        this.sleeptime  = sleeptime;
        this.sourceURI = sourceURI;
    }

    /*
     * returns true if we have or if we retrieved the file.
     */
    protected boolean unzipTextureFromSource() {
		File outFile = new File(texUrl.getFile());
		if (!outFile.canRead()){
			if (sourceURI != null){
				// we don't already have it ... so get it!
				return  ZipUtil.getFileFromZip(sourceURI, outFile);
			} else {
				return false;
			}
		}
		return true;
    }

    public Texture call() throws TaskException {
        Texture retVal = null;
        try {
        	retVal = TextureManager.load(new URLResourceSource(texUrl), minFilter, imgFormat, false);
	
        	if( retVal != null && retVal.getImage() != null ) {
        		if (retVal.equals(TextureState.getDefaultTexture())){
        			// see if it has a sourceURI
        			if (unzipTextureFromSource()){
        				retVal = TextureManager.load(new URLResourceSource(texUrl), minFilter, imgFormat, false);
        			}
        			if( retVal != null && retVal.getImage() != null ) {
        				retVal.setMagnificationFilter(magFilter);
        				retVal.setWrap(wrapMode);
        				retVal.setAnisotropicFilterPercent(anisotropy);
        			}
        		}
        	}
        	if(!( retVal != null && retVal.getImage() != null )) {
	            if(retVal == null) {
	                throw new TaskException("LoadTextureTask: TextureManager.load() returned a null texture");
	            }
	            
	            else if(retVal.getImage() == null) {
	                throw new TaskException("LoadTextureTask: TextureManager loaded a null image");
	            }
	            else {
	                throw new TaskException("LoadTextureTask: this should be unreachable");
	            }
        	}
            
            
            if(sleeptime > 0) {
                Thread.sleep(sleeptime);
            }
        }
        catch(TaskException te) {
            throw te;
        }
        catch(Throwable t) {
            throw new TaskException("Error loading texture: "+t.getMessage(), t);
        }
        return retVal;
    }
}
