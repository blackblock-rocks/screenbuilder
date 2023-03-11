package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

public class ImageWidget extends TextureWidget {

    public ImageWidget(WidgetTexture widget_texture) {
        super(widget_texture);
    }

    public ImageWidget(Identifier texture_identifier) {
        super(texture_identifier);
    }

    @Override
    public void addWithValue(TextBuilder builder, Object value) {
        this.widget_texture.addToBuilder(builder, this.x, this.y);
    }
}
