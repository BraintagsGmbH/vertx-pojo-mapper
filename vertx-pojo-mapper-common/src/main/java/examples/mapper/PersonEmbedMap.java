package examples.mapper;

import java.util.Map;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class PersonEmbedMap {
  @Id
  public String id;
  public String name;
  @Embedded
  public Map<String, Animal> animals;

  public PersonEmbedMap() {
  }

}
