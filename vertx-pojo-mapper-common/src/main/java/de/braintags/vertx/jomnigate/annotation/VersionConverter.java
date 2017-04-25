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
package de.braintags.vertx.jomnigate.annotation;

import de.braintags.vertx.jomnigate.versioning.IVersionConverter;

/**
 * Defines a converter, which shall be used for a versioned record to bring it into the correct state
 * 
 * @author Michael Remme
 * 
 */
public @interface VersionConverter {

  /**
   * Defines the version, which the converter shall reach
   * 
   * @return
   */
  long destinationVersion();

  /**
   * Defines the {@link IVersionConverter}, which shall be used for conversion
   * 
   * @return
   */
  Class<? extends IVersionConverter> converter();

}
