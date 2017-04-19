/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper.versioning;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;
import de.braintags.vertx.jomnigate.versioning.IMapperVersion;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity(version = 5)
public class VersioningWithInterface_V5 extends BaseRecord implements IMapperVersion {

  private long version;

  @Override
  public long getMapperVersion() {
    return version;
  }

  @Override
  public void setMapperVersion(long version) {
    this.version = version;
  }

}
