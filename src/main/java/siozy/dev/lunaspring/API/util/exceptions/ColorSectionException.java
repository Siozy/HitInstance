package siozy.dev.lunaspring.API.util.exceptions;

public class ColorSectionException extends LunaException {
    public ColorSectionException() {
        super("Секция с цветами не найдена, нужная секция: colors");
    }
}
