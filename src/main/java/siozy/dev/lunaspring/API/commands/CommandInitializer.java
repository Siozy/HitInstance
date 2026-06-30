package siozy.dev.lunaspring.API.commands;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import siozy.dev.lunaspring.API.commands.annotations.*;
import siozy.dev.lunaspring.API.commands.processor.CommandProcessor;
import siozy.dev.lunaspring.API.commands.processor.CommandReq;
import siozy.dev.lunaspring.API.commands.processor.NoArgCommand;
import siozy.dev.lunaspring.API.commands.processor.SubCommand;
import siozy.dev.lunaspring.API.util.exceptions.InvalidImplementationException;
import siozy.dev.lunaspring.API.util.utilities.Utils;
import siozy.dev.lunaspring.API.util.utilities.reflection.AnnotationScanner;
import siozy.dev.lunaspring.API.util.utilities.reflection.ClassEntry;
import siozy.dev.lunaspring.LunaPlugin;

import java.util.List;
import java.util.Set;

@UtilityClass
public final class CommandInitializer {
    @SneakyThrows
    public void initialize(LunaPlugin plugin, String... allowedPackages) {
        Set<ClassEntry<siozy.dev.lunaspring.API.commands.annotations.SubCommand>> classList = AnnotationScanner.findAnnotatedClasses(plugin, siozy.dev.lunaspring.API.commands.annotations.SubCommand.class, allowedPackages);
        Set<String> commands = plugin.getDescription().getCommands().keySet();

        Set<ClassEntry<ZeroArgCommand>> zeroArgSubCommandsList = AnnotationScanner.findAnnotatedClasses(plugin, ZeroArgCommand.class, allowedPackages);
        for (String command : commands) {
            CommandProcessor processor = new CommandProcessor(command);

            Utils.find(zeroArgSubCommandsList, zac -> zac.getAnnotation().value().equals(command))
                    .ifPresent(zeroArgSubCommandClassEntry -> processZeroArgsCommand(plugin, zeroArgSubCommandClassEntry, processor));

            for (ClassEntry<siozy.dev.lunaspring.API.commands.annotations.SubCommand> classEntry : classList) {
                processSubCommand(plugin, classEntry, command, processor);
            }

            if (!processor.isEmpty()) {
                plugin.registerCommandProcessor(processor);
            }
        }
    }

    @SneakyThrows
    private void processZeroArgsCommand(LunaPlugin lunaPlugin, ClassEntry<ZeroArgCommand> entry, CommandProcessor processor) {
        Class<?> clazz = entry.getClazz();

        String[] permissions = new String[] { };
        String permissionPathMessage = null;
        SubCommand.AccessFlag[] flags = new SubCommand.AccessFlag[] { };
        if (!Invocation.class.isAssignableFrom(clazz))
            throw new InvalidImplementationException(clazz, Invocation.class);

        Check checkAnnotation = (Check) entry.getAdditionalAnnotations().stream().filter(a -> a.annotationType().equals(Check.class)).findFirst().orElse(null);
        if (checkAnnotation != null) {
            permissions = checkAnnotation.permissions();
            flags = checkAnnotation.flags();
        }
        else {
            Permissions permissionsAnnotation = (Permissions) Utils.find(entry.getAdditionalAnnotations(), a -> a.annotationType().equals(Permissions.class)).orElse(null);
            if (permissionsAnnotation != null) {
                permissions = permissionsAnnotation.value();
                permissionPathMessage = permissionsAnnotation.errorMessagePath();
            }

            Flags flagsAnnotation = (Flags) Utils.find(entry.getAdditionalAnnotations(), a -> a.annotationType().equals(Flags.class)).orElse(null);
            if (flagsAnnotation != null) {
                flags = flagsAnnotation.value();
            }
        }

        Invocation commandInstance;
        try {
            commandInstance = (Invocation) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать класс " + clazz.getName(), e);
        }

        NoArgCommand noArgCommand = NoArgCommand.zBuilder()
                .plugin(lunaPlugin)
                .appliedCommand(processor.appliedCommand())
                .permissionMessagePath(permissionPathMessage)
                .flags(flags)
                .invocation(commandInstance)
                .permissions(permissions)
                .zBuild();
        processor.registerZeroArgCommand(noArgCommand);
    }

    @SneakyThrows
    private void processSubCommand(LunaPlugin lunaPlugin, ClassEntry<siozy.dev.lunaspring.API.commands.annotations.SubCommand> classEntry, String command, CommandProcessor processor) {
        siozy.dev.lunaspring.API.commands.annotations.SubCommand subCommandAnnotation = classEntry.getAnnotation();

        if (subCommandAnnotation.appliedCommand().equals(command)) {
            Class<?> clazz = classEntry.getClazz();

            if (!Invocation.class.isAssignableFrom(clazz))
                throw new InvalidImplementationException(clazz, Invocation.class);

            Check checkAnnotation = (Check) classEntry.getAdditionalAnnotations().stream().filter(a -> a.annotationType().equals(Check.class)).findFirst().orElse(null);
            String permissionErrorMessagePath = null;
            String[] permissions = new String[] { };
            SubCommand.AccessFlag[] flags = new NoArgCommand.AccessFlag[] { };
            String[] ignoreTabCompleting = new String[] { };
            int maxArgs = Integer.MAX_VALUE;
            int minArgs = Integer.MIN_VALUE;

            if (checkAnnotation != null) {
                permissions = checkAnnotation.permissions();
                flags = checkAnnotation.flags();
            }
            else {
                Permissions permissionsAnnotation = (Permissions) Utils.find(classEntry.getAdditionalAnnotations(), a -> a.annotationType().equals(Permissions.class)).orElse(null);
                if (permissionsAnnotation != null) {
                    permissions = permissionsAnnotation.value();
                    permissionErrorMessagePath = permissionsAnnotation.errorMessagePath();
                }

                Flags flagsAnnotation = (Flags) Utils.find(classEntry.getAdditionalAnnotations(), a -> a.annotationType().equals(Flags.class)).orElse(null);
                if (flagsAnnotation != null) {
                    flags = flagsAnnotation.value();
                }
            }

            Args argsAnnotation = (Args) Utils.find(classEntry.getAdditionalAnnotations(), a -> a.annotationType().equals(Args.class)).orElse(null);
            if (argsAnnotation != null) {
                maxArgs = argsAnnotation.max();
                minArgs = argsAnnotation.min();
            }

            TabCompleteIgnore tabCompleteIgnore = (TabCompleteIgnore) Utils.find(classEntry.getAdditionalAnnotations(), a -> a.annotationType().equals(TabCompleteIgnore.class)).orElse(null);
            if (tabCompleteIgnore != null) {
                if (tabCompleteIgnore.value().length == 0) {
                    ignoreTabCompleting = subCommandAnnotation.commandIdentifiers();
                } else
                    ignoreTabCompleting = tabCompleteIgnore.value();
            }

            CommandReq commandReq = new CommandReq(
                    permissionErrorMessagePath,
                    permissions,
                    flags,
                    List.of(ignoreTabCompleting),
                    maxArgs,
                    minArgs
            );

            Invocation commandInstance;
            try {
                commandInstance = (Invocation) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Не удалось создать класс " + clazz.getName(), e);
            }

            LunaCompleter completer = null;
            if (commandInstance instanceof LunaCompleter) {
                completer = (LunaCompleter) commandInstance;
            }

            SubCommand subCommand = SubCommand.builder()
                    .plugin(lunaPlugin)
                    .appliedCommand(command)
                    .commandIdentifiers(subCommandAnnotation.commandIdentifiers())
                    .invocation(commandInstance)
                    .tabCompleter(completer)
                    .commandReq(commandReq)
                    .build();

            processor.registerSubCommand(subCommand);
        }

    }
}
