package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.BaseSlot;

@FunctionalInterface
public interface SlotEventListener {
    @Nullable
    void onEvent(TexturedScreenHandler screen, BaseSlot slot);
}
