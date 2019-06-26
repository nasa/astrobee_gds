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
package gov.nasa.arc.verve.robot.scenegraph.shape.concepts;

import gov.nasa.arc.verve.ardor3d.scenegraph.shape.LathedCylinder;
import gov.nasa.arc.verve.ardor3d.scenegraph.shape.TexRing;
import gov.nasa.arc.verve.common.ardor3d.text.BMText;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.type.ReadOnlyColorRGBA;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.math.type.ReadOnlyVector3;

public interface WaypointIconStatusProvider {
    
	public void              setBaseColor(ReadOnlyColorRGBA clr);
	public ReadOnlyColorRGBA getBaseColor(ColorRGBA retVal);
	public ReadOnlyColorRGBA getColorFromStatus(int status, ColorRGBA clr);
    public String            getStringFromStatus(int status);
    public boolean           doTrackHeight(int status);
	
    public void              setVerbosity(int level);
    public int               getVerbosity();
    
    public boolean           showTextBackground();
	public boolean           showTextFromStatus(int status);
	public boolean           showToleranceFromStatus(int status);
    
	public LathedCylinder  createCenterCylinder(ReadOnlyVector3 off);
	public TexRing         createToleranceRing(ReadOnlyVector3 off);
	public OffsetQuad      createDirectionMarker(ReadOnlyVector3 off);

    BMText.Align           getTextAlign();
    BMText.Justify         getTextJustify();
    void                   setupText(BMText text);
    
	public ReadOnlyVector2 getTextPivotOffset(float offset);
    public ReadOnlyVector3 getTextXfmOffset(float offset);
    public ReadOnlyVector3 getCenterXfmOffset(float offset);
    public float           getToleranceThickness();

}
