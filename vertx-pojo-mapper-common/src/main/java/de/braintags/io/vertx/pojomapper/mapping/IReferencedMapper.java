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
package de.braintags.io.vertx.pojomapper.mapping;

import de.braintags.io.vertx.pojomapper.annotation.field.Referenced;

/**
 * This extension is meant to be be used for all {@link IField} which are annotated with {@link Referenced}
 * 
 * @author Michael Remme
 * 
 */

public interface IReferencedMapper extends IPropertyMapper {

}
