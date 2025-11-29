package dev.royalcore.annotations;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented

@NotForDeveloperUse

public @interface MarkedForRemoval {

    /**
     * Explains why this element is marked for removal or what will replace it.
     *
     * @return a human-readable reason for the planned removal
     */
    String reason() default "This feature may be deleted indefinitely.";
}
