package rocks.blackblock.screenbuilder.polymc;

import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.LineHeightFontCollection;
import rocks.blackblock.screenbuilder.textures.GuiTexture;

/**
 * PolyMC entrypoint for registering resource pack assets
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 * @version  0.1.1
 */
public class ScreenBuilderPoly implements PolyMcEntrypoint {

    @Override
    public void registerPolys(PolyRegistry registry) {

    }

    /**
     * Called by PolyMC (or Polyvalent) when a new resource pack is being geenrated
     *
     * @param   pack
     *
     * @since   0.1.1
     */
    public void registerModSpecificResources(ResourcePackMaker pack) {

        // Make all the ScreenBuilders register their items
        // (When using the old-style item texture override)
        for (ScreenBuilder sb : ScreenBuilder.screen_builders) {
            sb.registerPoly(pack);
        }

        // The `bbsb:space` font is always required, it allows us to horizontally move text
        pack.copyAsset(BBSB.NAMESPACE, "font/space.json");

        // There is a new font texture which is used for printing lines of text above the start position
        pack.copyFolder(BBSB.NAMESPACE, "assets/bbsb/textures/font", "");

        // Make all the LineHeight fonts add themselves to the resource pack
        for (LineHeightFontCollection fc : LineHeightFontCollection.collection.values()) {
            fc.addToResourcePack(pack);
        }

        GuiTexture.addToResourcePack(pack);
    }
}