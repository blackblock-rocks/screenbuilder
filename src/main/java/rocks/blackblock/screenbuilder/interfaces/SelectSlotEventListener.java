package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.SelectSlot;

@FunctionalInterface
public interface SelectSlotEventListener {
    @Nullable
    void onSelect(TexturedScreenHandler screen, SelectSlot slot, ItemStack stack);
}