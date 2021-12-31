package io.github.dxx.finders.http.annotation;

import io.github.dxx.finders.http.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark the method for processing the http requests.
 *
 * @author dxx
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {

    String path() default "";

    RequestMethod method() default RequestMethod.GET;

}
