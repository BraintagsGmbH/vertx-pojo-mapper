/*
 * #%L vertx-pojongo %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
package de.braintags.io.vertx.pojomapper.mongo.dataaccess;

import de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryPart;
import de.braintags.io.vertx.pojomapper.dataaccess.query.ISortDefinition;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression;
import de.braintags.io.vertx.pojomapper.dataaccess.query.impl.SortDefinition;
import de.braintags.io.vertx.pojomapper.mapping.IMapper;
import io.vertx.core.json.JsonObject;

/**
 * Mongo stores the query expression as JsonObject
 * 
 * @author Michael Remme
 */

public class MongoQueryExpression implements IQueryExpression {
  private JsonObject qDef = new JsonObject();
  private IMapper<?> mapper;
  private JsonObject sortArguments;

  /**
   * Get the original Query definition for Mongo
   * 
   * @return
   */
  public JsonObject getQueryDefinition() {
    return qDef;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setMapper(de.braintags.io.vertx.pojomapper.
   * mapping.IMapper)
   */
  @Override
  public void setMapper(IMapper<?> mapper) {
    this.mapper = mapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#buildQueryExpression(de.braintags.io.vertx.
   * pojomapper.dataaccess.query.IQueryPart)
   */
  @Override
  public void buildQueryExpression(IQueryPart queryPart) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#addSort(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.ISortDefinition)
   */
  @Override
  public IQueryExpression addSort(ISortDefinition<?> sortDef) {
    SortDefinition<?> sd = (SortDefinition<?>) sortDef;
    if (!sd.getSortArguments().isEmpty()) {
      sortArguments = new JsonObject();
      sd.getSortArguments().forEach(sda -> sortArguments.put(sda.fieldName, sda.ascending ? 1 : -1));
    }
    return this;
  }

  /**
   * Get the sort arguments, which were created by method {@link #addSort(ISortDefinition)}
   * 
   * @return the sortArguments or null, if none
   */
  public final JsonObject getSortArguments() {
    return sortArguments;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.query.impl.IQueryExpression#setNativeCommand(java.lang.Object)
   */
  @Override
  public void setNativeCommand(Object nativeCommand) {
    if (nativeCommand instanceof JsonObject) {
      qDef = (JsonObject) nativeCommand;
    } else if (nativeCommand instanceof String) {
      qDef = new JsonObject((String) nativeCommand);
    } else {
      throw new UnsupportedOperationException("the mongo datastore needs a Jsonobject as native format");
    }
  }

  @Override
  public String toString() {
    return String.valueOf(qDef) + " | sort: " + String.valueOf(sortArguments);
  }

}
