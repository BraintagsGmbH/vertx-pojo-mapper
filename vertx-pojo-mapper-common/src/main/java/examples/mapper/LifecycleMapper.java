package examples.mapper;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.AfterSave;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeDelete;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeLoad;
import de.braintags.io.vertx.pojomapper.annotation.lifecycle.BeforeSave;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mapping.ITriggerContext;
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
  public void afterLoad(ITriggerContext triggerContext) {
    name = "just after load";
    IDataStore ds = triggerContext.getMapper().getMapperFactory().getDataStore();
    IQuery<MiniMapper> q = ds.createQuery(MiniMapper.class);
    q.field("name").is("test");
    q.execute(qr -> {
      if (qr.failed()) {
        triggerContext.fail(qr.cause());
      } else {
        // do something
        triggerContext.complete();
      }
    });
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
