package dev.royalcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

public @interface UnstableOnServerStart {
    String reason() default "This should not be used on server start. It may cause crashes or errors!";
}
