/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.typehandler;

import de.braintags.io.vertx.pojomapper.json.typehandler.handler.IdTypeHandler;
import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mysql.SqlUtil;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;

/**
 * The IdTypeHandler is a special {@link ITypeHandler} which is used specially for the id field. Contrary to normal
 * fields, where the field type and column type should be "type safe", the ID field can vary, especially when a mapper
 * is moved from Mongo to MySql, for instance. In this case the java field can be String but the column can be numeric,
 * or vice versa.
 * 
 * NOTE: this is still a hack, cause we are expecting the id field to be a String or numeric field, because of a
 * potential switch from Mongo to MySql. Better implementation could be a TypeHandler, which chains two Typehandlers
 * which are reacting to the type of the java field on the one hand and on the type of the column on the other hand!
 * 
 * @author Michael Remme
 * 
 */

public class SqlIdTypeHandler extends IdTypeHandler {

  /**
   * @param typeHandlerFactory
   *          the {@link ITypeHandlerFactory} where the current instance is part of
   */
  public SqlIdTypeHandler(ITypeHandlerFactory typeHandlerFactory) {
    super(typeHandlerFactory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.braintags.io.vertx.util.pojomapper.json.typehandler.handler.IdTypeHandler#isCharacterColumn(de.braintags.io.vertx.util.
   * pojomapper.mapping.IField)
   */
  @Override
  protected boolean isCharacterColumn(IField field) {
    return SqlUtil.isCharacter(field.getColumnInfo());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.json.typehandler.handler.IdTypeHandler#isNumericColumn(de.braintags.io.vertx.util.
   * pojomapper.mapping.IField)
   */
  @Override
  protected boolean isNumericColumn(IField field) {
    return SqlUtil.isNumeric(field.getColumnInfo());
  }

}
