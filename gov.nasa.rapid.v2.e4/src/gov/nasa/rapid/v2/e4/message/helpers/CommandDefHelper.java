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
package gov.nasa.rapid.v2.e4.message.helpers;

import rapid.CommandDef;
import rapid.KeyTypePair;

public class CommandDefHelper {

    /** convert a CommandDef struct to a string representation similar to a method declaration */
    public static String toString(CommandDef cmdDef) {
        StringBuilder sb = new StringBuilder(cmdDef.name);
        sb.append("(");
        for(int i = 0; i < cmdDef.parameters.userData.size(); i++) {
            if(i > 0) 
                sb.append(", ");
            KeyTypePair param = (KeyTypePair)cmdDef.parameters.userData.get(i);
            sb.append(ParamHelper.toString(param.type));
            sb.append(" ");
            sb.append(param.key);
        }
        sb.append(")");
        return sb.toString();
    }
    
}
