package rocks.blackblock.screenbuilder.polymc;

import io.github.theepicblock.polymc.api.PolyMcEntrypoint;
import io.github.theepicblock.polymc.api.PolyRegistry;
import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import rocks.blackblock.screenbuilder.ScreenBuilder;

public class ScreenBuilderPoly implements PolyMcEntrypoint {
    @Override
    public void registerPolys(PolyRegistry registry) {

    }

    public void registerModSpecificResources(ResourcePackMaker pack) {
        for (ScreenBuilder sb : ScreenBuilder.screen_builders) {
            sb.registerPoly(pack);
        }
    }
}
