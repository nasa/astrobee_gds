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
package gov.nasa.arc.verve.robot.persist;

import gov.nasa.arc.verve.robot.AbstractRobot;
import gov.nasa.arc.verve.robot.parts.IRobotPart;
import gov.nasa.util.ThreadUtils;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URI;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ardor3d.math.ColorRGBA;

/**
 * Persist robot part values to xml 
 */
public class RobotPropertiesPersistence {
    public static final String      EXT    = ".parts";
    final private static Logger     logger = Logger.getLogger(RobotPropertiesPersistence.class);

    protected static final String[] IGNORE = { "Dirty" };
    protected static final String   POSE_PROVIDER = "PoseProvider";

    static String robotTagName(String robotName) {
        return robotName.replaceAll("/", "-");
    }
    static String tagToRobotName(String tagName) {
        return tagName.replaceAll("-", "/");
    }
    static String partTagName(String partName) {
        return partName.replaceAll(" ", "_");
    }
    static String tagToPartName(String tagName) {
        return tagName.replaceAll("_", " ");
    }

    public static void write(URI pathUri, AbstractRobot robot) {
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element root = doc.createElement(robotTagName(robot.getName()));
            doc.appendChild(root);

            //-- save PoseProvider state
            Element child = doc.createElement(POSE_PROVIDER);
            setElementFromObject(child, robot.getPoseProvider());
            root.appendChild(child);

            //-- save all RobotPart state
            for (String partName : robot.getPartNames()) {
                IRobotPart part  = robot.getPart(partName);
                child = doc.createElement(partTagName(partName));
                setElementFromObject(child, part);
                root.appendChild(child);
            }

            //== Output the XML ======================
            //-- set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //--create string from xml tree
            FileWriter fw = null;
            try {
                URI  fileUri = pathUri.resolve(robot.getName()+EXT);
                File file    = new File(fileUri);
                FileUtils.forceMkdir(file.getParentFile());
                fw = new FileWriter(file);
                StreamResult result = new StreamResult(fw);
                DOMSource source = new DOMSource(doc);
                trans.transform(source, result);
                //logger.debug("Saved robot properties for "+robot.getName()+" to "+file.toString());
            }
            catch (Exception ex) {
                logger.warn("Document write error", ex);
            }
            finally {
                if (fw != null) {
                    fw.close();
                }
            }
        }
        catch (Exception e) {
            logger.warn("Write error", e);
        }
    }

    /**
     * Set AbstractRobot state from xml persistence file 
     * @param pathUri
     * @param robot
     */
    public static void read(URI pathUri, AbstractRobot robot) {
        URI fileUri = pathUri.resolve(robot.getName()+EXT);
        File file = new File(fileUri);
        if (!file.exists()) {
            return;
        }

        try {    
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.parse(file);

            //-- Reading the XML tree
            doc.getDocumentElement().normalize();
            NodeList rootList = doc.getElementsByTagName(robotTagName(robot.getName()));

            if (rootList.getLength() <= 0) {
                return;
            }

            Element root = (Element) rootList.item(0);
            NodeList partElements = root.getChildNodes();

            for (int i = 0; i < partElements.getLength(); i++) {
                Node partNode = partElements.item(i);
                if (partNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element    element     = (Element) partNode;
                    String     elementName = element.getNodeName();
                    if(elementName.equals(POSE_PROVIDER)) {
                        setObjectFromElement(robot.getPoseProvider(), element);
                    }
                    else {
                        IRobotPart part = robot.getPart(tagToPartName(elementName));
                        if (part != null) {
                            setObjectFromElement(part, element);
                        }
                        else {  
                            logger.warn(robot.getName()+" has no part named \""+tagToPartName(elementName)+"\"");
                        }
                    }
                }
            }
            //logger.debug("Read robot properties for "+robot.getName()+" from "+file.toString());

        }
        catch (Exception e) {
            logger.warn("Read error:"+fileUri.toString()+" "+ThreadUtils.traceCaller()+" - "+e.getMessage());
        }
    }

    /**
     * 
     * @param child
     * @param instance
     */
    protected static void setElementFromObject(Element child, Object instance) {
        try {
            Class clazz = instance.getClass();
            for (Method method : clazz.getMethods()) {
                if (MethodUtil.isGetMethod(method)) {
                    String suffix = MethodUtil.getSuffix(method);
                    if (!shouldIgnore(suffix)) {
                        Class returnType = method.getReturnType();
                        if (true) { // MethodUtil.isReturnTypeSimple(returnType)){
                            try {
                                Method setMethod = MethodUtil.getSetMethod(clazz, suffix, returnType);
                                if (setMethod != null) {
                                    Object value = null;
                                    String valString = "";
                                    value = method.invoke(instance, new Object[] {});
                                    if (value != null) {
                                        valString = value.toString();
                                    }
                                    child.setAttribute(suffix, valString);
                                }
                            }
                            catch (Exception ex) {
                                // ex.printStackTrace();
                            }
                        }
                    }

                }
                else if (MethodUtil.isIsMethod(method)) {
                    String suffix = MethodUtil.getSuffix(method);
                    if (!shouldIgnore(suffix)) {
                        try {
                            Method setMethod = MethodUtil.getSetMethod(instance.getClass(), 
                                                                       suffix,
                                                                       method.getReturnType());
                            if (setMethod != null) {
                                Object value = null;
                                String valString = "";
                                try {
                                    value = method.invoke(instance, new Object[] {});
                                    if (value != null) {
                                        valString = value.toString();
                                    }
                                    child.setAttribute(suffix, valString);
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                        catch (SecurityException e) {
                            // logger.warn(e);
                        }
                        catch (NoSuchMethodException e) {
                            // logger.warn(e);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.warn("Document creation error", e);
        }
    }

    /**
     * Set Object instance state from Element values
     * @param instance
     * @param element
     */
    static void setObjectFromElement(Object instance, Element element) {
        //String elementName = element.getNodeName();
        Class  clazz   = instance.getClass();
        NamedNodeMap partChildren = element.getAttributes();
        try {
            for (int j = 0; j < partChildren.getLength(); j++) {
                Node   partChildNode  = partChildren.item(j);
                String partChild      = partChildNode.getNodeName();
                String partChildValue = partChildNode.getNodeValue();

                Method getMethod = null;
                try {
                    getMethod = MethodUtil.getGetMethod(clazz, partChild);
                }
                catch (NoSuchMethodException nsme) {
                    // empty
                }
                if (getMethod != null) {
                    Class returnType = MethodUtil.getReturnType(clazz, partChild);
                    Object converted = MethodUtil.convertStringToType(partChildValue, returnType);
                    if (converted == null) {
                        converted = convertSpecial(partChildValue, returnType);
                    }
                    if (converted != null) {
                        try {
                            Method setMethod = MethodUtil.getSetMethod(clazz, partChild, returnType);
                            if (setMethod != null) {
                                setMethod.invoke(instance, new Object[] { converted });
                            }
                        }
                        catch (NoSuchMethodException nsme) {
                            // empty
                        }
                    }
                }
                else {
                    Method isMethod = null;
                    try {
                        isMethod = MethodUtil.getIsMethod(clazz, partChild);
                    }
                    catch (NoSuchMethodException nsme) {
                        // empty
                    }

                    if (isMethod != null) {
                        boolean value = Boolean.parseBoolean(partChildValue.toLowerCase());
                        try {
                            Method setMethod = MethodUtil.getSetMethod(clazz, partChild,
                                                                       Boolean.TYPE);
                            if (setMethod != null) {
                                setMethod.invoke(instance, new Object[] { value });
                            }
                        }
                        catch (NoSuchMethodException nsme) {
                            // empty
                        }

                    }
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    /*
     * get the float value after the given key, followed by a space. ie R=1.0,
     * G=1.0, B=1.0, A=1.0]
     */
    public static float getValueAfter(String key, String string) {
        try {
            String clean = string.substring(string.indexOf(key) + key.length());
            String reallyClean = clean;
            int commaIndex = clean.indexOf(",");
            if (clean.indexOf(",") > 0) {
                reallyClean = clean.substring(0, commaIndex);
            }
            else {
                reallyClean = clean.substring(0, clean.indexOf("]"));
            }
            return Float.parseFloat(reallyClean);
        }
        catch (Exception ex) {
            return 1.0f;
        }
    }

    /*
     * If MethodUtil failed to convert this, try some special types. hackity
     * hack hack.
     */
    @SuppressWarnings("unchecked")
    public static Object convertSpecial(String string, Class type) {
        if (type.isAssignableFrom(ColorRGBA.class)) {
            float red   = getValueAfter("R=", string);
            float green = getValueAfter("G=", string);
            float blue  = getValueAfter("B=", string);
            float alpha = getValueAfter("A=", string);
            return new ColorRGBA(red, green, blue, alpha);
        }
        return null;
    }

    protected static boolean shouldIgnore(String me) {
        for (String comparee : IGNORE) {
            if (comparee.equals(me)) {
                return true;
            }
        }
        return false;
    }
}
