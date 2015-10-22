package de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;

import de.braintags.io.vertx.pojomapper.annotation.field.Id;
import de.braintags.io.vertx.pojomapper.exception.MappingException;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;

/**
 * Basic Record for testing {@link ITypeHandler}
 * 
 * @author Michael Remme
 * 
 */
public class BaseRecord {
  @Id
  public long id;

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

    if (!value.equals(compareValue))
      throw new MappingException("Contents are not equal: " + fieldName + ": " + value + " / " + compareValue);
    return true;
  }

}