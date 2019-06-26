package gov.nasa.arc.irg.georef.coordinates;

/**
 * Adapted from code at http://www.gpsy.com/gpsinfo/geotoutm/
 * 
 * Reference ellipsoids derived from Peter H. Dana's website-
 * http://www.utexas.edu/depts/grg/gcraft/notes/datum/elist.html
 * Department of Geography, University of Texas at Austin
 * Internet: pdana@mail.utexas.edu
 * 3/22/95
 * 
 * Source:
 * Defense Mapping Agency. 1987b. DMA Technical Report: Supplement to Department of Defense World Geodetic System
 * 1984 Technical Report. Part I and II. Washington, DC: Defense Mapping Agency
 */
public enum EllipsoidReference {
	
	//NOTE: order matters.  If you are modifying this make sure of your order.
    Airy(6377563, 0.00667054),
    Australian_National(6378160, 0.006694542),
    Bessel_1841(6377397, 0.006674372),
    Bessel_1841_Nambia(6377484, 0.006674372),
    Clarke_1866(6378206, 0.006768658),
    Clarke_1880(6378249, 0.006803511),
    Everest(6377276, 0.006637847),
    Fischer_1960_Mercury(6378166, 0.006693422),
    Fischer_1968(6378150, 0.006693422),
    GRS_1967(6378160, 0.006694605),
    GRS_1980(6378137, 0.00669438),
    Helmert_1906(6378200, 0.006693422),
    Hough(6378270, 0.00672267),
    International(6378388, 0.00672267),
    Krassovsky(6378245, 0.006693422),
    Modified_Airy(6377340, 0.00667054),
    Modified_Everest(6377304, 0.006637847),
    Modified_Fischer_1960(6378155, 0.006693422),
    South_American_1969(6378160, 0.006694542),
    WGS_60(6378165, 0.006693422),
    WGS_66(6378145, 0.006694542),
    WGS_72(6378135, 0.006694318),
    WGS_84(6378137, 0.081819190843*0.081819190843);

    private final Ellipsoid m_ellipsoid;
    
    private EllipsoidReference (double radius, double eccentricity){
    	m_ellipsoid = new Ellipsoid();
    	m_ellipsoid.setName(this.toString());
    	m_ellipsoid.setEccentricitySquared(eccentricity);
    	m_ellipsoid.setEquatorialRadius(radius);
    }
    
    public Ellipsoid getEllipsoid() {
    	return m_ellipsoid;
    }
    
    /**
     * returns the ordinal + 1
     * @return
     */
    public int getIndex() {
    	return this.ordinal() + 1;
    }

}
