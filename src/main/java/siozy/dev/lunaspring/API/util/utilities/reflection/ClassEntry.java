package siozy.dev.lunaspring.API.util.utilities.reflection;

import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public final class ClassEntry<A extends Annotation> {
    private final Class<?> clazz;
    private final A annotation;
    private final Set<Annotation> additionalAnnotations;
    public ClassEntry(Class<?> clazz, Class<A> annotationClass) {
        this.clazz = clazz;
        if (!clazz.isAnnotationPresent(annotationClass)) throw new RuntimeException(String.format("Отсутствует аннотация: %s", annotationClass.getSimpleName()));

        this.annotation = clazz.getDeclaredAnnotation(annotationClass);
        this.additionalAnnotations = Arrays.stream(clazz.getDeclaredAnnotations()).filter(a -> !a.equals(annotation)).collect(Collectors.toSet());
    }
}
