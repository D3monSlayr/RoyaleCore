package dev.royalcore.annotations;

import java.lang.annotation.*;

@NotForDeveloperUse

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NotForDeveloperUse {

    /**
     * Provides a human-readable explanation of why this element is unsafe for general developer use.
     *
     * @return the reason this element is restricted to internal usage
     */
    String reason() default "This feature is unsafe to use for developers. It is meant only for internal things.";

}
