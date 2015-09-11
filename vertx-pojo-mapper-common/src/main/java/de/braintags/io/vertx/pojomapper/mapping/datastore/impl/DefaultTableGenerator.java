/*
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

package de.braintags.io.vertx.pojomapper.mapping.datastore.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator;
import de.braintags.io.vertx.pojomapper.mapping.datastore.ITableInfo;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */

public class DefaultTableGenerator implements ITableGenerator {
  /**
   * If for a class a {@link IColumnHandler} was requested and found, it is cached by here with the class to handle as
   * key
   */
  private final Map<Class<?>, IColumnHandler> cachedColumnHandler = new HashMap<Class<?>, IColumnHandler>();
  private static final List<IColumnHandler> definedColumnHandlers = new ArrayList<IColumnHandler>();
  private IColumnHandler defaultColumnHandler = new DefaultColumnHandler();

  /**
   * 
   */
  public DefaultTableGenerator() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator#createTable(de.braintags.io.vertx.pojomapper
   * .mapping.IMapper)
   */
  @Override
  public ITableInfo createTableInfo(IMapper mapper) {
    return new DefaultTableInfo(mapper);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.mapping.datastore.ITableGenerator#getColumnHandler(de.braintags.io.vertx.pojomapper
   * .mapping.IField)
   */
  @Override
  public IColumnHandler getColumnHandler(IField field) {
    Class<?> fieldClass = field.getType();
    if (cachedColumnHandler.containsKey(fieldClass))
      return cachedColumnHandler.get(fieldClass);
    IColumnHandler handler = examineMatch(field);
    if (handler == null)
      handler = getDefaultColumnHandler();
    cachedColumnHandler.put(fieldClass, handler);
    return handler;
  }

  /**
   * Checks for a valid TypeHandler by respecting graded results
   * 
   * @param field
   * @return
   */
  private IColumnHandler examineMatch(IField field) {
    IColumnHandler returnHandler = null;
    List<IColumnHandler> ths = getDefinedColumnHandlers();
    for (IColumnHandler ch : ths) {
      short matchResult = ch.matches(field);
      switch (matchResult) {
      case ITypeHandler.MATCH_MAJOR:
        return ch;

      case ITypeHandler.MATCH_MINOR:
        returnHandler = ch;
        break;

      default:
        break;
      }
    }
    return returnHandler;
  }

  /**
   * Get the default handler to be used
   * 
   * @return the defined default handler or null, if none defined
   */
  public IColumnHandler getDefaultColumnHandler() {
    return defaultColumnHandler;
  }

  /**
   * Get all defined {@link IColumnHandler} defined for the current instance
   * 
   * @return
   */
  public List<IColumnHandler> getDefinedColumnHandlers() {
    return definedColumnHandlers;
  }

}
