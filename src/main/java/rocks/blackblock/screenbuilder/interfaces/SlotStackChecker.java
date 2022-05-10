package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

@FunctionalInterface
public interface SlotStackChecker {
    @Nullable
    Boolean checkAccess(TexturedScreenHandler handler, SlotBuilder slot, ItemStack stack);
}