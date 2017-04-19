package de.braintags.vertx.jomnigate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.braintags.vertx.jomnigate.mapping.IndexOption.IndexFeature;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE })
public @interface IndexOption {
  IndexFeature feature();

  boolean value();

}
