/*
 * #%L vertx-pojo-mapper-common %% Copyright (C) 2015 Braintags GmbH %% All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html #L%
 */
/**
 * :numbered:
 * 
 * = vertx pojo mapper common
 * === Map Java objects into datastores and back
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
 * <groupId>de.braintags</groupId>
 * <artifactId>{maven.artifactId}</artifactId>
 * <version>{maven.version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile "de.braintags:{maven.artifactId}:{maven.version}"
 * ----
 *
 * replace maven-artifactId against
 * 
 * * *vertx-pojongo* the mongo driver
 * * *vertx-pojo-mapper-mysql* the MySql driver
 * 
 *
 * === Initializing an IDataStore
 * The initialization of the {@link de.braintags.io.vertx.pojomapper.IDataStore} is the only action, where you are
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
 * === Creating a mapper
 * Creating a mapper is very simple:
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.MiniMapper}
 * ----
 * As you can see, you can specify any java class as a mapper by adding two annotations:
 * 
 * * {@link de.braintags.io.vertx.pojomapper.annotation.Entity}
 * is added at the class level and defines, that the class, where this annotation is added, is mappable by a datastore
 * * {@link de.braintags.io.vertx.pojomapper.annotation.field.Id}
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
 * {@link examples.Examples#example3(IDataStore, examples.mapper.MiniMapper)}
 * ----
 * 
 * To save one or more instances inside the datastore, we are first creating an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWrite}. As soon as we added the instance
 * into the IWrite, we are able to execute the save action on it and therefore save our MiniMapper into the
 * connected datastore.
 * In return we are receiving information about the action performed in an asynchrone way. The
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult}, which was delivered to our
 * handler contains general informations about the action and specific information about each object, which was
 * saved by the current action. These information - delivered as
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteEntry} -
 * include the type of action performed ( insert / update ), the id
 * of the instance ( especially for new instances ) and the native format of the instance, like it was translated to fit
 * the requirements of the connected datastore.
 * 
 * You may have noticed that up to here we did not have to execute any intialization of the mapper inside the datastore.
 * This, because the vertx-pojo-mapper is taking care about that completely automatic exactly then, when it is needed.
 * When you are creating an insert like above, or a query like later, the system checks, wether the mapper was
 * initialized already. If not, then the initialization is performed. Its that, why the vertx-pojo-mapper has only a
 * very little overhead on startup!
 * 
 * 
 * By creating the IWrite the system will
 * automatically perform the mapping process, if not done already. After adding the instance into the IWrite, we are
 * able to execute the action, which will return an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.write.IWriteResult}, by which we are getting further
 * information
 * about the action, like the generated id of the record and wether it was inserted or updated, for instance.
 * 
 * === Searching data
 * 
 * [source,java]
 * ----
 * link examples.Examples#example4(de.braintags.io.vertx.pojomapper.IDataStore )}
 * ----
 * 
 * To search in the datastore, we are creating first an instance of
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery} and define the query arguments. In the current
 * example we are only searching for the name, but cause IQuery supports a fluent api we could simply and quickly
 * add
 * further arguments. Again - with the creation of the IQuery - the system checks wether the class was mapped
 * already
 * and performs the mapping if not. The query is processed by calling the execute method, which in turn will deliver
 * an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQueryResult}, which contains several information like
 * the
 * native query and a reference to found records. The found records can be requested step by step by an Iterator or
 * once
 * as Array by requesting the method toArray. Both methods are requiring a Handler, since only during this request
 * the
 * Java object is created if not done already. For complexer objects this can mean, that further informations must
 * be
 * loaded from the IDataStore.
 * 
 * 
 * === Deleting data
 * 
 * [source,java]
 * ----
 * examples.Examples#example5(de.braintags.io.vertx.pojomapper.IDataStore, examples.mapper.SimpleMapper )
 * ----
 * 
 * Deletion is processed either by deleting concrete objects or by using an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery} as argument. Mixing of both is not possible. In
 * the
 * current example we are deleting an object, which we are expecting to exist in the datastore. First we are
 * creating an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDelete} and add the instance to be deleted. The
 * execution
 * od mthe delete is processed by calling method delete, which will return an instance of
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult}. The method
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.delete.IDeleteResult#getOriginalCommand()} returns the native
 * arguments which were used to perform the delete action
 * 
 * ----
 * examples.Examples#example6(de.braintags.io.vertx.pojomapper.IDataStore )
 * ----
 * 
 * This example shows how to perform a delete action by using an
 * {@link de.braintags.io.vertx.pojomapper.dataaccess.query.IQuery}. All records, which are fitting the arguments of
 * the
 * query are deleted.
 *
 *
 * == Working with vertx-pojo-mapper
 * 
 * === Mapping of Java classes There is no need to start a special mapping process in your application. The mapping
 * of
 * Java classes is automatically performed at the moment, when it is needed. During the mapping process the class is
 * inspected for several information. The persistent fields of a mapper are generated by inspecting public fields
 * and
 * BeanProperties. The rest of the configuration of a mapper is done by using annotations. Annotations are always
 * added
 * to a field or the Class itself. Even annotations for those properties, which aree defined as getter /
 * setter-method
 * are added to the underlaying field of the property.
 * 
 * You will find some mapper definitions in the example package, for instance:
 * 
 * * {@link examples.mapper.SimpleMapper} as a very simple mapper * {@link examples.mapper.DemoMapper} as an example
 * for
 * referenced and embedded usage
 * 
 * 
 * {@link de.braintags.io.vertx.pojomapper.annotation}
 * 
 * Init process beschreiben
 * 
 * == Creating a new implementation tbd
 * 
 * == Further links
 * MySql driver
 * MongoDriver
 *
 * @author Michael Remme
 */
@Document(fileName = "index.adoc")
package de.braintags.io.vertx.pojomapper;

import io.vertx.docgen.Document;
