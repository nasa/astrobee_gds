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
package gov.nasa.arc.verve.robot.rapid.parts.maps;

public class TileKey {
    public final int x;
    public final int y;
    
    public TileKey(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int hashCode() {
        int retVal = 1;
        final int p = 101;
        retVal = retVal*p + x;
        retVal = retVal*p + y;
        return retVal;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TileKey)) {
            return false;
        }
        final TileKey that = (TileKey)obj;
        if ((this.x == that.x) && (this.y == that.y))
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "Tile["+x+","+y+"]";
    }
}
