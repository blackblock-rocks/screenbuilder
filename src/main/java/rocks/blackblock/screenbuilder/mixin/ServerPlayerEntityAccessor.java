package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccessor {

    @Invoker("onScreenHandlerOpened")
    void invokeOnScreenHandlerOpened(ScreenHandler screenHandler);

}
