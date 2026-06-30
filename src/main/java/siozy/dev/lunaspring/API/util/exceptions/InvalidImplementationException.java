package siozy.dev.lunaspring.API.util.exceptions;

public class InvalidImplementationException extends LunaException {
    public InvalidImplementationException(Class<?> clazz, Class<?> interfaze) {
        super(String.format("Класс %s должен быть наследником/реализацией %s", clazz.getSimpleName(), interfaze.getSimpleName()));
    }
}
