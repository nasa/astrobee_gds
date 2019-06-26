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

import gov.nasa.arc.irg.plan.ui.plancompiler.Quaternion;

import org.junit.Test;

import junit.framework.TestCase;

public class TestQuaternion extends TestCase {
  @Test
  public void testCreation() {
    Quaternion q = new Quaternion(0.4, 0.5, -0.3, 0.1);
    assertEquals(q.vec[0], 0.4, 1e-3);
    assertEquals(q.vec[1], 0.5, 1e-3);
    assertEquals(q.vec[2], -0.3, 1e-3);
    assertEquals(q.scalar, 0.1, 1e-3);
  }

  @Test
  public void testInverse() {
    Quaternion q1 = new Quaternion(.577, .577, -.577, 0);
    Quaternion q2 = Quaternion.inverse(q1);
    assertEquals(q1.vec[0], -q2.vec[0]);
    assertEquals(q1.vec[1], -q2.vec[1]);
    assertEquals(q1.vec[2], -q2.vec[2]);
  }

  @Test
  public void testAngleAxis() {
    Quaternion q = new Quaternion(-0.342, 0.456, 0.684, 0.456);
    double angle = Quaternion.angle(q);
    assertEquals(angle, 125.762 * 3.14159 / 180.0, 1e-3);
    double[] axis = Quaternion.axis(q);
    assertEquals(axis[0], -0.384, 1e-3);
    assertEquals(axis[1], 0.512, 1e-3);
    assertEquals(axis[2], 0.769, 1e-3);
    Quaternion q2 = Quaternion.axisangle(angle, axis);
    assertEquals(q2.vec[0], -0.342, 1e-3);
    assertEquals(q2.vec[1], 0.456, 1e-3);
    assertEquals(q2.vec[2], 0.684, 1e-3);
    assertEquals(q2.scalar, 0.456, 1e-3);
  }

  @Test
    public void testMultiply() {
      Quaternion q1 = new Quaternion(0, 0, 0.707, 0.707);
      Quaternion q2 = new Quaternion(-0.342, 0.456, 0.684, 0.456);
      Quaternion q3 = Quaternion.multiply(q1, q2);
      // q3 = q1 * q2
      // q1' * q3 = q2
      // q3 * q2' = q1
      Quaternion q2_loopback = Quaternion.multiply(Quaternion.inverse(q1), q3);
      Quaternion q1_loopback = Quaternion.multiply(q3, Quaternion.inverse(q2));
      // So .. there's 2 representations for every quaternion. I should
      // probably be robust against that.
      assertEquals(q1.vec[2], q1_loopback.vec[2], 1e-3);
      assertEquals(q1.scalar, q1_loopback.scalar, 1e-3);
      assertEquals(q2.vec[2], q2_loopback.vec[2], 1e-3);
      assertEquals(q2.scalar, q2_loopback.scalar, 1e-3);
    }
  
  @Test
  public void testCreationFromEulerAngles() {
	  Quaternion forward = new Quaternion(0,0,0);
	  Quaternion aft = new Quaternion(0, 0, Math.PI);
	  Quaternion port = new Quaternion(0, 0, -Math.PI/2.0);
	  Quaternion starboard = new Quaternion(0, 0, Math.PI/2.0);
	  Quaternion overhead = new Quaternion(0, Math.PI/2.0, 0);
	  Quaternion deck = new Quaternion(0, -Math.PI/2.0, 0);
	  Quaternion rollright = new Quaternion(Math.PI/2.0, 0, 0);
	  Quaternion rollleft = new Quaternion(-Math.PI/2.0, 0, 0);
  
	  assertEquals("forward", new Quaternion(0,0,0,1), forward);
	  assertEquals("aft", new Quaternion(0,0,1,0), aft);
	  assertEquals("port", new Quaternion(0,0,-0.7071,0.7071), port);
	  assertEquals("starboard", new Quaternion(0,0,0.7071,0.7071), starboard);
	  assertEquals("overhead", new Quaternion(0,0.7071,0,0.7071), overhead);
	  assertEquals("deck", new Quaternion(0,-0.7071,0,0.7071), deck);
	  assertEquals("roll right", new Quaternion(0.7071,0,0,0.7071), rollright);
	  assertEquals("roll left", new Quaternion(-0.7071,0,0,0.7071), rollleft);
  }
}
