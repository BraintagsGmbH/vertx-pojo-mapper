package examples.mapper;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import io.vertx.docgen.Source;

@Source(translate = false)
@Entity
public class LifecycleMapper {
  @Id
  public String id;
  public String name;

  public LifecycleMapper() {
  }

  @BeforeLoad
  public void beforeLoad() {
    name = "just before load";
  }

  @AfterLoad
  public void afterLoad() {
    name = "just after load";
  }

  @BeforeSave
  public void beforeSave() {
    name = "just before save";
  }

  @AfterSave
  public void afterSave() {
    name = "just after save";
  }

  @BeforeDelete
  public void beforeDelete() {
    name = "just before deletion";
  }

  @AfterDelete
  public void afterDelete() {
    name = "just after deletion";
  }

}
