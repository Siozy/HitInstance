package siozy.dev.lunaspring.API.util.exceptions;

public class SerializerException extends LunaException {
    public SerializerException(String message) {
        super(String.format("Произошло непредвиденное исключение при использовании сериализатора (%s)", message));
    }
}
