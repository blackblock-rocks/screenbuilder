package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.blackblock.screenbuilder.inventories.ItemInventory;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onOnTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        ItemInventory.checkItemInventories(player, stack);
    }
}
