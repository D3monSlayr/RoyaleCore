package dev.royalcore.annotations;

import java.lang.annotation.*;

/**
 * Marks an API element as experimental and subject to change or removal.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@NotForDeveloperUse
@Documented
public @interface Experimental {

    /**
     * Explains why this element is considered experimental.
     *
     * @return the reason this feature is not recommended for official or production use
     */
    String reason() default "This feature is experimental and is not recommended for official use.";

}
