/*
 *
 */
package info.magnolia.forge.universalcontent.app.generic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import info.magnolia.forge.universalcontent.app.generic.format.PlainCellColumnFormatter;
import info.magnolia.ui.workbench.column.definition.ColumnFormatter;

/**
 * Annotation defining how format that field will be.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Format {
	public String formatter() default "";

	public Class<? extends ColumnFormatter> classFormatter() default PlainCellColumnFormatter.class;

	public int width() default 55;

	public float expandWidth() default 3.5F;
}
