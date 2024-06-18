package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.network.packet.c2s.play.RenameItemC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rocks.blackblock.bib.util.BibText;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {

    @Shadow
    public ServerPlayerEntity player;

    @Inject(at = @At("HEAD"), method="onRenameItem", cancellable = true)
    public void onRenameItem(RenameItemC2SPacket packet, CallbackInfo ci) {
        if (this.player.currentScreenHandler instanceof TexturedScreenHandler handler) {
            String string = BibText.stripInvalidChars(packet.getName());
            handler.onRenameItem(string);
            ci.cancel();
        }
    }
}