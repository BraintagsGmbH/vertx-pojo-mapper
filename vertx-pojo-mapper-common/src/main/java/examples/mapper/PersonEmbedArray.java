package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class PersonEmbedArray {
  @Id
  public String id;
  public String name;
  @Embedded
  public Animal[] animals;

  public PersonEmbedArray() {
  }

}
