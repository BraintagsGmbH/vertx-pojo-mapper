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
package examples.mapper;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.versioning.IMapperVersion;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity(name = "VersionedMapper") // <1>
@VersionInfo(version = 1) // <2>
public class VersionedMapper_V1 implements IMapperVersion {
  @Id
  public String id;
  private String name;
  private long mapperVersion = -1;

  @Override
  public long getMapperVersion() {
    return mapperVersion;
  }

  @Override
  public void setMapperVersion(long mapperVersion) {
    this.mapperVersion = mapperVersion;
  }

}
