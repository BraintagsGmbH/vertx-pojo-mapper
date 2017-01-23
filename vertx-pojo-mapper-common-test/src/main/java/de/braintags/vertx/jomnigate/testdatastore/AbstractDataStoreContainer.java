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
package de.braintags.vertx.jomnigate.testdatastore;

import de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDataStoreContainer implements IDatastoreContainer {
  public static String DEFAULT_KEY_GENERATOR = DefaultKeyGenerator.NAME;

}
