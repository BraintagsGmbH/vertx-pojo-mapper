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
 * ==== Creating and registering an Observer
 * To create an observer you willsimply implement the interface {@link de.braintags.vertx.jomnigate.observer.IObserver}
 * with your observer class. There are two methods to be implemented:
 * 
 * [source, java]
 * ----
 * {@link examples.DemoObserver}
 * ----
 * 
 * <1> The first is the method `handlesEvent` which returns true, if the observer shall handle the given event and false
 * otherwise. In most cases the definition, which observer will handle which event will be done by configuration or by
 * annotation, thus this method will return simply `true`. But there might exist use cases, where the oberver itself has
 * to decide this based on the current data of a concrete event.
 * 
 * <2> The second method is `handleEvent`, which will handle a concrete event. This method must return a `Future` if the
 * caller shall wait for the execution. If the method returns NULL, the event handling is executed as fire-and-forget.
 * 
 * Both methods receive two arguments. One is the IObserverEvent, which contains all existing data which are needed to
 * process the event. The content of the IObserverEvent differs depending on the event type and will be described below.
 * The other argument is the {@link de.braintags.vertx.jomnigate.observer.IObserverContext}, which is created in the
 * beginning of an action like saving object(s) and is delivered to any observer, which participates on this action, so
 * that participating obervers are able to share some data.
 * 
 * Registration of observers is done either by adding some information into the section `observerSettings` of the
 * DataStoreSettings or by adding the annotation {@link de.braintags.vertx.jomnigate.annotation.Observer} to a mapper
 * class.
 * 
 * ===== Register observer by configuration
 * The example configuration below shows some possible configurations, how to register observers for different events
 * and situations
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
 *       "observerClass": "examples.DemoObserver", //<1>
 *       "eventTypeList": ["BEFORE_SAVE"],
 *       "mapperSettings": [
 *         "classDefinition" : "examples.mapper.SimpleMapper"
 *       ],
 *       "priority": 5
 *     },
 *     {
 *       "observerClass": "my.observer.TestObserver", // <2>
 *       "eventTypeList": ["AFTER_DELETE", "BEFORE_SAVE" ],
 *       "mapperSettings": [],
 *       "priority": 200
 *     },
 *     {
 *       "observerClass": "my.observer.TestObserver", // <3>
 *       "eventTypeList": [],
 *       "mapperSettings": [
 *         "classDefinition" : "examples.mapper.SimpleMapper"
 *       ],
 *       "priority": 500
 *     },
 *     {
 *       "observerClass": "my.observer.TestObserver", // <4>
 *       "eventTypeList": [],
 *       "mapperSettings": [
 *         "annotation" : "com.fasterxml.jackson.annotation.JsonTypeInfo"
 *       ],
 *       "priority": 500
 *     },
 *     {
 *       "observerClass": "my.observer.TestObserver", // <5>
 *       "eventTypeList": [],
 *       "mapperSettings": [],
 *       "priority": 501
 *     }
 *   ]
 * }
 * 
 * ----
 * 
 * <1> The observer `examples.DemoObserver` is registered to handle the event type BEFORE_SAVE for the mapper
 * `examples.mapper.SimpleMapper`. The priority is set to be 5, where higher = more important.
 * 
 * <2> An observer is registered for the events AFTER_DELETE and BEFORE_SAVE. Because no mapper settings are defined,
 * this observer will be executed for every mapper class for those events
 * 
 * <3> An observer is registered for every event for the mapper class SimpleMapper
 * 
 * <4> An observer is registered for every event for those mappers, where the class contains the annotation JsonTypeInfo
 * 
 * <5> An observer is registered for any event and mapper
 * 
 * 
 * ===== Register observer by annotation
 * The annotation {@link de.braintags.vertx.jomnigate.annotation.Observer} can be used to register an observer for a
 * certain mapper class. The example below registeres an observer, sets the priority and the event types.
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.AnnotatedObserver}
 * ----
 * 
 * 
 * ==== The events of the observer system
 * Existing events are defined by {@link de.braintags.vertx.jomnigate.observer.ObserverEventType}
 * 
 * * {@link de.braintags.vertx.jomnigate.observer.ObserverEventType#BEFORE_MAPPING} +
 * This event is called before a class is mapped
 * 
 * 
 * TODO further documentation
 * 
 * 
 */
package de.braintags.vertx.jomnigate.observer;
