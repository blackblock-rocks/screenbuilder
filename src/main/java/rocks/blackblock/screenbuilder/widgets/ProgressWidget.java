package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

/**
 * Show some kind of progress bar
 *
 * @since   0.1.1
 */
public class ProgressWidget extends TextureWidget<Integer> {

    // The max value
    public final static int MAX_VALUE = 100;

    // The max amount to use
    protected int max_amount = 100;

    public ProgressWidget(WidgetTexture widget_texture) {
        super(widget_texture);
    }

    public ProgressWidget(Identifier texture_identifier) {
        super(texture_identifier);
    }

    /**
     * Get the minimum amount of texture pieces
     *
     * @since   0.1.1
     */
    public int getWantedAmountOfTexturePieces() {
        return this.max_amount;
    }

    /**
     * Add this widget to the textbuilder with the given value
     *
     * @param   builder
     * @param   value
     *
     * @since   0.1.1
     */
    @Override
    public void addWithValue(TextBuilder builder, Integer value) {

        if (value instanceof Integer int_value) {
            float percentage = this.calculatePercentageValue(int_value);
            int total_piece_count = this.widget_texture.getAmountOfPieces();
            int wanted_piece_count = Math.round(((percentage / 100) * total_piece_count));

            this.widget_texture.addToBuilder(builder, this.x, this.y, wanted_piece_count);
        } else {
            this.widget_texture.addToBuilder(builder, this.x, this.y);
        }
    }

    /**
     * Get the current percentage value
     *
     * @since   0.1.1
     */
    public float calculatePercentageValue(int value) {
        return (((float) value / (float) this.max_amount) * 100);
    }

    /**
     * Set the max amount
     *
     * @since   0.1.1
     */
    public void setMaxAmount(int max_amount) {
        this.max_amount = max_amount;
    }

    /**
     * Register this widget
     *
     * @since   0.2.1
     */
    public void register() {

        if (this.screen_builder == null) {
            return;
        }

        int container_y = this.screen_builder.getContainerY(this.y);
        int title_y = this.screen_builder.convertToUnderlyingTitleY(this.y);

        // This is the correct registration
        this.widget_texture.registerYOffset(title_y);

        // This one isn't, but for some reason it always does it
        this.widget_texture.registerYOffset(this.y);
    }

    /**
     * Append to a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public void appendToBibLogArg(@NotNull BibLog.Arg arg) {
        super.appendToBibLogArg(arg);
        arg.add("max_amount", this.max_amount);
    }
}
