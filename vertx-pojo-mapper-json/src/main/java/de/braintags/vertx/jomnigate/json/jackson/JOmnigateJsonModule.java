/*-
 * #%L
 * vertx-pojo-mapper-json
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.json.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This module only applies to JsonDatastores
 * 
 * @author mpluecker
 *
 */
public class JOmnigateJsonModule extends SimpleModule {

  @Override
  public void setupModule(SetupContext context) {
    if (context.getOwner().getJsonFactory() instanceof JOmnigateFactory) {
      super.setupModule(context);
    }
  }

  @Override
  public Object getTypeId() {
    return null;
  }

}
