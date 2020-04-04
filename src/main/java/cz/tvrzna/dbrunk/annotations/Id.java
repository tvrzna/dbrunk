package cz.tvrzna.dbrunk.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation defines Id member of database entity class.
 *
 * @author michalt
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id
{

}
