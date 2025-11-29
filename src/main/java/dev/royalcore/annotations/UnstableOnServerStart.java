package dev.royalcore.annotations;

import java.lang.annotation.*;

@NotForDeveloperUse

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented

public @interface UnstableOnServerStart {
    /**
     * Describes why this element should not be used during server startup.
     *
     * @return the reason this element may cause crashes or errors on server start
     */
    String reason() default "This should not be used on server start. It may cause crashes or errors!";

}
