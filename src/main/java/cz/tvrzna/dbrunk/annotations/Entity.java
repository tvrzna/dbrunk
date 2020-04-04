package cz.tvrzna.dbrunk.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation defines database entity classes. These classes should extend
 * <code>AbstractEntity</code>.
 *
 * @since 0.1.0
 * @author michalt
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface Entity
{

	/**
	 * Table name, where data are stored.
	 *
	 * @return the string
	 */
	String value();
}
