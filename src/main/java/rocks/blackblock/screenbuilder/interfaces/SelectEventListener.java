package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

@FunctionalInterface
public interface SelectEventListener {
    @Nullable
    void onSelect(TexturedScreenHandler screen, ItemStack stack);
}
