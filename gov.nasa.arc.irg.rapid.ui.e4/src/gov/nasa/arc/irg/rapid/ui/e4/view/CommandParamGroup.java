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

import gov.nasa.rapid.v2.e4.message.helpers.ParameterList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Text;

import rapid.DataType;
import rapid.Mat33f;

/**
 * This class holds the parameter name, data type, and Text that will have the value in it
 * @author ddwheele
 *
 */
public class CommandParamGroup {
	private final String paramKey;
	private final DataType paramType;
	private final List<Text> textFields;
	
	public CommandParamGroup(String key, DataType type, Text text) {
		paramKey = key;
		paramType = type;
		textFields = new ArrayList<Text>();
		textFields.add(text);
	}
	
	public CommandParamGroup(String key, DataType type, List<Text> text) {
		paramKey = key;
		paramType = type;
		textFields = text;
	}
	
	public String getKey() {
		return paramKey;
	}
	
	public DataType getDataType() {
		return paramType;
	}
	
	public ParameterList setParameterInCommand(ParameterList pl) { 
		pl.add(paramKey, paramType);
		// DataType extends Enum, WHY can't I use a switch statement?!?!?!?!
		if(paramType.equals(DataType.RAPID_BOOL)) {
			pl.set(paramKey, Boolean.valueOf(textFields.get(0).getText()));
		}
		else if(paramType.equals(DataType.RAPID_DOUBLE)) {
			pl.set(paramKey, Double.valueOf(textFields.get(0).getText()));
		}
		else if(paramType.equals(DataType.RAPID_FLOAT)) {
			pl.set(paramKey, Float.valueOf(textFields.get(0).getText()));
		}
		else if(paramType.equals(DataType.RAPID_INT)) {
			pl.set(paramKey, Integer.valueOf(textFields.get(0).getText()));
		}
		else if(paramType.equals(DataType.RAPID_LONGLONG)) {
			pl.set(paramKey, Long.valueOf(textFields.get(0).getText()));
		}
		else if(paramType.equals(DataType.RAPID_STRING)) {
			pl.set(paramKey, textFields.get(0).getText());
		}
		else if(paramType.equals(DataType.RAPID_VEC3d)) {
			pl.set(paramKey, Double.valueOf(textFields.get(0).getText()),
							 Double.valueOf(textFields.get(1).getText()),
							 Double.valueOf(textFields.get(2).getText()));
		}
		else if(paramType.equals(DataType.RAPID_MAT33f)) {
			Mat33f mat = new Mat33f();
			for(int i=0; i<9; i++) {
				mat.userData[i] = Float.valueOf(textFields.get(i).getText());
			}			
			pl.set(paramKey, mat);
		}
		
		return pl;
	}
}
