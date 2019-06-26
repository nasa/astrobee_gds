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

import java.text.DecimalFormat;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>UTM</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.UTM#getEasting <em>Easting</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.UTM#getLetterDesignator <em>Letter Designator</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.UTM#isNorthernHemisphere <em>Northern Hemisphere</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.UTM#getNorthing <em>Northing</em>}</li>
 *   <li>{@link gov.nasa.arc.irg.georef.coordinates.UTM#getZone <em>Zone</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UTM extends AbstractModelObject {
    
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
     s_decimalFormat.setMaximumFractionDigits(2);
     s_decimalFormat.setMinimumFractionDigits(1);
    }
    
    public static final String EASTING = "easting";
    public static final String NORTHING = "northing";
    public static final String NORTHERN_HEMISPHERE = "northernHemisphere";
    public static final String ZONE = "zone";
    public static final String LETTER_DESIGNATOR = "letterDesignator";
	/**
     * The default value of the '{@link #getEasting() <em>Easting</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEasting()
     * @generated
     * @ordered
     */
	protected static final double EASTING_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getEasting() <em>Easting</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEasting()
     * @generated
     * @ordered
     */
	protected double easting = EASTING_EDEFAULT;

	/**
     * The flag representing whether the Easting attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int EASTING_ESETFLAG = 1 << 0;

    /**
     * The default value of the '{@link #getLetterDesignator() <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getLetterDesignator()
     * @generated
     * @ordered
     */
	protected static final Letter LETTER_DESIGNATOR_EDEFAULT = Letter.C;

	/**
     * The offset of the flags representing the value of the '{@link #getLetterDesignator() <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int LETTER_DESIGNATOR_EFLAG_OFFSET = 1;

    /**
     * The flags representing the default value of the '{@link #getLetterDesignator() <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int LETTER_DESIGNATOR_EFLAG_DEFAULT = LETTER_DESIGNATOR_EDEFAULT.ordinal() << LETTER_DESIGNATOR_EFLAG_OFFSET;

    /**
     * The array of enumeration values for '{@link Letter Letter}'
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    private static final Letter[] LETTER_DESIGNATOR_EFLAG_VALUES = Letter.values();

    /**
     * The flags representing the value of the '{@link #getLetterDesignator() <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLetterDesignator()
     * @generated
     * @ordered
     */
    protected static final int LETTER_DESIGNATOR_EFLAG = 0x1f << LETTER_DESIGNATOR_EFLAG_OFFSET;

    /**
     * The flag representing whether the Letter Designator attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int LETTER_DESIGNATOR_ESETFLAG = 1 << 6;

    /**
     * The default value of the '{@link #isNorthernHemisphere() <em>Northern Hemisphere</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isNorthernHemisphere()
     * @generated
     * @ordered
     */
	protected static final boolean NORTHERN_HEMISPHERE_EDEFAULT = false;

	/**
     * The flag representing the value of the '{@link #isNorthernHemisphere() <em>Northern Hemisphere</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNorthernHemisphere()
     * @generated
     * @ordered
     */
    protected static final int NORTHERN_HEMISPHERE_EFLAG = 1 << 7;

    /**
     * The flag representing whether the Northern Hemisphere attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int NORTHERN_HEMISPHERE_ESETFLAG = 1 << 8;

    /**
     * The default value of the '{@link #getNorthing() <em>Northing</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNorthing()
     * @generated
     * @ordered
     */
	protected static final double NORTHING_EDEFAULT = 0.0;

	/**
     * The cached value of the '{@link #getNorthing() <em>Northing</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNorthing()
     * @generated
     * @ordered
     */
	protected double northing = NORTHING_EDEFAULT;

	/**
     * The flag representing whether the Northing attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int NORTHING_ESETFLAG = 1 << 9;

    /**
     * The default value of the '{@link #getZone() <em>Zone</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getZone()
     * @generated
     * @ordered
     */
	protected static final int ZONE_EDEFAULT = 0;

	/**
     * The cached value of the '{@link #getZone() <em>Zone</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getZone()
     * @generated
     * @ordered
     */
	protected int zone = ZONE_EDEFAULT;

	/**
     * The flag representing whether the Zone attribute has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected static final int ZONE_ESETFLAG = 1 << 10;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public UTM() {
        super();
    }
	
	/**
	 * @param easting
	 * @param northing
	 * @param zoneLetter
	 * @param zoneNumber
	 * @param northern
	 */
	public UTM(double easting, double northing, Letter zoneLetter, int zoneNumber){
		super();
		setEasting(easting);
		setNorthing(northing);
		setLetterDesignator(zoneLetter);
		setZone(zoneNumber);
	}
	
	/**
	 * @param easting
	 * @param northing
	 * @param letter (as a char)
	 * @param zoneNumber
	 */
	public UTM(double easting, double northing, char letter, int zoneNumber){
		super();
		setLetterDesignator(letter);
		setEasting(easting);
		setNorthing(northing);
		setZone(zoneNumber);
		
	}

	/**
     * Returns the value of the '<em><b>Easting</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Easting</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Easting</em>' attribute.
     * @see #isSetEasting()
     * @see #unsetEasting()
     * @see #setEasting(double)
     * @generated
     */
	public double getEasting() {
        return easting;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getEasting <em>Easting</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Easting</em>' attribute.
     * @see #isSetEasting()
     * @see #unsetEasting()
     * @see #getEasting()
     * @generated
     */
	public void setEasting(double newEasting) {
        double oldEasting = easting;
        easting = newEasting;
        flags |= EASTING_ESETFLAG;
        firePropertyChange(EASTING, oldEasting, newEasting);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getEasting <em>Easting</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetEasting()
     * @see #getEasting()
     * @see #setEasting(double)
     * @generated
     */
	public void unsetEasting() {
        double oldEasting = easting;
        easting = EASTING_EDEFAULT;
        flags &= ~EASTING_ESETFLAG;
        firePropertyChange(EASTING, oldEasting, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getEasting <em>Easting</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Easting</em>' attribute is set.
     * @see #unsetEasting()
     * @see #getEasting()
     * @see #setEasting(double)
     * @generated
     */
	public boolean isSetEasting() {
        return (flags & EASTING_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Letter Designator</b></em>' attribute.
     * The literals are from the enumeration {@link gov.nasa.arc.irg.georef.coordinates.Letter}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Letter Designator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Letter Designator</em>' attribute.
     * @see gov.nasa.arc.irg.georef.coordinates.Letter
     * @see #isSetLetterDesignator()
     * @see #unsetLetterDesignator()
     * @see #setLetterDesignator(Letter)
     * @generated
     */
	public Letter getLetterDesignator() {
        return LETTER_DESIGNATOR_EFLAG_VALUES[(flags & LETTER_DESIGNATOR_EFLAG) >>> LETTER_DESIGNATOR_EFLAG_OFFSET];
    }
	
	/**
	 * Returns true if this was really a valid letter designator and it was set.
	 * @param letter
	 */
	public boolean setLetterDesignator(char letter){
		StringBuffer letterString = new StringBuffer(letter);
		Letter ld = null;
        try {
            ld = Letter.valueOf(letterString.toString().toUpperCase());
        } catch (Exception ex){
            //gulp
        }
		if (ld != null){
			setLetterDesignator(ld);
			return true;
		}
		return false;
	}

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getLetterDesignator <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Letter Designator</em>' attribute.
     * @see gov.nasa.arc.irg.georef.coordinates.Letter
     * @see #isSetLetterDesignator()
     * @see #unsetLetterDesignator()
     * @see #getLetterDesignator()
     * @generated
     */
	public void setLetterDesignator(Letter newLetterDesignator) {
        Letter oldLetterDesignator = LETTER_DESIGNATOR_EFLAG_VALUES[(flags & LETTER_DESIGNATOR_EFLAG) >>> LETTER_DESIGNATOR_EFLAG_OFFSET];
        if (newLetterDesignator == null) newLetterDesignator = LETTER_DESIGNATOR_EDEFAULT;
        flags = flags & ~LETTER_DESIGNATOR_EFLAG | newLetterDesignator.ordinal() << LETTER_DESIGNATOR_EFLAG_OFFSET;
        flags |= LETTER_DESIGNATOR_ESETFLAG;
        firePropertyChange(LETTER_DESIGNATOR, oldLetterDesignator, newLetterDesignator);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getLetterDesignator <em>Letter Designator</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetLetterDesignator()
     * @see #getLetterDesignator()
     * @see #setLetterDesignator(Letter)
     * @generated
     */
	public void unsetLetterDesignator() {
        Letter oldLetterDesignator = LETTER_DESIGNATOR_EFLAG_VALUES[(flags & LETTER_DESIGNATOR_EFLAG) >>> LETTER_DESIGNATOR_EFLAG_OFFSET];
        flags = flags & ~LETTER_DESIGNATOR_EFLAG | LETTER_DESIGNATOR_EFLAG_DEFAULT;
        flags &= ~LETTER_DESIGNATOR_ESETFLAG;
        firePropertyChange(LETTER_DESIGNATOR, oldLetterDesignator, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getLetterDesignator <em>Letter Designator</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Letter Designator</em>' attribute is set.
     * @see #unsetLetterDesignator()
     * @see #getLetterDesignator()
     * @see #setLetterDesignator(Letter)
     * @generated
     */
	public boolean isSetLetterDesignator() {
        return (flags & LETTER_DESIGNATOR_ESETFLAG) != 0;
    }

    /**
	 * Returns the value of the '<em><b>Northern Hemisphere</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Northern Hemisphere</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Northern Hemisphere</em>' attribute.
	 * @see #isSetNorthernHemisphere()
	 * @see #unsetNorthernHemisphere()
	 * @see #setNorthernHemisphere(boolean)
	 * @generated not
	 */
	public boolean isNorthernHemisphere() {
	    if (isSetLetterDesignator()){
	        return isNorthernHemisphere(getLetterDesignator());
	    }
		return (flags & NORTHERN_HEMISPHERE_EFLAG) != 0;
	}
	
	/**
	 * Returns true if the letter < N, false otherwise
	 * @param letter
	 * @return
	 */
	public static boolean isNorthernHemisphere(Letter letter) {
	    if(letter.ordinal() < Letter.N.ordinal()){
	      return false;
	    }
	    return true;
	  }


	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#isNorthernHemisphere <em>Northern Hemisphere</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Northern Hemisphere</em>' attribute.
     * @see #isSetNorthernHemisphere()
     * @see #unsetNorthernHemisphere()
     * @see #isNorthernHemisphere()
     * @generated
     */
	public void setNorthernHemisphere(boolean newNorthernHemisphere) {
        boolean oldNorthernHemisphere = (flags & NORTHERN_HEMISPHERE_EFLAG) != 0;
        if (newNorthernHemisphere) flags |= NORTHERN_HEMISPHERE_EFLAG; else flags &= ~NORTHERN_HEMISPHERE_EFLAG;
        flags |= NORTHERN_HEMISPHERE_ESETFLAG;
        firePropertyChange(NORTHERN_HEMISPHERE, oldNorthernHemisphere, newNorthernHemisphere);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#isNorthernHemisphere <em>Northern Hemisphere</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetNorthernHemisphere()
     * @see #isNorthernHemisphere()
     * @see #setNorthernHemisphere(boolean)
     * @generated
     */
	public void unsetNorthernHemisphere() {
        boolean oldNorthernHemisphere = (flags & NORTHERN_HEMISPHERE_EFLAG) != 0;
        if (NORTHERN_HEMISPHERE_EDEFAULT) flags |= NORTHERN_HEMISPHERE_EFLAG; else flags &= ~NORTHERN_HEMISPHERE_EFLAG;
        flags &= ~NORTHERN_HEMISPHERE_ESETFLAG;
        firePropertyChange(NORTHERN_HEMISPHERE, oldNorthernHemisphere, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#isNorthernHemisphere <em>Northern Hemisphere</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Northern Hemisphere</em>' attribute is set.
     * @see #unsetNorthernHemisphere()
     * @see #isNorthernHemisphere()
     * @see #setNorthernHemisphere(boolean)
     * @generated
     */
	public boolean isSetNorthernHemisphere() {
        return (flags & NORTHERN_HEMISPHERE_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Northing</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Northing</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Northing</em>' attribute.
     * @see #isSetNorthing()
     * @see #unsetNorthing()
     * @see #setNorthing(double)
     * @generated
     */
	public double getNorthing() {
        return northing;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getNorthing <em>Northing</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Northing</em>' attribute.
     * @see #isSetNorthing()
     * @see #unsetNorthing()
     * @see #getNorthing()
     * @generated
     */
	public void setNorthing(double newNorthing) {
        double oldNorthing = northing;
        northing = newNorthing;
        flags |= NORTHING_ESETFLAG;
        firePropertyChange(NORTHING, oldNorthing, newNorthing);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getNorthing <em>Northing</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetNorthing()
     * @see #getNorthing()
     * @see #setNorthing(double)
     * @generated
     */
	public void unsetNorthing() {
        double oldNorthing = northing;
        northing = NORTHING_EDEFAULT;
        flags &= ~NORTHING_ESETFLAG;
        firePropertyChange(NORTHING, oldNorthing, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getNorthing <em>Northing</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Northing</em>' attribute is set.
     * @see #unsetNorthing()
     * @see #getNorthing()
     * @see #setNorthing(double)
     * @generated
     */
	public boolean isSetNorthing() {
        return (flags & NORTHING_ESETFLAG) != 0;
    }

	/**
     * Returns the value of the '<em><b>Zone</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Zone</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Zone</em>' attribute.
     * @see #isSetZone()
     * @see #unsetZone()
     * @see #setZone(int)
     * @generated
     */
	public int getZone() {
        return zone;
    }

	/**
     * Sets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getZone <em>Zone</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Zone</em>' attribute.
     * @see #isSetZone()
     * @see #unsetZone()
     * @see #getZone()
     * @generated
     */
	public void setZone(int newZone) {
        int oldZone = zone;
        zone = newZone;
        flags |= ZONE_ESETFLAG;
        firePropertyChange(ZONE, oldZone, newZone);
    }

	/**
     * Unsets the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getZone <em>Zone</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetZone()
     * @see #getZone()
     * @see #setZone(int)
     * @generated
     */
	public void unsetZone() {
        int oldZone = zone;
        zone = ZONE_EDEFAULT;
        flags &= ~ZONE_ESETFLAG;
        firePropertyChange(ZONE, oldZone, null);
    }

	/**
     * Returns whether the value of the '{@link gov.nasa.arc.irg.georef.coordinates.UTM#getZone <em>Zone</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Zone</em>' attribute is set.
     * @see #unsetZone()
     * @see #getZone()
     * @see #setZone(int)
     * @generated
     */
	public boolean isSetZone() {
        return (flags & ZONE_ESETFLAG) != 0;
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated not
	 */
	@Override
	public String toString() {
		
		StringBuffer result = new StringBuffer();
		if (isSetZone()) {
			result.append(zone);
			result.append(" ");
		}
		if (isSetLetterDesignator()) {
			result.append(getLetterDesignator());
			result.append(" ");
		}
		if (isSetEasting()) {
			result.append(s_decimalFormat.format(easting));
			result.append(" m E ");
		}
		if (isSetNorthing()) {
			result.append(s_decimalFormat.format(northing));
			result.append(" m N");
		}
		//if (northernHemisphereESet) result.append(northernHemisphere); else result.append("<unset>");
		return result.toString();
	}
	
	/**
	 * Populates this UTM with the one passed in.
	 * @param other
	 */
	public void fillValues(UTM other){
		if (other == null || other.equals(this)){
			return;
		}
		if (other.isSetEasting()){
			setEasting(other.getEasting());
		}
		if (other.isSetNorthing()){
			setNorthing(other.getNorthing());
		}
		if (other.isSetNorthernHemisphere()){
			setNorthernHemisphere(other.isNorthernHemisphere());
		}
		if (other.isSetZone()){
			setZone(other.getZone());
		}
		if (other.isSetLetterDesignator()){
			setLetterDesignator(other.getLetterDesignator());
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
    public UTM clone(){
		UTM result = new UTM();
		result.fillValues(this);
		return result;
	}

} // UTM
