package de.braintags.io.vertx.pojomapper.datastoretest;

import org.junit.Test;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.datastoretest.mapper.OnlyIdMapper;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class TestOnlyIdMapper extends DatastoreBaseTest {

  @Test
  public void testInsert() {
    OnlyIdMapper sm = new OnlyIdMapper();
    ResultContainer resultContainer = saveRecord(sm);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;

    // SimpleQuery for all records
    IQuery<OnlyIdMapper> query = getDataStore().createQuery(OnlyIdMapper.class);
    resultContainer = find(query, 1);
    if (resultContainer.assertionError != null)
      throw resultContainer.assertionError;
  }

}
