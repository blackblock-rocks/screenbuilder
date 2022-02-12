package rocks.blackblock.screenbuilder.mixin;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import rocks.blackblock.chunker.Chunker;
import rocks.blackblock.chunker.chunk.Lump;
import rocks.blackblock.chunker.world.Plane;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.text.TextBuilder;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Unique
    private TexturedScreenHandler bbsbScreenHandler = null;

    @ModifyVariable(
            method = "openHandledScreen",
            at = @At("STORE")
    )
    private ScreenHandler captureScreenHandler(ScreenHandler value) {

        if (value instanceof TexturedScreenHandler texturedScreenHandler) {
            this.bbsbScreenHandler = texturedScreenHandler;
        } else {
            this.bbsbScreenHandler = null;
        }

        return value;
    }

    @Redirect(
            method = "openHandledScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/NamedScreenHandlerFactory;getDisplayName()Lnet/minecraft/text/Text;")
    )
    public Text getModifiedDisplayName(NamedScreenHandlerFactory factory) {

        Text title = factory.getDisplayName();

        if (this.bbsbScreenHandler != null) {
            TexturedScreenHandler texturedScreenHandler = this.bbsbScreenHandler;
            this.bbsbScreenHandler = null;

            TextBuilder textBuilder = texturedScreenHandler.getTextBuilder();

            return textBuilder.build();
        }

        return title;
    }

}
