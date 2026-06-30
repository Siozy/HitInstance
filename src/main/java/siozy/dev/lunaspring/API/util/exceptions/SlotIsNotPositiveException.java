package siozy.dev.lunaspring.API.util.exceptions;

public class SlotIsNotPositiveException extends LunaException {
    public SlotIsNotPositiveException() {
        super("Слот не может быть отрицательным");
    }
}
