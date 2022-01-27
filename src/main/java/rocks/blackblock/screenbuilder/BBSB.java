package rocks.blackblock.screenbuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.util.Identifier;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.PixelFontCollection;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.util.Optional;

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
        System.out.println("!!! BBSB has loaded !!!");
        System.out.println(" -- LH04: " + Font.LH04);
        System.out.println(" -- PIXEL: " + PixelFontCollection.PX01);
    }
}
