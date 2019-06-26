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
package gov.nasa.rapid.v2.e4.message.helpers;

import com.google.common.base.Objects;

public class TwoStringKey {
    protected final String string1;
    protected final String string2;
    
    public TwoStringKey(String str1, String str2) {
        this.string1 = str1;
        this.string2 = str2;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(string1, string2);
    }
    
    @Override
    public String toString() {
        return string1+"::"+string2;
    }
    
    @Override 
    public boolean equals(Object obj) {
        if(obj instanceof TwoStringKey) {
            TwoStringKey other = (TwoStringKey)obj;
            if(other.string1.equals(string1) && other.string2.equals(string2)) {
                return true;
            }
        }
        return false;
    }

}
