package examples.mapper;

import java.util.List;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Embedded;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class PersonEmbedList {
  @Id
  public String id;
  public String name;
  @Embedded
  public List<Animal> animals;

  public PersonEmbedList() {
  }

}
