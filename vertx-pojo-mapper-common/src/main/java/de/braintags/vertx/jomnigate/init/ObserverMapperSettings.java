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
package de.braintags.vertx.jomnigate.init;

import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.mapping.IMapper;

/**
 * The part of {@link ObserverSettings} which defines, on which {@link IMapper} the ObserverSettings shall be executed.
 * The property classDefinition contains the definition. Examples for such a definition are:
 * <UL>
 * <LI>my.mapper.class<br/>
 * the parent ObserverSettings will be applied for a mapper with the class my.mapper.class
 * <LI>instanceof my.mapper.class<br/>
 * the parent ObserverSettings will be applied for all classes, which are instanceof my.mapper.class
 * </UL>
 * 
 * @author Michael Remme
 * 
 */
public class ObserverMapperSettings {
  private static final String INSTANCEOF = "instanceof";
  private String classDefinition;

  /**
   * The class definition which defines for which mapper classes the definition shall be applied
   * 
   * @return the classDefinition
   */
  public String getClassDefinition() {
    return classDefinition;
  }

  /**
   * The class definition which defines for which mapper classes the definition shall be applied
   * 
   * @param classDefinition
   *          the classDefinition to set
   */
  public void setClassDefinition(String classDefinition) {
    this.classDefinition = classDefinition;
  }

  /**
   * This method checks, whether the current definition is applyable to the given instance of IMapper.
   * 
   * @param mapper
   * @return true, if appliable
   */
  boolean isApplyableFor(IMapper mapper) {
    boolean instOf = false;
    String clsName = classDefinition;
    int index = clsName.indexOf(INSTANCEOF);
    if (index >= 0) {
      instOf = true;
      clsName = clsName.substring(index + INSTANCEOF.length()).trim();
    }
    try {
      Class mapperClass = Class.forName(clsName);
      if (instOf) {
        return mapper.getMapperClass().isAssignableFrom(mapperClass);
      } else {
        return mapperClass == mapper.getMapperClass();
      }
    } catch (ClassNotFoundException e) {
      throw new MappingException(e);
    }
  }

}
