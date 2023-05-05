package rocks.blackblock.screenbuilder.interfaces;

import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

@FunctionalInterface
public interface RenamedEventListener {
    @Nullable
    void onRenamed(TexturedScreenHandler current_screen, String value);
}
