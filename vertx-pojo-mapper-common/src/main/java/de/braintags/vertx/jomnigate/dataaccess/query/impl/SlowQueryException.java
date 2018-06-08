package de.braintags.vertx.jomnigate.dataaccess.query.impl;

@SuppressWarnings("serial")
public class SlowQueryException extends RuntimeException {

  public SlowQueryException(final Query<?> query) {
    super(query.toString());
  }

}
