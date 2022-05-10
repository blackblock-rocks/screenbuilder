package rocks.blackblock.screenbuilder.widgets;

import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

public class ImageWidget extends TextureWidget {

    public ImageWidget(WidgetTexture widget_texture) {
        super(widget_texture);
    }

    @Override
    public void addWithValue(TextBuilder builder, Object value) {
        this.widget_texture.addToBuilder(builder, this.x, this.y);
    }
}
