package rocks.blackblock.screenbuilder.widgets;

import net.minecraft.screen.NamedScreenHandlerFactory;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.BaseWidgetChangeEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetAddedListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.text.TextBuilder;

import java.util.UUID;

public abstract class Widget<T> implements BibLog.Argable {

    // The name of this widget
    protected String id = null;

    // The added listener
    protected WidgetAddedListener added_listener = null;

    // The default value
    protected T default_value = null;

    // Generic change listener
    protected BaseWidgetChangeEventListener on_change = null;

    // The ScreenBuilder this was added to
    protected ScreenBuilder screen_builder = null;

    /**
     * Set the widget's ID
     *
     * @since   0.1.3
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the widget's ID
     *
     * @since   0.1.3
     */
    public String getId() {

        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }

        return this.id;
    }

    /**
     * Set the default value
     *
     * @since   0.1.3
     */
    public void setDefaultValue(T default_value) {
        this.default_value = default_value;
    }

    /**
     * Get the ScreenBuilder instance this widget was added to
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public ScreenBuilder getScreenBuilder() {
        return this.screen_builder;
    }

    /**
     * Set the ScreenBuilder instance this widget was added to
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   screen_builder   The ScreenBuilder instance
     */
    public void setScreenBuilder(ScreenBuilder screen_builder) {
        this.screen_builder = screen_builder;
    }

    /**
     * Set the on-change listener
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void setOnChangeListener(BaseWidgetChangeEventListener on_change) {
        this.on_change = on_change;
    }

    /**
     * Listen for value updates
     *
     * @since   0.1.1
     */
    public abstract void addWithValue(TextBuilder builder, T value);

    /**
     * Prepare the slots
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void prepareSlots(ScreenBuilder builder) {}

    /**
     * Add the widget to the text builder
     *
     * @since   0.1.1
     */
    public void addToTextBuilder(TextBuilder builder) {

        TexturedScreenHandler handler = builder.getScreenHandler();

        if (handler != null) {
            NamedScreenHandlerFactory factory = handler.getOriginFactory();
            WidgetDataProvider provider = handler.getWidgetDataProvider();

            if (provider != null) {

                T value = provider.getWidgetValue(this);

                this.addWithValue(builder, value);

                if (this.added_listener != null) {
                    this.added_listener.onAdded(builder, null);
                }

                return;
            }
        }

        this.addWithDefaultValue(builder);

        if (this.added_listener != null) {
            this.added_listener.onAdded(builder, null);
        }
    }

    /**
     * Set the added listener
     *
     * @since   0.1.1
     */
    public void setAddedListener(WidgetAddedListener listener) {
        this.added_listener = listener;
    }

    /**
     * Add this with the default value
     *
     * @since   0.1.3
     */
    public void addWithDefaultValue(TextBuilder builder) {
        this.addWithValue(builder, this.default_value);
    }

    /**
     * Register this widget
     *
     * @since   0.2.1
     */
    public void register() {

    }

    /**
     * Create a BibLog.Arg representation
     *
     * @since 0.5.0
     */
    @Override
    public BibLog.Arg toBBLogArg() {
        var result = BibLog.createArg(this);
        result.add("id", this.id);
        this.appendToBibLogArg(result);
        return result;
    }

    /**
     * Append to the BibLog.Arg instance
     *
     * @since 0.5.0
     */
    abstract protected void appendToBibLogArg(@NotNull BibLog.Arg arg);

    /**
     * Return a string representation of this widget
     *
     * @since   0.5.0
     */
    @Override
    public String toString() {
        return this.toBBLogArg().toString();
    }
}
