package siozy.dev.lunaspring.API.messageActions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import siozy.dev.lunaspring.API.util.exceptions.InvalidAnnotationPresentException;
import siozy.dev.lunaspring.API.util.utilities.AnnounceUtils;

@Getter @RequiredArgsConstructor
public abstract class AbsMessageAction<E extends CommandSender> implements IMessageAction<E> {
    private final String id;
    public AbsMessageAction() {
        MessageAction annot = this.getClass().getAnnotation(MessageAction.class);
        if (annot == null) throw new InvalidAnnotationPresentException(this.getClass(), MessageAction.class);
        this.id = annot.value();
    }
}
