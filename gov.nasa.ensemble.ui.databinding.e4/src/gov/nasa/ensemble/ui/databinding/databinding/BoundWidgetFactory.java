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
package gov.nasa.ensemble.ui.databinding.databinding;

import gov.nasa.ensemble.ui.databinding.util.MethodUtil;
import gov.nasa.ensemble.ui.databinding.widgets.AbstractDatabindingWidget;
import gov.nasa.ensemble.ui.databinding.widgets.GenericBoundFormWidget;
import gov.nasa.ensemble.ui.databinding.widgets.GenericBoundWidget;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.framework.Bundle;

/**
 * This factory provides Databinding widgets which have been registered via the BoundWidget extension point.
 * They can be initialized with a model or created just with a class.
 * 
 * If the widget is not found a generic bound widget will be created.
 * 
 * @author tecohen
 *
 */
public class BoundWidgetFactory {

    final private static Logger logger = Logger.getLogger(BoundWidgetFactory.class);

    protected static final String DIVIDER = "|";	// if there is a specialized widget registered with a name, use this divider for the string key
    protected static BoundWidgetFactory s_factory;

    protected HashMap<String, IConfigurationElement> m_configElementMap = new HashMap<String, IConfigurationElement>(); // map of names to configuration elements
    protected HashMap<String, Class <? extends AbstractDatabindingWidget>> m_widgetMap = new HashMap<String, Class <? extends AbstractDatabindingWidget>>(); // map of names to widget classes
    protected Set<String> m_undefinedWidgetList = new HashSet<String>(); // set of class names that have been found to have no widget.

    protected HashMap<String, IConfigurationElement> m_fieldConfigElementMap = new HashMap<String, IConfigurationElement>(); // map of names to configuration elements for field widgets
    protected HashMap<String, Class <? extends AbstractDatabindingWidget>> m_fieldWidgetMap = new HashMap<String, Class <? extends AbstractDatabindingWidget>>(); // map of names to field widget classes
    protected Set<String> m_undefinedFieldWidgetList = new HashSet<String>(); // set of class names that have been found to have no field widget.

    protected boolean m_genericFactoriesLoaded = false;
    protected HashMap<String, List<IConfigurationElement>> m_genericFactoryConfigElementMap = new HashMap<String, List<IConfigurationElement>>(); // map of model class to configuration element

    protected HashMap<String, Class <? extends AbstractDatabindingWidget>> m_genericWidgetMap = new HashMap<String, Class <? extends AbstractDatabindingWidget>>(); // super generic widgets
    protected HashMap<String, Class <? extends AbstractDatabindingWidget>> m_genericFormWidgetMap = new HashMap<String, Class <? extends AbstractDatabindingWidget>>(); // form generic widgets
    @Inject
    protected IExtensionRegistry m_registry;
    
    
    protected static BoundWidgetFactory getStaticInstance() {
    	if(s_factory == null) {
    		logger.error("Sombody forgot to make a BoundWidgetFactory");
    	}
    	return s_factory;
    }
    
    
    /**
     * Initializes based on extensions
     */
    @PostConstruct
    protected void initialize() {
 //       IConfigurationElement[] extensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.widget.BoundWidget");
    	IConfigurationElement[] extensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.BoundWidget");
    	for (IConfigurationElement element : extensions){
            String name =element.getAttribute("name");
            String theClass = element.getAttribute("class");
            if (name != null && name.length() > 0){
                StringBuffer buffer = new StringBuffer(theClass);
                buffer.append(DIVIDER);
                buffer.append(name);
                theClass = buffer.toString();
            }
            m_configElementMap.put(theClass, element);
        }
        //gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.FieldWidget
        //gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.BoundWidget
        //gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.Customization
        //gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.GenericWidget
        //IConfigurationElement[] genericExtensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.widget.GenericWidget");
        IConfigurationElement[] genericExtensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.GenericWidget");
        for (IConfigurationElement element : genericExtensions){
            String theClass = element.getAttribute("model");
            List<IConfigurationElement> celist = m_genericFactoryConfigElementMap.get(theClass);
            if (celist == null){
                celist = new ArrayList<IConfigurationElement>();
                m_genericFactoryConfigElementMap.put(theClass, celist);
            }
            celist.add(element);
        }

        //IConfigurationElement[] fieldExtensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.widget.FieldWidget");
        IConfigurationElement[] fieldExtensions = m_registry.getConfigurationElementsFor("gov.nasa.ensemble.ui.databinding.e4.gov.nasa.ensemble.ui.databinding.widget.FieldWidget");
        for (IConfigurationElement element : fieldExtensions){
            String name =element.getAttribute("name");
            String theClass = element.getAttribute("class");
            if (name != null && name.length() > 0){
                StringBuffer buffer = new StringBuffer(theClass);
                buffer.append(DIVIDER);
                buffer.append(name);
                theClass = buffer.toString();
            }
            m_fieldConfigElementMap.put(theClass, element);
        }
    }

    @Inject
    public void setExtensionRegistry(IExtensionRegistry ier) {
    	m_registry = ier;
    }
    
    
    /**
     * protected constructor; initializes
     * (has to be public for DI to work)
     */
    @Inject
    public BoundWidgetFactory(IExtensionRegistry ier) {
//        initialize();
    	m_registry = ier;
    	s_factory = this;
    }

    /**
     * Really construct the widget based on what has been loaded.
     * Note this will activate the plugin if necessary.
     * @param pClass
     * @param toolkit
     * @param parent
     * @param style
     * @param horizontal
     * @param description
     * @param forceAllFields
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    protected static AbstractDatabindingWidget getWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, boolean horizontal, String description, boolean forceAllFields) throws NullPointerException, ClassNotFoundException, InstantiationException{

        if (pClass == null || parent == null){
            return null;
        }

        if (forceAllFields){
            if (toolkit != null){
                return(getGenericFormWidget(pClass, toolkit, parent, style, horizontal));
            } else {
                return(getGenericWidget(pClass, parent, style, horizontal));
            }
        }

        // name of the key for the map
        StringBuffer buffer = new StringBuffer(pClass.getName());
        if (description != null && description.length() > 0){
            buffer.append(DIVIDER);
            buffer.append(description);
        }
        String name = buffer.toString();

        if (getStaticInstance().m_undefinedWidgetList.contains(name)){
            if (toolkit != null){
                return(getGenericFormWidget(pClass, toolkit, parent, style, horizontal));
            } else {
                return(getGenericWidget(pClass, parent, style, horizontal));
            }
        }

        Class<? extends AbstractDatabindingWidget> widgetClass = getStaticInstance().m_widgetMap.get(name);

        if (widgetClass == null){
            // try loading it; this makes it lazy
            IConfigurationElement element = getStaticInstance().m_configElementMap.get(name);
            if (element == null){
                // look up the class hierarchy
                element = getParentClassElement(pClass, description);
            }
            if (element == null){
                // try looking up the parent widget in the class hierarchy
                if (widgetClass == null){
                    widgetClass = getParentClassWidget(pClass, description);
                }
            }
            if (element == null){
                // try looking up the interfaces
                List<Class> interfaces = MethodUtil.getAllInterfaces(pClass);
                for (Class i : interfaces){
                    element = getStaticInstance().m_configElementMap.get(i.getCanonicalName());
                    if (element != null){
                        break;
                    }
                }
            }
            if (widgetClass == null) {
                if (element == null) {
                    getStaticInstance().m_undefinedWidgetList.add(name);
                    if (toolkit != null){
                        return(getGenericFormWidget(pClass, toolkit, parent, style, horizontal));
                    } else {
                        return(getGenericWidget(pClass, parent, style, horizontal));
                    }
                } else {
                    //This is where the other plugin will be initialized.
                    Bundle bun = Platform.getBundle(element.getContributor().getName());
                    String widgetName = element.getAttribute("widget");
                    widgetClass = (Class<? extends AbstractDatabindingWidget>) bun.loadClass(widgetName);
                }
            }
            getStaticInstance().m_widgetMap.put(name, widgetClass);
        }
        for (Constructor constructor : widgetClass.getConstructors()){
            Class[] params = constructor.getParameterTypes();
            if (params.length == 3){
                if (params[0].equals(Composite.class) &&
                        params[1].getName().equals(int.class.getName()) &&
                        params[2].getName().equals(boolean.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(parent, style, horizontal);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            } 
            if (params.length == 2){
                if (params[0].equals(Composite.class) &&
                        params[1].getName().equals(int.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(parent, style);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            }  
            if (params.length == 4){
                if (params[0].equals(Class.class) &&
                        params[1].getName().equals(FormToolkit.class.getName()) &&
                        params[2].getName().equals(Composite.class.getName()) &&
                        params[3].getName().equals(int.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(pClass, toolkit, parent, style);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected static  AbstractDatabindingWidget getGenericWidget(Class pClass, Composite parent, int style, boolean horizontal) throws NullPointerException, ClassNotFoundException, InstantiationException{

        boolean found = false;
        List<Class> allInterfaces = MethodUtil.getAllInterfaces(pClass);

        for (Class i :allInterfaces) {
            String interfaceString = i.getCanonicalName();
            if (getStaticInstance().m_genericFactoryConfigElementMap.keySet().contains(interfaceString)) {
                Class widgetClass = getStaticInstance().m_genericWidgetMap.get(interfaceString);
                if(widgetClass == null){
                    //load it
                    List<IConfigurationElement> elementList = getStaticInstance().m_genericFactoryConfigElementMap.get(interfaceString);
                    for (IConfigurationElement element:elementList){
                        Bundle bun = Platform.getBundle(element.getContributor().getName());
                        String widgetName = element.getAttribute("widget");
                        widgetClass = bun.loadClass(widgetName);
                        String formString = element.getAttribute("form");
                        if (formString != null && formString.equals(Boolean.TRUE.toString())){
                            // put it in the generic form widget map instead
                            getStaticInstance().m_genericFormWidgetMap.put(interfaceString,widgetClass);
                        } else {
                            found = true;
                            getStaticInstance().m_genericWidgetMap.put(interfaceString, widgetClass);
                            break;
                        }
                    }
                }
                if (found && widgetClass != null){
                    // invoke it!
                    Class[] parameters = {pClass.getClass(), Composite.class, int.class, boolean.class};
                    try {
                        Constructor constructor = widgetClass.getConstructor(parameters);
                        return (AbstractDatabindingWidget)constructor.newInstance(pClass, parent, style, horizontal);
                    } catch (SecurityException e) {
                        logger.warn(e);
                    } catch (NoSuchMethodException e) {
                        logger.warn(e);
                    } catch (IllegalArgumentException e) {
                        logger.warn(e);
                    } catch (IllegalAccessException e) {
                        logger.warn(e);
                    } catch (InvocationTargetException e) {
                        logger.warn(e);
                    }
                }
            }
        }

        // construct it
        AbstractDatabindingWidget adw = new GenericBoundWidget(pClass, parent, style, horizontal);
        return(adw);
    }

    @SuppressWarnings("unchecked")
    protected static  AbstractDatabindingWidget getGenericFormWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, boolean horizontal) throws NullPointerException, ClassNotFoundException, InstantiationException{
        boolean found = false;
        List<Class> allInterfaces = MethodUtil.getAllInterfaces(pClass);
        for (Class i :allInterfaces) {
            String interfaceString = i.getCanonicalName();
            if (getStaticInstance().m_genericFactoryConfigElementMap.keySet().contains(interfaceString)) {
                Class widgetClass = getStaticInstance().m_genericFormWidgetMap.get(interfaceString);
                if(widgetClass == null){
                    //load it
                    List<IConfigurationElement> elementList = getStaticInstance().m_genericFactoryConfigElementMap.get(interfaceString);
                    for (IConfigurationElement element:elementList){
                        Bundle bun = Platform.getBundle(element.getContributor().getName());
                        String widgetName = element.getAttribute("widget");
                        widgetClass = bun.loadClass(widgetName);
                        String formString = element.getAttribute("form");
                        if (formString != null && formString.equals(Boolean.TRUE.toString())){
                            // put it in the generic form widget map instead
                            found = true;
                            getStaticInstance().m_genericFormWidgetMap.put(interfaceString,widgetClass);
                            break;
                        } else {
                            getStaticInstance().m_genericWidgetMap.put(interfaceString, widgetClass);
                        }
                    }
                }
                if (found && widgetClass != null){
                    // invoke it!
                    Class[] parameters = {pClass.getClass(), FormToolkit.class, Composite.class, int.class, boolean.class};
                    try {
                        Constructor constructor = widgetClass.getConstructor(parameters);
                        return (AbstractDatabindingWidget)constructor.newInstance(pClass, toolkit, parent, style, horizontal);
                    } catch (SecurityException e) {
                        logger.warn(e);
                    } catch (NoSuchMethodException e) {
                        logger.warn(e);
                    } catch (IllegalArgumentException e) {
                        logger.warn(e);
                    } catch (IllegalAccessException e) {
                        logger.warn(e);
                    } catch (InvocationTargetException e) {
                        logger.warn(e);
                    }
                }
            }
        }

        // construct it
        AbstractDatabindingWidget adw = new GenericBoundFormWidget(pClass, toolkit, parent, style, horizontal);
        return(adw);
    }

    /**
     * @param model
     * @param parent
     * @param style
     * @param horizontal
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    public static AbstractDatabindingWidget getWidget(Object model, Composite parent, int style, boolean horizontal, boolean forceAllFields) throws NullPointerException, ClassNotFoundException, InstantiationException{
        if (model == null || parent == null){
            return null;
        }

        AbstractDatabindingWidget result = getWidget(model.getClass(), null, parent, style, horizontal, null, forceAllFields);
        result.setModel(model);
        return result;
    }

    /**
     * @param model
     * @param parent
     * @param style
     * @param horizontal
     * @param description
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    public static AbstractDatabindingWidget getWidget(Object model, Composite parent, int style, boolean horizontal, String description, boolean forceAllFields) throws NullPointerException, ClassNotFoundException, InstantiationException{
        if (model == null || parent == null){
            return null;
        }

        AbstractDatabindingWidget result = getWidget(model.getClass(), null, parent, style, horizontal, description, forceAllFields);
        result.setModel(model);
        return result;
    }

    /**
     * @param pClass
     * @param parent
     * @param style
     * @param horizontal
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static AbstractDatabindingWidget getWidget(Class pClass, Composite parent, int style, boolean horizontal, boolean forceAllFields) throws NullPointerException, ClassNotFoundException, InstantiationException{
        return getWidget(pClass, null, parent, style, horizontal, null, forceAllFields);
    }

    /**
     * @param pClass
     * @param parent
     * @param style
     * @param horizontal
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static AbstractDatabindingWidget getWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, boolean horizontal, boolean forceAllFields) throws NullPointerException, ClassNotFoundException, InstantiationException{
        return getWidget(pClass, toolkit, parent, style, horizontal, null, forceAllFields);
    }

    /**
     * Look up the inheritance chain to see if there is a configuration element for one of the parent classes.
     * Does not check for class Object.
     * @param pClass
     * @param description
     * @return null if none found
     */
    @SuppressWarnings("unchecked")
    protected static IConfigurationElement getParentClassElement(Class pClass, String description){
        Class superClass = pClass.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)){
            return null;
        }

        // name of the key for the map
        StringBuffer buffer = new StringBuffer(superClass.getName());
        if (description != null && description.length() > 0){
            buffer.append(DIVIDER);
            buffer.append(description);
        }
        String name = buffer.toString();

        IConfigurationElement element = getStaticInstance().m_configElementMap.get(name);

        if (element == null){
            return getParentClassElement(superClass, description);
        }
        return element;
    }

    /**
     * Look up the inheritance chain to see if there is a widget for one of the parent classes.
     * Does not check for class Object
     * @param pClass
     * @param description
     * @return null if none found
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends AbstractDatabindingWidget> getParentClassWidget(Class pClass, String description){
        Class superClass = pClass.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)){
            return null;
        }

        // name of the key for the map
        StringBuffer buffer = new StringBuffer(superClass.getName());
        if (description != null && description.length() > 0){
            buffer.append(DIVIDER);
            buffer.append(description);
        }
        String name = buffer.toString();

        Class<? extends AbstractDatabindingWidget> widgetClass = getStaticInstance().m_widgetMap.get(name);
        if (widgetClass == null){
            return getParentClassWidget(superClass, description);
        }
        return widgetClass;
    }

    /**
     * Look up the inheritance chain to see if there is a field widget for one of the parent classes.
     * Does not check for class Object
     * @param pClass
     * @param description
     * @return null if none found
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends AbstractDatabindingWidget> getParentClassFieldWidget(Class pClass, String description){
        Class superClass = pClass.getSuperclass();
        if (superClass == null || superClass.equals(Object.class)){
            return null;
        }

        // name of the key for the map
        StringBuffer buffer = new StringBuffer(superClass.getName());
        if (description != null && description.length() > 0){
            buffer.append(DIVIDER);
            buffer.append(description);
        }
        String name = buffer.toString();

        Class<? extends AbstractDatabindingWidget> widgetClass = getStaticInstance().m_fieldWidgetMap.get(name);
        if (widgetClass == null){
            return getParentClassFieldWidget(superClass, description);
        }
        return widgetClass;
    }

    private static String getMapKey(Class pClass, String description) {
        // name of the key for the map
        StringBuffer buffer = new StringBuffer(pClass.getName());
        if (description != null && description.length() > 0){
            buffer.append(DIVIDER);
            buffer.append(description);
        }
        return buffer.toString();
    }

    private static Class<? extends AbstractDatabindingWidget> getFieldWidgetClass(Class pClass, String description) throws ClassNotFoundException {
        String name = getMapKey(pClass, description);
        Class<? extends AbstractDatabindingWidget> widgetClass = getStaticInstance().m_fieldWidgetMap.get(name);

        if (widgetClass == null){
            // try loading it; this makes it lazy
            IConfigurationElement element = getStaticInstance().m_fieldConfigElementMap.get(name);
            if (element == null){
                // look up the class hierarchy
                element = getParentClassElement(pClass, description);
            }
            if (element == null){
                // try looking up the parent widget in the class hierarchy
                if (widgetClass == null){
                    widgetClass = getParentClassFieldWidget(pClass, description);
                }
            }
            if (widgetClass == null) {
                if (element == null){
                    getStaticInstance().m_undefinedFieldWidgetList.add(name);
                    return null;
                } else {
                    Bundle bun = Platform.getBundle(element.getContributor().getName());
                    String widgetName = element.getAttribute("widget");
                    widgetClass = (Class<? extends AbstractDatabindingWidget>) bun.loadClass(widgetName);
                }
            }
            getStaticInstance().m_fieldWidgetMap.put(name, widgetClass);
        }
        return widgetClass;
    }

    /**
     * Look up a defined field widget
     * @param pClass
     * @param toolkit
     * @param parent
     * @param style
     * @param horizontal
     * @param description
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    protected static AbstractDatabindingWidget getFieldWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, boolean horizontal, String description) throws NullPointerException, ClassNotFoundException, InstantiationException{

        if (pClass == null || parent == null){
            return null;
        }

        // name of the key for the map
        String name = getMapKey(pClass, description);

        if (getStaticInstance().m_undefinedFieldWidgetList.contains(name)){
            return null;
        }

        Class<? extends AbstractDatabindingWidget> widgetClass;
        widgetClass = getFieldWidgetClass(pClass, description); 
        if(widgetClass == null) 
            return null;

        for (Constructor constructor : widgetClass.getConstructors()){
            Class[] params = constructor.getParameterTypes();
            if (params.length == 3){
                if (params[0].equals(Composite.class) &&
                        params[1].getName().equals(int.class.getName()) &&
                        params[2].getName().equals(boolean.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(parent, style, horizontal);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                } else if (params[0].equals(Class.class) &&
                        params[1].getName().equals(Composite.class.getName()) &&
                        params[2].getName().equals(int.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(pClass, parent, style);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            } 
            if (params.length == 2){
                if (params[0].equals(Composite.class) &&
                        params[1].getName().equals(int.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(parent, style);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            }  
            if (params.length == 4){
                if (params[0].equals(Class.class) &&
                        params[1].getName().equals(FormToolkit.class.getName()) &&
                        params[2].getName().equals(Composite.class.getName()) &&
                        params[3].getName().equals(int.class.getName())){
                    try {
                        return (AbstractDatabindingWidget) constructor.newInstance(pClass, toolkit, parent, style);
                    } catch (IllegalArgumentException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (IllegalAccessException e) {
                        throw new InstantiationException(e.getMessage());
                    } catch (InvocationTargetException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                }
            }
        }
        return null;
    }

    /**
     * This will NOT generate a widget if there is not one registered.
     * @param pClass
     * @param parent
     * @param style
     * @param horizontal
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static AbstractDatabindingWidget getRegisteredFieldWidget(Class pClass, Composite parent, int style, String description, boolean horizontal) throws NullPointerException, ClassNotFoundException, InstantiationException{
        if (pClass.isPrimitive()) {
            return null;
        }
        return getFieldWidget(pClass, null, parent, style, horizontal, description);
    }

    /**
     * Test whether an AbstractDatabindingWidget has been registered for pClass
     * @param pClass
     * @param description
     * @return
     */
    public static boolean hasRegisteredFieldWidget(Class pClass, String description) {
        try {
            Class<? extends AbstractDatabindingWidget> widgetClass;
            widgetClass = getFieldWidgetClass(pClass, description);
            return widgetClass != null;
        }
        catch(Throwable t) {
            return false;
        }
    }

    /**
     * This will NOT generate a widget if there is not one registered
     * @param pClass
     * @param parent
     * @param style
     * @param horizontal
     * @return
     * @throws NullPointerException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     */
    @SuppressWarnings("unchecked")
    public static AbstractDatabindingWidget getRegisteredFieldWidget(Class pClass, FormToolkit toolkit, Composite parent, int style, String description, boolean horizontal) throws NullPointerException, ClassNotFoundException, InstantiationException{
        if (pClass.isPrimitive()) {
            return null;
        }
        return getFieldWidget(pClass, toolkit, parent, style, horizontal, description);
    }
}
