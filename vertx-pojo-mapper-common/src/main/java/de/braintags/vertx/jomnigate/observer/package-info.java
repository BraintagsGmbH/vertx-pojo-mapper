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
 * An application, which uses jomnigate, may be registered to react to several events, which are suppported by
 * jomnigate. IObserver is the instance, which can be used to register to those events and to extend jomnigate in a
 * comprehensive way for writing audit logs, checking data permissions, performing data versioning etc.
 * Observers can be executed as fire-and-forget, or the caller is waiting for the execution.
 * 
 * The registration of observers can be done:
 * 
 * * globally by DatastoreSettings.observerSettings
 * a definition can be something like "execute observer myObserver.class for all events of type afterSave", "execute
 * observer myObserver.class for all events for all instances of IAuditable.class", "execute observer myObserver.class
 * with priority 500 for events afterSave, afterDelete for all instances with the annotation Auditable.class"
 * 
 * * per annotation inside a mapper definition
 * the annotation {@link de.braintags.vertx.jomnigate.annotation.Observer} as class annotation defines the events to be
 * executed, the observer class and the priority
 * 
 * An observer can be any class, which implements IObserver. An observer receives informations about the event, the
 * instance to be handled and an ObserverContext, which is created at the beginning of an action inside jomnigate. The
 * observer normally returns a Future, where the caller is waiting for. If it returs null, then the observer is executed
 * as fire-and-forget.
 * 
 * 
 * [source, json]
 * ----
 * 
 * {
 *   "datastoreInit": "de.braintags.vertx.jomnigate.mongo.init.MongoDataStoreInit",
 *   "properties": {
 *     "localPort": "27017",
 *     "connection_string": "mongodb://localhost:27017",
 *     "defaultKeyGenerator": "DefaultKeyGenerator"
 *   },
 *   "databaseName": "UnitTestDatabase",
 *   "encoders": [
 *     {
 *       "name": "StandardEncoder",
 *       "encoderClass": "de.braintags.vertx.util.security.crypt.impl.StandardEncoder",
 *       "properties": {
 *         "salt": "0F06BFA0BF70A46BB9E39121904DC402684543E4B152464D6FAD4324A15BAAED"
 *       }
 *     }
 *   ],
 *   "observerSettings": [
 *     {
 *       "observerClass": "de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver",
 *       "eventTypeList": [],
 *       "mapperSettings": [
 *         "classDefinition" : "de.braintags.vertx.jomnigate.testdatastore.mapper.SimpleMapper",
 *         "annotation" : "com.fasterxml.jackson.annotation.JsonTypeInfo"
 *       ],
 *       "priority": 500
 *     },
 *     {
 *       "observerClass": "de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver2",
 *       "eventTypeList": ["AFTER_DELETE", "BEFORE_SAVE" ],
 *       "mapperSettings": [],
 *       "priority": 200
 *     },
 *     {
 *       "observerClass": "de.braintags.vertx.jomnigate.testdatastore.observer.TestObserver3",
 *       "eventTypeList": [],
 *       "mapperSettings": [],
 *       "priority": 501
 *     }
 *   ]
 * }
 * 
 * ----
 * 
 * TODO further documentation
 * 
 * 
 */
package de.braintags.vertx.jomnigate.observer;
