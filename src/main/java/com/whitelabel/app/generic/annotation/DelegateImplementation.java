package com.whitelabel.app.generic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DelegateImplementation {

	/**
	 * ParameterClass
	 *
	 * @return Object.class
	 */
	public Class parameterClass() default Object.class;

}