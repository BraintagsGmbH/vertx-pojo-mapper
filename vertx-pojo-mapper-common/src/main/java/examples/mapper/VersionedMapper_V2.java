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
import de.braintags.vertx.jomnigate.annotation.VersionConverterDefinition;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import examples.mapper.converter.V2Converter;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity(name = "VersionedMapper")
@VersionInfo(version = 2, versionConverter = {
    @VersionConverterDefinition(destinationVersion = 2, converter = V2Converter.class) })
public class VersionedMapper_V2 extends VersionedMapper_V1 {
  public String newProperty = "newValue";
}
