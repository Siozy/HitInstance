package siozy.dev.lunaspring.API.util.exceptions;

public class NoProvidingPluginException extends LunaException {
    public NoProvidingPluginException(String plugin) {
        super(String.format("Для работы, сервис требует плагин %s, но он отсутствует!", plugin));
    }
}
