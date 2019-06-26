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
package gov.nasa.arc.irg.rapid.ui.e4.view;

import gov.nasa.arc.irg.util.NameValue;
import gov.nasa.rapid.v2.e4.message.MessageType;

import org.apache.log4j.Logger;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;

import rapid.JointConfig;
import rapid.JointSample;

public class JointsPart extends AbstractTelemetryTablePart {
	private static final Logger logger = Logger.getLogger(JointsPart.class);
	private JointConfig m_configObject = null;
	private MessageType m_sampleType = MessageType.getTypeFromTopic("rapid_position_sample");
	private MessageType m_configType = MessageType.getTypeFromTopic("rapid_position_config");
	
	public JointsPart(Composite parent, EPartService eps) {
		super(parent, eps);
	}

	@Override
	protected Object getConfigObject() {
		return m_configObject;
	}

	@Override
	protected void setConfigObject(Object config) {
		if (config == null){
			m_configObject = null;
		}
		if (config instanceof JointConfig){
			m_configObject = (JointConfig)config;
		}
	}

	@Override
	protected Class getConfigClass() {
		return JointConfig.class;
	}

	@Override
	protected String getMementoKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected NameValue[] getNameValues(Object input) {
		// Oops, JointSamples are more complicated than just Name/Value
		
//		if (input != null && input instanceof PositionSample){
//			PositionSample sample = (PositionSample)input;
//			int sampleID = sample.hdr.serial;
//			int configID = m_configObject.hdr.serial;
//            if (configID == sampleID){
//            	m_positionSampleHelper.setData(sample, m_configObject);
//            	
//            	int size = 6;
//            	 if (sample.values != null){
//                 	ValueSequence64 vals = sample.values;
//                 	size += vals.userData.size();
//                 }
//            	 
//            	NameValue[] result = new NameValue[size];
//            	double[] xyz = m_positionSampleHelper.getXyz();
//            	result[0] = new NameValue(X, xyz[0], true);
//            	result[1] = new NameValue(Y, xyz[1], true);
//            	result[2] = new NameValue(Z, xyz[2], true);
//                
//                double[] rotations = m_positionSampleHelper.getRpy();
//                result[3] = new NameValue(ROLL, Math.toDegrees(rotations[0]), true);
//                result[4] = new NameValue(PITCH, Math.toDegrees(rotations[1]), true);
//                result[5] = new NameValue(YAW, Math.toDegrees(rotations[2] % 360), true);
//                
//                
//                if (sample.values != null){
//                	ValueSequence64 vals = sample.values;
//                	int index = 6;
//                	Iterator iter = vals.userData.iterator();
//                	while (iter.hasNext()){
//                		ParameterUnion pu = (ParameterUnion)iter.next();
//                		Object obj = null;
//                		 int ordinal = pu._d.ordinal();
//                         switch(ordinal) {
//	                         case DataType._RAPID_BOOL:      obj = pu.b; break;
//	                         case DataType._RAPID_DOUBLE:    obj = pu.d; break;
//	                         case DataType._RAPID_FLOAT:     obj = pu.f; break;
//	                         case DataType._RAPID_INT:       obj = pu.i; break;
//	                         case DataType._RAPID_LONGLONG:  obj = pu.ll; break;
//	                         case DataType._RAPID_MAT33f:    obj = StrUtil.arrayToString(pu.mat33f.userData, ","); break;
//	                         case DataType._RAPID_STRING:    obj = pu.s; break;
//	                         case DataType._RAPID_VEC3d:     obj = StrUtil.arrayToString(pu.vec3d.userData, ","); break;
//                         }
//                         if (obj != null){
//                        	 result[index] = new NameValue("", obj, false);
//                        	 index++;
//                         }
//                	}
//                }
//                return result;
//            } else {
//                logger.info(" - received position, but serial id does not match that of Config.");
//                logger.info("   sample.hdr.serial="+sample.hdr.serial+"  config.hdr.serial="+ m_configObject.hdr.serial);
//                
//                System.out.println(" - received position, but serial id does not match that of Config.");
//                System.out.println("   sample.hdr.serial="+sample.hdr.serial+"  config.hdr.serial="+ m_configObject.hdr.serial);
//                
//            }
//			
//		}
		return null;
	}

	@Override
	protected boolean configIdMatchesSampleId(Object configObj, Object eventObj) {
		if (configObj == null){
            logger.warn(" - receieved joints, but Config is null");
            return false;
        }
        final JointConfig config = (JointConfig)configObj;
        int configID = config.hdr.serial;
        
        final JointSample sample = (JointSample)eventObj;
        int sampleID = sample.hdr.serial;
        // XXX Huh???
        if (configID == sampleID){
//        	m_timestamp.updateText(getSimpleTime(sample.hdr.timeStamp));
			return true;
        }
		return false;
	}

	@Override
	protected MessageType getSampleType() {
		return m_sampleType;
	}

	@Override
	protected MessageType getConfigType() {
		return m_configType;
	}

	@Override
	protected void onSubtopicSelected(String subtopic) {
		// TODO Auto-generated method stub
		
	}

}
