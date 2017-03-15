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

import java.math.BigDecimal;

/**
 * 
 * 
 * @author Michael Remme
 * 
 */
public class BigDecimalMapper {
  private BigDecimal big1 = new BigDecimal(55.55);
  public BigDecimal big2 = new BigDecimal("55.55");

  /**
   * 
   */
  public BigDecimalMapper() {
  }

  /**
   * @return the big1
   */
  public BigDecimal getBig1() {
    return big1;
  }

  /**
   * @param big1
   *          the big1 to set
   */
  public void setBig1(BigDecimal big1) {
    this.big1 = big1;
  }

}
