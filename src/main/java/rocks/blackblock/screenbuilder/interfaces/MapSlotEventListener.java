package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.chunker.chunk.Lump;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.ClickType;
import rocks.blackblock.screenbuilder.slots.ListenerWidgetSlot;

@FunctionalInterface
public interface MapSlotEventListener {
    @Nullable
    void onClick(TexturedScreenHandler screen, ListenerWidgetSlot slot, ClickType type, ItemStack stack, Lump lump);
}
