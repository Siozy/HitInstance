package siozy.dev.lunaspring.API.commands.processor;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public record CommandReq(String permissionMessagePath,
                         String[] permissions,
                         NoArgCommand.AccessFlag[] accessFlags,
                         List<String> tabCompleteIgnore,
                         int maxArgs,
                         int minArgs) {
    public CommandReq(String permissionMessagePath, String[] permissions, NoArgCommand.AccessFlag[] accessFlags, List<String> tabCompleteIgnore, int maxArgs, int minArgs) {
        this.permissions = permissions;
        this.accessFlags = accessFlags;
        this.tabCompleteIgnore = tabCompleteIgnore;
        this.maxArgs = maxArgs;
        this.minArgs = minArgs;
        this.permissionMessagePath = permissionMessagePath;
    }

    @Override
    public @NotNull String toString() {
        return "CommandReq{" +
                "permissions=" + Arrays.toString(permissions) +
                ", accessFlags=" + Arrays.toString(accessFlags) +
                ", tabCompleteIgnore=" + tabCompleteIgnore +
                ", maxArgs=" + maxArgs +
                ", minArgs=" + minArgs +
                '}';
    }
}
