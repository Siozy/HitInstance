package siozy.dev.lunaspring.API.util.exceptions;

import java.lang.annotation.Annotation;

public class InvalidAnnotationPresentException extends LunaException {
    public InvalidAnnotationPresentException(Class<?> targetClass, Class<? extends Annotation> annotationClass) {
        super(String.format("Класс %s должен иметь аннотацию %s", targetClass.getSimpleName(), annotationClass.getSimpleName()));
    }
}
