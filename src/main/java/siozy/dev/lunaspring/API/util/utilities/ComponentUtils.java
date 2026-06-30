package siozy.dev.lunaspring.API.util.utilities;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import siozy.dev.lunaspring.API.util.service.managers.ColorManager;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class ComponentUtils {
    private final Pattern url = Pattern.compile("^(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?$");

    public BaseComponent[] createClickableText(String line, ClickEvent.Action action) {
        String message = ColorManager.color(line);
        ComponentBuilder builder = new ComponentBuilder();
        String[] parts = message.split("\\*%\\*", -1);

        int mode = 0;
        String buttonText = null;
        for (String part : parts) {
            switch (mode) {
                case 0 -> {
                    builder.append(ComponentUtils.fromLegacyText(part));
                    mode = 1;
                }
                case 1 -> {
                    buttonText = part;
                    mode = 2;
                }
                case 2 -> {
                    if (buttonText != null) {
                        for (BaseComponent baseComponent : ComponentUtils.fromLegacyText(buttonText)) {
                            baseComponent.setClickEvent(new ClickEvent(action, part));
                            builder.append(baseComponent);
                        }
                    }
                    buttonText = null;
                    mode = 0;
                }
            }
        }

        return builder.create();
    }

    public BaseComponent[] createHoverableText(String line) {
        String message = ColorManager.color(line);
        ComponentBuilder builder = new ComponentBuilder();
        String[] parts = message.split("\\*%\\*", -1);

        int mode = 0;
        String buttonText = null;
        for (String part : parts) {
            switch (mode) {
                case 0 -> {
                    builder.append(ComponentUtils.fromLegacyText(part));
                    mode = 1;
                }
                case 1 -> {
                    buttonText = part;
                    mode = 2;
                }
                case 2 -> {
                    if (buttonText != null) {
                        for (BaseComponent baseComponent : ComponentUtils.fromLegacyText(buttonText)) {
                            baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(part)));
                            builder.append(baseComponent);
                        }
                    }
                    buttonText = null;
                    mode = 0;
                }
            }
        }
        return builder.create();
    }

    public BaseComponent[] fromLegacyText(String message) {
        return ComponentUtils.fromLegacyText(message, ChatColor.WHITE);
    }

    public BaseComponent[] fromLegacyText(String message, ChatColor defaultColor) {
        ArrayList<BaseComponent> components = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();

        Matcher matcher = url.matcher(message);
        for(int i = 0; i < message.length(); ++i) {
            char c = message.charAt(i);
            TextComponent old;
            if (c == 167) {
                ++i;
                if (i >= message.length()) {
                    break;
                }

                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c = (char)(c + 32);
                }

                ChatColor format;
                if (c == 'x' && i + 12 < message.length()) {
                    StringBuilder hex = new StringBuilder("#");

                    for(int j = 0; j < 6; ++j) {
                        hex.append(message.charAt(i + 2 + j * 2));
                    }

                    try {
                        format = ChatColor.of(hex.toString());
                    } catch (IllegalArgumentException var11) {
                        format = null;
                    }

                    i += 12;
                } else {
                    format = ChatColor.getByChar(c);
                }

                if (format != null) {
                    if (!builder.isEmpty()) {
                        old = component;
                        component = new TextComponent(component);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }

                    if (format == ChatColor.BOLD) {
                        component.setBold(true);
                    } else if (format == ChatColor.ITALIC) {
                        component.setItalic(true);
                    } else if (format == ChatColor.UNDERLINE) {
                        component.setUnderlined(true);
                    } else if (format == ChatColor.STRIKETHROUGH) {
                        component.setStrikethrough(true);
                    } else if (format == ChatColor.MAGIC) {
                        component.setObfuscated(true);
                    } else if (format == ChatColor.RESET) {
                        format = defaultColor;
                        component = new TextComponent();
                        component.setColor(format);
                        component.setBold(false);
                        component.setItalic(false);
                        component.setStrikethrough(false);
                        component.setObfuscated(false);
                        component.setUnderlined(false);
                    } else {
                        component = new TextComponent();
                        component.setColor(format);
                    }
                }
            } else {
                int pos = message.indexOf(32, i);
                if (pos == -1) {
                    pos = message.length();
                }

                if (matcher.region(i, pos).find()) {
                    if (!builder.isEmpty()) {
                        old = component;
                        component = new TextComponent(component);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }

                    old = component;
                    component = new TextComponent(component);
                    String urlString = message.substring(i, pos);
                    component.setText(urlString);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : "http://" + urlString));
                    components.add(component);
                    i += pos - i - 1;
                    component = old;
                } else {
                    builder.append(c);
                }
            }
        }

        component.setText(builder.toString());
        components.add(component);
        return components.toArray(new BaseComponent[0]);
    }
}
