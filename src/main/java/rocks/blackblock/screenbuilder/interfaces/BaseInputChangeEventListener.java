package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.BaseInput;

@FunctionalInterface
public interface BaseInputChangeEventListener {
    @Nullable
    void onEvent(TexturedScreenHandler screen, BaseInput input);
}
