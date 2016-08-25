package de.braintags.io.vertx.pojomapper.testdatastore.mapper;

import java.util.ArrayList;
import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler.BaseRecord;

@Entity
public class City extends BaseRecord {
  public String name;
  @Embedded
  public List<Street> streets = new ArrayList<Street>();

}