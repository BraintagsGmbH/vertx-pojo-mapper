/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.dataaccess.write;

import java.util.Collection;

import de.braintags.vertx.jomnigate.dataaccess.IAccessResult;

/**
 * This object is created by a save action and contains the information about the action itself and the objects saved
 * 
 * 
 * @author Michael Remme
 *
 */
public interface IWriteResult extends Collection<IWriteEntry>, IAccessResult {

}
