/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.mapping.impl.keygen;

import de.braintags.io.vertx.pojomapper.IDataStore;

/**
 * The {@link DebugGenerator} is reset to 0 by each program start and can be used for testing. It generates an
 * identifyer as long
 * 
 * @author Michael Remme
 * 
 */
public class DebugGenerator extends AbstractKeyGenerator {
  public static final String NAME = "DEBUG";
  private long counter = 0;

  /**
   * @param name
   * @param datastore
   */
  public DebugGenerator(IDataStore datastore) {
    super(NAME, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.mapping.IKeyGenerator#generateKey()
   */
  @Override
  public Object generateKey() {
    return ++counter;
  }

}
