package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.util.Identifier;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.text.TextBuilder;

/**
 * A widget that doesn't really add anything
 */
public class DummyWidget extends TextureWidget {


    /**
     * Create the widget
     *
     * @since 0.1.1
     */
    public DummyWidget() {
        super((Identifier) null);
    }

    @Override
    public void addWithValue(TextBuilder builder, Object value) {
        return;
    }
}
