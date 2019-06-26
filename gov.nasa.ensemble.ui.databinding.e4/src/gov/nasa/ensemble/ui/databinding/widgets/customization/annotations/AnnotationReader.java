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
package gov.nasa.ensemble.ui.databinding.widgets.customization.annotations;

import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.ensemble.ui.databinding.validation.AngleConverter;
import gov.nasa.ensemble.ui.databinding.validation.ISimpleConverter;
import gov.nasa.ensemble.ui.databinding.validation.LimitsValidator;
import gov.nasa.ensemble.ui.databinding.validation.MultiplierConverter;
import gov.nasa.ensemble.ui.databinding.widgets.customization.ComboEntry;
import gov.nasa.ensemble.ui.databinding.widgets.customization.Customization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.FieldCustomization.WidgetType;
import gov.nasa.ensemble.ui.databinding.widgets.customization.GroupCustomization;
import gov.nasa.ensemble.ui.databinding.widgets.customization.ICustomization;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to extract useful information from classes that use the defined annotations.
 * @author tecohen
 *
 */
public class AnnotationReader {

    /**
     * See if the method has an @Advanced annotation
     * @param method
     * @return
     */
    public static boolean isAdvanced(Method method){
        if (method.isAnnotationPresent(Advanced.class)){
            return true;
        }
        return false;
    }

    /**
     * See if the method has a @Description annotation
     * @param method
     * @return
     */
    public static String getDescription(Method method){
        if (method.isAnnotationPresent(Description.class)) {
            Description l = method.getAnnotation(Description.class);
            return l.value();
        }
        return null;
    }

    /**
     * See if the method has a @FieldLabel annotation
     * @param method
     * @return
     */
    public static String getFieldLabel(Method method){
        if (method.isAnnotationPresent(FieldLabel.class)) {
            FieldLabel l = method.getAnnotation(FieldLabel.class);
            return l.value();
        }
        return null;
    }

    /**
     * See if the method has a @Format annotation
     * @param method
     * @return
     */
    public static String getFormat(Method method){
        if (method.isAnnotationPresent(Format.class)) {
            Format l = method.getAnnotation(Format.class);
            return l.value();
        }
        return null;
    }

    /**
     * See if the class has an @OrderedWidgets annotation
     * @param pClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<String> getOrderedWidgets(Class pClass){
        if (pClass.isAnnotationPresent(OrderedWidgets.class)) {
            OrderedWidgets ow = (OrderedWidgets) pClass.getAnnotation(OrderedWidgets.class);
            List<String> result = new ArrayList<String>();
            for (String child : ow.children()) {
                result.add(child);
            }
            return result;
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * See if the method has a @ReadOnly annotation
     * @param method
     * @return
     */
    public static boolean isReadOnly(Method method){
        if (method.isAnnotationPresent(ReadOnly.class)){
            return true;
        }
        return false;
    }

    /**
     * See if the method has a @Hidden annotation
     * @param method
     * @return
     */
    public static boolean isHidden(Method method){
        if (method.isAnnotationPresent(Hidden.class)){
            return true;
        }
        return false;
    }
    
    /**
     * See if the method has a @Trigger annotation
     * @param method
     * @return
     */
    public static boolean isTrigger(Method method){
        if (method.isAnnotationPresent(Trigger.class)){
            return true;
        }
        return false;
    }
    
    /**
     * See if the method has a @Complex annotation
     * @param method
     * @return
     */
    public static boolean isComplex(Method method){
        if (method.isAnnotationPresent(Complex.class)){
            return true;
        }
        return false;
    }

    /**
     * See if the method has a units label annotation
     * @param method
     * @return
     */
    public static String getUnitsLabel(Method method){
        if (method.isAnnotationPresent(UnitsLabel.class)) {
            UnitsLabel l = method.getAnnotation(UnitsLabel.class);
            return l.value();
        }
        return null;
    }


    /**
     * See if the method has a Limits annotation
     * @param method
     * @return a LimitsValidator, or null.
     */
    public static LimitsValidator getLimits(Method method){
        if (method.isAnnotationPresent(Limits.class)) {
            Limits limits = method.getAnnotation(Limits.class);
            LimitsValidator result = new LimitsValidator(limits.min(), limits.max(), limits.minCanEqual(), limits.maxCanEqual());
            return result;
        }
        return null;
    }

    /**
     * Get a MultiplierConverter based on the Multiplier
     * @param method
     * @return
     */
    public static MultiplierConverter getMultiplier(Method method){
        if (method.isAnnotationPresent(Multiplier.class)){
            Multiplier multiplier = method.getAnnotation(Multiplier.class);
            MultiplierConverter result = new MultiplierConverter(multiplier.value(), multiplier.type());
            return result;
        }
        return null;
    }

    /**
     * Create an AngleConverter based on annotations
     * @param method
     * @return
     */
    public static AngleConverter getAngle(Method method){
        if (method.isAnnotationPresent(Angle.class)){
            Angle angle = method.getAnnotation(Angle.class);
            AngleConverter result = new AngleConverter(angle.displayType(), angle.storedType(), angle.type());
            return result;
        }
        return null;
    }

    /**
     * Get the widget type for a particular field
     * @param method
     * @return
     */
    public static WidgetType getWidgetType(Method method){
        if (method.isAnnotationPresent(FieldWidgetType.class)){
            FieldWidgetType fwt = method.getAnnotation(FieldWidgetType.class);
            return fwt.value();
        }
        return null;
    }

    /**
     * See if the method has a @ComboEntryMethodName annotation
     * @param method
     * @return
     */
    public static String getComboEntryMethodName(Method method){
        if (method.isAnnotationPresent(ComboEntryMethodName.class)) {
        	ComboEntryMethodName l = method.getAnnotation(ComboEntryMethodName.class);
            return l.value();
        }
        return null;
    }
    
    /**
     * Get the list of combo entries for a particular field
     * @param method
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<ComboEntry> getComboEntries(Method method){
        if (method.isAnnotationPresent(ComboEntries.class)){
            List<ComboEntry> result = new ArrayList<ComboEntry>();
            ComboEntries comboEntriesAnnotation = method.getAnnotation(ComboEntries.class);
            EntryString[] strings = comboEntriesAnnotation.entryStrings();
            if (strings != null && strings.length > 0){
                for (EntryString s : strings){
                    result.add(new ComboEntry(s.label(), s.data()));
                }
                return result;
            }
            EntryInt[] ints = comboEntriesAnnotation.entryInts();
            if (ints != null && ints.length > 0){
                for (EntryInt s : ints){
                    result.add(new ComboEntry(s.label(), s.data()));
                }
                return result;
            }
            EntryFloat[] floats = comboEntriesAnnotation.entryFloats();
            if (floats != null && floats.length > 0){
                for (EntryFloat s : floats){
                    result.add(new ComboEntry(s.label(), s.data()));
                }
                return result;
            }
            EntryDouble[] doubles = comboEntriesAnnotation.entryDoubles();
            if (doubles != null && doubles.length > 0){
                for (EntryDouble s : doubles){
                    result.add(new ComboEntry(s.label(), s.data()));
                }
                return result;
            }
            EntryClass[] classes = comboEntriesAnnotation.entryClasses();
            if (classes != null && classes.length > 0){
                for (EntryClass s : classes){
                    result.add(new ComboEntry(s.label(), s.data()));
                }
                return result;
            }
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * See if the class has groups defined via the @Groups annotation
     * @param pClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Group> getGroups(Class pClass){
        if (pClass.isAnnotationPresent(Groups.class)){
            Groups groups = (Groups) pClass.getAnnotation(Groups.class);
            Group[] groupArray = groups.groups();
            if (groupArray != null && groupArray.length > 0){
                List<Group> result = new ArrayList<Group>();
                for (Group group : groupArray){
                    result.add(group);
                }
                return result;
            }
        }
        return Collections.EMPTY_LIST;
    }


    /**
     * Extract field customization information from a method based on its annotations
     * Right now this returns a field customization even for fields that have no annations
     * @param method
     * @return
     */
    public static FieldCustomization createFieldCustomization(Method method){
        if (method == null){
            return null;
        }
        String methodName = MethodUtil.getSuffix(method);

        boolean isTrigger = false;
        // if it is not a get or is, skip it
        if (methodName.equals(method.getName())){
        	// check if it is a trigger
        	if (AnnotationReader.isTrigger(method) || method.getName().startsWith(MethodUtil.TRIGGER)) {
        		// it is a trigger, we will want a button
        		isTrigger = true;
        	} else {
        		return null;
        	}
        }
        
        if (!isTrigger){
	        try {
	            // make sure there is a set method
	            MethodUtil.getSetMethod(method.getDeclaringClass(), methodName, method.getReturnType());
	        } catch (Exception e) {
	            return null;
	        }

        }
        FieldCustomization result = new FieldCustomization(methodName);
        
        if (AnnotationReader.isComplex(method)){
        	result.setComplex(true);
        } else {
        	result.setComplex(!MethodUtil.isReturnTypeSimple(method.getReturnType()) && MethodUtil.hasGetterSetter(method.getReturnType()));
        }
        
        if (method.getAnnotations().length == 0) {
            // make sure it has no params
            if (method.getParameterTypes().length > 0){
                return null;
            }
            return result;
        }
       
        result.setLabel(AnnotationReader.getFieldLabel(method));
        result.setDescription(AnnotationReader.getDescription(method));
        result.setFormat(AnnotationReader.getFormat(method));
        result.setUnitsLabel(AnnotationReader.getUnitsLabel(method));
        result.setAdvanced(AnnotationReader.isAdvanced(method));
        result.setReadOnly(AnnotationReader.isReadOnly(method));
        result.setHidden(AnnotationReader.isHidden(method));
        result.setWidgetType(AnnotationReader.getWidgetType(method));
        if (result.getWidgetType() != null && result.getWidgetType().equals(WidgetType.COMBO)){
            result.setComboEntries(AnnotationReader.getComboEntries(method));
            result.setComboEntryMethodName(AnnotationReader.getComboEntryMethodName(method));
        }
        result.setValidator(AnnotationReader.getLimits(method));
        ISimpleConverter converter = AnnotationReader.getMultiplier(method);
        if (converter == null){
            converter = AnnotationReader.getAngle(method);
        }
        result.setConverter(converter);
        return result;
    }

    /**
     * Create a GroupCustomization from a group.
     * @param group
     * @return
     */
    public static GroupCustomization createGroupCustomization(Group group){
        if (group == null || group.name() == null){
            return null;
        }

        GroupCustomization result = new GroupCustomization(group.name());
        result.setAdvanced(group.advanced());
        if (group.description() != null && group.description().length() > 0){
            result.setDescription(group.description());
        }
        result.setTwistie(group.twistie());
        List<String> children = new ArrayList<String>();
        String[] childArray = group.children();
        if (childArray != null){
            for (String child : childArray){
                children.add(child);
            }
        }

        result.setChildren(children);

        if (group.flagName() != null && group.flagName().length() > 0){
            result.setFlagName(group.flagName());
        }

        result.setSkipLabel(group.skipLabel());
        result.setExpanded(group.expanded());
        result.setTitleBar(group.titleBar());
        return result;
    }

    /**
     * Create a customization based on annotations.
     * @param pClass
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ICustomization createCustomization(final Class pClass){
        Customization result = new Customization() {
            @Override
            protected void initialize() {
                super.initialize();

                // set up the fields
                List<FieldCustomization> fieldCustomizations = new ArrayList<FieldCustomization>();
                for (Method method : pClass.getMethods()) {
                    FieldCustomization fc = createFieldCustomization(method);
                    if (fc != null){
                        fieldCustomizations.add(fc);
                    }
                }
                setFields(fieldCustomizations);

                // read the groups
                List<Group> groups = AnnotationReader.getGroups(pClass);
                List<GroupCustomization> groupCustomizations = new ArrayList<GroupCustomization>();
                if (groups != null){
                    for (Group g : groups) {
                        GroupCustomization gc = createGroupCustomization(g);
                        if (gc != null){	
                            groupCustomizations.add(gc);
                        }
                    }
                }
                setGroups(groupCustomizations);

                // read the ordered widgets
                setOrderedWidgets(AnnotationReader.getOrderedWidgets(pClass));
            }
        };
        if (!result.isEmpty()){
            return result;
        }
        return null;
    }

}
