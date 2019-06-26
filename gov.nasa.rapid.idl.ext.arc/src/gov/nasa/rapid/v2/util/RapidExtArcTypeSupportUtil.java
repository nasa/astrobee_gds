/*******************************************************************************
 * Copyright (c) 2011 United States Government as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *******************************************************************************/
package gov.nasa.rapid.v2.util;

import gov.nasa.dds.rti.util.AbstractTypeSupportUtil;

import com.rti.dds.infrastructure.Copyable;

public class RapidExtArcTypeSupportUtil extends AbstractTypeSupportUtil {
    /**
     * this method has to be implemented in the concrete class in 
     * order for class lookup to succeed
     */
    @Override
    public Class getTypeSupportClassFor(Class<? extends Copyable> copyClass) throws ClassNotFoundException {
        final String typeSuppName = copyClass.getName()+"TypeSupport";
        return Class.forName(typeSuppName);
    }

    @Override
    public Class classForName(String className) throws ClassNotFoundException {
        return Class.forName(className);
    }
}
