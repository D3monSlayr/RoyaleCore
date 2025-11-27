package dev.royalcore.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotForDeveloperUse

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotForDeveloperUse {

    String reason() default "This feature is unsafe to use for developers. It is meant only for internal things.";

}
