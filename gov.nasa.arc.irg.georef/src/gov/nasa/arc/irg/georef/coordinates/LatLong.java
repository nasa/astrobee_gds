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
/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gov.nasa.arc.irg.georef.coordinates;

import gov.nasa.arc.irg.util.bean.AbstractModelObject;
import gov.nasa.util.StrUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lat Long</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLatitude <em>Latitude</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLongitude <em>Longitude</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LatLong extends AbstractModelObject {
    
    /**
     * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected int flags = 0;

    /**
     * generally this is for formatting numbers, which should be right aligned if they are in a table.
     */
    public static DecimalFormat s_decimalFormat = new DecimalFormat("##.##"); 
    {
     s_decimalFormat.setDecimalSeparatorAlwaysShown(true);
     s_decimalFormat.setMaximumFractionDigits(10);
     s_decimalFormat.setMinimumFractionDigits(1);
    }
    
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    
	/**
     * The default value of the '{@link #getLatitude() <em>Latitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLatitude()
     * @generated
     * @ordered
     */
	protected static final double LATITUDE_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getLatitude() <em>Latitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLatitude()
     * @generated
     * @ordered
     */
	protected double latitude = LATITUDE_EDEFAULT;

	/**
     * The flag representing whether the Latitude attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int LATITUDE_ESETFLAG = 1 << 0;

    /**
     * The default value of the '{@link #getLongitude() <em>Longitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLongitude()
     * @generated
     * @ordered
     */
	protected static final double LONGITUDE_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getLongitude() <em>Longitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLongitude()
     * @generated
     * @ordered
     */
	protected double longitude = LONGITUDE_EDEFAULT;

	/**
     * The flag representing whether the Longitude attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int LONGITUDE_ESETFLAG = 1 << 1;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public LatLong() {
        super();
    }


	/**
	 * Take a delimiter spaced listed of doubles and turn it into a list of LatLong
	 * @param initialValues
	 * @return List<LatLong> which may be empty but won't be null.
	 */
	public static List<LatLong> toLatLong(String initialValues) {
		double[] parsedValues = StrUtil.toDoubleArray(initialValues);
		List<LatLong> result = new ArrayList<LatLong>();
		if (parsedValues != null && (parsedValues.length % 2) == 0){
			for (int i = 0; i < parsedValues.length; i += 2){
				double lat = parsedValues[i];
				double lon = parsedValues[i+1];
				result.add(new LatLong(lat, lon));
			}
		}
		return result;
	}
	
	/**
	 * Convert an array of lat longs to a return separated String of lat long content
	 * ie 
	 * 3.0 4.0
	 * 30.0 40.0
	 * @param latLongList
	 * @return empty string or properly filled out string
	 */
	public static String toSimpleString(List<LatLong> latLongList){
		if (latLongList == null || latLongList.isEmpty()){
			return "";
		}
		StringBuffer result = new StringBuffer();
		for (LatLong ll : latLongList){
			result.append(ll.toString());
			result.append("\n");
		}
		return result.toString();
	}
	
	/**
	 * Construct a latlong
	 * @param latitude
	 * @param longitude
	 */
	public LatLong(double latitude, double longitude){
		super();
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	/**
     * Returns the value of the '<em><b>Latitude</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Latitude</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Latitude</em>' attribute.
     * @see #isSetLatitude()
     * @see #unsetLatitude()
     * @see #setLatitude(double)
     * @generated
     */
	public double getLatitude() {
        return latitude;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLatitude <em>Latitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Latitude</em>' attribute.
     * @see #isSetLatitude()
     * @see #unsetLatitude()
     * @see #getLatitude()
     * @generated
     */
	public void setLatitude(double newLatitude) {
        double oldLatitude = latitude;
        latitude = newLatitude;
        flags |= LATITUDE_ESETFLAG;
        firePropertyChange(LATITUDE, oldLatitude, newLatitude);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLatitude <em>Latitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetLatitude()
     * @see #getLatitude()
     * @see #setLatitude(double)
     * @generated
     */
	public void unsetLatitude() {
        double oldLatitude = latitude;
        latitude = LATITUDE_EDEFAULT;
        flags &= ~LATITUDE_ESETFLAG;
        //TODO this does not handle things not being set
        firePropertyChange(LATITUDE, oldLatitude, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLatitude <em>Latitude</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Latitude</em>' attribute is set.
     * @see #unsetLatitude()
     * @see #getLatitude()
     * @see #setLatitude(double)
     * @generated
     */
	public boolean isSetLatitude() {
        return (flags & LATITUDE_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Longitude</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Longitude</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Longitude</em>' attribute.
     * @see #isSetLongitude()
     * @see #unsetLongitude()
     * @see #setLongitude(double)
     * @generated
     */
	public double getLongitude() {
        return longitude;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLongitude <em>Longitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Longitude</em>' attribute.
     * @see #isSetLongitude()
     * @see #unsetLongitude()
     * @see #getLongitude()
     * @generated
     */
	public void setLongitude(double newLongitude) {
        double oldLongitude = longitude;
        longitude = newLongitude;
        flags |= LONGITUDE_ESETFLAG;
        firePropertyChange(LONGITUDE, oldLongitude, newLongitude);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLongitude <em>Longitude</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetLongitude()
     * @see #getLongitude()
     * @see #setLongitude(double)
     * @generated
     */
	public void unsetLongitude() {
        double oldLongitude = longitude;
        longitude = LONGITUDE_EDEFAULT;
        flags &= ~LONGITUDE_ESETFLAG;
        firePropertyChange(LONGITUDE, oldLongitude, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.LatLong#getLongitude <em>Longitude</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Longitude</em>' attribute is set.
     * @see #unsetLongitude()
     * @see #getLongitude()
     * @see #setLongitude(double)
     * @generated
     */
	public boolean isSetLongitude() {
        return (flags & LONGITUDE_ESETFLAG) != 0;
    }


	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@Override
	public String toString() {

		StringBuffer result = new StringBuffer();
		if (isSetLatitude()) {
		    result.append( s_decimalFormat.format(getLatitude()));
			result.append(" ");
		}
		if (isSetLongitude()){
			result.append(s_decimalFormat.format(getLongitude()));
		}
		return result.toString();
	}
	
	/**
	 * Populate values based on input values.
	 * @param other
	 */
	public void fillValues(LatLong other){
		if (other == null || other.equals(this)){
			return;
		}
		if (other.isSetLatitude()){
			setLatitude(other.getLatitude());
		}
		if (other.isSetLongitude()){
			setLongitude(other.getLongitude());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
    public LatLong clone(){
		LatLong result = new LatLong();
		result.fillValues(this);
		return result;
	}

} // LatLong
