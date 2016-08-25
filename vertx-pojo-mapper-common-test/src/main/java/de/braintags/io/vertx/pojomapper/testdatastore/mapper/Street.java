package de.braintags.io.vertx.pojomapper.testdatastore.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;

@Entity
public class Street extends BaseRecord {
  public String name;

}