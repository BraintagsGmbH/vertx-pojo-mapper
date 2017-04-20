package de.braintags.vertx.jomnigate;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.databind.JsonNode;

import de.braintags.vertx.jomnigate.dataaccess.query.ISearchCondition;
import de.braintags.vertx.jomnigate.dataaccess.query.QueryOperator;
import de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper;
import de.braintags.vertx.util.json.JsonConfig;
import io.vertx.core.json.Json;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
@RunWith(Parameterized.class)
public class TSerializeSearchConditions {

  private ISearchCondition searchCondition;

  @Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] { { ISearchCondition.isEqual(SimpleMapper.NAME, "test") },
        { ISearchCondition.condition(SimpleMapper.NAME, QueryOperator.EQUALS, "test") },
        { ISearchCondition.contains(SimpleMapper.NAME, "test") },
        { ISearchCondition.createFieldCondition(SimpleMapper.NAME, QueryOperator.EQUALS, "test") },
        { ISearchCondition.endsWith(SimpleMapper.NAME, "test") },
        { ISearchCondition.in(SimpleMapper.NAME, "test", "test2") },
        { ISearchCondition.larger(SimpleMapper.NAME, "test") },
        { ISearchCondition.largerOrEqual(SimpleMapper.NAME, "test") },
        { ISearchCondition.near(SimpleMapper.NAME, 7.388499, 51.113399, 10) },
        { ISearchCondition.notEqual(SimpleMapper.NAME, "test") },
        { ISearchCondition.notIn(SimpleMapper.NAME, "test", "test2") },
        { ISearchCondition.smaller(SimpleMapper.NAME, "test") },
        { ISearchCondition.smallerOrEqual(SimpleMapper.NAME, "test") },
        { ISearchCondition.startsWith(SimpleMapper.NAME, "test") },
        { ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "test"),
            ISearchCondition.isEqual(SimpleMapper.NAME, "test2")) },
        { ISearchCondition.or(ISearchCondition.isEqual(SimpleMapper.NAME, "test"),
            ISearchCondition.isEqual(SimpleMapper.NAME, "test2")) },
        { ISearchCondition.or(
            ISearchCondition.and(ISearchCondition.isEqual(SimpleMapper.NAME, "test"),
                ISearchCondition.isEqual(SimpleMapper.NAME, "test3")),
            ISearchCondition.isEqual(SimpleMapper.NAME, "test2"),
            ISearchCondition.isEqual(SimpleMapper.NAME, "test4")) } });
  }

  public TSerializeSearchConditions(ISearchCondition searchCondition) {
    this.searchCondition = searchCondition;
  }

  @BeforeClass
  public static void prepareTest() {
    JsonConfig.addConfig(mapper -> {
    });
  }

  @Test
  public void testSerialization() {
    JsonNode tree = Json.mapper.valueToTree(searchCondition);
    ISearchCondition convertedValue = Json.mapper.convertValue(tree, searchCondition.getClass());
    assertThat(convertedValue, is(searchCondition));
  }

}
