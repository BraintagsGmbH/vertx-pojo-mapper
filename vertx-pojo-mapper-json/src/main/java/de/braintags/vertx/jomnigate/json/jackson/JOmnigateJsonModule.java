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

}
