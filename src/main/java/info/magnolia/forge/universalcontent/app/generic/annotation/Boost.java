/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for define a fields that can have a Boost In UI we can define how
 * it will be boost.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Boost {

	/**
	 * Name.
	 *
	 * @return the string
	 */
	public String name() default "";
}
