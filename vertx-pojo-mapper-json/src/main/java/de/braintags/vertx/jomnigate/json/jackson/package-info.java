/*
 * #%L
 * vertx-pojo-mapper-json
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
 * Mapping in JsonDatastore is not performed by using {@link de.braintags.vertx.jomnigate.typehandler.ITypeHandler} but
 * by using jackson ( https://github.com/FasterXML )
 * 
 * === Internal ObjectMapper and jackson modules
 * The JsonDatastore contains two own {@link com.fasterxml.jackson.databind.ObjectMapper} ( regular / pretty ), which
 * are for internal use only and should not be used from external applications. Inside those mappers the
 * {@link de.braintags.vertx.jomnigate.json.jackson.JacksonModuleJomnigate} is registered. Thos module contains
 * extensions, which are mostly needed to create the bridge between the asynchrone execution of jomnigate and the
 * synchronous execution of jackson, especially when properties are serialized or deserialized, which are annotated as
 * {@link de.braintags.vertx.jomnigate.annotation.field.Referenced}
 * 
 * === Adding other jackson modules
 * If you need to register other modules, you may do that during the init phase of the datastore by using something like
 * 
 * [source,java]
 * ----
 * ((JsonDatastore)getDatastore()).getJacksonMapper().registerModule(...)
 * ----
 * 
 * But since some modules can have side effects, you should check the correct functionality of your application.
 * 
 * === Known restriction:
 * If a mapper contains a property, which is annotated as
 * {@link de.braintags.vertx.jomnigate.annotation.field.Referenced}, this property can't be member of a constructor,
 * which is annotated as {@link com.fasterxml.jackson.annotation.JsonCreator}, because jackson would try to generate an
 * instance with the record references and not with the resolved records on deserialization.
 * 
 * === Known issue
 * The usage of the ParameterNamesModule causes some datatypes to fail. This includes StringBuffer, sql Time etc.
 * Additionally Maps and Arrays, which are annotated as Referenced will fail.
 * 
 * 
 * @author Michael Remme
 * 
 */
package de.braintags.vertx.jomnigate.json.jackson;
