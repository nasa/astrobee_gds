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
package gov.nasa.rapid.v2.e4.util;

import gov.nasa.dds.rti.util.TypeSupportUtil;

import java.util.ArrayList;

import rapid.Ack;
import rapid.AgentConfig;
import rapid.AgentState;
import rapid.Command;
import rapid.CommandConfig;
import rapid.CommandDef;
import rapid.CommandDefSequence;
import rapid.CommandRecord;
import rapid.FloatRangeValue;
import rapid.FloatSequence64;
import rapid.FrameDef;
import rapid.FrameStoreConfig;
import rapid.Header;
import rapid.ImageMetadata;
import rapid.ImageSensorSample;
import rapid.ImageSensorState;
import rapid.JointConfig;
import rapid.JointDef;
import rapid.JointDefSequence;
import rapid.JointSample;
import rapid.KeyTypeSequence16;
import rapid.KeyTypeValueSequence16;
import rapid.KeyTypeValueSequence64;
import rapid.LongSequence64;
import rapid.MacroConfig;
import rapid.MacroState;
import rapid.Mat33f;
import rapid.Message;
import rapid.NFSeqSequence16;
import rapid.NFSeqSequence32;
import rapid.NamedFloatRangeValueSequence16;
import rapid.NamedOptionSetValueSequence16;
import rapid.OptionSetValue;
import rapid.ParameterSequence16;
import rapid.ParameterUnion;
import rapid.PointCloudConfig;
import rapid.PointCloudSample;
import rapid.PositionConfig;
import rapid.PositionSample;
import rapid.QueueState;
import rapid.SingleQueue;
import rapid.Subsystem;
import rapid.SubsystemType;
import rapid.TextMessage;
import rapid.Transform3D;
import rapid.ValueSequence64;
import rapid.Vec3d;
import rapid.ext.NavMapConfig;
import rapid.ext.NavMapSample;
import rapid.ext.ProcessIoSample;
import rapid.ext.ProcessManagerConfig;
import rapid.ext.ProcessManagerState;
import rapid.ext.RTrans2DMeta;
import rapid.ext.RangeScanConfig;
import rapid.ext.RangeScanSample;
import rapid.ext.SystemInfoConfig;
import rapid.ext.SystemInfoSample;
import rapid.ext.Trajectory2DConfig;
import rapid.ext.Trajectory2DSample;
import rapid.ext.TrajectoryConfig;
import rapid.ext.TrajectorySample;
import rapid.ext.VEStopConfig;
import rapid.ext.VEStopState;

public class MessageSizeUtil {
    public static MessageSizeUtil INSTANCE = new MessageSizeUtil();

    protected final ArrayList<Class> m_classes = new ArrayList<Class>();


    public MessageSizeUtil() {
        initDefaultClasses();
    }

    public void printMinMaxSerializedSizes() {
        for(Class clazz : m_classes) {
            System.out.println(String.format("%25s: %8d - %8d", 
                                             clazz.getSimpleName(), 
                                             TypeSupportUtil.getMinSerializedSizeFor(clazz),
                                             TypeSupportUtil.getMaxSerializedSizeFor(clazz)) );
        }
    }

    public void printMinMaxSerializedSizesCSV() {
        System.out.println("#struct,\"min size(bytes)\",\"max size(bytes)\"");
        for(Class clazz : m_classes) {
            System.out.println(String.format("\"%s\",%d,%d", 
                                             clazz.getSimpleName(), 
                                             TypeSupportUtil.getMinSerializedSizeFor(clazz),
                                             TypeSupportUtil.getMaxSerializedSizeFor(clazz)) );
        }
    }

    public void initDefaultClasses() {
        m_classes.clear();
        m_classes.add(Header.class);
        m_classes.add(Message.class);
        m_classes.add(Ack.class);
        m_classes.add(AgentConfig.class);
        m_classes.add(AgentState.class);
        m_classes.add(Command.class);
        m_classes.add(CommandConfig.class);
        m_classes.add(CommandDef.class);
        m_classes.add(CommandDefSequence.class);
        m_classes.add(Subsystem.class);
        m_classes.add(SubsystemType.class);
        m_classes.add(FrameStoreConfig.class);
        m_classes.add(FrameDef.class);
        m_classes.add(ImageMetadata.class);
        m_classes.add(ImageSensorSample.class);
        m_classes.add(ImageSensorState.class);
        m_classes.add(JointConfig.class);
        m_classes.add(JointDef.class);
        m_classes.add(JointDefSequence.class);
        m_classes.add(JointSample.class);
        m_classes.add(MacroConfig.class);
        m_classes.add(MacroState.class);
        m_classes.add(NavMapConfig.class);
        m_classes.add(NavMapSample.class);
        m_classes.add(PointCloudConfig.class);
        m_classes.add(PointCloudSample.class);
        m_classes.add(PositionConfig.class);
        m_classes.add(PositionSample.class);
        m_classes.add(ProcessIoSample.class);
        m_classes.add(ProcessManagerConfig.class);
        m_classes.add(ProcessManagerState.class);
        m_classes.add(QueueState.class);
        m_classes.add(SingleQueue.class);
        m_classes.add(CommandRecord.class);
        m_classes.add(RangeScanConfig.class);
        m_classes.add(RangeScanSample.class);
        m_classes.add(SystemInfoConfig.class);
        m_classes.add(SystemInfoSample.class);
        m_classes.add(TextMessage.class);
        m_classes.add(Trajectory2DConfig.class);
        m_classes.add(Trajectory2DSample.class);
        m_classes.add(RTrans2DMeta.class);
        m_classes.add(TrajectoryConfig.class);
        m_classes.add(TrajectorySample.class);
        m_classes.add(VEStopConfig.class);
        m_classes.add(VEStopState.class);

        m_classes.add(Vec3d.class);
        m_classes.add(Mat33f.class);
        m_classes.add(Transform3D.class);
        m_classes.add(FloatSequence64.class);
        m_classes.add(LongSequence64.class);
        m_classes.add(NFSeqSequence16.class);
        m_classes.add(NFSeqSequence32.class);
        m_classes.add(NamedFloatRangeValueSequence16.class);
        m_classes.add(FloatRangeValue.class);
        m_classes.add(NamedOptionSetValueSequence16.class);
        m_classes.add(OptionSetValue.class);
        m_classes.add(KeyTypeSequence16.class);
        m_classes.add(KeyTypeValueSequence16.class);
        m_classes.add(KeyTypeValueSequence64.class);
        m_classes.add(ParameterUnion.class);
        m_classes.add(ParameterSequence16.class);
        m_classes.add(ValueSequence64.class);
    }

}
