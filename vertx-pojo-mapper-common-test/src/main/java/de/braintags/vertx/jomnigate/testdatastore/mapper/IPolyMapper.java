package de.braintags.vertx.jomnigate.testdatastore.mapper;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interface for {@link PolyMapper} and {@link PolySubMapper}
 * 
 * @author sschmitt
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface IPolyMapper {

  String getId();

  String getMainField();

}