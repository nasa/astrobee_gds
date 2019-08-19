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

import gov.nasa.arc.irg.plan.freeflyer.plan.FreeFlyerPlan;
import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.IPoseProvider;
import gov.nasa.arc.verve.robot.RobotRegistry;
import gov.nasa.arc.verve.robot.exception.TelemetryException;
import gov.nasa.arc.verve.robot.freeflyer.plan.PlanPreviewLocationProvider;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.arc.verve.robot.scenegraph.RobotNode;
import gov.nasa.arc.verve.robot.scenegraph.RobotUpdateController;

import org.apache.log4j.Logger;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.CullHint;

public class PlanPreviewRobot extends AbstractRobot {
	private static final Logger 	logger = Logger.getLogger(PlanPreviewRobot.class);
	protected RobotPartPlanPreview  m_part;
	protected String partName = "PlanPreviewPart";
	protected RobotNode             m_robotNode    = null; 
	protected RobotUpdateController m_controller   = null;
	protected boolean robotShowing = false;

	protected boolean m_telemetryEnabled = false;
	
	protected Quaternion rotation = new Quaternion(0,0,0,1);
	protected Vector3 translation = new Vector3(0,0,0);

	public void previewPlan(FreeFlyerPlan plan) {
		PlanPreviewLocationProvider.getInstance().previewThisPlan(plan);
	}
	
	public void stopPreview() {
		PlanPreviewLocationProvider.getInstance().stopPreview();
	}
	
	public void moveRobot(double x, double y, double z, double qx, double qy, double qz, double qw) {
		translation.set(x, y, z);
		rotation.set(qx, qy, qz, qw);
	}
	
	@Override
	public boolean isInterestPointEnabled() {
		return false;
	}

	@Override
	public String getName() {
		return "PlanPreviewRobot";
	}

	@Override
	public String[] getPartNames() {
		return new String[]{partName};
	}

	@Override
	public IRobotPart getPart(String partName) {
		return m_part;
	}

	@Override
	public IPoseProvider getPoseProvider() {
		return null;
	}

	@Override
	public boolean isTelemetryEnabled() {
		return m_telemetryEnabled;
	}

	@Override
	public void setTelemetryEnabled(boolean state) throws TelemetryException {
		if(state) {
			m_telemetryEnabled = true;
			m_part.connectTelemetry();

		}
		else {
			m_part.disconnectTelemetry();
			m_telemetryEnabled = false;
		}
	}

	@Override
	public void attachToNodesIn(Node model) throws IllegalStateException,
	TelemetryException {
		String illegal = "";

		if(model == null) {
			throw new IllegalStateException("3D Model Node cannot be null");
		}
		m_robotNode = new RobotNode(this, model);

		m_part = new RobotPartPlanPreview(getName(), this);

		try {
			m_part.attachToNodesIn(model);
		}
		catch (IllegalStateException e) {
			illegal += getName()+"."+partName+": " + e.getMessage()
					+ "\n";
		}


		if( illegal.length() > 0 ) {
			logger.warn(illegal);
			throw new IllegalStateException(illegal);
		}
		m_part.setVisible(true);

		// Set up handleSceneUpdate to be called every frame
		m_controller = new RobotUpdateController(this);
		m_robotNode.addController(m_controller);

		// add robot to the registry
		// also, load persisted robot properties via the 
		// persistenceHandler that was created in 
		// the RobotActivator start() method
		RobotRegistry.register(getName(), this);
	}

	@Override
	public void handleFrameUpdate(long currentTimeMillis) {
		ReadOnlyTransform tfm = PlanPreviewLocationProvider.getInstance().getTransformAtMillis(currentTimeMillis);
		if(tfm != null) {
			if(!robotShowing) {
				getRobotNode().getSceneHints().setCullHint(CullHint.Inherit);
				robotShowing = true;
			}
			m_robotNode.setTransform(tfm);
		} else {
			if(robotShowing) {
				getRobotNode().getSceneHints().setCullHint(CullHint.Always);
				robotShowing = false;
			}
		}
	}

	@Override
	public RobotNode getRobotNode() {
		return m_robotNode;
	}

	@Override
	protected ReadOnlyVector3 getNextGoalPosition() {
		// TODO Auto-generated method stub
		return null;
	}
}
