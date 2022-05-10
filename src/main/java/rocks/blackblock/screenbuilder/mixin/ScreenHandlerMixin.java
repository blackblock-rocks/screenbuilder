package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Redirect(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;canInsertItemIntoSlot(Lnet/minecraft/screen/slot/Slot;Lnet/minecraft/item/ItemStack;Z)Z"))
    private boolean modifyCanInsertItemIntoSlot(Slot slot, ItemStack stack, boolean allowOverflow) {

        Object self = this;

        if (slot instanceof SlotBuilder sb && self instanceof TexturedScreenHandler tsh) {
            if (!sb.checkStackInputAccess(stack)) {
                return false;
            }
        }

        return ScreenHandler.canInsertItemIntoSlot(slot, stack, allowOverflow);
    }

}
