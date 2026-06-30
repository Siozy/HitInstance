package siozy.dev.lunaspring.API.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)

public @interface Args {
    /**
     * Установка минимального кол-ва аргументов у команды
     * ВАЖНО! Сохраняет индексацию аргументов, то есть подкоманда - args[0], то есть тоже аргумент!
     */
    int min();

    /**
     * Установка максимального кол-ва аргументов у команды
     * ВАЖНО! Сохраняет индексацию аргументов, то есть подкоманда - args[0], то есть тоже аргумент!
     */
    int max();
}
