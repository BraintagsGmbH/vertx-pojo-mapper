/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

package de.braintags.io.vertx.pojomapper.mysql.mapping.datastore;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnHandler;
import de.braintags.io.vertx.pojomapper.mapping.datastore.IColumnInfo;
import de.braintags.io.vertx.pojomapper.mapping.datastore.impl.DefaultColumnInfo;

/**
 * An implementation of {@link IColumnInfo} for use with sql based datastores.
 * 
 * @author Michael Remme
 * 
 */

public class SqlColumnInfo extends DefaultColumnInfo {

  public SqlColumnInfo(IField field, IColumnHandler columnHandler) {
    super(field, columnHandler);
  }

}
