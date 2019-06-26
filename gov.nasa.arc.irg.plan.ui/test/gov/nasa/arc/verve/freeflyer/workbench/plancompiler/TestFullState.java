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
package gov.nasa.arc.verve.freeflyer.workbench.plancompiler;

import gov.nasa.arc.irg.plan.model.Point6Dof;
import gov.nasa.arc.irg.plan.model.Position;
import gov.nasa.arc.irg.plan.ui.plancompiler.FullState;
import gov.nasa.arc.irg.plan.ui.plancompiler.SimplifiedState;
import gov.nasa.arc.irg.plan.util.NumberUtil;
import junit.framework.TestCase;

import org.junit.Test;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.Vector3;
import com.ardor3d.math.type.ReadOnlyVector3;

public class TestFullState extends TestCase {

	@Test
	public void testPositionToQuaternion() {
		Vector3 startVec = new Vector3(0, 0, 0);
	
		// Test around Z axis
		Point6Dof startPtZ = new Point6Dof(0, 0, 0, 0, 0, 45);
		Position startPosZ = new Position(startPtZ);
		Vector3 endVecZ = new Vector3(2, 2, 0);

		Vector3 startToEndZ = endVecZ.subtract(startVec, null);
		Quaternion fromVectorToVectorZ = new Quaternion();
		fromVectorToVectorZ.fromVectorToVector(Vector3.UNIT_X, startToEndZ);
		Quaternion fromPositionZ = FullState.positionToQuaternion(startPosZ);
		assertTrue(quaternionsAreEqualEnough(fromVectorToVectorZ,fromPositionZ));
		
		// Test around Y axis
		Point6Dof startPtY = new Point6Dof(0, 0, 0, 0, 45, 0);
		Position startPosY = new Position(startPtY);
		Vector3 endVecY = new Vector3(2, 0, -2);

		Vector3 startToEndY = endVecY.subtract(startVec, null);
		Quaternion fromVectorToVectorY = new Quaternion();
		fromVectorToVectorY.fromVectorToVector(Vector3.UNIT_X, startToEndY);
		Quaternion fromPositionY = FullState.positionToQuaternion(startPosY);
		assertTrue(quaternionsAreEqualEnough(fromVectorToVectorY,fromPositionY));
		
		// We would not be rotating about X axis to face the direction of motion with our +X face
		// so testing that it handles zero rotation properly
		Point6Dof startPtX = new Point6Dof(0, 0, 0, 0, 0, 0);
		Position startPosX = new Position(startPtX);
		Vector3 endVecX = new Vector3(2, 0, 0);

		Vector3 startToEndX = endVecX.subtract(startVec, null);
		Quaternion fromVectorToVectorX = new Quaternion();
		fromVectorToVectorX.fromVectorToVector(Vector3.UNIT_X, startToEndX);
		Quaternion fromPositionX = FullState.positionToQuaternion(startPosX);
		assertTrue(quaternionsAreEqualEnough(fromVectorToVectorX,fromPositionX));
	}

	public boolean quaternionsAreEqualEnough(Quaternion a, Quaternion b) {
		if(!NumberUtil.equals(a.getX(), b.getX())) { 
			return false;
		}
		if(!NumberUtil.equals(a.getY(), b.getY())) { 
			return false;
		}
		if(!NumberUtil.equals(a.getZ(), b.getZ())) { 
			return false;
		}
		if(!NumberUtil.equals(a.getW(), b.getW())) { 
			return false;
		}
		return true;
	}

	@Test
	public void testInterpolatedState() {
		// Initial state variables
		SimplifiedState tstate = new SimplifiedState(0, 0, 1, 1);
		double[] pos_0 = {3, -3, 3};
		double[] unit_velocity = {0.57735, 0.57735, -0.57735};
		SimplifiedState ostate = new SimplifiedState(0.1, 0.1, 1, 0);
		Quaternion orient_0 = new Quaternion(0.707, 0.707, 0, 0);
		//    double[] axis = {1, 0, 0};
		ReadOnlyVector3 axis = Vector3.UNIT_X;

		// Render some examples of the state being interpolated
		FullState render00 =
				FullState.createInterpolatedState(tstate, pos_0, unit_velocity,
						ostate, orient_0, axis, 0);
		FullState render01 =
				FullState.createInterpolatedState(tstate, pos_0, unit_velocity,
						ostate, orient_0, axis, 0.1);
		FullState render10 =
				FullState.createInterpolatedState(tstate, pos_0, unit_velocity,
						ostate, orient_0, axis, 1.0);

		// Verify that we are orient_0 and pos_0, meaning orientation was
		// interpolated backwards and position was interpolated at 0.
		assertEquals(render00.pos[0], pos_0[0], 1e-3);
		assertEquals(render00.pos[1], pos_0[1], 1e-3);
		assertEquals(render00.pos[2], pos_0[2], 1e-3);
		assertEquals(render00.orient.getX(), orient_0.getX(), 1e-3);
		assertEquals(render00.orient.getY(), orient_0.getY(), 1e-3);
		assertEquals(render00.orient.getZ(), orient_0.getZ(), 1e-3);
		assertEquals(render00.orient.getW(), orient_0.getW(), 1e-3);

		// Verify that we are at properly advanced for position1. Not testing the quaternions .. though ...
		assertEquals(render10.pos[0],  1.5 * 0.577 + 3, 1e-3);
		assertEquals(render10.pos[1],  1.5 * 0.577 - 3, 1e-3);
		assertEquals(render10.pos[2], -1.5 * 0.577 + 3, 1e-3);
	}

	@Test
	public void testTrajectoryTrapezoid() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {0, 0, 0},
						new double[] {10, -10, 10},
						1.0, 3.0,
						new Quaternion(0, 0, 0, 1),
						new Quaternion(0.707, 0.707, 0, 0),
						5 * 3.1415 / 180.0,
						3.1415 / 20);
		assertEquals(fstates.length, 7);

		// Verify that the beginning is where it is supposed to be
		assertEquals(fstates[0].t, 0, 1e-3);
		assertEquals(fstates[0].pos[0], 0, 1e-3);
		assertEquals(fstates[0].pos[1], 0, 1e-3);
		assertEquals(fstates[0].pos[2], 0, 1e-3);
		assertEquals(fstates[0].orient.getX(), 0, 1e-3);
		assertEquals(fstates[0].orient.getY(), 0, 1e-3);
		assertEquals(fstates[0].orient.getZ(), 0, 1e-3);
		assertEquals(fstates[0].orient.getW(), 1, 1e-3);

		// we're smearing these now instead of going straight to top speed for both
//		// Verify that we got up to velocity speed in the second step
//		assertEquals(fstates[1].vel[0], 0.577, 1e-3);
//		assertEquals(fstates[1].vel[1], -0.577, 1e-3);
//		assertEquals(fstates[1].vel[2], 0.577, 1e-3);
//
//		// Verify that we got up to angular velocity speed in the third step
//		assertEquals(fstates[2].ang_vel[0], 0.062, 1e-3);
//		assertEquals(fstates[2].ang_vel[1], 0.062, 1e-3);
//		assertEquals(fstates[2].ang_vel[2], 0, 1e-3);

		// Verify that the ending is where it is supposeid to be
		assertTrue(fstates[6].t > 0);
		assertEquals(fstates[6].pos[0], 10, 1e-3);
		assertEquals(fstates[6].pos[1], -10, 1e-3);
		assertEquals(fstates[6].pos[2], 10, 1e-3);
		assertEquals(fstates[6].orient.getX(), 0.707, 1e-3);
		assertEquals(fstates[6].orient.getY(), 0.707, 1e-3);
		assertEquals(fstates[6].orient.getZ(), 0, 1e-3);
		assertEquals(fstates[6].orient.getW(), 0, 1e-3);
	}

	@Test
	public void testTranslationOnly() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {0, 0, 0},
						new double[] {10, 0, 0},
						1.0 /*max vel*/, 1.0 /*max acc*/,
						new Quaternion(0, 0.707, 0, 0.707),
						new Quaternion(0, 0.707, 0, 0.707),
						5 * 3.1415 / 180.0 /*max deg vel*/,
						3.1415 / 20 /*max deg acc*/);
		assertEquals(fstates.length, 4);
		// Verify that we start and end on the right spots.
		assertEquals(0, fstates[0].pos[0], 1e-3);
		assertEquals(0, fstates[0].pos[1], 1e-3);
		assertEquals(0, fstates[0].pos[2], 1e-3);
		assertEquals(10, fstates[3].pos[0], 1e-3);
		assertEquals(0, fstates[3].pos[1], 1e-3);
		assertEquals(0, fstates[3].pos[2], 1e-3);
		// Verify that the quaternion never changes
		assertEquals(0, fstates[0].orient.getX(), 1e-3);
		assertEquals(0.707, fstates[0].orient.getY(), 1e-3);
		assertEquals(0, fstates[0].orient.getZ(), 1e-3);
		assertEquals(0.707, fstates[0].orient.getW(), 1e-3);
		assertEquals(0, fstates[1].orient.getX(), 1e-3);
		assertEquals(0.707, fstates[1].orient.getY(), 1e-3);
		assertEquals(0, fstates[1].orient.getZ(), 1e-3);
		assertEquals(0.707, fstates[1].orient.getW(), 1e-3);
		assertEquals(0, fstates[2].orient.getX(), 1e-3);
		assertEquals(0.707, fstates[2].orient.getY(), 1e-3);
		assertEquals(0, fstates[2].orient.getZ(), 1e-3);
		assertEquals(0.707, fstates[2].orient.getW(), 1e-3);
		assertEquals(0, fstates[3].orient.getX(), 1e-3);
		assertEquals(0.707, fstates[3].orient.getY(), 1e-3);
		assertEquals(0, fstates[3].orient.getZ(), 1e-3);
		assertEquals(0.707, fstates[3].orient.getW(), 1e-3);
	}

	@Test
	public void testOrientationOnly() {
		FullState[] fstates =
				FullState.createTrajectoryTrapezoid(new double[] {2, 0, 0},
						new double[] {2, 0, 0},
						1.0 /*max vel*/, 1.0 /*max acc*/,
						new Quaternion(0, 0, 0, 1),
						new Quaternion(0.707, 0.707, 0, 0),
						5 * 3.1415 / 180.0 /*max deg vel*/,
						3.1415 / 20 /*max deg acc*/);
		assertEquals(fstates.length, 4);
		// Verify that the position always stays the same
		assertEquals(2, fstates[0].pos[0], 1e-3);
		assertEquals(0, fstates[0].pos[1], 1e-3);
		assertEquals(0, fstates[0].pos[2], 1e-3);
		assertEquals(2, fstates[1].pos[0], 1e-3);
		assertEquals(0, fstates[1].pos[1], 1e-3);
		assertEquals(0, fstates[1].pos[2], 1e-3);
		assertEquals(2, fstates[2].pos[0], 1e-3);
		assertEquals(0, fstates[2].pos[1], 1e-3);
		assertEquals(0, fstates[2].pos[2], 1e-3);
		assertEquals(2, fstates[3].pos[0], 1e-3);
		assertEquals(0, fstates[3].pos[1], 1e-3);
		assertEquals(0, fstates[3].pos[2], 1e-3);
		// Verify that the first and last quaternions are correct
		assertEquals(0, fstates[0].orient.getX(), 1e-3);
		assertEquals(0, fstates[0].orient.getY(), 1e-3);
		assertEquals(0, fstates[0].orient.getZ(), 1e-3);
		assertEquals(1, fstates[0].orient.getW(), 1e-3);
		assertEquals(0.707, fstates[3].orient.getX(), 1e-3);
		assertEquals(0.707, fstates[3].orient.getY(), 1e-3);
		assertEquals(0, fstates[3].orient.getZ(), 1e-3);
		assertEquals(0, fstates[3].orient.getW(), 1e-3);
	}
}
