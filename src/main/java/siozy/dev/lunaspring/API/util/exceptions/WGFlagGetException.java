package siozy.dev.lunaspring.API.util.exceptions;

public class WGFlagGetException extends LunaException {
    public WGFlagGetException(String exceptionText) {
        super("Произошла ошибка во время получения StateFlag (WorldGuard): " + exceptionText);
    }
}
