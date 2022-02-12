package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.chunker.chunk.Lump;
import rocks.blackblock.screenbuilder.text.TextBuilder;

@FunctionalInterface
public interface MapWidgetAddedListener {
    @Nullable
    void onAdded(TextBuilder builder, Lump lump, Slot slot);
}
