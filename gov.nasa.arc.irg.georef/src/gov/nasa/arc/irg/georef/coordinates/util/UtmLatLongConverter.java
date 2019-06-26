package gov.nasa.arc.irg.georef.coordinates.util;

import gov.nasa.arc.irg.georef.coordinates.Ellipsoid;
import gov.nasa.arc.irg.georef.coordinates.EllipsoidReference;
import gov.nasa.arc.irg.georef.coordinates.LatLong;
import gov.nasa.arc.irg.georef.coordinates.Letter;
import gov.nasa.arc.irg.georef.coordinates.UTM;


/**
 * Utility converter to go back and forth between UTM and LatLong Note this is a
 * lossy conversion.
 * 
 * Adapted from code at http://www.gpsy.com/gpsinfo/geotoutm/
 * 
 * @author tecohen
 * 
 */
public class UtmLatLongConverter {

    public static double FOURTHPI = Math.PI / 4.0;
    public static double DEG_TO_RAD = Math.PI / 180.0;
    public static double RAD_TO_DEG = 180.0 / Math.PI;

    /**
     * converts lat/long to UTM coords. Equations from USGS Bulletin 1532 East
     * Longitudes are positive, West longitudes are negative. North latitudes
     * are positive, South latitudes are negative lat and lon are in decimal
     * degrees Written by Chuck Gantz- chuck.gantz@globalstar.com
     */
    /**
     * @param latLong
     * @param referenceEllipsoid
     * @return
     */
    public static final UTM toUTM(LatLong latLong, Ellipsoid referenceEllipsoid) {

        double lon = latLong.getLongitude();
        double lat = latLong.getLatitude();
        double easting;
        double northing;
        Letter zoneLetter;
        int zoneNumber;

        double eccSquared = referenceEllipsoid.getEccentricitySquared();
        final double k0 = 0.9996;

        double lonOrigin;
        double eccPrimeSquared;
        double N, T, C, A, M;

        // Make sure the longitude is between -180.00 .. 179.9
        double longTemp = (lon + 180.0) - (int) ((lon + 180.0) / 360.0) * 360.0 - 180.0; // -180.00 .. 179.9;
        double latRad = lat * DEG_TO_RAD;
        double lonRad = longTemp * DEG_TO_RAD;
        double lonOriginRad;

        zoneNumber = (int) ((longTemp + 180) / 6) + 1;

        if (lat >= 56.0 && lat < 64.0 && longTemp >= 3.0 && longTemp < 12.0)
            zoneNumber = 32;

        // Special zones for Svalbard
        if (lat >= 72.0 && lat < 84.0) {
            if (longTemp >= 0.0 && longTemp < 9.0)
                zoneNumber = 31;
            else if (longTemp >= 9.0 && longTemp < 21.0)
                zoneNumber = 33;
            else if (longTemp >= 21.0 && longTemp < 33.0)
                zoneNumber = 35;
            else if (longTemp >= 33.0 && longTemp < 42.0)
                zoneNumber = 37;
        }
        lonOrigin = (zoneNumber - 1) * 6 - 180 + 3; // +3 puts origin in middle of zone
        lonOriginRad = lonOrigin * DEG_TO_RAD;

        // compute the UTM Zone from the latitude and longitude
        zoneLetter = Letter.getLetterDesignator(lat);

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        N = referenceEllipsoid.getEquatorialRadius() / Math.sqrt(1 - eccSquared * Math.sin(latRad) * Math.sin(latRad));
        T = Math.tan(latRad) * Math.tan(latRad);
        C = eccPrimeSquared * Math.cos(latRad) * Math.cos(latRad);
        A = Math.cos(latRad) * (lonRad - lonOriginRad);

        M = referenceEllipsoid.getEquatorialRadius()
                * ((1 - eccSquared / 4 - 3 * eccSquared * eccSquared / 64 - 5
                        * eccSquared * eccSquared * eccSquared / 256)
                        * latRad
                        - (3 * eccSquared / 8 + 3 * eccSquared * eccSquared
                                / 32 + 45 * eccSquared * eccSquared
                                * eccSquared / 1024)
                                * Math.sin(2 * latRad)
                                + (15 * eccSquared * eccSquared / 256 + 45 * eccSquared
                                        * eccSquared * eccSquared / 1024)
                                        * Math.sin(4 * latRad) - (35 * eccSquared * eccSquared
                                                * eccSquared / 3072)
                                                * Math.sin(6 * latRad));

        easting =  (k0
                * N
                * (A + (1 - T + C) * A * A * A / 6 + (5 - 18 * T + T * T + 72
                        * C - 58 * eccPrimeSquared)
                        * A * A * A * A * A / 120) + 500000.0);

        northing =  (k0 * (M + N
                * Math.tan(latRad)
                * (A * A / 2 + (5 - T + 9 * C + 4 * C * C) * A * A * A * A / 24 + (61
                        - 58 * T + T * T + 600 * C - 330 * eccPrimeSquared)
                        * A * A * A * A * A * A / 720)));
        if (lat < 0) {
            northing += 10000000.0; // 10000000 meter offset for southern
            // hemisphere
        }

        // TODO is the equator northern or southern hemisphere?
        UTM result = new UTM(easting, northing, zoneLetter, zoneNumber);
        result.setNorthernHemisphere(lat >= 0);
        return result;
    }

    /**
     * @param latitude
     * @param longitude
     * @param referenceEllipsoid
     * @return
     */
    public static final UTM toUTM(double latitude, double longitude,
                                  Ellipsoid referenceEllipsoid) {
        LatLong ll = new LatLong(latitude, longitude);
        return toUTM(ll, referenceEllipsoid);
    }

    /**
     * Use WGS_84 as default ellipsoid reference
     * @param latLong
     * @return
     */
    public static final UTM toUTM(LatLong latLong){
        return toUTM(latLong.getLatitude(), latLong.getLongitude(), EllipsoidReference.WGS_84.getEllipsoid());
    }

    /**
     * Use WGS_84 as default ellipsoid reference
     * @param latitude
     * @param longitude
     * @return
     */
    public static final UTM toUTM(double latitude, double longitude){
        return toUTM(latitude, longitude, EllipsoidReference.WGS_84.getEllipsoid());
    }


    /**
     * Use WGS_84 as default ellipsoid reference
     * @param utm
     * @return
     */
    public static final LatLong toLatLong(UTM utm){
        return toLatLong(utm, EllipsoidReference.WGS_84.getEllipsoid());
    }

    /**
     * converts UTM coords to lat/long. Equations from USGS Bulletin 1532 East
     * Longitudes are positive, West longitudes are negative. North latitudes
     * are positive, South latitudes are negative lat and lon are in decimal
     * degrees. Written by Chuck Gantz- chuck.gantz@globalstar.com
     */
    /**
     * @param utm
     * @param referenceEllipsoid
     * @return
     */
    public static final LatLong toLatLong(UTM utm, Ellipsoid referenceEllipsoid) {
        double lat;
        double lon;

        final double k0 = 0.9996;
        double a = referenceEllipsoid.getEquatorialRadius();
        double eccSquared = referenceEllipsoid.getEccentricitySquared();
        double eccPrimeSquared;
        double e1 = (1 - Math.sqrt(1 - eccSquared))
                / (1 + Math.sqrt(1 - eccSquared));
        double N1, T1, C1, R1, D, M;
        double lonOrigin;
        double mu, phi1Rad;
        double x, y;

        x = utm.getEasting() - 500000.0; // remove 500,000 meter offset for
        y = utm.getNorthing();

        if (!utm.isNorthernHemisphere()) {
            y -= 10000000.0; // remove 10,000,000 meter offset used for southern
            // hemisphere
        }

        lonOrigin = ((utm.getZone() - 1) * 6) - 180 + 3; // +3 puts origin in
        // middle of zone

        eccPrimeSquared = (eccSquared) / (1 - eccSquared);

        M = y / k0;
        mu = M/(a*(1-eccSquared/4-3*eccSquared*eccSquared/64-5*eccSquared*eccSquared*eccSquared/256));

        phi1Rad = mu + (3*e1/2-27*e1*e1*e1/32)*Math.sin(2*mu)
                + (21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(4*mu)
                +(151*e1*e1*e1/96)*Math.sin(6*mu);

        N1 = a/Math.sqrt(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad));
        T1 = Math.tan(phi1Rad)*Math.tan(phi1Rad);
        C1 = eccPrimeSquared*Math.cos(phi1Rad)*Math.cos(phi1Rad);
        R1 = a*(1-eccSquared)/Math.pow(1-eccSquared*Math.sin(phi1Rad)*Math.sin(phi1Rad), 1.5);
        D = x/(N1*k0);

        lat = phi1Rad - (N1*Math.tan(phi1Rad)/R1)*(D*D/2-(5+3*T1+10*C1-4*C1*C1-9*eccPrimeSquared)*D*D*D*D/24
                +(61+90*T1+298*C1+45*T1*T1-252*eccPrimeSquared-3*C1*C1)*D*D*D*D*D*D/720);
        lat = lat * RAD_TO_DEG;

        lon = (D-(1+2*T1+C1)*D*D*D/6+(5-2*C1+28*T1-3*C1*C1+8*eccPrimeSquared+24*T1*T1)
                *D*D*D*D*D/120)/Math.cos(phi1Rad);
        lon = lonOrigin + lon * RAD_TO_DEG;
        return new LatLong(lat, lon);
    }

    /**
     * @param easting
     * @param northing
     * @param letterDesignator
     * @param zone
     * @param northernHemisphere
     * @param referenceEllipsoid
     * @return
     */
    public static final LatLong toLatLong(double easting, double northing,
                                          String letterDesignator, int zone, boolean northernHemisphere,
                                          Ellipsoid referenceEllipsoid) {
        Letter ld = null;
        if (letterDesignator != null && !(letterDesignator.length() == 0)){
            try {
                ld = Letter.valueOf(letterDesignator);
            } catch (Exception ex){
                //gulp
            }
        }
        UTM utm = new UTM(easting, northing, ld, zone);
        return toLatLong(utm, referenceEllipsoid);
    }
}
