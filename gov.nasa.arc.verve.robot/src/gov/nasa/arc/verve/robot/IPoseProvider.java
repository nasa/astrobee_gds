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
package gov.nasa.arc.verve.robot;

import gov.nasa.arc.verve.robot.exception.TelemetryException;

import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

public interface IPoseProvider {

    void connectTelemetry() throws TelemetryException;
    void disconnectTelemetry() throws TelemetryException;
    
    /**
     * set of valid xyz source values
     */
	String[] getXyzSourceValues();
	/**
	 * set of valid rotation source values
	 */
	String[] getRotSourceValues();
	
	/**
	 * Set the xyz data source. No-op if name is invalid
     * @return can't return anything, otherwise not recognized by databinding as a valid setter. Ugh
	 */
	void    setXyzSource(String name);
	String  getXyzSource();
	
	/**
	 * Set the rotation data source. No-op if name is invalid
	 * @return can't return anything, otherwise not recognized by databinding as a valid setter. Ugh
	 */
	void    setRotSource(String name);
	String  getRotSource();
	
	
	void    setFixedZOffset(double fixedZ);
	double  getFixedZOffset();
	void    setFixedZ(boolean use);
	boolean isFixedZ();
	
	ReadOnlyVector3 getXyz();
	Vector3 getXyz(Vector3 ret);
	
	/**
	 * calculate transform using the
	 * most recent telemetry.
	 */
	public ReadOnlyTransform calculateTransform();
	
	/**
	 * calculateTransform() should be called at the beginning of 
	 * every frame, then all subsequent callers should use this 
	 * method in order to avoid unnecessary re-calculation
	 * @return the stored transform 
	 */
	public ReadOnlyTransform getTransform();

}
