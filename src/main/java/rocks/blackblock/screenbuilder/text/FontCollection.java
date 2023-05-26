package rocks.blackblock.screenbuilder.text;

import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of fonts
 *
 * @since   0.3.1
 */
public abstract class FontCollection {

    public static final List<FontCollection> ALL_COLLECTIONS = new ArrayList<>();

    // The original height of a character
    protected final int character_height;

    /**
     * Create the collection
     *
     * @param   character_height    The original height of a character
     *
     * @since   0.3.1
     */
    public FontCollection(int character_height) {
        this.character_height = character_height;
        ALL_COLLECTIONS.add(this);
    }

    /**
     * Return the perceived height of a character
     *
     * @since   0.3.1
     */
    public int getCharacterHeight() {
        return this.character_height;
    }

    /**
     * Generate the actual fonts
     *
     * @since   0.3.1
     */
    abstract protected void generateFonts();

    /**
     * Add all fonts to the given resource pack
     *
     * @param   pack   The (PolyMC) resource pack to add the fonts to
     *
     * @since   0.3.1
     */
    abstract public void addToResourcePack(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger);

}
