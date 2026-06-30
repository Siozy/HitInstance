package siozy.dev.lunaspring.API.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TabCompleteIgnore {
    /**
     * Массив индентификаторов подкоманд, которые не должны отображаться в таб-комплитере
     */
    String[] value() default {};
}
