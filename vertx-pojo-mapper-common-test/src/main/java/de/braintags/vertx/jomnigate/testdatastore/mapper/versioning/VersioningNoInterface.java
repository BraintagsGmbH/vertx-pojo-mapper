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
package de.braintags.vertx.jomnigate.testdatastore.mapper.versioning;

import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.VersionInfo;
import de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler.BaseRecord;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
@VersionInfo(version = 5)
public class VersioningNoInterface extends BaseRecord {
}
