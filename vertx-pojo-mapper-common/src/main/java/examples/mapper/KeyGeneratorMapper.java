package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.KeyGenerator;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
@KeyGenerator
public class KeyGeneratorMapper {
  @Id
  public String id;

}
