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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Ellipsoid</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEccentricitySquared <em>Eccentricity Squared</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEquatorialRadius <em>Equatorial Radius</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Ellipsoid extends AbstractModelObject {
	/**
     * A set of bit flags representing the values of boolean attributes and whether unsettable features have been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected int flags = 0;
    
    public static final String NAME = "name";
    public static final String ECCENTRICITY_SQUARED = "eccentricitySquared";
    public static final String EQUATORIAL_RADIUS = "equatorialRadius";

    /**
     * The default value of the '{@link #getEccentricitySquared() <em>Eccentricity Squared</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEccentricitySquared()
     * @generated
     * @ordered
     */
	protected static final double ECCENTRICITY_SQUARED_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getEccentricitySquared() <em>Eccentricity Squared</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEccentricitySquared()
     * @generated
     * @ordered
     */
	protected double eccentricitySquared = ECCENTRICITY_SQUARED_EDEFAULT;

	/**
     * The flag representing whether the Eccentricity Squared attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int ECCENTRICITY_SQUARED_ESETFLAG = 1 << 0;

    /**
     * The default value of the '{@link #getEquatorialRadius() <em>Equatorial Radius</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEquatorialRadius()
     * @generated
     * @ordered
     */
	protected static final double EQUATORIAL_RADIUS_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getEquatorialRadius() <em>Equatorial Radius</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEquatorialRadius()
     * @generated
     * @ordered
     */
	protected double equatorialRadius = EQUATORIAL_RADIUS_EDEFAULT;

	/**
     * The flag representing whether the Equatorial Radius attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int EQUATORIAL_RADIUS_ESETFLAG = 1 << 1;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected static final String NAME_EDEFAULT = null;

	/**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected String name = NAME_EDEFAULT;

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Ellipsoid() {
        super();
    }

	/**
     * Returns the value of the '<em><b>Eccentricity Squared</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Eccentricity Squared</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Eccentricity Squared</em>' attribute.
     * @see #isSetEccentricitySquared()
     * @see #unsetEccentricitySquared()
     * @see #setEccentricitySquared(double)
     * @generated
     */
	public double getEccentricitySquared() {
        return eccentricitySquared;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEccentricitySquared <em>Eccentricity Squared</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Eccentricity Squared</em>' attribute.
     * @see #isSetEccentricitySquared()
     * @see #unsetEccentricitySquared()
     * @see #getEccentricitySquared()
     * @generated
     */
	public void setEccentricitySquared(double newEccentricitySquared) {
        double oldEccentricitySquared = eccentricitySquared;
        eccentricitySquared = newEccentricitySquared;
        flags |= ECCENTRICITY_SQUARED_ESETFLAG;
        firePropertyChange(ECCENTRICITY_SQUARED, oldEccentricitySquared, newEccentricitySquared);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEccentricitySquared <em>Eccentricity Squared</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetEccentricitySquared()
     * @see #getEccentricitySquared()
     * @see #setEccentricitySquared(double)
     * @generated
     */
	public void unsetEccentricitySquared() {
        double oldEccentricitySquared = eccentricitySquared;
        eccentricitySquared = ECCENTRICITY_SQUARED_EDEFAULT;
        flags &= ~ECCENTRICITY_SQUARED_ESETFLAG;
        firePropertyChange(ECCENTRICITY_SQUARED, oldEccentricitySquared, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEccentricitySquared <em>Eccentricity Squared</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Eccentricity Squared</em>' attribute is set.
     * @see #unsetEccentricitySquared()
     * @see #getEccentricitySquared()
     * @see #setEccentricitySquared(double)
     * @generated
     */
	public boolean isSetEccentricitySquared() {
        return (flags & ECCENTRICITY_SQUARED_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Equatorial Radius</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Equatorial Radius</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Equatorial Radius</em>' attribute.
     * @see #isSetEquatorialRadius()
     * @see #unsetEquatorialRadius()
     * @see #setEquatorialRadius(double)
     * @generated
     */
	public double getEquatorialRadius() {
        return equatorialRadius;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEquatorialRadius <em>Equatorial Radius</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Equatorial Radius</em>' attribute.
     * @see #isSetEquatorialRadius()
     * @see #unsetEquatorialRadius()
     * @see #getEquatorialRadius()
     * @generated
     */
	public void setEquatorialRadius(double newEquatorialRadius) {
        double oldEquatorialRadius = equatorialRadius;
        equatorialRadius = newEquatorialRadius;
        flags |= EQUATORIAL_RADIUS_ESETFLAG;
        firePropertyChange(EQUATORIAL_RADIUS, oldEquatorialRadius, newEquatorialRadius);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEquatorialRadius <em>Equatorial Radius</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetEquatorialRadius()
     * @see #getEquatorialRadius()
     * @see #setEquatorialRadius(double)
     * @generated
     */
	public void unsetEquatorialRadius() {
        double oldEquatorialRadius = equatorialRadius;
        equatorialRadius = EQUATORIAL_RADIUS_EDEFAULT;
        flags &= ~EQUATORIAL_RADIUS_ESETFLAG;
        firePropertyChange(EQUATORIAL_RADIUS, oldEquatorialRadius, null);

    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getEquatorialRadius <em>Equatorial Radius</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Equatorial Radius</em>' attribute is set.
     * @see #unsetEquatorialRadius()
     * @see #getEquatorialRadius()
     * @see #setEquatorialRadius(double)
     * @generated
     */
	public boolean isSetEquatorialRadius() {
        return (flags & EQUATORIAL_RADIUS_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @generated
     */
	public String getName() {
        return name;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.Ellipsoid#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
	public void setName(String newName) {
        String oldName = name;
        name = newName;
        firePropertyChange(NAME, oldName, newName);
    }

	/**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
	public String toString() {

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (eccentricitySquared: ");
        if ((flags & ECCENTRICITY_SQUARED_ESETFLAG) != 0) result.append(eccentricitySquared); else result.append("<unset>");
        result.append(", equatorialRadius: ");
        if ((flags & EQUATORIAL_RADIUS_ESETFLAG) != 0) result.append(equatorialRadius); else result.append("<unset>");
        result.append(", name: ");
        result.append(name);
        result.append(')');
        return result.toString();
    }

} // Ellipsoid
