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
package gov.nasa.arc.viz.io.importer;

import java.util.Map;

import com.ardor3d.image.Texture;
import com.ardor3d.image.TextureStoreFormat;

/**
 * Holds default values to be used during import; e.g. texure min filter.
 * @author mallan
 *
 */
public class ModelImporterDefaults {
    public Texture.MagnificationFilter magFilter = Texture.MagnificationFilter.Bilinear;
    public Texture.MinificationFilter minFilter = Texture.MinificationFilter.Trilinear;
    public TextureStoreFormat imageFormat = TextureStoreFormat.GuessCompressedFormat;

    /**
     * Override default values from parameter map
     * Parameters supported:<ul>
     * <li>Texture.MinificationFilter (String)
     * <li>Texture.MagnificationFilter (String)
     * <li>Image.Format (String) 
     * </ul>
     * @param params user provided parameters
     * @see com.ardor3d.image.Texture.MinificationFilter
     * @see com.ardor3d.image.Texture.MagnificationFilter
     * @see com.ardor3d.image.Image.Format
     */
    public void processParams(Map<String,Object> params) {
        String tmpStr;
        tmpStr = (String)params.get("Texture.MinificationFilter");
        if(tmpStr != null) {
            for(Texture.MinificationFilter filter : Texture.MinificationFilter.values()) {
                if(filter.toString().equals(tmpStr)) {
                    minFilter = filter;
                    break;
                }
            }
        }
        tmpStr = (String)params.get("Texture.MagnificationFilter");
        if(tmpStr != null) {
            for(Texture.MagnificationFilter filter : Texture.MagnificationFilter.values()) {
                if(filter.toString().equals(tmpStr)) {
                    magFilter = filter;
                    break;
                }
            }
        }
        tmpStr = (String)params.get("TextureStoreFormat");
        if(tmpStr != null) {
            for(TextureStoreFormat format : TextureStoreFormat.values()) {
                if(format.toString().equals(tmpStr)) {
                    imageFormat = format;
                    break;
                }
            }
        }
    }
}
