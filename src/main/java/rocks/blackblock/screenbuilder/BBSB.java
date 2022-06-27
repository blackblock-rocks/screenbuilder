package rocks.blackblock.screenbuilder;

import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.server.ScreenbuilderCommands;
import rocks.blackblock.screenbuilder.text.PixelFontCollection;
import rocks.blackblock.screenbuilder.textures.BaseTexture;
import rocks.blackblock.screenbuilder.textures.GuiTexture;
import rocks.blackblock.screenbuilder.textures.IconTexture;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

import static com.diogonunes.jcolor.Attribute.*;

public class BBSB implements ModInitializer {

    public static final String NAMESPACE = "bbsb";
    public static final Logger LOGGER = LogManager.getLogger(NAMESPACE);

    // Log colours & formats
    private static Attribute BLACK_BACK = BACK_COLOR(0, 0, 0);
    private static AnsiFormat BoldYellowOnRed = new AnsiFormat(YELLOW_TEXT(), RED_BACK(), BOLD());
    private static AnsiFormat BoldGrayOnBlack = new AnsiFormat(TEXT_COLOR(120, 120, 120), BLACK_BACK, BOLD());
    private static AnsiFormat CyanOnBlack = new AnsiFormat(CYAN_TEXT(), BLACK_BACK);
    private static AnsiFormat YellowText = new AnsiFormat(BRIGHT_YELLOW_TEXT());
    private static AnsiFormat RedText = new AnsiFormat(BRIGHT_RED_TEXT());
    private static AnsiFormat GreenText = new AnsiFormat(BRIGHT_GREEN_TEXT());
    private static AnsiFormat BlueText = new AnsiFormat(BRIGHT_BLUE_TEXT());

    // GUI items
    public static final GuiItem GUI_TRUE = GuiItem.create("true");
    public static final GuiItem GUI_FALSE = GuiItem.create("false");
    public static final GuiItem GUI_RECYCLE = GuiItem.create("recycle");
    public static final GuiItem GUI_EDIT = GuiItem.create("edit");
    public static final GuiItem GUI_PLUS = GuiItem.create("plus");
    public static final GuiItem GUI_NUMBER = GuiItem.create("number");
    public static final GuiItem GUI_TEXT = GuiItem.create("text");
    public static final GuiItem GUI_BOOLEAN = GuiItem.create("boolean");
    public static final GuiItem GUI_OBJECT_TYPE = GuiItem.create("object_type");
    public static final GuiItem GUI_UNKOWN_TYPE = GuiItem.create("unknown_type");
    public static final GuiItem GUI_ENTITY_TYPE = GuiItem.create("entity_type");
    public static final GuiItem GUI_CHECKBOX_UNCHECKED = GuiItem.create("checkbox_unchecked");
    public static final GuiItem GUI_CHECKBOX_CHECKED = GuiItem.create("checkbox_checked");
    public static final GuiItem GUI_IF = GuiItem.create("if");
    public static final GuiItem GUI_DO = GuiItem.create("do");
    public static final GuiItem GUI_EQ = GuiItem.create("eq");
    public static final GuiItem GUI_NEQ = GuiItem.create("neq");
    public static final GuiItem GUI_LT = GuiItem.create("lt");
    public static final GuiItem GUI_LTE = GuiItem.create("lte");
    public static final GuiItem GUI_GT = GuiItem.create("gt");
    public static final GuiItem GUI_GTE = GuiItem.create("gte");
    public static final GuiItem GUI_ARROW_LEFT = GuiItem.create("arrow_left");
    public static final GuiItem GUI_TRANSPARENT = GuiItem.create("transparent");
    // Semi transparency doesn't work: it blocks out underlying backgrounds
    //public static final GuiItem GUI_SEMI_TRANSPARENT = GuiItem.create("transparency_15");
    public static final WidgetTexture BUTTON_LARGE = new WidgetTexture(id("gui/button_l"));
    public static final WidgetTexture BUTTON_MEDIUM = new WidgetTexture(id("gui/button_m"));
    public static final WidgetTexture BUTTON_SMALL = new WidgetTexture(id("gui/button_s"));
    public static final WidgetTexture BUTTON_EXTRA_SMALL = new WidgetTexture(id("gui/button_xs"));
    public static final WidgetTexture WRENCH_SMALL = new WidgetTexture(id("gui/wrench_small"));
    public static final WidgetTexture LOCK_RED = new WidgetTexture(id("gui/lock_red"));
    public static final WidgetTexture UNLOCK_GREEN = new WidgetTexture(id("gui/unlock_green"));
    public static final WidgetTexture BLACK_FRAME = new WidgetTexture(id("gui/black_frame"), 4);
    public static final WidgetTexture BLACK_FRAME_3X = new WidgetTexture(id("gui/black_frame_3x"));
    public static final WidgetTexture BUTTON_ACCEPT = new WidgetTexture(id("gui/button_accept"));
    public static final WidgetTexture BUTTON_DENY = new WidgetTexture(id("gui/button_deny"));
    public static final WidgetTexture MIRROR_SLOT = new WidgetTexture(id("gui/mirror_slot"));
    public static final WidgetTexture ARROW_LEFT = new WidgetTexture(id("gui/arrow_left"));
    public static final WidgetTexture ARROW_RIGHT = new WidgetTexture(id("gui/arrow_right"));
    public static final WidgetTexture PAGER = new WidgetTexture(id("gui/pager"));
    public static final GuiTexture EMPTY_54 = new GuiTexture(id("gui/generic_54_empty"), 0, 0);
    public static final GuiTexture BOOK_V2 = new GuiTexture(id("gui/book_big_v02"), 17, 106);
    public static final GuiTexture BOOK_V3 = new GuiTexture(id("gui/book_big_v03"), 40, 106);
    public static final IconTexture ARROW_DOWN_ICON = new IconTexture(id("gui/icons/arrow_down"));
    public static final IconTexture COG_ICON = new IconTexture(id("gui/icons/cog"));
    public static final IconTexture PLUS_ICON = new IconTexture(id("gui/icons/plus"));
    public static final IconTexture DOWNLOAD_ICON = new IconTexture(id("gui/icons/download"));
    public static final IconTexture DIAMOND_ICON = new IconTexture(id("gui/icons/diamond"));
    public static final IconTexture WAREHOUSE_ICON = new IconTexture(id("gui/icons/warehouse"));
    public static final IconTexture TRASH_ICON = new IconTexture(id("gui/icons/trash"));
    public static final WidgetTexture SLOT_FRAME = new WidgetTexture(id("gui/slot_frame"));

    /**
     * Create an identifier
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public static Identifier id(String name) {
        return new Identifier(NAMESPACE, name);
    }

    /**
     * Get the BBSB prefix
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    1.1.2
     */
    private static String getPrefix() {
        return BoldGrayOnBlack.format("[") + CyanOnBlack.format("BBSB") + BoldGrayOnBlack.format("]") + " ";
    }

    /**
     * Output to the Blackblock logger
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    1.1.2
     *
     * @param    level    The log level
     * @param    message  The actual message
     */
    public static void log(Level level, Object message) {
        LOGGER.log(level, getPrefix() + message);
    }

    /**
     * Output to the Blackblock logger using the info level
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    1.1.2
     *
     * @param    message  The actual message
     */
    public static void log(Object message) {
        LOGGER.log(Level.INFO, getPrefix() + message);
    }

    /**
     * Output to the Blackblock logger using the info level
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    1.1.2
     *
     * @param    args  Multiple arguments
     */
    public static void log(Object... args) {

        StringBuilder builder = new StringBuilder();
        int i = 0;

        for (Object arg : args) {

            if (i > 0) {
                builder.append(" ");
            }

            String entry = String.valueOf(arg);

            if (arg instanceof Number) {
                entry = BlueText.format(entry);
            } else if (arg instanceof Boolean bool) {
                if (bool) {
                    entry = GreenText.format(entry);
                } else {
                    entry = RedText.format(entry);
                }
            } else if (arg instanceof String) {
                entry = YellowText.format(entry);
            }

            builder.append(entry);
            i++;
        }

        log(builder.toString());
    }

    /**
     * Output to the Blackblock logger by grabbing some attention
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    1.1.2
     *
     * @param    message  The actual message
     */
    public static void attention(Object message) {

        log("");
        log(BoldYellowOnRed.format("»»»»»»»»»» Attention ««««««««««"));
        log(Level.WARN, message);
        log(BoldYellowOnRed.format("==============================="));
        log("");
    }

    @Override
    public void onInitialize() {
        ScreenbuilderCommands.registerCommands();

        ScreenBuilder dummy = new ScreenBuilder("dummy");

        for (int row = 0; row < 6; row++) {
            int offset = 18 * row;
            BUTTON_LARGE.registerYOffset(dummy, 16 + offset);
            BUTTON_MEDIUM.registerYOffset(dummy, 17 + offset);

            // Aligned to the top
            BUTTON_SMALL.registerYOffset(dummy, 17 + offset);

            // Aligned to the bottom
            BUTTON_SMALL.registerYOffset(dummy, 18 + offset);

            BUTTON_EXTRA_SMALL.registerYOffset(dummy, 18 + offset);

            WRENCH_SMALL.registerYOffset(dummy, 17 + offset);
            LOCK_RED.registerYOffset(dummy, 17 + offset);
            UNLOCK_GREEN.registerYOffset(dummy, 17 + offset);

            BLACK_FRAME.registerYOffset(dummy, 17 + offset);
            BLACK_FRAME_3X.registerYOffset(dummy, 17 + offset);

            BUTTON_ACCEPT.registerYOffset(dummy, 17 + offset);
            BUTTON_DENY.registerYOffset(dummy, 17 + offset);
            MIRROR_SLOT.registerYOffset(dummy, 17 + offset);

            ARROW_LEFT.registerYOffset(dummy, 17 + 5 + offset);
            ARROW_RIGHT.registerYOffset(dummy, 17 + 5 + offset);
            PAGER.registerYOffset(dummy, 17 + 1 + offset);

            SLOT_FRAME.registerYOffset(dummy, 17 + offset);

            for (int i = 0; i < 4; i++) {
                ARROW_DOWN_ICON.registerYOffset(dummy, 17 + offset + i);
                COG_ICON.registerYOffset(dummy, 17 + offset + i);
                PLUS_ICON.registerYOffset(dummy, 17 + offset + i);
                DOWNLOAD_ICON.registerYOffset(dummy, 17 + offset + i);
                DIAMOND_ICON.registerYOffset(dummy, 17 + offset + i);
                WAREHOUSE_ICON.registerYOffset(dummy, 17 + offset + i);
                TRASH_ICON.registerYOffset(dummy, 17 + offset + i);
            }
        }

        EMPTY_54.registerYOffset(0);
        BOOK_V2.registerYOffset(0);
        BOOK_V3.registerYOffset(0);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // Make sure PX01 is loaded
            PixelFontCollection.PX01.getHeight();

            BaseTexture.calculateAll();
        });
    }
}
