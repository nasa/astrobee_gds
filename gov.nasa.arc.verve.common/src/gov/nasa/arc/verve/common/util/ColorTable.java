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

/**
 * Utility color table. Returns RGB float triple
 */
public class ColorTable {
    private final int    m_colorTableSize = 101;
    private final float[][][]  m_colorTable = new float[m_colorTableSize][m_colorTableSize][3];
    
    public ColorTable() {
        createColorTable();
    }
    
    public ColorTable(int low, int high) {
        createColorTable(low, high);
    }
    
    /** default color table */
    public void createColorTable() {
    	createColorTable(0, m_colorTableSize);
    }
    
    /** restricted range color table */
    public void createColorTable(int low, int high) {
        float hue,sat,val;
        int range = high-low;
        int hr;
        for(int h = 0; h < m_colorTableSize; h++) {
            // low = red (non traversable)
        	// high = green (traversable)
        	hr = h-low;
        	if(hr < 0) {
        		hue = 335;
        		sat = 0.8f;
        	}
        	else if(hr > range) {
        		hue = 150;
        		sat = 0.2f;
        	}
        	else {
        		hue = ((float)hr/(float)range) * 120f;
        		sat = 0.35f;
        	}
            
            for(int s = 0; s < m_colorTableSize; s++) {
                val = (float)s/(float)(m_colorTableSize+10);
                ColorUtil.convertHSVtoRGB(hue, sat, val, m_colorTable[h][s]);
            }
        }
    }
    
    static final float[] badIndex = new float[] { 1,0,1 };
    /** 
     * 
     * @param hue
     * @return
     */
    public float[] getColor(int hue, int val) {
        if(hue >= 0 && hue < m_colorTableSize && 
        	val >= 0 && val < m_colorTableSize) {
            return m_colorTable[hue][val];
        }
        return badIndex;
    }

}
