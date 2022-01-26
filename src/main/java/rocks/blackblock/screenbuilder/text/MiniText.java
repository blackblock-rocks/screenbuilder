package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.LiteralText;

/**
 * Basically the same as LiteralText,
 * but will be serialized as a single string if possible
 *
 * @since   0.1.1
 */
public class MiniText extends LiteralText {
    public MiniText(String string) {
        super(string);
    }
}
