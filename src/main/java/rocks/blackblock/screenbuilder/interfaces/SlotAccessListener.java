package rocks.blackblock.screenbuilder.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.slots.SlotBuilder;

@FunctionalInterface
public interface SlotAccessListener {
    @Nullable
    boolean checkAccess(SlotBuilder slot, PlayerEntity player);
}