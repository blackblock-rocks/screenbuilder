package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.widgets.TextWidget;

/**
 * The base Booklet input:
 * a GUI styled to look like a book.
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.3
 * @version 0.1.3
 */
public class BookletInput extends EmptyInput {

    protected TextWidget text = new TextWidget();

    /**
     * Construct the BookletInput
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public BookletInput() {
        super();

        // The X coordinate of the main text widget
        this.text.setX(25);

        // The Y coordinate of the top of the main text widget
        this.text.setY(14);

        // The maximum width of the text widget
        this.text.setWidth(333);

        // The default text color
        this.text.setColor(TextColor.fromRgb(0x3f3f3f));

        // Make sure the title is not shown
        this.setDisplayName("");
    }

    /**
     * Create a new ScreenBuilder base:
     * this automatically sets the namespace & gui image
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public ScreenBuilder createBasicScreenBuilder(String name) {
        ScreenBuilder sb = new ScreenBuilder(name);
        sb.setCloneSlots(false);
        sb.setTitle("");

        sb.setNamespace(BBSB.NAMESPACE);
        sb.setFontTexture(BBSB.BOOK_V4);
        return sb;
    }

    /**
     * Clear everything
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public void clearAll() {
        this.clearText();
    }

    /**
     * Clear all the text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public void clearText() {
        this.text.clear();
    }

    /**
     * Print text and a new line
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printLine(String line) {
        this.text.printLine(line);
    }

    /**
     * Add a line of text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printLine(MiniText text) {
        this.text.printLine(text);
    }

    /**
     * Print text
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void printText(MiniText text) {
        this.text.printText(text);
    }

    /**
     * Get a new screenbuilder to actually send to the player
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        BookletInput self = this;

        ScreenBuilder sb = this.createBasicScreenBuilder("booklet");

        // Add the intro text widget
        sb.addWidget(this.text);

        return sb;
    }
}
