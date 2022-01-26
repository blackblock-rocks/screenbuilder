package rocks.blackblock.screenbuilder.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rocks.blackblock.screenbuilder.text.MiniText;

import java.lang.reflect.Type;

@Mixin(Text.Serializer.class)
public class TextSerializerMixin {

    @Inject(method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;", at = @At("HEAD"), cancellable = true)
    public void modifySerialize(Text text, Type type, JsonSerializationContext jsonSerializationContext, CallbackInfoReturnable<JsonElement> cir) {

        if (text instanceof MiniText mini_text) {
            JsonPrimitive jsonPrimitive = new JsonPrimitive(mini_text.getRawString());
            cir.setReturnValue(jsonPrimitive);
        }
    }
}