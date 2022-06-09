package rocks.blackblock.screenbuilder.polymc;

import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.text.LineHeightFont;
import rocks.blackblock.screenbuilder.text.LineHeightFontCollection;
import rocks.blackblock.screenbuilder.text.PixelFontCollection;
import rocks.blackblock.screenbuilder.textures.BaseTexture;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import javax.imageio.ImageIO;
import java.nio.file.Files;

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
     * Called by PolyMC (or Polyvalent) when a new resource pack is being generated
     *
     * @param   pack
     *
     * @since   0.1.1
     */
    public void registerModSpecificResources(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        // Make all the ScreenBuilders register their items
        // (When using the old-style item texture override)
        for (ScreenBuilder sb : ScreenBuilder.screen_builders) {
            sb.registerPoly(moddedResources, pack, logger);
        }

        // The `bbsb:space` font is always required, it allows us to horizontally move text
        copyFile(moddedResources, pack, BBSB.NAMESPACE, "font/space.json");

        // There are new font textures which is used for printing lines of text above the start position
        pack.setAsset(BBSB.NAMESPACE, "textures/font/asciix10_0.png", (location, gson) -> {
            GuiUtils.writeToPath(location, LineHeightFont.getFontImage(0));
        });

        pack.setAsset(BBSB.NAMESPACE, "textures/font/asciix10_1.png", (location, gson) -> {
            GuiUtils.writeToPath(location, LineHeightFont.getFontImage(1));
        });

        copyFile(moddedResources, pack, BBSB.NAMESPACE, "textures/font/pixel.png");
        copyFile(moddedResources, pack, BBSB.NAMESPACE, "textures/font/space_nosplit.png");
        copyFile(moddedResources, pack, BBSB.NAMESPACE, "textures/font/space_split.png");

        // Make all the LineHeight fonts add themselves to the resource pack
        for (LineHeightFontCollection fc : LineHeightFontCollection.getAllFontCollections()) {
            fc.addToResourcePack(moddedResources, pack, logger);
        }

        PixelFontCollection.PX01.addToResourcePack(moddedResources, pack, logger);

        BaseTexture.addToResourcePack(moddedResources, pack, logger);
    }

    /**
     * Copy an entire folder to the new resource pack
     *
     * @param   pack
     *
     * @since   0.1.1
     */
    public static void copyFile(ModdedResources moddedResources, PolyMcResourcePack pack, String namespace, String path) {
        pack.setAsset(namespace, path, (location, gson) -> {
            GuiUtils.writeToPath(location, moddedResources.getInputStream(namespace, path).readAllBytes());
        });
    }
}