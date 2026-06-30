package siozy.dev.lunaspring.API.commands.annotations;

import siozy.dev.lunaspring.API.commands.processor.NoArgCommand;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Flags {
    NoArgCommand.AccessFlag[] value() default {};
}
