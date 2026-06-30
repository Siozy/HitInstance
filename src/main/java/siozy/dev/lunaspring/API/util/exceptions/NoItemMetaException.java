package siozy.dev.lunaspring.API.util.exceptions;

import org.bukkit.inventory.ItemStack;

public class NoItemMetaException extends LunaException {
    public NoItemMetaException(ItemStack itemStack) {
        super(String.format("У ItemStack типа %s отсутствует ItemMeta!", itemStack.getType()));
    }
}
