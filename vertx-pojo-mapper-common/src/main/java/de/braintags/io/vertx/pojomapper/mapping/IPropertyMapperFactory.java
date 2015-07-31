/*
 *
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * 
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 * 
 * You may elect to redistribute this code under either of these licenses.
 */

package de.braintags.io.vertx.pojomapper.mapping;

/**
 * Factory to build instances of {@link IPropertyMapper}
 * 
 * @author Michael Remme
 * 
 */

public interface IPropertyMapperFactory {

  /**
   * Get an instance of {@link IPropertyMapper}
   * 
   * @param cls
   *          the interface, for which an implementation shall be retrieved. This can be {@link IPropertyMapper},
   *          {@link IEmbeddedMapper} or {@link IReferencedMapper}
   * @return an imeplementation of the required interface
   */
  public IPropertyMapper getPropertyMapper(Class<? extends IPropertyMapper> cls);
}
