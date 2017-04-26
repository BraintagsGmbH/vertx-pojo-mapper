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
 * The data versioning and conversion system of jomnigate allows to declare versions of mappers and to convert existing
 * data, when a new mapper version is released.
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.VersionedMapper_V1}
 * ----
 * 
 * <1> To use the data versioning and conversion system, you have to add the annotation VersionInfo to your mapper and
 * set the property version to the current version. Additionally the mapper must implement the interface
 * {@link de.braintags.vertx.jomnigate.versioning.IMapperVersion}, otherwise the initialization of the mapper will fail.
 * 
 * <2> In general it is a good idea to integrate a version flag into the mapper class name as well from the beginning on
 * and to use the "name"-property of the annotation Entity either, so that following versions will write into the same
 * table or collection.
 * 
 * By declaring a mapper in this described way, the data versioning and conversion system will automatically set the
 * value of the property "mapperVersion" to the current version for new instances.
 * 
 * Lets now assume, that there were some requirements, which are changing the VersionedMapper. The mapper shall get a
 * new property "newProperty" as String. The value of all records, which are existing already, shall be "oldValue", for
 * new instances it shall be "newValue".
 * 
 * We are declaring the new version of the mapper:
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.VersionedMapper_V2}
 * ----
 * 
 * First we are raising the version number of the mapper to 2, so that all new instances from now on will automatically
 * get the mapperVersion 2. Second we are adding the property "versionConverter", which will be explained below.
 * 
 * We are setting the default value of "newProperty" to "newValue", so that new instances are getting this value by
 * default. If after this change we would load, modify and save an existing record, it would get the same value - which
 * is not our aim. So we are defining now a version converter:
 * 
 * [source, java]
 * ----
 * {@link examples.mapper.converter.V2Converter}
 * ----
 * 
 * In the implementation of the method "convert" we are setting the value of the property "newProperty" to "oldValue" as
 * required. The activation of this converter is done by the the addition "versionConverter" in the annotation
 * VersionInfo, which shall contain a list of annotations VersionConverterDefinition.
 * 
 * The annotation {@link de.braintags.vertx.jomnigate.annotation.VersionConverterDefinition} declares the
 * destinationVersion, which shall be the result after the conversion was done, and the converter class, which shall be
 * executed, which is the V2Converter in our case.
 * Additionally we can define the phase, during which the conversion shall be executed, by adding the property
 * "eventType" to the annotation VersionInfo. Currently the value can be defined as BEFORE_UPDATE or AFTER_LOAD, where
 * the default is BEFORE_UPDATE.
 * 
 * From now on, for each existing record, the system is checking wether a conversion must be processed by comparing the
 * mapperVersion to the current version definition. If the version of the record is smaller than the current version and
 * if converters are defined, the system will search for all converters, which must be applied, execute them in the
 * correct order and sets the current version to the destination version of the converter.
 * 
 */
package de.braintags.vertx.jomnigate.versioning;
