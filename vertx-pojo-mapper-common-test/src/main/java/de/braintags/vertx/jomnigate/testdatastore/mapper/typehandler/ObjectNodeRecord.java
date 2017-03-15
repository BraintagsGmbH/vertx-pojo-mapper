/*
 * #%L
 * vertx-pojo-mapper-common-test
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.vertx.jomnigate.testdatastore.mapper.typehandler;

import com.fasterxml.jackson.databind.node.ObjectNode;

import de.braintags.vertx.jomnigate.annotation.Entity;
import io.vertx.core.json.Json;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
@Entity
public class ObjectNodeRecord extends BaseRecord {
  private ObjectNode objectNode = Json.mapper.createObjectNode();

  public ObjectNodeRecord() {
    objectNode.put("field1", true);
    objectNode.put("field2", 5);
    objectNode.put("field3", "testString");
    objectNode.put("field4", 5.5);
  }

  /**
   * @return the objectNode
   */
  public ObjectNode getObjectNode() {
    return objectNode;
  }

  /**
   * @param objectNode
   *          the objectNode to set
   */
  public void setObjectNode(ObjectNode objectNode) {
    this.objectNode = objectNode;
  }

  @Override
  public boolean equals(Object compare) {
    ObjectNodeRecord cn = (ObjectNodeRecord) compare;
    return equal("field1", cn.objectNode, objectNode) && equal("field2", cn.objectNode, objectNode)
        && equal("field3", cn.objectNode, objectNode) && equal("field4", cn.objectNode, objectNode);
  }

  private boolean equal(String key, ObjectNode n1, ObjectNode n2) {
    return n1.get(key).equals(n2.get(key));
  }

}
