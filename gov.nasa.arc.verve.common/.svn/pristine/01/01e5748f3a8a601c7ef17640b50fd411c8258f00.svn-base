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
package gov.nasa.arc.verve.common.interest;

/**
 * 
 *
 */
public interface InterestPointProvider extends Comparable<InterestPointProvider > {
    
    public String   getInterestPointName();
    public String[] getInterestPointModes();
        
    public boolean isInterestPointEnabled();
    
    /** 
     * A listener should only be able to subscribe for one mode at a time.
     * If null is passed for the mode, or if the mode is not understood by the
     * provider, a default mode will be selected.
     */
    public void addInterestPointListener(InterestPointListener listener, String mode);
    
    /**
     * 
     * @param listener
     */
    public void removeInterestPointListener(InterestPointListener listener);
    
    /**
     * 
     */
    public void updateInterestPointListeners();
}
