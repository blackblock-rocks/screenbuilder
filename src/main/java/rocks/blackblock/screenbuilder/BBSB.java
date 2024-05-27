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

    public static boolean DEBUG = false;

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
    public static final WidgetTexture BUTTON_TAB_TOP_SELECTED = new WidgetTexture(id("gui/tab_top_selected"));
    public static final WidgetTexture BUTTON_TAB_TOP_UNSELECTED = new WidgetTexture(id("gui/tab_top_unselected"));
    public static final WidgetTexture BUTTON_TAB_LEFT_SELECTED = new WidgetTexture(id("gui/tab_left_selected"));
    public static final WidgetTexture BUTTON_TAB_LEFT_UNSELECTED = new WidgetTexture(id("gui/tab_left_unselected"));
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
    public static final GuiTexture TOP_FOUR = new GuiTexture(id("gui/generic_54_top_4"), 0, 0);
    public static final GuiTexture BOOK_V2 = new GuiTexture(id("gui/book_big_v02"), 17, 106);
    public static final GuiTexture BOOK_V3 = new GuiTexture(id("gui/book_big_v03"), 40, 106);
    public static final GuiTexture BOOK_V4 = new GuiTexture(id("gui/book_big_v04"), 104, 106);

    public static final IconTexture COG_ICON = new IconTexture(id("gui/icons/cog"));
    public static final IconTexture PLUS_ICON = new IconTexture(id("gui/icons/plus"));
    public static final IconTexture DOWNLOAD_ICON = new IconTexture(id("gui/icons/download"), 2);
    public static final IconTexture DIAMOND_ICON = new IconTexture(id("gui/icons/diamond"));
    public static final IconTexture WAREHOUSE_ICON = new IconTexture(id("gui/icons/warehouse"));
    public static final IconTexture TRASH_ICON = new IconTexture(id("gui/icons/trash"));

    public static final IconTexture SORT_ICON = new IconTexture(id("gui/icons/sort_icon"));
    public static final IconTexture SORT_ALPHABETICAL = new IconTexture(id("gui/icons/sort_alphabetical"));
    public static final IconTexture SORT_OWNER = new IconTexture(id("gui/icons/sort_owner"));
    public static final IconTexture SORT_MINED = new IconTexture(id("gui/icons/sort_mined"));
    public static final IconTexture SORT_BROKEN = new IconTexture(id("gui/icons/sort_broken"));
    public static final IconTexture SORT_CRAFTED = new IconTexture(id("gui/icons/sort_crafted"));
    public static final IconTexture SORT_USED = new IconTexture(id("gui/icons/sort_used"));
    public static final IconTexture SORT_PICKED_UP = new IconTexture(id("gui/icons/sort_picked_up"));
    public static final IconTexture SORT_DROPPED = new IconTexture(id("gui/icons/sort_dropped"));
    public static final IconTexture SORT_DESCENDING = new IconTexture(id("gui/icons/sort_descending"));
    public static final IconTexture SORT_ASCENDING = new IconTexture(id("gui/icons/sort_ascending"));

    public static final IconTexture ARROW_DOWN_ICON = new IconTexture(id("gui/icons/arrow_down"));
    public static final IconTexture ARROW_UP_ICON = new IconTexture(id("gui/icons/arrow_up"));
    public static final IconTexture ARROW_RIGHT_ICON = new IconTexture(id("gui/icons/arrow_right"));
    public static final IconTexture ARROW_LEFT_ICON = new IconTexture(id("gui/icons/arrow_left"));

    public static final IconTexture CIRCLE_ICON = new IconTexture(id("gui/icons/circle"), 2);
    public static final IconTexture CLOUD_ICON = new IconTexture(id("gui/icons/cloud"));
    public static final IconTexture EXCLAMATION_ICON = new IconTexture(id("gui/icons/exclamation"));
    public static final IconTexture HOME_ICON = new IconTexture(id("gui/icons/home"));
    public static final IconTexture CYCLE_ICON = new IconTexture(id("gui/icons/recycle"));
    public static final IconTexture REDO_ICON = new IconTexture(id("gui/icons/redo"));
    public static final IconTexture UNDO_ICON = new IconTexture(id("gui/icons/undo"));
    public static final IconTexture STRIKE_ICON = new IconTexture(id("gui/icons/strike_thin"), 2);
    public static final IconTexture STRIKE_THICK_ICON = new IconTexture(id("gui/icons/strike_thick"), 2);
    public static final IconTexture TRIANGLE_ICON = new IconTexture(id("gui/icons/triangle"));
    public static final IconTexture SUN_ICON = new IconTexture(id("gui/icons/sun"));
    public static final IconTexture MOON_ICON = new IconTexture(id("gui/icons/moon"));
    public static final IconTexture QUESTION_ICON = new IconTexture(id("gui/icons/question"));
    public static final IconTexture ASTERISK_ICON = new IconTexture(id("gui/icons/asterisk"));
    public static final IconTexture CUBE_ICON = new IconTexture(id("gui/icons/cube"));
    public static final IconTexture CUBE_SPECIAL_ICON = new IconTexture(id("gui/icons/cube_special"));
    public static final IconTexture CHEVRON_RIGHT_ICON = new IconTexture(id("gui/icons/chevron_right"));
    public static final IconTexture CHEVRON_LEFT_ICON = new IconTexture(id("gui/icons/chevron_left"));
    public static final IconTexture CHEVRON_UP_ICON = new IconTexture(id("gui/icons/chevron_up"));
    public static final IconTexture CHEVRON_DOWN_ICON = new IconTexture(id("gui/icons/chevron_down"));
    public static final IconTexture DOTTED_LINE_ICON = new IconTexture(id("gui/icons/dotted_line"));
    public static final IconTexture PENCIL_ICON = new IconTexture(id("gui/icons/pencil"), 2);
    public static final IconTexture FILE_ICON = new IconTexture(id("gui/icons/file"));
    public static final IconTexture FOLDER_ICON = new IconTexture(id("gui/icons/folder"));
    public static final IconTexture CHECK_ICON = new IconTexture(id("gui/icons/check"));
    public static final IconTexture CROSS_ICON = new IconTexture(id("gui/icons/cross"));
    public static final IconTexture CITY_ICON = new IconTexture(id("gui/icons/city"), 2);
    public static final IconTexture HEART_ICON = new IconTexture(id("gui/icons/heart"), 2);
    public static final IconTexture DRUMSTICK_ICON = new IconTexture(id("gui/icons/drumstick"));
    public static final IconTexture INGOT_ICON = new IconTexture(id("gui/icons/ingot"));
    public static final IconTexture WORLD_ICON = new IconTexture(id("gui/icons/world"), 2);
    public static final IconTexture PEOPLE_ICON = new IconTexture(id("gui/icons/people"), 2);
    public static final IconTexture STEVE_ICON = new IconTexture(id("gui/icons/steve"), 2);
    public static final IconTexture FLAG_ICON = new IconTexture(id("gui/icons/flag"), 2);
    public static final IconTexture MINI_FLAG_ICON = new IconTexture(id("gui/icons/mini_flag"));
    public static final IconTexture PERSON_ICON = new IconTexture(id("gui/icons/person"), 2);
    public static final IconTexture VILLAGER_ICON = new IconTexture(id("gui/icons/villager"), 2);
    public static final IconTexture TOPHAT_ICON = new IconTexture(id("gui/icons/tophat"));

    public static final WidgetTexture SLOT_FRAME = new WidgetTexture(id("gui/slot_frame"));

    static {
        BOOK_V4.setTextCoordinates(25, 14);
    }

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

        EMPTY_54.registerYOffset(0);
        TOP_FOUR.registerYOffset(0);
        BOOK_V2.registerYOffset(0);
        BOOK_V3.registerYOffset(0);
        BOOK_V4.registerYOffset(0);

        WidgetTexture.forEachRowOffset((dummy, row, row_offset, jitter) -> {

            BUTTON_LARGE.registerYOffset(dummy, 16 + row_offset);
            BUTTON_MEDIUM.registerYOffset(dummy, 17 + row_offset);

            // Aligned to the top
            BUTTON_SMALL.registerYOffset(dummy, 17 + row_offset);

            // Aligned to the bottom
            BUTTON_SMALL.registerYOffset(dummy, 18 + row_offset);

            BUTTON_EXTRA_SMALL.registerYOffset(dummy, 18 + row_offset);

            BUTTON_TAB_TOP_SELECTED.registerYOffset(dummy, 16 + row_offset);
            BUTTON_TAB_TOP_UNSELECTED.registerYOffset(dummy, 16 + row_offset);
            BUTTON_TAB_LEFT_SELECTED.registerYOffset(dummy, 16 + row_offset);
            BUTTON_TAB_LEFT_UNSELECTED.registerYOffset(dummy, 16 + row_offset);

            WRENCH_SMALL.registerYOffset(dummy, 17 + row_offset);
            LOCK_RED.registerYOffset(dummy, 17 + row_offset);
            UNLOCK_GREEN.registerYOffset(dummy, 17 + row_offset);

            BLACK_FRAME.registerYOffset(dummy, 17 + row_offset);
            BLACK_FRAME_3X.registerYOffset(dummy, 17 + row_offset);

            BUTTON_ACCEPT.registerYOffset(dummy, 17 + row_offset);
            BUTTON_DENY.registerYOffset(dummy, 17 + row_offset);
            MIRROR_SLOT.registerYOffset(dummy, 17 + row_offset);

            ARROW_LEFT.registerYOffset(dummy, 17 + 5 + row_offset);
            ARROW_RIGHT.registerYOffset(dummy, 17 + 5 + row_offset);
            PAGER.registerYOffset(dummy, 17 + 1 + row_offset);

            SLOT_FRAME.registerYOffset(dummy, 17 + row_offset);
        });

        IconTexture[] icons = {
                ARROW_DOWN_ICON,
                COG_ICON,
                PLUS_ICON,
                DOWNLOAD_ICON,
                DIAMOND_ICON,
                WAREHOUSE_ICON,
                TRASH_ICON,
                SORT_ICON,
                SORT_ALPHABETICAL,
                SORT_OWNER,
                SORT_MINED,
                SORT_BROKEN,
                SORT_CRAFTED,
                SORT_USED,
                SORT_PICKED_UP,
                SORT_DROPPED,
                SORT_DESCENDING,
                SORT_ASCENDING,
                ARROW_UP_ICON,
                ARROW_LEFT_ICON,
                ARROW_RIGHT_ICON,
                CIRCLE_ICON,
                CLOUD_ICON,
                EXCLAMATION_ICON,
                HOME_ICON,
                CYCLE_ICON,
                REDO_ICON,
                UNDO_ICON,
                STRIKE_ICON,
                STRIKE_THICK_ICON,
                TRIANGLE_ICON,
                SUN_ICON,
                MOON_ICON,
                QUESTION_ICON,
                ASTERISK_ICON,
                CUBE_ICON,
                CUBE_SPECIAL_ICON,
                CHEVRON_RIGHT_ICON,
                CHEVRON_LEFT_ICON,
                CHEVRON_UP_ICON,
                CHEVRON_DOWN_ICON,
                DOTTED_LINE_ICON,
                PENCIL_ICON,
                FILE_ICON,
                FOLDER_ICON,
                CHECK_ICON,
                CROSS_ICON,
                CITY_ICON,
                HEART_ICON,
                DRUMSTICK_ICON,
                INGOT_ICON,
                WORLD_ICON,
                FLAG_ICON,
                MINI_FLAG_ICON,
                PEOPLE_ICON,
                STEVE_ICON,
                PERSON_ICON,
                VILLAGER_ICON,
                TOPHAT_ICON
        };

        WidgetTexture.registerForAllSlots(icons);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {

            // Make sure PX01 is loaded
            PixelFontCollection.PX01.getCharacterHeight();

            BaseTexture.calculateAll();
        });
    }
}
