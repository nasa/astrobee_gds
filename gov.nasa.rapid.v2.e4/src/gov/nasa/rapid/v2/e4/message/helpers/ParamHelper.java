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

import rapid.DataType;
import rapid.ParameterUnion;
import rapid.ParameterUnionSeq;

public class ParamHelper {

    public static String toString(ParameterUnion param) {
        return toString(param._d)+"("+valueString(param)+")";
    }

    public static String toString(DataType dt) {
        switch(dt.ordinal()) {
        case DataType._RAPID_BOOL    : return "bool";
        case DataType._RAPID_DOUBLE  : return "double";
        case DataType._RAPID_FLOAT   : return "float";
        case DataType._RAPID_INT     : return "int";
        case DataType._RAPID_LONGLONG: return "long long";
        case DataType._RAPID_STRING  : return "String";
        case DataType._RAPID_MAT33f  : return "Mat33f";
        case DataType._RAPID_VEC3d   : return "Vec3d";

        }
        return dt.toString();
    }

    public static String valueString(ParameterUnion param) {
        switch(param._d.ordinal()) {
        case DataType._RAPID_BOOL:    return Boolean.toString(param.b);
        case DataType._RAPID_DOUBLE:  return Double.toString(param.d);
        case DataType._RAPID_FLOAT:   return Float.toString(param.f);
        case DataType._RAPID_INT:     return Integer.toString(param.i);
        case DataType._RAPID_LONGLONG:return Long.toString(param.ll);
        case DataType._RAPID_STRING:  return "\""+param.s+"\"";
        case DataType._RAPID_MAT33f:  return String.format("[%.2f,%.2f,%.2f; %.2f,%.2f,%.2f; %.2f,%.2f,%.2f]",
                                                           param.mat33f.userData[0], param.mat33f.userData[1], param.mat33f.userData[2],
                                                           param.mat33f.userData[3], param.mat33f.userData[4], param.mat33f.userData[5],
                                                           param.mat33f.userData[6], param.mat33f.userData[7], param.mat33f.userData[8]);

        case DataType._RAPID_VEC3d:   return String.format("[%.3f,%.3f,%.3f]",
                                                           param.vec3d.userData[0], param.vec3d.userData[1], param.vec3d.userData[2]);
        default:
            return param.toString();
        }
    }

    public static String valueString(ParameterUnionSeq params) {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < params.size(); i++) {
            if(i > 0) 
                builder.append(", ");
            builder.append(valueString((ParameterUnion)params.get(i)));
        }
        return builder.toString();
    }
}
