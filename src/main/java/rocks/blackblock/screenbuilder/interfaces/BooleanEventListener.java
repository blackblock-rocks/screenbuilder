package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

@FunctionalInterface
public interface BooleanEventListener {
    @Nullable
    void onChange(TexturedScreenHandler screen, Boolean new_value);
}