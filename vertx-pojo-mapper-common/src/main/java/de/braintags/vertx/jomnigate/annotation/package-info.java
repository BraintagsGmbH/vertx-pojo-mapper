/*-
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

/**
 * 
 * AS explained above, the definition of the mapping is currently done by using annotations, which are added to class
 * header of the pojo or to the single properties, to define the behavior of this class in terms of mapping.
 * 
 * Existing annotations are:
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.Entity}
 * 
 * ( name = "tableName" ) +
 * The annotation {@link de.braintags.vertx.jomnigate.annotation.Entity} defines a POJO to be mappable.
 * Additionally you are able to set the name of the table, which is used to store the information in the
 * {@link de.braintags.vertx.jomnigate.IDataStore}. By default the system will use the short classname of the
 * mapper.
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.field.Id}
 * 
 * One field of the mapper must be annotated by {@link de.braintags.vertx.jomnigate.annotation.field.Id}, which
 * will mark the annotated field as primary key
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.field.Property}
 * 
 * Properties of a mapper are stored inside the {@link de.braintags.vertx.jomnigate.IDataStore} by using the
 * field name by default. By annotating a field with the annotation Property, you are able to modify the name of the
 * column in the table. Additionally you are able to define other attributes, which are very datastore specific, so you
 * should use them never or only very carefully:
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.field.Referenced}
 * 
 * When you define a mapper, which internally references with one property to another mapper ( see example Person and
 * his animals ), then you can define the way, how subobjects are stored inside the datastore. With this annotation you
 * define, that the subobjects are stored inside a separate table, and in the field itself only a reference - typically
 * the identifier - is saved. When reading the instance then from the datastore, the references are resolved
 * automatically.
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.field.Embedded}
 * The counterpart to {@link de.braintags.vertx.jomnigate.annotation.field.Referenced}. A property, which is marked
 * with this annotation will be saved completely inside the table. How this is done, is decided by the implementation of
 * the {@link de.braintags.vertx.jomnigate.IDataStore} you are using.
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.ObjectFactory}
 * 
 * By default the {@link de.braintags.vertx.jomnigate.mapping.IObjectFactory} is defined inside each
 * {@link de.braintags.vertx.jomnigate.mapping.IMapper} by using a default implementation. If you need another
 * implementation you are able to set it by adding this annotation to the mapper class and reference the class of the
 * {@link de.braintags.vertx.jomnigate.mapping.IObjectFactory} you want to use.
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad}
 * 
 * All methods, which are annotated by this annotation are executed after an instance was loaded from the
 * {@link de.braintags.vertx.jomnigate.IDataStore}
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad}
 * 
 * All methods, which are annotated by this annotation are executed before an instance is loaded from the
 * {@link de.braintags.vertx.jomnigate.IDataStore}. That means, first the new instance is created, then the
 * method is executed and then the data are transferred into the instance
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave}
 * 
 * All methods, which are annotated by this annotation are executed after an instance was saved into the
 * {@link de.braintags.vertx.jomnigate.IDataStore}
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave}
 * 
 * All methods, which are annotated by this annotation are executed before an instance is saved into the
 * {@link de.braintags.vertx.jomnigate.IDataStore}
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete}
 * 
 * All methods, which are annotated by this annotation are executed after an instance was deleted from the
 * {@link de.braintags.vertx.jomnigate.IDataStore}
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete}
 * 
 * All methods, which are annotated by this annotation are executed before an instance is deleted from the
 * {@link de.braintags.vertx.jomnigate.IDataStore}
 * 
 * 
 * ===== {@link de.braintags.vertx.jomnigate.annotation.KeyGenerator}
 * With this annotation you may define the {@link de.braintags.vertx.jomnigate.mapping.IKeyGenerator}, which shall
 * be used for the mapper. Normally the IKeyGenerator is used, which is defined as default by
 * {@link de.braintags.vertx.jomnigate.IDataStore#getDefaultKeyGenerator()}, which should be normally
 * {@link de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator}. As a value for this annotation you
 * are defining the name of the IKeyGenerator, which shall be used
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.KeyGeneratorMapper}
 * ----
 * 
 *
 */
package de.braintags.vertx.jomnigate.annotation;
