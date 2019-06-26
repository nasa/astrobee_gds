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
package gov.nasa.arc.verve.robot.rapid;

import gov.nasa.arc.irg.georef.coordinates.UTM;
import gov.nasa.arc.irg.georef.coordinates.util.UtmLatLongConverter;
import gov.nasa.arc.verve.common.scenario.ScenarioPreferences;
import gov.nasa.arc.verve.robot.RobotFrameNames;
import gov.nasa.arc.viz.scenegraph.NamedFrame;
import gov.nasa.arc.viz.scenegraph.visitor.FindNamedFrameVisitor;
import gov.nasa.rapid.v2.e4.agent.Agent;

import org.apache.log4j.Logger;

import com.ardor3d.math.Matrix3;
import com.ardor3d.math.Vector2;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector2;
import com.ardor3d.scenegraph.Node;

/**
 * As a TEMPORARY hack to work around the lack of a 
 * frame store, get a fixed site frame identifier
 */
public class SiteFrameHack {
    private static final Logger logger = Logger.getLogger(SiteFrameHack.class);

    public static RobotFrameNames getSiteFrameName(Agent agent) {
        if( agent.hasTag(Agent.Tag.ZUP)) {
            return RobotFrameNames.XNorth_YWest_ZUp_TrueNorth;
        }
        return RobotFrameNames.NED;
    }

    public static Node getSiteFrameNode(Agent agent, Node root) {
        Node retVal = null;
        FindNamedFrameVisitor visitor = new FindNamedFrameVisitor();
        root.acceptVisitor(visitor, false);
        retVal = visitor.getNamedFrame(getSiteFrameName(agent).toString());
        return retVal;
    }

    /**
     * quick and dirty way to determine angle between grid north and true north
     * at the application-set site frame
     */
    public static double trueNorthOffset() {
        return trueNorthOffset(ScenarioPreferences.getSiteFrameLatLonOffset());
    }

    /**
     * quick and dirty way to determine angle between grid north and true north
     */
    public static double trueNorthOffset(ReadOnlyVector2 latLon1) {
        final double latOffset = 0.0045; // approx 500m
        Vector2 latLon2 = new Vector2(latLon1);
        latLon2.setX(latLon1.getX()+latOffset);
        UTM utm1 = UtmLatLongConverter.toUTM(latLon1.getX(), latLon1.getY());
        UTM utm2 = UtmLatLongConverter.toUTM(latLon2.getX(), latLon2.getY());
        double eas1,eas2;
        double nor1,nor2;
        eas1 = utm1.getEasting();
        nor1 = utm1.getNorthing();
        eas2 = utm2.getEasting();
        nor2 = utm2.getNorthing();
        double angle = -Math.atan( (eas2-eas1)/(nor2-nor1) );
        if(false) {
            double degrees = angle * (180 / Math.PI);
            logger.debug("offset between grid and true north is "+degrees+" degrees");
            logger.debug("   northing diff = "+(nor2-nor1)+"m, easting diff = "+(eas2-eas1)+"m");
        }
        return angle;
    }

    /**
     * update true north frames when site frame changes
     */
    public static void updateTrueNorthSite(Node root) {
        double trueNorthOffset = SiteFrameHack.trueNorthOffset();
        Matrix3 rot = new Matrix3();
        if(root != null) {
            FindNamedFrameVisitor visitor = new FindNamedFrameVisitor();
            root.acceptVisitor(visitor, true);

            // Z-Up True North
            NamedFrame frame = visitor.getNamedFrame(RobotFrameNames.XNorth_YWest_ZUp_TrueNorth.toString());
            if(frame != null) {
                rot.fromAngleNormalAxis( trueNorthOffset, Vector3.UNIT_Z);
                frame.setRotation(rot);
            }
            else {
                logger.warn("Could not find TrueNorth frame");
            }
        }
    }


}
