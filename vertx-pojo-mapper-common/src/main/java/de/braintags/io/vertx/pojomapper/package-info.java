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
/**
 * == vertx pojo mapper common - Map Java objects into datastores and back
 * 
 * This module contains common interfaces and default implementations for pojo mapper.
 *
 * To use this project, add the following dependency to the _dependencies_ section of your build descriptor:
 *
 * * Maven (in your `pom.xml`):
 *
 * [source,xml,subs="+attributes"]
 * ----
 * <dependency>
 *   <groupId>{maven-groupId}</groupId>
 *   <artifactId>{maven-artifactId}</artifactId>
 *   <version>{maven-version}</version>
 * </dependency>
 * ----
 *
 * * Gradle (in your `build.gradle` file):
 *
 * [source,groovy,subs="+attributes"]
 * ----
 * compile {maven-groupId}:{maven-artifactId}:{maven-version}
 * ----
 *
 * == Basic concepts
 * tbd
 * 
 * 
 * == Working with a vertx-pojo-mapper
 * === Making a Java class mappable
 * 
 * === Initializing 
 * 
 * [source,java]
 * ----
 * {@link examples.Examples#example1(io.vertx.core.Vertx )}
 * ----
 * 
 * === Saving data
 * 
 * === Searching data
 * 
 * === Deleting data
 * 
 * == Creating a new implementation 
 * tbd
 *
 * @author Michael Remme
 */
@Document(fileName = "index.adoc")
@GenModule(name = "vertx-pojo-mapper-common", groupPackageName = "de.braintags")
package de.braintags.io.vertx.pojomapper;

import io.vertx.codegen.annotations.GenModule;
import io.vertx.docgen.Document;

