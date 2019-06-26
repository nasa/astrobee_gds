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
package gov.nasa.arc.verve.common.util;

import java.util.HashMap;
import java.util.Set;

import com.ardor3d.light.DirectionalLight;
import com.ardor3d.light.Light;
import com.ardor3d.light.PointLight;
import com.ardor3d.light.SpotLight;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyColorRGBA;

/**
 * Hacked over from Java3d
 */
public class LightManager {
    protected static LightManager s_instance = null;
    public static LightManager instance() {
        if( s_instance == null ) {
            s_instance = new LightManager();
        }
        return s_instance;
    }

    public static final String SUN = "sun";

    protected HashMap<String,Light>	m_lights = new HashMap<String,Light>();

    protected boolean   m_sunColorEnable = true;
    protected ColorRGBA m_sunBase = new ColorRGBA( 1.4f, 1.5f, 1.6f, 1.0f); // base color of the sun light
    protected ColorRGBA m_sunScale = new ColorRGBA( 0.0f, 0.8f, 1.8f, 1.0f);
    protected ColorRGBA m_sunColor = new ColorRGBA( m_sunBase );
    protected float     m_sunBright= 1.0f;

    private Vector3 tV0 = new Vector3();
    //private Vector3 tV1 = new Vector3();
    private ColorRGBA	 tC0 = new ColorRGBA();

    /**
     * register a light with the LightManager. 
     * @param lightName
     * @param light
     */
    public void addLight(String lightName, Light light) {
        m_lights.put(lightName, light);
    }

    /**
     * return Light associated with lightName 
     * @param lightName
     * @return
     */
    public Light getLight(String lightName) {
        Light light = m_lights.get(lightName);
        return light;
    }

    public Set<String> getLightNames() {
        return m_lights.keySet();
    }

    /**
     * If lightName exists AND is a DirectionalLight, return it. Else return null. 
     * @param lightName
     * @return 
     */
    public DirectionalLight getDirectionalLight(String lightName) {
        Light light = m_lights.get(lightName);
        if(light != null && light instanceof DirectionalLight) {
            return (DirectionalLight)light;
        }
        return null;
    }

    /**
     * modify color of sun light based on vector to simulate sunset
     */
    public void setSunColorModification(boolean state) {
        m_sunColorEnable = state;
    }
    
    public boolean isSunColorModification() {
        return m_sunColorEnable;
    }
    
    /** 
     * get the sun
     */
    public DirectionalLight getSun() {
        DirectionalLight sun = getDirectionalLight(SUN);
        return sun;
    }

    /**
     * If lightName exists AND is a SpotLight, return it. else,  return null. 
     * @param lightName
     * @return 
     */
    public SpotLight getSpotLight(String lightName) {
        Light light = m_lights.get(lightName);
        if(light != null && light instanceof SpotLight) {
            return (SpotLight)light;
        }
        return null;
    }

    /**
     * If lightName exists AND is a PointLight, return it. Else return null. 
     * @param lightName
     * @return 
     */
    public PointLight getPointLight(String lightName) {
        Light light	= m_lights.get(lightName);
        if(light != null && light instanceof PointLight) {
            return (PointLight)light;
        }
        return null;
    }

    public void setSunVector(Vector3 newVector) {
        updateSunVector(newVector);
    }
    /**
     * Convenience functions for a "sun" light. A DirectionalLight
     * named "sun" must be registered in the LightManager for these
     * to be useful. 
     * @param newVector
     */
    public void updateSunVector(Vector3 newVector) {
        DirectionalLight sun = getDirectionalLight(SUN);
        if(sun != null) {
            sun.setDirection(newVector);

            if(m_sunColorEnable) {
                // change color of the sun based on angle
                tV0.set(newVector);
                tV0.normalizeLocal();
                float a = (float) Math.abs(tV0.dot(Vector3.UNIT_Z));
                float s = 1-a;
                float r = m_sunBase.getRed()  - s*m_sunScale.getRed();
                float g = m_sunBase.getGreen()- s*m_sunScale.getGreen();
                float b = m_sunBase.getBlue() - s*m_sunScale.getBlue();
                //System.out.println(String.format("%1.2f %1.2f %1.2f", r, g, b));
                tC0.set(r,g,b,1);
                tC0.clampLocal();
                tC0.addLocal(0.12f, 0.1f, 0, 0);
                m_sunColor.set(tC0);
                tC0.multiplyLocal(m_sunBright);
                sun.setDiffuse(tC0);
            }
            else {
                tC0.set(m_sunBase);
                tC0.multiplyLocal(m_sunBright);
                sun.setDiffuse(tC0);
            }
        }
    }

    /**
     * @param setVec storage for direction of "sun" light
     * @return setVec
     */
    public Vector3 getSunVector(Vector3 setVec) {
        DirectionalLight sun = getDirectionalLight(SUN);
        if(sun != null) {
            setVec.set(sun.getDirection());
        }
        return setVec;
    }

    public ReadOnlyColorRGBA getSunColor() {
        return m_sunColor;
    }

    public ReadOnlyColorRGBA getSunColor(ColorRGBA set) {
        return set.set(m_sunColor);
    }

    public float getSunBrightness() {
        return m_sunBright;
    }

    /** 
     * scaling factor for sun color
     * @param brightness default is 1
     */
    public void setSunBrightness(float brightness) {
        m_sunBright = brightness;
        updateSunVector(getSunVector(new Vector3()));
    }
}
