package siozy.dev.lunaspring.API.util.exceptions;

import siozy.dev.lunaspring.LunaSpring;

public abstract class LunaException extends RuntimeException {
    public LunaException(String message) {
        super(String.format("LunaSpring v%s exception: %s", LunaSpring.getInstance().getVersion(), message));
    }
}
