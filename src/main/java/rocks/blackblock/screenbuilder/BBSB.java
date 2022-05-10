package rocks.blackblock.screenbuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.server.ScreenbuilderCommands;
import rocks.blackblock.screenbuilder.text.PixelFontCollection;
import rocks.blackblock.screenbuilder.textures.BaseTexture;
import rocks.blackblock.screenbuilder.textures.GuiTexture;
import rocks.blackblock.screenbuilder.textures.WidgetTexture;

public class BBSB implements ModInitializer {

    public static final String NAMESPACE = "bbsb";

    // GUI items
    public static final GuiItem GUI_TRUE = GuiItem.create("true");
    public static final GuiItem GUI_FALSE = GuiItem.create("false");
    public static final GuiItem GUI_RECYCLE = GuiItem.create("recycle");
    public static final GuiItem GUI_EDIT = GuiItem.create("edit");
    public static final GuiItem GUI_PLUS = GuiItem.create("plus");
    public static final GuiItem GUI_NUMBER = GuiItem.create("number");
    public static final GuiItem GUI_TEXT = GuiItem.create("text");
    public static final GuiItem GUI_BOOLEAN = GuiItem.create("boolean");
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

    /**
     * Create an identifier
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public static Identifier id(String name) {
        return new Identifier(NAMESPACE, name);
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
        }

        EMPTY_54.registerYOffset(0);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // Make sure PX01 is loaded
            PixelFontCollection.PX01.getHeight();

            BaseTexture.calculateAll();
        });
    }
}
