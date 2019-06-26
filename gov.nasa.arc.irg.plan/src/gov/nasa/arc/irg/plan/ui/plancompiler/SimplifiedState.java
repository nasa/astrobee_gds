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
package gov.nasa.arc.irg.plan.ui.plancompiler;

public class SimplifiedState {
  public double t, x, x_dot, x_dotdot;

  public SimplifiedState(double t, double x, double x_dot, double x_dotdot) {
    this.t = t;
    this.x = x;
    this.x_dot = x_dot;
    this.x_dotdot = x_dotdot;
  }

  public static SimplifiedState
  createInterpolatedState(SimplifiedState delta,
                          double dt) {
    return new SimplifiedState(delta.t + dt,
                               delta.x + delta.x_dot * dt +
                               0.5 * delta.x_dotdot * dt * dt,
                               delta.x_dot + delta.x_dotdot * dt,
                               delta.x_dotdot);
  }

  public static SimplifiedState[]
  createTrajectoryTrapezoid(double x_0, double x_1, double x_dot, double x_dotdot) {
    // The trapezoid is:
    //     /--------\
    //   /            \
    //  * a    b    c  *
    //  A = Ramp up
    //  B = At Velocity
    //  C = Ramp down

    // Fix direction for derivatives
    x_dot = Math.abs(x_dot);
    x_dotdot = Math.abs(x_dotdot);
    if (x_1 < x_0) {
      x_dot = -x_dot;
      x_dotdot = -x_dotdot;
    }

    // See what the max velocity is that we can get up to. If it is lower than
    // what the user specified, use it instead.
    double half_delta_x = (x_1 - x_0) / 2.0;
    double time_to_middle = Math.sqrt(2 * half_delta_x / x_dotdot);
    double x_dot_max = x_dotdot * time_to_middle;
    if (Math.abs(x_dot_max) < Math.abs(x_dot)) {
      x_dot = x_dot_max;
    }

    // Solve for time needed to get up to x_dot
    // x_dot_max = x_dot_starting + x_dotdot * t
    double time_ramp_up = x_dot / x_dotdot;

    // Solve for the position change that happened during ramp up and ramp down.
    double delta_x = x_dotdot * time_ramp_up * time_ramp_up;

    // Solve for the time duration at velocity
    double delta_x_remaining = x_1 - x_0 - delta_x;
    double time_at_velocity = 0;
    if (Math.abs(delta_x_remaining) > 1e-9) {
      time_at_velocity = delta_x_remaining / x_dot;
    }

    // Render the trajectory
    SimplifiedState[] states;
    if (time_ramp_up < 1e-3) {
      // There is no time to ramp up, this means we are stationary.
      states = new SimplifiedState[1];
      states[0] = new SimplifiedState(0.0, x_0, 0.0, 0.0);
    } else if (time_at_velocity < 1e-3) {
      // Only need a ramp up, ramp down, stop
      states = new SimplifiedState[3];
      states[0] = new SimplifiedState(0.0, x_0, 0.0, x_dotdot);
      states[1] = new SimplifiedState(time_ramp_up, x_0 + delta_x / 2.0, x_dot, -x_dotdot);
      states[2] = new SimplifiedState(time_ramp_up * 2, x_1, 0.0, 0.0);
    } else {
      // Need also the cruise phase
      states = new SimplifiedState[4];
      states[0] = new SimplifiedState(0.0, x_0, 0.0, x_dotdot);
      states[1] = new SimplifiedState(time_ramp_up, x_0 + delta_x / 2.0, x_dot, 0);
      states[2] = new SimplifiedState(time_ramp_up + time_at_velocity,
                                      x_0 + delta_x / 2.0 + delta_x_remaining,
                                      x_dot, -x_dotdot);
      states[3] = new SimplifiedState(2 * time_ramp_up + time_at_velocity, x_1, 0.0, 0.0);
    }
    return states;
  }
}

