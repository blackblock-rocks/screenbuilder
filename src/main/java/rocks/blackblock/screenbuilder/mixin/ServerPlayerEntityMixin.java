package rocks.blackblock.screenbuilder.mixin;

import com.mojang.datafixers.kinds.IdF;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
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

            //TextBuilder textBuilder = new TextBuilder();
            TextBuilder textBuilder = texturedScreenHandler.getTextBuilder();

            /*
            textBuilder.setY(-5);
            textBuilder.addLine("Line -5!");
            textBuilder.addLine("Line -4!");
            textBuilder.addLine("Line -3!");
            textBuilder.addLine("Line -2!");
            textBuilder.addLine("Line -1!");

             */
            textBuilder.addLine("Line 0!");
            textBuilder.addLine("Line 1!");
            textBuilder.addLine("Line 2!");
            textBuilder.addLine("Line 3!");
            textBuilder.addLine("Line 4!");
            textBuilder.addLine("Line 5!");
            textBuilder.addLine("Line 6!");

            System.out.println("TextBuilder: " + textBuilder.toString());

            return textBuilder.build();

            /*LiteralText space = new LiteralText("-112345678");
            space.setStyle(space.getStyle().withFont(Identifier.tryParse("bbsb:space")));

            LiteralText text = new LiteralText("Line 1");
            text.setStyle(text.getStyle().withFont(new Identifier("bbsb", "l-1")));

            LiteralText text2 = new LiteralText("Line 2");
            text2.setStyle(text.getStyle().withFont(new Identifier("bbsb", "l-2")));

            MutableText mutableText = new LiteralText("");

            mutableText.append(space.shallowCopy().append(text));
            mutableText.append(space.shallowCopy().append(text2));
            mutableText.append(space.shallowCopy().append(title));*/

            //return mutableText;

            //text.append(text2);

            //return text.append(title);
        }

        return title;
    }

}
