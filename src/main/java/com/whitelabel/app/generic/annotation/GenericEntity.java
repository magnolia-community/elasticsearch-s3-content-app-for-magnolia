/*
 *
 */
package com.whitelabel.app.generic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for defining entity usefull for generic integration
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface GenericEntity {

	/**
	 * Name of generic item
	 *
	 * @return the string
	 */
	public String name() default "";

	/**
	 * Key of entity
	 *
	 * @return the string
	 */
	public String fieldId() default "";
}
