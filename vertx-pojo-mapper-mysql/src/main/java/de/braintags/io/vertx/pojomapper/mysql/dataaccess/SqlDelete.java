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

package de.braintags.io.vertx.pojomapper.mysql.dataaccess;

import de.braintags.io.vertx.pojomapper.IDataStore;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult;
import de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.Delete;
import de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery;
import de.braintags.io.vertx.pojomapper.mysql.MySqlDataStore;
import de.braintags.io.vertx.pojomapper.mysql.SqlUtil;
import de.braintags.io.vertx.pojomapper.mysql.exception.SqlException;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.sql.UpdateResult;

/**
 * An implementation of {@link IDelete} for sql databases
 * 
 * @param <T>
 *          the type of the mapper, which is handled here
 * @author Michael Remme
 * 
 */

public class SqlDelete<T> extends Delete<T> {

  /**
   * @param mapperClass
   * @param datastore
   */
  public SqlDelete(Class<T> mapperClass, IDataStore datastore) {
    super(mapperClass, datastore);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.pojomapper.dataaccess.delete.impl.Delete#deleteQuery(de.braintags.io.vertx.pojomapper.
   * dataaccess.query.IQuery, io.vertx.core.Handler)
   */
  @Override
  protected void deleteQuery(IQuery<T> q, Handler<AsyncResult<IDeleteResult>> resultHandler) {
    SqlQuery<T> query = (SqlQuery<T>) q;
    query.buildQueryExpression(qExpResul -> {
      if (qExpResul.failed()) {
        resultHandler.handle(Future.failedFuture(qExpResul.cause()));
      } else {
        handleDelete((SqlExpression) qExpResul.result(), resultHandler);
      }
    });
  }

  private void handleDelete(SqlExpression expression, Handler<AsyncResult<IDeleteResult>> resultHandler) {
    SqlUtil.updateWithParams((MySqlDataStore) getDataStore(), expression.getDeleteExpression(),
        expression.getParameters(), ur -> {
          if (ur.failed()) {
            resultHandler.handle(Future.failedFuture(new SqlException(expression, ur.cause())));
            return;
          }
          UpdateResult updateResult = ur.result();
          SqlDeleteResult deleteResult = new SqlDeleteResult(getDataStore(), getMapper(), expression, updateResult);
          resultHandler.handle(Future.succeededFuture(deleteResult));
        });

  }

}
