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
package de.braintags.vertx.jomnigate.json.mapping.jackson;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

import de.braintags.vertx.jomnigate.mapping.IPropertyAccessor;

/**
 * An implementation of {@link IPropertyAccessor} for jackson
 * 
 * @author Michael Remme
 * 
 */
public class JacksonPropertyAccessor implements IPropertyAccessor {
  private AnnotatedMember member;
  private String name;

  /**
   * 
   */
  public JacksonPropertyAccessor(AnnotatedMember member) {
    this.member = member;
    this.name = member.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyAccessor#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyAccessor#readData(java.lang.Object)
   */
  @Override
  public Object readData(Object record) {
    return member.getValue(record);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.vertx.jomnigate.mapping.IPropertyAccessor#writeData(java.lang.Object, java.lang.Object)
   */
  @Override
  public void writeData(Object record, Object data) {
    member.setValue(record, data);
  }

}
