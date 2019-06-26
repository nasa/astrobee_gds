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

import gov.nasa.arc.irg.plan.ui.plancompiler.SimplifiedState;

import org.junit.Test;

import junit.framework.TestCase;

public class TestSimplifiedState extends TestCase {
  @Test
  public void testCreateTrajectoryTrapezoid() {
    SimplifiedState[] states =
      SimplifiedState.createTrajectoryTrapezoid(-2, 4, 2, 1);
    assertEquals(states.length, 4);
    // Verfiy velocity is zero, but accelerate is not in the first
    assertEquals(states[0].x, -2, 1e-3);
    assertEquals(states[0].x_dot, 0, 1e-3);
    assertEquals(states[0].x_dotdot, 1, 1e-3);
    // Verify that we are not accelerating anymore and are at speed
    assertEquals(states[1].x_dot, 2, 1e-3);
    assertEquals(states[1].x_dotdot, 0, 1e-3);
    // Verify that we are decelerating
    assertEquals(states[2].x_dot, 2, 1e-3);
    assertEquals(states[2].x_dotdot, -1, 1e-3);
    // Verify that the final position is at destination
    assertEquals(states[3].x, 4, 1e-3);
    assertEquals(states[3].x_dot, 0, 1e-3);
    assertEquals(states[3].x_dotdot, 0, 1e-3);
  }

  @Test
  public void testCreateTrajectoryReverse() {
    SimplifiedState[] states =
      SimplifiedState.createTrajectoryTrapezoid(4, -2, 2, 1);
    assertEquals(states.length, 4);
    // Verify velocity is zero, but we are accelerating
    assertEquals(states[0].x, 4, 1e-3);
    assertEquals(states[0].x_dot, 0, 1e-3);
    assertEquals(states[0].x_dotdot, -1, 1e-3);
    // Verify that we are not accerating but are up to speed
    assertEquals(states[1].x_dot, -2, 1e-3);
    assertEquals(states[1].x_dotdot, 0, 1e-3);
    // Verify that we are decelerating
    assertEquals(states[2].x_dot, -2, 1e-3);
    assertEquals(states[2].x_dotdot, 1, 1e-3);
    // Verify that the final position is at destination
    assertEquals(states[3].x, -2, 1e-3);
    assertEquals(states[3].x_dot, 0, 1e-3);
    assertEquals(states[3].x_dotdot, 0, 1e-3);
  }

  @Test
  public void testCreateTrajectoryShort() {
    // In this case, they don't get up to speed.
    SimplifiedState[] states =
      SimplifiedState.createTrajectoryTrapezoid(1, 2, 2, 0.5);
    assertEquals(states.length, 3);
    // Verify velocity is zero, but we are accelerating
    assertEquals(states[0].x, 1, 1e-3);
    assertEquals(states[0].x_dot, 0, 1e-3);
    assertEquals(states[0].x_dotdot, 0.5, 1e-3);
    // Verify that we are decelerating
    assertEquals(states[1].x_dotdot, -0.5, 1e-3);
    // Verify that the final position is at destination
    assertEquals(states[2].x, 2, 1e-3);
    assertEquals(states[2].x_dot, 0, 1e-3);
    assertEquals(states[2].x_dotdot, 0, 1e-3);
  }

  @Test
  public void testCreateInterpolatedState() {
    SimplifiedState state0 = new SimplifiedState(2, 4, 1, 1);
    SimplifiedState state1 = SimplifiedState.createInterpolatedState(state0, 3);
    assertEquals(state1.t, 2 + 3, 1e-3);
    assertEquals(state1.x_dotdot, 1, 1e-3);
    assertEquals(state1.x_dot, 4, 1e-3);
    assertEquals(state1.x, 4 + 3 + 0.5 * 9, 1e-3);
  }

  @Test
  public void testNullMovementTrajectory() {
    SimplifiedState[] states = SimplifiedState.createTrajectoryTrapezoid(2, 2, 1, 1);
    assertEquals(1, states.length);
    assertEquals(states[0].t, 0, 1e-3);
    assertEquals(states[0].x, 2, 1e-3);
    assertEquals(states[0].x_dot, 0, 1e-3);
    assertEquals(states[0].x_dotdot, 0, 1e-3);
  }
}
