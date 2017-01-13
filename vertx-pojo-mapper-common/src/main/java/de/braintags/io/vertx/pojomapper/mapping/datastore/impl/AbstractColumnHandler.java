/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mapping.datastore.impl;

import java.util.Arrays;
import java.util.List;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;

/**
 * An abstract implementation of {@link IColumnHandler}
 * 
 * @author Michael Remme
 * 
 */

public abstract class AbstractColumnHandler implements IColumnHandler {
  private final List<Class<?>> classesToHandle;

  /**
   * 
   * @param classesToDeal
   *          the classes to be handled by the current instance
   */
  public AbstractColumnHandler(Class<?>... classesToDeal) {
    classesToHandle = Arrays.asList(classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler#matches(de.braintags.io.vertx.pojomapper.mapping
   * .IField)
   */
  @Override
  public short matches(IField field) {
    return matches(field.getType());
  }

  private short matches(Class<?> cls) {
    for (Class<?> dCls : classesToHandle) {
      if (dCls.equals(cls))
        return MATCH_MAJOR;
    }
    for (Class<?> dCls : classesToHandle) {
      if (dCls.isAssignableFrom(cls))
        return MATCH_MINOR;
    }
    return MATCH_NONE;
  }

}
