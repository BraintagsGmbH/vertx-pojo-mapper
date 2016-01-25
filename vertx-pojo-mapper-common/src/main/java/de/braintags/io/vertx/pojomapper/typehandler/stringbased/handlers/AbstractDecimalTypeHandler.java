/*
 * #%L
 * vertx-pojongo
 * %%
 * Copyright (C) 2015 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package de.braintags.io.vertx.pojomapper.typehandler.stringbased.handlers;

import de.braintags.io.vertx.pojomapper.mapping.IField;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandler;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerFactory;
import de.braintags.io.vertx.pojomapper.typehandler.ITypeHandlerResult;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

/**
 * An abstract implementation of {@link ITypeHandler} for those, which are using decimal numbers
 * 
 * @author Michael Remme
 * 
 */
public abstract class AbstractDecimalTypeHandler extends AbstractNumericTypeHandler {

  /**
   * @param typeHandlerFactory
   * @param classesToDeal
   */
  public AbstractDecimalTypeHandler(ITypeHandlerFactory typeHandlerFactory, Class<?>... classesToDeal) {
    super(typeHandlerFactory, classesToDeal);
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.braintags.io.vertx.util.pojomapper.typehandler.ITypeHandler#fromStore(java.lang.Object,
   * de.braintags.io.vertx.util.pojomapper.mapping.IField, java.lang.Class, io.vertx.core.Handler)
   */
  @Override
  public final void fromStore(Object source, IField field, Class<?> cls,
      Handler<AsyncResult<ITypeHandlerResult>> resultHandler) {
    String s = source == null || ((String) source).trim().hashCode() == 0 ? "0" : ((String) source).trim();
    try {
      success(createInstance(s), resultHandler);
    } catch (NumberFormatException e) {
      success(createInstance(cleanup(s)), resultHandler);
    }
  }

  private String cleanup(String value) {
    // Replace "," by "."
    // Store old value to test for "," later if necessary.
    /*
     * Build StringTokenizer to determine if there is more than one dot in the newValue (like in 1.000,00 where you
     * end up with to many dots and get a NumberFormatException).
     */
    String newValue = value.replaceAll(",", ".");
    final String oldValue = value;

    String[] elements = newValue.split("\\.");
    if (elements.length > 2) {
      Buffer buffer = Buffer.buffer();
      // Reassemble the value parts and position the dot where it belongs.
      for (int j = 0; j < elements.length; j++) {
        // Reassemble all values before the last part without dot
        if (j < elements.length - 1) {
          buffer.appendString(elements[j]);
        } else {
          /*
           * Test if the old value contains comma. If so, position a dot before the last part. If not, leave it out.
           */
          if (oldValue.indexOf(',') > 0) {
            buffer.appendString(".").appendString(elements[j]);
          } else {
            buffer.appendString(elements[j]);
          }
        }
      }
      newValue = buffer.toString();
    }
    return newValue;
  }

}
