package rocks.blackblock.screenbuilder.textures;

import net.minecraft.util.Identifier;

/**
 * The Texture class used for icons
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.2.1
 * @version  0.3.0
 */
public class IconTexture extends WidgetTexture {
    public IconTexture(Identifier texture_identifier) {
        super(texture_identifier, 1);
    }

    public IconTexture(Identifier texture_identifier, double scale) {
        this(texture_identifier);
        this.setScale(scale);
    }
}
