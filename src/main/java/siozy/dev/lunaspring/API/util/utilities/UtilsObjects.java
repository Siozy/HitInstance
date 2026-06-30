package siozy.dev.lunaspring.API.util.utilities;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class UtilsObjects {
    public final Pattern PLACEHOLDER_BRACKET_PATTERN = Pattern.compile("\\{([^}]+)}");
    public final Pattern COMPARISON_PATTERN = Pattern.compile("([^<>=!]+)([<>=!]=?|!=|===?)([^<>=!]+)");
    public final Collection<Character> CHAR_COLORS_COLLECTION = Set.of('&', '§');
    public final int MAX_ATTEMPTS_ON_REPLACE_PLACEHOLDERS = 8;
    public final String DEFAULT_RANDOM_KEY_KIT = "qwertyuiopasdfghjklzxcvbnm1234567890";
    public final BiConsumer<Location, ItemStack> DROP_ITEM_CONSUMER = (l, i) -> {
        l.getWorld().dropItem(l, i);
    };
}
