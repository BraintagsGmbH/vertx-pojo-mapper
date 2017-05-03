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
package de.braintags.vertx.jomnigate.versioning;

/**
 * IMapperVersion is the interface which is used by the mapperversioning system to store the version of a mapper inside
 * an entity. The version will be used to check, wether data conversions must be processed
 * 
 * @author Michael Remme
 * 
 */
public interface IMapperVersion {

  /**
   * Get the mapper version of the mapper, by which the entity was saved
   * 
   * @return
   */
  long getMapperVersion();

  /**
   * Set the mapper version by which the entity is saved. This value will be automatically set.
   * 
   * @param mapperVersion
   */
  void setMapperVersion(long mapperVersion);
}
