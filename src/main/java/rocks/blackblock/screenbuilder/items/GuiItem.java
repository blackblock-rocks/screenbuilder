package rocks.blackblock.screenbuilder.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

public class GuiItem extends Item {
    public GuiItem() {
        super(new FabricItemSettings().maxCount(1));
    }

    public static Item get(String name) {
        return GuiUtils.getGuiItem("gui_" + name);
    }

    public static GuiItem create(String name) {
        return GuiUtils.createGuiItem("gui_" + name);
    }
}