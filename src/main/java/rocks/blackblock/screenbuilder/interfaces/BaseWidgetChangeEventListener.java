package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.BaseInput;
import rocks.blackblock.screenbuilder.widgets.Widget;

@FunctionalInterface
public interface BaseWidgetChangeEventListener {
    @Nullable
    void onEvent(TexturedScreenHandler screen, Widget input);
}
