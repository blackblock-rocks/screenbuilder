package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScreenHandler.class)
public interface ScreenHandlerAccessor {

    @Accessor
    int getQuickCraftStage();

    @Accessor
    ScreenHandlerSyncHandler getSyncHandler();
}
