/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.testdatastore.mapper.typehandler;

import java.util.Locale;

import de.braintags.io.vertx.pojomapper.annotation.Entity;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class LocaleRecord extends BaseRecord {
  public Locale locale = Locale.FRANCE;

}
