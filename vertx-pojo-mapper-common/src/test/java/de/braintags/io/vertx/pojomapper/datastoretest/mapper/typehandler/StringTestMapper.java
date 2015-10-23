package de.braintags.io.vertx.pojomapper.datastoretest.mapper.typehandler;

/**
 * Testing tpyehandler for Strings
 * 
 * @author Michael Remme
 * 
 */
public class StringTestMapper extends BaseRecord {
  public String stringField = "myString";
  public StringBuffer stringBufferField = new StringBuffer("myStringbuffer");
  public StringBuilder stringBuilderField = new StringBuilder("myStringbuilder");

  public StringTestMapper() {
  }

  public StringTestMapper(int counter) {
    stringField += " " + counter;
    stringBufferField.append(" ").append(counter);
    stringBuilderField.append(" ").append(counter);
  }

}