package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;

import de.braintags.io.vertx.pojomapper.annotation.Entity;
import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Basic Record for testing {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class BaseRecord {
  @Id
  public String id;

  @Override
  public boolean equals(Object ob) {
    Field[] fields = getClass().getFields();
    for (Field field : fields) {
      compare(field, ob);
    }

    return true;
  }

  private boolean compare(Field field, Object compare) {
    if (field.getName().equals("id"))
      return true;
    if (field.getName().equals("buffer")) {
      @SuppressWarnings("unused")
      String test = "test ";
    }
    try {
      Object value = field.get(this);
      Object compareValue = field.get(compare);
      equalValues(value, compareValue, field.getName());
      return true;
    } catch (Exception e) {
      throw new RuntimeException("Error in field " + field.getName(), e);
    }
  }

  public boolean compareId(Object id, Object compareId) {
    boolean idEqual = false;
    if (compareId == null && id == null)
      idEqual = true;
    else if (compareId == null || id == null)
      idEqual = false;
    else
      idEqual = id.equals(compareId);
    return idEqual;
  }

  @SuppressWarnings({ "unused", "rawtypes" })
  private boolean equalValues(Object value, Object compareValue, String fieldName) {
    if (value == null && compareValue == null)
      return true;
    if (value instanceof CharSequence) {
      value = value.toString();
      compareValue = compareValue.toString();
    }

    if (value.getClass().isArray()) {
      if (!compareValue.getClass().isArray())
        throw new MappingException("Contents are not equal: " + fieldName);
      for (int i = 0; i < Array.getLength(value); i++) {
        if (!Array.get(value, i).equals(Array.get(compareValue, i)))
          throw new MappingException("Contents are not equal: " + fieldName);
      }
      return true;
    }

    if (value instanceof Date) {
      long t = ((Date) value).getTime();
      long p = ((Date) compareValue).getTime();
      if (t != p)
        throw new MappingException(
            "Contents are not equal: " + fieldName + ": " + value + " - " + t + " / " + compareValue + " - " + p);
    }

    if (value instanceof Collection) {
      return compareCollections((Collection) value, (Collection) compareValue, fieldName);
    }

    // by saving arrays or List as JsonArray, a type change can happen from Long to Integer for instance
    if (value instanceof Number) {
      if (((Number) value).hashCode() != ((Number) compareValue).hashCode())
        throw new MappingException("Contents are not equal: " + fieldName + ": " + value + " - " + value.hashCode()
            + " / " + compareValue + " - " + compareValue.hashCode());
      return true;
    }

    if (value instanceof Map) {
      return compareMaps((Map) value, (Map) compareValue, fieldName);
    }

    if (!value.equals(compareValue))
      throw new MappingException("Contents are not equal: " + fieldName + ": " + value + " / " + compareValue);
    return true;
  }

  @SuppressWarnings("rawtypes")
  private boolean compareMaps(Map value, Map compareValue, String fieldName) {
    if (value == null && compareValue == null)
      return true;
    if (value.size() != compareValue.size())
      throw new MappingException(
          "Contents are not equal, unequal length: " + fieldName + ": " + value.size() + " / " + compareValue.size());
    Iterator<?> it = value.keySet().iterator();
    while (it.hasNext()) {
      Object key = it.next();
      Object value1 = value.get(key);
      Object value2 = compareValue.get(key);
      equalValues(value1, value2, fieldName);
    }
    return true;
  }

  @SuppressWarnings("rawtypes")
  private boolean compareCollections(Collection coll1, Collection coll2, String fieldName) {
    return compareCollectionsExistence(coll1, coll2, fieldName);
  }

  /**
   * Checks wether all elements inside the collections are existing, equal the position
   * 
   * @param coll1
   * @param coll2
   * @param fieldName
   * @return
   */
  @SuppressWarnings("rawtypes")
  private boolean compareCollectionsExistence(Collection coll1, Collection coll2, String fieldName) {
    for (Object o : coll1) {
      if (!coll2.contains(o)) {
        Assert.fail("collections are different, element does not exist: " + o);
      }
    }
    for (Object o : coll2) {
      if (!coll1.contains(o)) {
        Assert.fail("collections are different, element does not exist: " + o);
      }
    }
    return true;
  }

}