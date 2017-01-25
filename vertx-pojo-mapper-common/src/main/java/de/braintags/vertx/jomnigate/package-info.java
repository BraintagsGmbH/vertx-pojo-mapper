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
 * :numbered:
 * :toc: left
 * :toclevels: 3
 * 
 * = vertx pojo mapper common
 * 
 * Map Java objects into datastores and back
 * 
 * == Quick-Start
 * Although this library will deal with completely different types of datastores like SQL, NoSQL etc., we are using some
 * idioms inside the further description, which are more or less database depending. Thus a "table" will refer to a
 * table inside a MySQL for instance and to a collection inside a MongoDB - just to avoid stringy text constructions.
 *
 * === Using vertx-pojo-mapper
 * To use this project, add the following dependency to the _dependencies_ section of your build descriptor:
 * 
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>${maven.groupId}</groupId>
 * <artifactId>${maven.artifactId}</artifactId>
 * <version>${maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * dependencies {
 * compile '${maven.groupId}:${maven.artifactId}:${maven.version}'
 * }
 * ----
 *
 *
 * replace maven-artifactId against one of
 * 
 * * *vertx-pojongo* the mongo driver
 * * *vertx-pojo-mapper-mysql* the MySql driver
 * 
 *
 * === Initializing an IDataStore
 * The initialization of the {@link de.braintags.vertx.jomnigate.IDataStore} is the only action, where you are
 * directly referring to a concrete implementation. In this example we will use a Mongo-DB as datastore:
 * 
 * [source, java]
 * ----
 * JsonObject config = new JsonObject();
 * config.put("connection_string", "mongodb://localhost:27017");
 * config.put("db_name", "PojongoTestDatabase");
 * MongoClient mongoClient = MongoClient.createNonShared(vertx, config);
 * MongoDataStore mongoDataStore = new MongoDataStore(vertx, mongoClient, config);
 * ----
 * To initialize the datastore, we are first creating an instance of MongoClient from the vertx-mongo-client.
 * To create an instance we define the properties with the minimum definition connection_string and db_name, which
 * are defining the location of the running Mongo-DB and the database to be used.
 * With this MongoClient we are then able to create a MongoDataStore, which will be the base for all the followings.
 * 
 * ==== Initialize by DataStoreSettings
 * {@link de.braintags.vertx.jomnigate.init}
 * 
 * === Creating a mapper
 * Creating a mapper is very simple:
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.MiniMapper}
 * ----
 * As you can see, you can specify any java class as a mapper by adding two annotations:
 * 
 * * {@link de.braintags.vertx.jomnigate.annotation.Entity}
 * is added at the class level and defines, that the class, where this annotation is added, is mappable by a datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.field.Id}
 * is added at one property field of the class and defines this field to be the key field, where inside the
 * identifyer of a record is generated and stored
 * 
 * [small]#don't bother about the upper annotation @Source, which is needed to generate this documentation
 * and has nothing to do with the mapping definition#
 * 
 * Instead of using public field, we could have defined the fields as private and added the suitable getter / setter
 * methods, but for this example its the shorter way.
 * 
 * === Saving data into a datastore
 * First we are creating an instance like - lets say - instances are created in java?
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example2()}
 * ----
 * 
 * Next we want to save this MiniMapper into the connected datastore.
 * [source,java]
 * ----
 * {@link examples.Examples#example3(IDataStore, examples.mapper.DemoMapper)}
 * ----
 * 
 * To save one or more instances inside the datastore, we are first creating an
 * {@link de.braintags.vertx.jomnigate.dataaccess.write.IWrite}. As soon as we added the instance
 * into the IWrite, we are able to execute the save action on it and therefore save our MiniMapper into the
 * connected datastore.
 * In return we are receiving information about the action performed in an asynchrone way. The
 * {@link de.braintags.vertx.jomnigate.dataaccess.write.IWriteResult}, which was delivered to our
 * handler contains general informations about the action and specific information about each object, which was
 * saved by the current action. These information - delivered as
 * {@link de.braintags.vertx.jomnigate.dataaccess.write.IWriteEntry} -
 * include the type of action performed ( insert / update ), the id
 * of the instance ( especially for new instances ) and the native format of the instance, like it was translated to fit
 * the requirements of the connected datastore.
 * 
 * NOTE: You may have noticed, that up to here we did not have to execute any intialization of the mapper inside the
 * datastore. This is, because the vertx-pojo-mapper is taking care about that completely automatic exactly then, when
 * it is
 * needed.
 * When you are creating an insert like above, or a query like later, the system checks, wether the mapper was
 * initialized already. If not, then the initialization is performed, which implements the automatic creation and update
 * of tables, collections etc. inside the connected datastore - so you don't have to care about that, either.
 * Its this behaviour, why the vertx-pojo-mapper has only a very little overhead on startup!
 * 
 * 
 * === Searching data in a datastore
 * 
 * To search inside the connected datastore, we are creating first an instance of
 * {@link de.braintags.vertx.jomnigate.dataaccess.query.IQuery}, then we are adding the query arguments on it.
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example4(IDataStore)}
 * ----
 * 
 * In the current example we are only searching for the name, but as IQuery supports a fluent api
 * we could simply and quickly add further arguments. Again - with the creation of the IQuery - the system checks wether
 * the mapper class was mapped already and performs the mapping if not. +
 * The query is processed by calling the execute method, which in turn will deliver an
 * {@link de.braintags.vertx.jomnigate.dataaccess.query.IQueryResult}. The IQueryResult contains several
 * information like the native query and a reference to found records. The found records can be requested step by step
 * by an Iterator or once as Array by requesting the method toArray.
 * 
 * NOTE: To return as fast as possible and to produce the least overhead, in the first step the query only stores the
 * native result of the query inside the IQueryResult together with some meta information. Only when you are accessing
 * concrete objects by using the iterator of the IQueryResult or the method toArray, the needed java objects are
 * created, if not done already.
 * 
 * 
 * === Deleting data
 * 
 * To delete instanced from the datastore, we are using
 * {@link de.braintags.vertx.jomnigate.dataaccess.delete.IDelete}, where
 * we can add some concrete objects to be deleted or add an
 * {@link de.braintags.vertx.jomnigate.dataaccess.query.IQuery}, which defines
 * the criteria for a deletion. Mixing both isn't possible.
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example5(IDataStore, examples.mapper.DemoMapper)}
 * ----
 * 
 * In the current example we are deleting an object, which we are expecting to exist in the datastore. First we are
 * creating an {@link de.braintags.vertx.jomnigate.dataaccess.delete.IDelete} and add the instance to be deleted.
 * The execution of the delete is processed by calling the method delete, which will return an instance of
 * {@link de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult}. The method
 * {@link de.braintags.vertx.jomnigate.dataaccess.delete.IDeleteResult#getOriginalCommand()} returns the native
 * arguments which were used to perform the delete action
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example6(IDataStore)}
 * ----
 * 
 * This example shows how to perform a delete action by using an
 * {@link de.braintags.vertx.jomnigate.dataaccess.query.IQuery}.
 * All records, which are fitting the arguments of the query are deleted.
 *
 * === Executing native, database specific commands
 * If the facilities of vertx-pojo-mapper aren't enough, you are able to execute native commands directly in two ways:
 * 
 * ==== Using the internal driver
 * The method {@link de.braintags.vertx.jomnigate.IDataStore#getClient()} returns the internall client, which is
 * used to communicate with the database. Casting this to the correct Class will allow you to send native commands in
 * any form to the database and deal with the native format, like in the example here for a MongoDb:
 * 
 * <pre>
 * [source,java]
 * ----
  MongoClient client = (MongoClient) datastore.getClient();
  JsonObject insertCommand = new JsonObject();
  insertCommand.put("name", "testName");
  client.insert("TestCollection", insertCommand, result -> {
    if (result.failed()) {
      logger.error("", result.cause());
    } else {
      logger.info("executed: " + result.result());
    }
  });
 * ----
 * </pre>
 * 
 * ==== Using IQuery for a native command
 * The method {@link de.braintags.vertx.jomnigate.dataaccess.query.IQuery#setNativeCommand(Object)} allows you to
 * define
 * an object with a native, database specific query expression. If this argument is passed and the IQuery is executed,
 * then the system will use this command to perform the query and will transform the result into instances of the
 * defined mapper.
 * In the example below we are performing a native execution for MySqlDataStore:
 * 
 * 
 * [source, java]
 * ----
 * IQuery<MiniMapper> query = datastore.createQuery(MiniMapper.class);
 * String qs = "select * from MiniMapper where name LIKE \"native%\"";
 * query.setNativeCommand(qs);
 * query.execute(qr -> {
 * if (qr.succeeded()) {
 * IteratorAsync<MiniMapper> it = qr.result().iterator();
 * while (it.hasNext()) {
 * ...
 * }
 * }
 * });
 * 
 * ----
 * 
 * === Complexer mapper definitions
 * 
 * The example above was very simple and straightforward, just to explain the basics of vertx-pojo-mapper. But of course
 * there are
 * existing much more possibilities to define mappers, where from we are listing some here ( the complete list of
 * annotations you will find below).
 * 
 * ==== Handling of subobjects
 * Often you will have to define some mappers, where inside you are placing one or more properties, which are not of a
 * simple type like int, String, boolean etc., but which are based upon a complexer type. Think about a scenario, where
 * a person has one or more animals. +
 * For those relations you can define two ways, how the data are stored into the datastore:
 * 
 * * embedded +
 * the subobjects ( animals ) are stored inside the same table than the main object ( person )
 * * referenced +
 * the subobjects ( animals ) are saved inside an own table; inside the main object ( person ) is saved a reference to
 * the subobjects, typically the key of the subobjects
 * 
 * ===== Storing subobjects embedded
 * 
 * To define, that a subobject shall be saved embedded is simply done by adding the annotation
 * {@link de.braintags.vertx.jomnigate.annotation.field.Embedded} to the appropriate field
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.PersonEmbed}
 * ----
 * 
 * How the embedding is technically processed, is decided by the {@link de.braintags.vertx.jomnigate.IDataStore}. In
 * the same way you are storing simple child objects, you are able to integrate lists, maps and arrays.
 * 
 * Subobjects as array of Animal:
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.PersonEmbedArray}
 * ----
 * 
 * Subobjects as List of Animal:
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.PersonEmbedList}
 * ----
 * 
 * Subobjects as Map of Animal:
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.PersonEmbedMap}
 * ----
 * 
 * ===== Storing subobjects referenced
 * 
 * According the previous description, storing subobjects referenced is done by adding the annotation
 * {@link de.braintags.vertx.jomnigate.annotation.field.Referenced} to the appropriate fields of the
 * mapper. Of course here, too, you are able to store lists, maps and arrays either.
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.PersonRef}
 * ----
 * 
 * ==== Lifecycle methods
 * 
 * In vertx-pojo-mapper are existing a series of lifecycle annotations, by which you can modify the content
 * of objects as a function of its lifecycle. If you are annotating one or more methods of a mapper class with
 * one of the lifecycle annotations, then those method(s) are executed inside the suitable situation
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.LifecycleMapper}
 * ----
 *
 * Currently are existing 6 lifecycle annotations
 * 
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeSave} +
 * methods annotated with this, will be executed just before saving an instance into the datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterSave} +
 * methods annotated with this, will be executed just after saving an instance into the datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeLoad} +
 * methods annotated with this, will be executed just before loading an instance from the datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterLoad} +
 * methods annotated with this, will be executed just after loading an instance from the datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.BeforeDelete} +
 * methods annotated with this, will be executed just before deleting an instance from the datastore
 * * {@link de.braintags.vertx.jomnigate.annotation.lifecycle.AfterDelete} +
 * methods annotated with this, will be executed just after deleting an instance from the datastore
 * 
 * The trigger methods can be empty, or get the parameter
 * {@link de.braintags.vertx.jomnigate.mapping.ITriggerContext}, by which you are able to access the current
 * {@link de.braintags.vertx.jomnigate.IDataStore} for instance, like shown in the example method afterLoad
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.LifecycleMapper#afterLoad(de.braintags.vertx.jomnigate.mapping.ITriggerContext)}
 * ----
 *
 * ==== Encryption
 * By using the annotation {@link de.braintags.vertx.jomnigate.annotation.field.Encoder} you can encrypt field
 * contents like passwords for instance.
 * 
 * [source,java]
 * ----
 * {@link examples.mapper.MiniMapperEncoded}
 * ----
 *
 * In the above example the field password is annotated with
 * {@link de.braintags.vertx.jomnigate.annotation.field.Encoder}, which is getting the name of the encoder as
 * reference. Each datastore integrates one decoder by default,
 * {@link de.braintags.vertx.util.security.crypt.impl.StandardEncoder} with the name StandardEncoder, which we are
 * referencing here. If you want to add another encoder, you can do that by modifying the
 * {@link de.braintags.vertx.jomnigate.init.DataStoreSettings} by adding an instance of
 * {@link de.braintags.vertx.jomnigate.init.EncoderSettings}
 *
 * === Working with geodata
 * {@link de.braintags.vertx.jomnigate.datatypes.geojson}
 *
 * 
 * For more infos on how you can influence the mapping process, see the further descriptions above.
 *
 * == More details about vertx-pojo-mapper
 * 
 * === IDataStore
 * {@link de.braintags.vertx.jomnigate.IDataStore} is the startpoint and the center of vertx-pojo-mapper.
 * By IDataStore you will access all the main instances you need, to deal with the underlaying datastore.
 * To instantiate a certain implementation of IDataStore, it should be the only time, where you are directly referencing
 * to a certain datastore or database. The way, how an implementation is instantiated, is depending on the
 * implementation itself:
 * 
 * Currently there are existing 2 implementations of IDataStore
 * 
 * * MongoDataStore +
 * in the sub project link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojongo[vertx-pojongo],
 * is an implementation which deals with Mongo-DB. Go
 * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojongo[here] to get more informations on
 * how to create an instance of MongoDataStore
 * * MySqlDataStore +
 * in the sub project
 * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojo-mapper-mysql[vertx-pojo-mapper-mysql]
 * is an implementation which deals with MySql or MariaDb. Go
 * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojo-mapper-mysql[here] to get more
 * information on how to create an instance of MySqlDataStore
 * * more implementations will follow soon
 * 
 * Where by using the links above you will get some specific information how to initialize one of those implementations,
 * in the following parts we will go into the detail for some concepts of the api.
 * 
 * === KeyGenerator
 * 
 * If you are inserting new records into a database, those records normally need to get a unique identifyer, typically a
 * primary key. All databases can generate such a key in an automatic manner, but not every database is returning the
 * generated key. For those databases, which don't return the generated key, like MySql, the concept of
 * {@link de.braintags.vertx.jomnigate.mapping.IKeyGenerator} was implemented to allow a key generation with local
 * access before a new instance is saved into the datastore. Another use case is, when the datastore itself creates a
 * cryptic ID and a numeric one is needed +
 * The config below defines a default datastore, which is used for all mappers, where no KeyGenerator is defined.
 * 
 * [source,java]
 * ----
 * JsonObject datastoreConfig = new JsonObject().put("database", database)
 * .put(IKeyGenerator.DEFAULT_KEY_GENERATOR, FileKeyGenerator.NAME);
 * IDataStore datastore = new MySqlDataStore(vertx, mySQLClient, mySQLClientConfig);
 * ...
 * ----
 * 
 * To add an IKeyGenerator to a mapper, you will add the annotation
 * {@link de.braintags.vertx.jomnigate.annotation.KeyGenerator} to the classes head and optionally define the type
 * of keygenerator, which shall be used.
 * 
 * Currently there are existing three implementations of {@link de.braintags.vertx.jomnigate.mapping.IKeyGenerator}:
 * 
 * * {@link de.braintags.vertx.jomnigate.mapping.impl.keygen.DefaultKeyGenerator} +
 * an implementation which uses the eventbus to request a key from
 * {@link de.braintags.vertx.keygenerator.KeyGeneratorVerticle}. To init and launch the KeyGeneratorVerticle, please
 * refer to the doscumentation of the project
 * link:https://github.com/BraintagsGmbH/vertx-key-generator/blob/master/src/docs/asciidoc/java/index.adoc[*vertx-key-
 * generator*]
 * 
 * * {@link de.braintags.vertx.keygenerator.impl.DebugGenerator} +
 * a local implementation which starts at zero by each launch and maybe useful for unint tests etc.
 * 
 * * NULL as a special solution +
 * use {@link de.braintags.vertx.jomnigate.annotation.KeyGenerator#NULL_KEY_GENERATOR} as value to define, that no
 * keygenerator shall be used. This value is useful, when a default keygenerator is set and a certain class shal not use
 * one.
 * 
 * An {@link de.braintags.vertx.jomnigate.IDataStore} implementation might contain a set of
 * {@link de.braintags.vertx.jomnigate.mapping.IKeyGenerator}, which are supported by this implementation.
 * KeyGenerators are stored inside a map by their name and an instance. When initializing an
 * {@link de.braintags.vertx.jomnigate.IDataStore} you can add the property
 * {@link de.braintags.vertx.jomnigate.mapping.IKeyGenerator#DEFAULT_KEY_GENERATOR} together
 * with the name of the KeyGenerator, which shall be used as default. Additionally you can add the annotation
 * {@link de.braintags.vertx.jomnigate.annotation.KeyGenerator} to a mapper, where you are specifying the name of
 * the KeyGenerator, which shall be used for this mapper.
 * 
 * === Mapping of Java classes
 * {@link de.braintags.vertx.jomnigate.mapping}
 * 
 * === TypeHandlers
 * {@link de.braintags.vertx.jomnigate.typehandler}
 * 
 * === Existing annotations
 * {@link de.braintags.vertx.jomnigate.annotation}
 * 
 * == Creating a new implementation
 * 
 * tbd
 * 
 * == Further links
 * To get specific information about the concrete implementation of an
 * {@link de.braintags.vertx.jomnigate.IDataStore}, especially the initialization, go to:
 * 
 * * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojo-mapper-mysql[implementation for
 * MySql]
 * * link:https://github.com/BraintagsGmbH/vertx-pojo-mapper/tree/master/vertx-pojongo[implementation for Mongo-DB]
 *
 *
 */
@Document(fileName = "index.adoc")
package de.braintags.vertx.jomnigate;

import io.vertx.docgen.Document;
