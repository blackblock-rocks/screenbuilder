package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

@FunctionalInterface
public interface SlotWrapperListener {
    @Nullable
    ItemStack processStack(TexturedScreenHandler screen, SlotBuilder slot, ItemStack stack);
}