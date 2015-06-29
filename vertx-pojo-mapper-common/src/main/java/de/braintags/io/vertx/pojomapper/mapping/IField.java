/*
 * Copyright 2014 Red Hat, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping;

/**
 * Describes a field of an {@link IMapper}
 * 
 * @author Michael Remme
 * 
 */

public interface IField {

  /**
   * Get the fitting {@link IPropertyAccessor} for the current field
   * 
   * @return
   */
  public IPropertyAccessor getPropertyDescriptor();

  /**
   * Get the fitting {@link ITypeHandler} which is responsible to change data into and from the propriate format
   * 
   * @return
   */
  public ITypeHandler getTypeHandler();

}
