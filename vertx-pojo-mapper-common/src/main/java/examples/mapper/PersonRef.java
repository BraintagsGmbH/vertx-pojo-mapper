package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class PersonRef {
  @Id
  public String id;
  public String name;
  @Referenced
  public Animal animal;

  public PersonRef() {
  }

}
