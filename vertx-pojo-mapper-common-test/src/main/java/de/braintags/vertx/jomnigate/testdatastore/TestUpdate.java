package de.braintags.vertx.jomnigate.testdatastore;

import java.util.Arrays;

import org.junit.Test;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Id;
import de.braintags.vertx.jomnigate.dataaccess.query.IQuery;
import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.write.IWrite;
import de.braintags.vertx.jomnigate.dataaccess.write.WriteAction;
import de.braintags.vertx.jomnigate.util.QueryHelper;
import io.vertx.ext.unit.TestContext;

public class TestUpdate extends DatastoreBaseTest {

  private static final String SHOULD_BE_UPDATED_FIELD = "shouldBeUpdated";
  private static final String TEST_ID_3 = "test3";
  private static final String TEST_ID_2 = "test2";
  private static final String TEST_ID_1 = "test1";

  @Entity
  public static class UpdateTester {

    @Id
    private String id;
    private boolean updated;
    private boolean shouldBeUpdated;

    UpdateTester(final String id, final boolean updated, final boolean shouldBeUpdated) {
      super();
      this.id = id;
      this.updated = updated;
      this.shouldBeUpdated = shouldBeUpdated;
    }

    public String getId() {
      return id;
    }

    public void setId(final String id) {
      this.id = id;
    }

    public boolean isUpdated() {
      return updated;
    }

    public UpdateTester setUpdated(final boolean updated) {
      this.updated = updated;
      return this;
    }

    public boolean isShouldBeUpdated() {
      return shouldBeUpdated;
    }

    public void setShouldBeUpdated(final boolean shouldBeUpdated) {
      this.shouldBeUpdated = shouldBeUpdated;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + (shouldBeUpdated ? 1231 : 1237);
      result = prime * result + (updated ? 1231 : 1237);
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      UpdateTester other = (UpdateTester) obj;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      if (shouldBeUpdated != other.shouldBeUpdated)
        return false;
      if (updated != other.updated)
        return false;
      return true;
    }

  }

  @Test
  public void testUpdateWithQuery(final TestContext context) {
    clearTable(context, UpdateTester.class);
    UpdateTester tester1 = new UpdateTester(TEST_ID_1, false, true);
    UpdateTester tester2 = new UpdateTester(TEST_ID_2, false, true);
    UpdateTester tester3 = new UpdateTester(TEST_ID_3, false, false);

    saveRecords(context, Arrays.asList(tester1, tester2, tester3));

    IQuery<UpdateTester> query = getDataStore(context).createQuery(UpdateTester.class);
    query.setSearchCondition(ISearchCondition.isEqual(SHOULD_BE_UPDATED_FIELD, true));

    IWrite<UpdateTester> write = getDataStore(context).createWrite(UpdateTester.class);
    UpdateTester updatedTest1 = new UpdateTester(TEST_ID_1, true, true);
    UpdateTester updatedTest2 = new UpdateTester(TEST_ID_2, true, true);
    UpdateTester updatedTest3 = new UpdateTester(TEST_ID_3, true, false);
    write.addAll(Arrays.asList(updatedTest1, updatedTest2, updatedTest3));
    write.setQuery(query);

    ResultContainer resultContainer = write(context, write, getDataStore(context).createQuery(UpdateTester.class), 3);
    resultContainer.writeResult.forEach(entry -> {
      if (entry.getId().equals(TEST_ID_1) || entry.getId().equals(TEST_ID_2)) {
        context.assertTrue(entry.getAction().equals(WriteAction.UPDATE));
      } else {
        context.assertTrue(entry.getAction().equals(WriteAction.NOT_MATCHED));
      }
    });
    QueryHelper.queryResultToList(resultContainer.queryResult, context.asyncAssertSuccess(list -> {
      context.assertTrue(list.contains(updatedTest1));
      context.assertTrue(list.contains(updatedTest2));
      list.stream().forEach(entry -> {
        if (((UpdateTester) entry).getId().equals(TEST_ID_1) || ((UpdateTester) entry).getId().equals(TEST_ID_2)) {
          context.assertTrue(((UpdateTester) entry).isUpdated());
        }
      });
      context.assertFalse(list.contains(updatedTest3));
      context.assertTrue(list.contains(tester3));
    }));
  }

  @Test
  public void testUpdateWithNoneMatchingQuery(final TestContext context) {
    clearTable(context, UpdateTester.class);
    UpdateTester tester1 = new UpdateTester(TEST_ID_1, false, false);
    UpdateTester tester2 = new UpdateTester(TEST_ID_2, false, false);
    UpdateTester tester3 = new UpdateTester(TEST_ID_3, false, false);

    saveRecords(context, Arrays.asList(tester1, tester2, tester3));

    IQuery<UpdateTester> query = getDataStore(context).createQuery(UpdateTester.class);
    query.setSearchCondition(ISearchCondition.isEqual(SHOULD_BE_UPDATED_FIELD, true));

    IWrite<UpdateTester> write = getDataStore(context).createWrite(UpdateTester.class);
    UpdateTester updatedTest1 = tester1.setUpdated(true);
    UpdateTester updatedTest2 = tester2.setUpdated(true);
    UpdateTester updatedTest3 = tester3.setUpdated(true);
    write.addAll(Arrays.asList(updatedTest1, updatedTest2, updatedTest3));
    write.setQuery(query);

    ResultContainer resultContainer = write(context, write, getDataStore(context).createQuery(UpdateTester.class), 3);
    resultContainer.writeResult.forEach(entry -> context.assertTrue(entry.getAction().equals(WriteAction.NOT_MATCHED)));
  }

}
