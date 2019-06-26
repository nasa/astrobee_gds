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
package gov.nasa.arc.irg.util;


/**
 * Simple pairing of name to value, used for tree / table displays
 * @author tecohen
 *
 */
public class NameValue {
    public String name;
    public Object value;
    public boolean format;
    
    public NameValue(String name, Object value, boolean da){
        this.name = name;
        this.value = value;
        this.format = da;
    }
    
    public NameValue(String name, Object value){
    	this.name = name;
    	this.value = value;
    	this.format = false;
    }
	
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(name);
		result.append("\t" + value.toString());
		return result.toString();
	}
}
