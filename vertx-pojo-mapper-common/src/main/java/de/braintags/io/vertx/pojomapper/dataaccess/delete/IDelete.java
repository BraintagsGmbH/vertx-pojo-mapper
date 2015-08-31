/*
 * Copyright 2015 Braintags GmbH
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * You may elect to redistribute this code under this licenses.
 */

package de.braintags.io.vertx.pojomapper.dataaccess.delete;

import de.braintags.io.vertx.pojomapper.dataaccess.IDataAccessObject;

/**
 * IDelete is responsible for the deletion of instances from the connected datastore
 * 
 * @author Michael Remme
 * 
 */

public interface IDelete<T> extends IDataAccessObject<T> {

}
