/******************************************************************************
 * Copyright © 2019, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration. All 
 * rights reserved.
 * 
 * The Astrobee Control Station platform is licensed under the Apache License, 
 * Version 2.0 (the "License"); you may not use this file except in compliance 
 * with the License. You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0. 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 *****************************************************************************/
package gov.nasa.arc.verve.robot.freeflyer;

import com.ardor3d.math.Quaternion;
import com.ardor3d.scenegraph.Node;

import gov.nasa.arc.verve.common.VerveUserData;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.freeflyer.parts.FreeFlyerBasicModel;
import gov.nasa.arc.verve.robot.parts.AbstractRobotPart;

public class RobotPartPlanPreview extends AbstractRobotPart {
	private FreeFlyerBasicModel m_preview = null;
	public RobotPartPlanPreview(String partName, AbstractRobot parent) {
		super(partName, parent);
	}

	public void moveRobot(double x, double y, double z, double qx, double qy, double qz, double qw) {
		System.out.println("moving " + x + ", " + y + ", " + z);
		m_node.setTranslation(x, y, z);
		Quaternion rot = new Quaternion(qx, qy, qz, qw);
		m_node.setRotation(rot);
	}
	
	@Override
	public void connectTelemetry() throws TelemetryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnectTelemetry() throws TelemetryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void attachToNodesIn(Node model) throws IllegalStateException {
		m_node = new Node(getRobot().getName() + ":PlanPreview");
		m_preview = new FreeFlyerBasicModel();

		getRobot().getRobotNode().getConceptsNode().attachChild(m_node);

		m_node.attachChild(m_preview);
		m_node.updateWorldBound(false);

		VerveUserData.setCameraFollowable(m_node, false);
	}

	@Override
	public void handleFrameUpdate(long currentTime) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
