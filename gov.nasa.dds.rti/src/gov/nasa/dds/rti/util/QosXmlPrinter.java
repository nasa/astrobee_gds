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
package gov.nasa.dds.rti.util;

import gov.nasa.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Converts a QoS struct to an XML string
 * @author mallan
 *
 */
public class QosXmlPrinter {

    public final ArrayList<String> topLevelIgnores = new ArrayList<String>();
    public static final String INDENT = "    ";

    public QosXmlPrinter() {
        // default ignores
        topLevelIgnores.add("id");
        topLevelIgnores.add("policy_name");
    }

    /**
     * convert QoS object to XML String
     * @param obj
     * @return
     */
    public String toString(Object obj) {
        return toString(obj, "");
    }

    public String toString(Object obj, String indent) {
        StringBuilder builder = new StringBuilder();
        String qosName = qosName(obj.getClass());
        builder.append(indent).append("<"+qosName+">\n");
        printObject(obj, builder, indent+INDENT, topLevelIgnores);
        builder.append(indent).append("</"+qosName+">\n");
        return builder.toString();
    }

    protected String qosName(Class clazz) {
        if(clazz.getSimpleName().equals("DataWriterQos")) {
            return "datawriter_qos";
        }
        if(clazz.getSimpleName().equals("DataReaderQos")) {
            return "datareader_qos";
        }
        if(clazz.getSimpleName().equals("DomainParticipantQos")) {
            return "participant_qos";
        }
        return clazz.getSimpleName();
    }

    protected static List<Field> getAllPublicFields(Class clazz) {
        LinkedList<Field> retVal = new LinkedList<Field>();
        for(Field field : ReflectionUtils.getAllFields(clazz, false)) {
            if(Modifier.isPublic(field.getModifiers()) ) {
                retVal.add(field);
            }
        }
        return retVal;
    }

    protected void printObject(Object obj, StringBuilder builder, final String indent, ArrayList<String> ignoreFields) {
        Class clazz = obj.getClass();
        List<Field> fields = getAllPublicFields(clazz);
        for(Field field : fields) {
            try {
                String fieldName = field.getName();
                if(ignoreFields != null && ignoreFields.contains(fieldName)) {
                    continue;
                }
                Object fieldObj  = field.get(obj);
                if(fieldObj == null) {
                    builder.append(indent).append("<!--"+fieldName+">NULL</"+fieldName+"-->\n");
                }
                else {
                    int numFieldFields = getAllPublicFields(fieldObj.getClass()).size();
                    if(numFieldFields > 0) {
                        // recurse
                        builder.append(indent).append("<"+fieldName+">\n");
                        printObject(fieldObj, builder, indent+INDENT, ignoreFields);
                        builder.append(indent).append("</"+fieldName+">\n");
                    }
                    else {
                        builder.append(indent).append("<"+fieldName+">");
                        if(fieldObj instanceof List) {
                            printList((List)fieldObj, builder, indent);
                        }
                        else {
                            builder.append(fieldObj.toString());                        
                        }
                        //builder.append(" class="+fieldObj.getClass().getSimpleName());
                        builder.append("</"+fieldName+">\n");
                    }
                }
            }
            catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }

    protected void printList(List list, StringBuilder builder, String indent) {
        if(list.size() > 0) {
            Object obj = list.get(0);
            int numFields = getAllPublicFields(obj.getClass()).size();
            if(numFields > 0) {
                printObjectList(list, builder, indent+INDENT);
                builder.append(indent);
            }
            else {
                for(int i = 0; i < list.size(); i++) {
                    if(i > 0) { builder.append(","); }
                    builder.append(list.get(i).toString());
                }
            }
        }
    }
    
    protected void printObjectList(List list, StringBuilder builder, String indent) {
        builder.append("\n");
        for(int i = 0; i < list.size(); i++) {
            builder.append(indent).append("<element>\n");
            printObject(list.get(i), builder, indent+INDENT, null);
            builder.append(indent).append("</element>\n");
        }
    }
}
