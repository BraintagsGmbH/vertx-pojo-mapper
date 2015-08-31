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

package de.braintags.io.vertx.pojomapper.dataaccess.write;

/**
 * When an instance is written, the used action is stored inside {@link IWriteEntry} as info
 * 
 * @author Michael Remme
 * 
 */

public enum WriteAction {
  INSERT,
  UPDATE,
  UNKNOWN;
}
