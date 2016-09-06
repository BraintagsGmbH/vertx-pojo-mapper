package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Encoder;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class MiniMapperEncoded {
  @Id
  public String id;
  public String name;
  public int number;
  @Encoder(name = "StandardEncoder")
  public String password;

  public MiniMapperEncoded() {
  }

}
