/*
 * #%L
 * vertx-pojo-mapper-mysql
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.vertx.jomnigate.mysql.typehandler;

import de.braintags.vertx.jomnigate.json.typehandler.handler.IdTypeHandler;
import de.braintags.vertx.jomnigate.mapping.IField;
import de.braintags.vertx.jomnigate.mysql.SqlUtil;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandler;
import de.braintags.vertx.jomnigate.typehandler.ITypeHandlerFactory;

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
   * de.braintags.vertx.jomnigate.json.typehandler.handler.IdTypeHandler#isCharacterColumn(de.braintags.vertx.util.
   * pojomapper.mapping.IField)
   */
  @Override
  protected boolean isCharacterColumn(IField field) {
    return SqlUtil.isCharacter(field.getColumnInfo());
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.json.typehandler.handler.IdTypeHandler#isNumericColumn(de.braintags.vertx.util.
   * pojomapper.mapping.IField)
   */
  @Override
  protected boolean isNumericColumn(IField field) {
    return SqlUtil.isNumeric(field.getColumnInfo());
  }

}
