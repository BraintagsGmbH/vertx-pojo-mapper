package de.braintags.io.vertx.pojomapper.mysql.mapping;

import de.braintags.io.vertx.pojomapper.datatypes.geojson.GeoJsonObject;
import de.braintags.io.vertx.pojomapper.mapping.impl.MappedField;
import de.braintags.io.vertx.pojomapper.mapping.impl.Mapper;
import de.braintags.io.vertx.pojomapper.mapping.impl.MapperFactory;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class SqlMapper<T> extends Mapper<T> {
  private boolean queryWithFieldNames = false;
  private String queryFieldList = null;

  /**
   * @param mapperClass
   * @param mapperFactory
   */
  public SqlMapper(Class<T> mapperClass, MapperFactory mapperFactory) {
    super(mapperClass, mapperFactory);
  }

  /**
   * Get the sequence of fieldnames for a query
   * If there are existing fields as GeoJson, a query sequence is returned, where not the wildcard is used but the
   * single fields
   * 
   * @return
   */
  public String getQueryFieldNames() {
    if (queryFieldList == null) {
      initFieldList();
    }
    return queryWithFieldNames ? queryFieldList : "*";
  }

  private void initFieldList() {
    getFieldNames().forEach(name -> addFieldEntry(name));
  }

  private void addFieldEntry(String name) {
    MappedField mf = (MappedField) getField(name);
    if (queryFieldList != null) {
      queryFieldList += ", ";
    } else {
      queryFieldList = "";
    }
    if (GeoJsonObject.class.isAssignableFrom(mf.getType())) {
      queryWithFieldNames = true;
      queryFieldList = queryFieldList + "AsText( " + mf.getColumnInfo().getName() + " )";
    } else {
      queryFieldList += mf.getColumnInfo().getName();
    }
  }

}
