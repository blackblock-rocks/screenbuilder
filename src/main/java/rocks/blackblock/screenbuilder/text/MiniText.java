package rocks.blackblock.screenbuilder.text;

import com.google.common.collect.Lists;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

/**
 * Basically the same as LiteralText,
 * but will be serialized as a single string if possible
 *
 * @since   0.1.1
 */
public class MiniText extends MutableText {

    protected boolean can_be_primitive = false;

    public MiniText(String string) {
        super(new LiteralTextContent(string), Lists.newArrayList(), Style.EMPTY);
    }

    /**
     * Should this text be serialized as a single string?
     *
     * @param   can_be_primitive
     *
     * @since   0.1.1
     */
    public void setCanBePrimitive(boolean can_be_primitive) {
        this.can_be_primitive = can_be_primitive;
    }

    /**
     * Can this text be serialized as a single string?
     *
     * @since   0.1.1
     */
    public boolean getCanBePrimitive() {
        return this.can_be_primitive;
    }

    /**
     * Append a Text instance to this text and return *that* text
     *
     * @param   text
     *
     * @since   0.1.1
     */
    public MutableText append(Text text) {
        MutableText mutable_text = shallowClone(text);
        super.append(mutable_text);

        this.can_be_primitive = false;

        return mutable_text;
    }

    /**
     * Append a string to this text and return *that* text
     *
     * @param   string
     *
     * @since   0.1.1
     */
    public MiniText append(String string) {

        MiniText text = new MiniText(string);
        super.append(text);

        this.can_be_primitive = false;

        return text;
    }

    /**
     * Get the raw string
     *
     * @since   0.2.0
     */
    public String getRawString() {
        return this.getString();
    }

    /**
     * Create a shallow clone
     *
     * @since   0.2.0
     */
    public static MutableText shallowClone(Text text) {

        MutableText result = MutableText.of(text.getContent());

        for (var sibling : text.getSiblings()) {
            result.append(sibling);
        }

        result.setStyle(text.getStyle());

        return result;
    }

}
