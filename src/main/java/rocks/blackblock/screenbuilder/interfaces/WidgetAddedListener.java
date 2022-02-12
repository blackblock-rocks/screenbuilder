package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.text.TextBuilder;

@FunctionalInterface
public interface WidgetAddedListener {
    @Nullable
    void onAdded(TextBuilder builder, Object optional_value);
}