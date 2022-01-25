package rocks.blackblock.screenbuilder.text;

import net.minecraft.text.*;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TextGroup {

    private final TextBuilder builder;
    private final TextGroup parent;

    private TextColor color = null;
    private Identifier font = null;
    private ClickEvent click_event = null;
    private HoverEvent hover_event = null;

    private String main_text = null;

    private List<TextGroup> children = new ArrayList<>();

    public TextGroup(TextBuilder builder) {
        this(builder, null);
    }

    public TextGroup(TextBuilder builder, TextGroup parent) {
        this.builder = builder;
        this.parent = parent;
    }

    /**
     * Does this group inherit the current color?
     *
     * @since   0.1.1
     */
    public boolean inheritsColor() {
        return this.color == null;
    }

    /**
     * Does this group inherit the current font?
     *
     * @since   0.1.1
     */
    public boolean inheritsFont() {
        return this.font == null;
    }

    /**
     * Get the current active color
     *
     * @since   0.1.1
     */
    public TextColor getColor() {

        if (this.color != null) {
            return this.color;
        }

        if (this.parent != null) {
            return this.parent.getColor();
        }

        return this.builder.getDefaultColor();
    }

    /**
     * Set the color this group should use
     *
     * @param   color
     *
     * @since   0.1.1
     */
    public TextGroup setColor(TextColor color) {

        TextColor active_color = this.getColor();

        if (active_color == null || !active_color.equals(color)) {
            this.color = color;
        }

        return this;
    }

    /**
     * Get the current active font
     *
     * @since   0.1.1
     */
    public Identifier getFont() {

        if (this.font != null) {
            return this.font;
        }

        if (this.parent != null) {
            return this.parent.getFont();
        }

        return this.builder.getDefaultFont();
    }

    /**
     * Set the current active font
     *
     * @since   0.1.1
     */
    public TextGroup setFont(Identifier font) {

       Identifier active_font = this.getFont();

       if (active_font == null || !active_font.equals(font)) {
           this.font = font;
       }

       return this;
    }

    /**
     * Set the style of the current group.
     * Warning: this will switch all the previous entries too
     *
     * @param   style
     *
     * @since   0.1.1
     */
    public TextGroup setStyle(Style style) {

        TextColor new_color = style.getColor();

        if (new_color != null) {
            this.setColor(new_color);
        }

        Identifier new_font = style.getFont();

        if (new_font != null) {
            this.setFont(new_font);
        }

        return this;
    }

    /**
     * Does the given style change anything?
     *
     * @since   0.1.1
     */
    public boolean usesStyle(Style style) {

        TextColor current_color = this.getColor();
        TextColor new_color = style.getColor();

        if (new_color != null && !new_color.equals(current_color)) {
            return false;
        }

        Identifier current_font = this.getFont();
        Identifier new_font = style.getFont();

        if (new_font != null && !new_font.equals(current_font)) {
            return false;
        }

        return true;
    }

    /**
     * Force-start a new group
     *
     * @since   0.1.1
     */
    public TextGroup createChild() {
        return this.createChild(null);
    }

    /**
     * Force-start a new group and give it the specified style
     *
     * @since   0.1.1
     */
    public TextGroup createChild(Style style) {

        TextGroup child = new TextGroup(this.builder, this);
        this.children.add(child);

        return child;
    }

    /**
     * Create the style for this group
     *
     * @since   0.1.1
     */
    public Style buildStyle() {

        Style style = Style.EMPTY;

        if (this.color != null) {
            style = style.withColor(this.color);
        }

        if (this.font != null) {
            style = style.withFont(this.font);
        }

        if (style == Style.EMPTY) {
            return null;
        }

        return style;
    }

    /**
     * Is this group empty?
     *
     * @since   0.1.1
     */
    public boolean isEmpty() {

        if (this.main_text != null && this.main_text.length() > 0) {
            return false;
        }

        if (this.children.isEmpty()) {
            return true;
        }

        return !this.hasChildren();
    }

    /**
     * Does this group have children?
     *
     * @since   0.1.1
     */
    public boolean hasChildren() {

        // If there are no child groups, there are no children
        if (this.children.isEmpty()) {
            return false;
        }

        // If there are child groups, and they are NOT empty, there are children
        for (TextGroup child : this.children) {
            if (!child.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Compile into an existing Text
     *
     * @since   0.1.1
     */
    public void buildInto(LiteralText parent_text) {

        if ((this.main_text == null || this.main_text.isEmpty()) && this.children.isEmpty()) {
            return;
        }

        Style style = this.buildStyle();
        LiteralText text = parent_text;

        if (style == null) {
            if (this.main_text != null && !this.main_text.isEmpty()) {
                text.append(this.main_text);
            }
        } else {
            String main_text = this.main_text;

            if (main_text == null) {
                main_text = "";
            }

            text = new LiteralText(main_text);
            parent_text.append(text);
        }

        if (this.hasChildren()) {
            for (TextGroup child : this.children) {
                child.buildInto(text);
            }
        }
    }

    /**
     * Compile to a new Text
     *
     * @since   0.1.1
     */
    public Text build() {

        String main_text = this.main_text;

        if (main_text == null) {
            main_text = "";
        }

        LiteralText text = new LiteralText(main_text);

        if (this.hasChildren()) {
            for (TextGroup child : this.children) {
                child.buildInto(text);
            }
        }

        Style style = this.buildStyle();
        Text result;

        if (style != null) {
            result = text.getWithStyle(style).get(0);
        } else {
            result = text;
        }

        return result;
    }

    /**
     * Make sure we're on a group with the given style
     *
     * @since   0.1.1
     */
    public TextGroup ensureGroup(Style style) {

        if (style == null) {
            return this;
        }

        if (this.usesStyle(style)) {
            return this;
        }

        TextGroup result = this.createChild(style);

        return result;
    }

    /**
     * Add a string and return the group it was added to
     * (This can be the current group or a new child)
     *
     * @since   0.1.1
     */
    public TextGroup append(String str) {

        if (str == null) {
            return this;
        }

        if (this.hasChildren()) {
            return this.createChild().append(str);
        }

        if (this.main_text == null) {
            this.main_text = str;
        } else {
            this.main_text += str;
        }

        return this;
    }

}
