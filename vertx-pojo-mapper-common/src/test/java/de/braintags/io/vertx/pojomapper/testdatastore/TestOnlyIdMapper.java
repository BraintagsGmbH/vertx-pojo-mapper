package de.braintags.io.vertx.pojomapper.testdatastore;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.testdatastore.mapper.OnlyIdMapper;
import io.vertx.ext.unit.TestContext;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestOnlyIdMapper extends DatastoreBaseTest {

  @Test
  public void testInsert(TestContext context) {
    clearTable(context, "OnlyIdMapper");
    OnlyIdMapper sm = new OnlyIdMapper();
    ResultContainer resultContainer = saveRecord(context, sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<OnlyIdMapper> query = getDataStore(context).createQuery(OnlyIdMapper.class);
    resultContainer = find(context, query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

}
