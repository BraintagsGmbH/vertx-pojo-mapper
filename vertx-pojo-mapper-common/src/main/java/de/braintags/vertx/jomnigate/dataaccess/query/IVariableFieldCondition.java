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
package de.braintags.vertx.jomnigate.dataaccess.query;

/**
 * Marker interface to mark a field condition that has a variable as value and must be passed through an
 * {@link IFieldValueResolver}
 *
 * @author sschmitt
 *
 */
public interface IVariableFieldCondition extends IFieldCondition {

}
