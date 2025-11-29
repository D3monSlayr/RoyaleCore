package dev.royalcore.annotations;

import java.lang.annotation.*;

/**
 * Marks an API element as deprecated and planned for removal in a future version.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@NotForDeveloperUse

@Documented
public @interface MarkedForRemoval {

    /**
     * Explains why this element is marked for removal or what will replace it.
     *
     * @return a human-readable reason for the planned removal
     */
    String reason() default "This feature may be deleted indefinitely.";
}
