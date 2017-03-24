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
package de.braintags.vertx.jomnigate.json.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.PackageVersion;

import de.braintags.vertx.jomnigate.IDataStore;
import de.braintags.vertx.jomnigate.annotation.Entity;
import de.braintags.vertx.jomnigate.annotation.field.Embedded;
import de.braintags.vertx.jomnigate.annotation.field.Referenced;
import de.braintags.vertx.jomnigate.datatypes.geojson.GeoPoint;
import de.braintags.vertx.jomnigate.exception.MappingException;
import de.braintags.vertx.jomnigate.json.jackson.deserializer.geo.GeoPointDeserializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.embedded.EmbeddedObjectSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.geo.GeoPointSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedArraySerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedCollectionSerializer;
import de.braintags.vertx.jomnigate.json.jackson.serializer.referenced.ReferencedObjectSerializer;

/**
 * Used to initialize serializers and deserializers for the special need of jomnigate ( Referenced / Embedded for
 * instance )
 * 
 * @author Michael Remme
 * 
 */
public class AnnotationIntrospectorJomnigate extends NopAnnotationIntrospector implements Versioned {
  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 1L;
  private static final String EMBEDDED_STRING = "embedded";
  private static final String REFERENCED_STRING = "referenced";

  private IDataStore<?, ?> datastore;

  /**
   * 
   */
  public AnnotationIntrospectorJomnigate(IDataStore<?, ?> datastore) {
    this.datastore = datastore;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.fasterxml.jackson.databind.AnnotationIntrospector#version()
   */
  @Override
  public Version version() {
    return PackageVersion.VERSION;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.fasterxml.jackson.databind.AnnotationIntrospector#findSerializer(com.fasterxml.jackson.databind.introspect.
   * Annotated)
   */
  @Override
  public Object findSerializer(Annotated am) {
    if (am.hasAnnotation(Referenced.class)) {
      return findReferencedSerializer(am);
    } else if (am.hasAnnotation(Embedded.class)) {
      return findEmbeddedSerializer(am);
    } else if (am.getType() != null && am.getType().isTypeOrSubTypeOf(GeoPoint.class)) {
      return new GeoPointSerializer(datastore);
    }
    return super.findSerializer(am);
  }

  /**
   * @param am
   * @return
   */
  private JsonSerializer<?> findEmbeddedSerializer(Annotated am) {
    JavaType jt = am.getType();
    if (jt.isArrayType() || jt.isCollectionLikeType() || jt.isMapLikeType() || jt.isEnumType()) {
      return null;
    } else {
      AnnotationIntrospectorJomnigate.checkEntity(jt.getRawClass(), EMBEDDED_STRING);
      return new EmbeddedObjectSerializer(datastore, am.getRawType());
    }
  }

  /**
   * We are defining serializer of Maps, which are annotated as {@link Referenced}
   */
  @Override
  public Object findContentSerializer(Annotated am) {
    JavaType jt = am.getType();
    if (am.hasAnnotation(Referenced.class) && am.getType().isMapLikeType()) {
      return new ReferencedObjectSerializer(datastore);
    } else if (am.hasAnnotation(Embedded.class)) {
      return getEmbeddedContentSerializer(am, jt);
    }
    return super.findContentSerializer(am);
  }

  /**
   * @param am
   * @param jt
   * @return
   */
  private JsonSerializer<?> getEmbeddedContentSerializer(Annotated am, JavaType jt) {
    if (am.getType().isMapLikeType() || am.getType().isArrayType() || am.getType().isCollectionLikeType()) {
      AnnotationIntrospectorJomnigate.checkEntity(jt.getContentType().getRawClass(), EMBEDDED_STRING);
      return new EmbeddedObjectSerializer(datastore, jt.getContentType().getRawClass());
    } else {
      throw new UnsupportedOperationException("Content type as Embedded is not supported: " + am.getType());
    }
  }

  /**
   * @param am
   * @return
   */
  private Object findReferencedSerializer(Annotated am) {
    JavaType jt = am.getType();
    if (jt.isArrayType()) {
      checkEntity(am.getType().getContentType().getRawClass(), REFERENCED_STRING);
      return new ReferencedArraySerializer(datastore);
    } else if (jt.isCollectionLikeType()) {
      checkEntity(am.getType().getContentType().getRawClass(), REFERENCED_STRING);
      return new ReferencedCollectionSerializer(datastore);
    } else if (jt.isMapLikeType()) {
      checkEntity(am.getType().getContentType().getRawClass(), REFERENCED_STRING);
      // this is done by using the default serializer with a separate content serializer in findContentSerializer
      return null;
    } else if (jt.isEnumType()) {
      throw new UnsupportedOperationException("referenced Enum is not supported");
    } else {
      checkEntity(am.getType().getRawClass(), REFERENCED_STRING);
      return new ReferencedObjectSerializer(datastore);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.fasterxml.jackson.databind.AnnotationIntrospector#findDeserializer(com.fasterxml.jackson.databind.introspect.
   * Annotated)
   */
  @Override
  public Object findDeserializer(Annotated am) {
    if (am.getType() != null && am.getType().isTypeOrSubTypeOf(GeoPoint.class)) {
      return new GeoPointDeserializer(datastore, am);
    }
    return super.findDeserializer(am);
  }

  /**
   * Checks wether the mapper class is a defined {@link Entity}
   * 
   * @param am
   * @param mapperClass
   * @param annotation
   */
  public static void checkEntity(Class<?> mapperClass, String annotation) {
    if (mapperClass.getAnnotation(Entity.class) == null) {
      throw new MappingException(annotation + " entities must be annotated as Entity: " + mapperClass.getName());
    }
  }

}
