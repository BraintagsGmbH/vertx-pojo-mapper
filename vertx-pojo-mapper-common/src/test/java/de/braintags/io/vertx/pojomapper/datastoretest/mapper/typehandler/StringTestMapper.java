package de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler;

import de.braintags.io.vertx.pojomapper.annotation.Entity;

/**
 * Testing tpyehandler for Strings
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class StringTestMapper extends BaseRecord {
  public int counter;
  public String stringField = "myString";
  public StringBuffer stringBufferField = new StringBuffer("myStringbuffer");
  public StringBuilder stringBuilderField = new StringBuilder("myStringbuilder");

  public StringTestMapper() {
  }

  public StringTestMapper(int counter) {
    this.counter = counter;
    stringField += " " + counter;
    stringBufferField.append(" ").append(counter);
    stringBuilderField.append(" ").append(counter);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return counter;
  }

  @Override
  public boolean equals(Object ob) {
    if (!getClass().equals(ob.getClass()))
      return false;
    return ((StringTestMapper) ob).counter == counter;
  }

}