package gov.nasa.arc.irg.util.bean;

/*
 * Copyright (c) 2002, 2011 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *   IBM - Initial API and implementation
 *
 * $Id: Enumerator.java,v 1.3 2005/10/28 13:57:55 davidms Exp $
 */


/**
 * An interface implemented by the enumerators of a type-safe enum.
 */
public interface Enumerator
{
  /**
   * Returns the name of the enumerator.
   * @return the name.
   */
  String getName();

  /**
   * Returns the <code>int</code>value of the enumerator.
   * @return the value.
   */
  int getValue();
  
  /**
   * Returns the literal value of the enumerator.
   * @return the literal.
   */
  String getLiteral();
}
