package rocks.blackblock.screenbuilder.textures;

public class TexturePlacement {

    public int x = 0;
    public int y = 0;
    public WidgetTexture texture = null;

    public TexturePlacement(WidgetTexture texture, int x, int y) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }
}
