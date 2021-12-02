package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

@FunctionalInterface
public interface GuiListener {
    @Nullable
    void listenMethod(TexturedScreenHandler screen, SlotBuilder slot, ItemStack stack, int amount);
}