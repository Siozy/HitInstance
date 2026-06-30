package siozy.dev.lunaspring.API.util.utilities.reflection;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.LunaPlugin;
import siozy.dev.lunaspring.LunaSpring;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@UtilityClass
public class AnnotationScanner {
    @SneakyThrows
    public <A extends Annotation> Set<ClassEntry<A>> findAnnotatedClasses(LunaPlugin plugin, Class<A> annotationClass, List<Class<?>> ignoredClasses, String... allowedPackages) {
        Set<ClassEntry<A>> annotatedClasses = Sets.newHashSet();
        allowedPackages = Arrays.stream(allowedPackages).map(p -> p.replace("#", plugin.getClass().getPackage().getName())).toList().toArray(new String[0]);
        try (JarFile jar = new JarFile(plugin.getJar())) {
            Enumeration<JarEntry> e = jar.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName().replace(".class", "").replace('/', '.');
                    if (allowedPackages.length > 0 && Arrays.stream(allowedPackages).noneMatch(className::startsWith)) continue;

                    Class<?> clazz = Class.forName(className);
                    if (ignoredClasses != null && !ignoredClasses.isEmpty() && ignoredClasses.contains(clazz)) continue;

                    Utils.debug(LunaSpring.getInstance(), clazz.getName() + ": " + annotationClass.getSimpleName());
                    if (clazz.isAnnotationPresent(annotationClass)) {
                        ClassEntry<A> classEntry = new ClassEntry<>(clazz, annotationClass);
                        annotatedClasses.add(classEntry);
                    }
                }
            }
        } catch (IllegalStateException ignored) {}
        return annotatedClasses;
    }

    public <A extends Annotation> Set<ClassEntry<A>> findAnnotatedClasses(LunaPlugin plugin, Class<A> annotationClass, String... allowedPackages) {
        return findAnnotatedClasses(plugin, annotationClass, null, allowedPackages);
    }

    public <A extends Annotation> Set<ClassEntry<A>> findAnnotatedClasses(LunaPlugin plugin, Class<A> annotationClass, List<Class<?>> ignoredClasses) {
        return findAnnotatedClasses(plugin, annotationClass, ignoredClasses, new String[]{});
    }
}
