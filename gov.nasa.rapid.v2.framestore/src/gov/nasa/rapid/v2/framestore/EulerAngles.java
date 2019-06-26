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
package gov.nasa.rapid.v2.framestore;

/**
 * EulerAngles maintains a set of 3 Euler angles and their type.
 *  
 * @author Lorenzo Flueckiger
 */
public class EulerAngles implements ReadOnlyEulerAngles {

	protected double m_angles[] = new double[3];
	protected Type m_type;

	// Not sure if a default EulerAngles should be allowed to be created...
	EulerAngles() {
		m_angles[0] = 0.0;
		m_angles[1] = 0.0;
		m_angles[2] = 0.0;
		m_type = Type.ZYXr;
	}

	public static EulerAngles fromDegrees(Type type, double angle1, double angle2, double angle3) {
		double DEG2RAD = Math.PI/180.0;
		return new EulerAngles(type, angle1*DEG2RAD, angle2*DEG2RAD, angle3*DEG2RAD);
	}
	
	public EulerAngles(ReadOnlyEulerAngles angles) {
		set(angles.getType(), angles.getAngle1(), angles.getAngle2(),angles.getAngle3());
	}

	public EulerAngles(Type type) {
		m_angles[0] = 0.0;
		m_angles[1] = 0.0;
		m_angles[2] = 0.0;
		m_type = type;
	}

	public EulerAngles(Type type, double angle1, double angle2, double angle3) {
		setAngle1(angle1);
		setAngle2(angle2);
		setAngle3(angle3);
		m_type = type;
	}

	public EulerAngles set(Type type, double angle1, double angle2, double angle3) {
		setAngle1(angle1);
		setAngle2(angle2);
		setAngle3(angle3);
		m_type = type;
		return this;
	}

	@Override
	public double getAngle1() {
		return m_angles[0];
	}

	@Override
	public double getAngle2() {
		return m_angles[1];
	}

	@Override
	public double getAngle3() {
		return m_angles[2];
	}

	@Override
	public Type getType() {
		return m_type;
	}

	public void setAngle1(double angle1) {
		if(-Math.PI > angle1 || angle1 > Math.PI ) {
			throw new IllegalArgumentException("angle1 must be between -180 and 180 degrees");
		}
		m_angles[0] = angle1;
	}

	public void setAngle2(double angle2) {
		if(-Math.PI/2.0 > angle2 || angle2 > Math.PI/2.0) {
			throw new IllegalArgumentException("angle2 must be between -90 and 90 degrees");
		}
		m_angles[1] = angle2;
	}

	public void setAngle3(double angle3) {
		if(-Math.PI > angle3 || angle3 > Math.PI) {
			throw new IllegalArgumentException("angle3 must be between -180 and 180 degrees");
		}
		m_angles[2] = angle3;
	}

	public void set(double angle1, double angle2, double angle3) {
		setAngle1(angle1);
		setAngle2(angle2);
		setAngle3(angle3);
	}

	public void set(ReadOnlyEulerAngles angles) {
		setAngle1(angles.getAngle1());
		setAngle2(angles.getAngle2());
		setAngle3(angles.getAngle3());
		m_type = angles.getType();
	}

	@SuppressWarnings("unused")
	private void set(Type type) {
		// The type of an Euler angle cannot be changed
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof ReadOnlyEulerAngles)) {
			return false;
		}
		final ReadOnlyEulerAngles comp = (ReadOnlyEulerAngles)o;
		if ( this.m_type != comp.getType() ) { return false; }
		if ( !ConvertUtils.anglesEqual(this.m_angles[0], comp.getAngle1()) ) { return false; }
		if ( !ConvertUtils.anglesEqual(this.m_angles[1], comp.getAngle2()) ) { return false; }
		if ( !ConvertUtils.anglesEqual(this.m_angles[2], comp.getAngle3()) ) { return false; }
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_type == null) ? 0 : m_type.hashCode());
		result = prime * result + Double.valueOf(m_angles[0]).hashCode();
		result = prime * result + Double.valueOf(m_angles[1]).hashCode();
		result = prime * result + Double.valueOf(m_angles[2]).hashCode();
		return result;
	}

	@Override
	public double[] toArray(double[] store) {
		if ( store == null ) {
			store = new double[3];
		}
		store[0] = m_angles[0];
		store[1] = m_angles[1];
		store[2] = m_angles[2];
		return null;
	}

	@Override
	public String toString() {
		return "EulerAngles [a1=" + getAngle1() + ", a2=" + getAngle2() + ", a3=" + getAngle3() + ", " + getType() + "]";
	}

	@Override
	public EulerAngles clone() {
		return new EulerAngles(this);
	}

}
