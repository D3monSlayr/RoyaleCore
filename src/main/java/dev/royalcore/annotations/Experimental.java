package dev.royalcore.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@NotForDeveloperUse

public @interface Experimental {
    /**
     * Explains why this element is considered experimental.
     *
     * @return the reason this feature is not recommended for official or production use
     */
    String reason() default "This feature is experimental and is not recommended for official use.";

}
