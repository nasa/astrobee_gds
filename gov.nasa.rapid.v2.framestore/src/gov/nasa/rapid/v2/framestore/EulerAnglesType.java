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
package gov.nasa.rapid.v2.framestore;

/**
 * EulerAnglesType allows to express the 24 conventions for Euler angles using 4
 * parameters: first axis, parity, repetition and reference.
 * 
 * <dl>
 * <dt>InnerAxis:</dt>
 * <dd>defines the axis around which the first rotation is performed</dd>
 * <dt>Parity:</dt>
 * <dd>indicates if the successions of rotation axis is regular or reversed. For
 * example, XYZ id "EVEN" and XZY is "ODD"</dd>
 * <dt>Repetition</dt>
 * <dd>indicates if the third rotation re-use the first axis or not. For example
 * XYZ will be "SAME", and XYX will be "DIFF"
 * <dt>Frame:</dt>
 * <dd>defines if the rotations are performed around the fixed reference frame
 * (STATIC) or rotating frame (ROTATION)</dd>
 * </dl>
 * 
 * <i>Warning: this class is currently being built and only supports 2 set of
 * Euler angles!</i>
 * 
 * @author Lorenzo Flueckiger
 * 
 */
public class EulerAnglesType {

    public enum InnerAxis {
        X, Y, Z
    }

    public enum Parity {
        EVEN, ODD
    }

    public enum Repetition {
        DIFF, SAME
    }

    public enum Frame {
        STATIC, ROTATION
    }

    public EulerAnglesType(ReadOnlyEulerAngles.Type type) {
        switch (type) {
        case XYZs:
            set(InnerAxis.X, Parity.EVEN, Repetition.DIFF, Frame.STATIC);
            break;
        case ZYXr:
            set(InnerAxis.X, Parity.EVEN, Repetition.DIFF, Frame.STATIC);
            break;
        default:
            System.err.println("Non existant EulerAngle Type!");
        }
    }

    protected void set(InnerAxis axis, Parity parity, Repetition repetition,
            Frame frame) {
        m_innerAxis = axis;
        m_parity = parity;
        m_repetition = repetition;
        m_frame = frame;
    }

    protected InnerAxis m_innerAxis;
    protected Parity m_parity;
    protected Repetition m_repetition;
    protected Frame m_frame;

}
