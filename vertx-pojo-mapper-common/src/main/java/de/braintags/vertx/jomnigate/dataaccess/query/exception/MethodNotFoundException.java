package de.braintags.vertx.jomnigate.dataaccess.query.exception;

/**
 * 
 * 
 * @author sschmitt
 * 
 */
public class MethodNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 422908178501965274L;

  public MethodNotFoundException(String msg) {
    super(msg);
  }

  public MethodNotFoundException(String msg, Exception e) {
    super(msg, e);
  }

}
