package rocks.blackblock.screenbuilder.polymc;

import io.github.theepicblock.polymc.api.resource.ResourcePackMaker;
import rocks.blackblock.screenbuilder.ScreenBuilder;

public class ScreenBuilderPoly {
    public void registerModSpecificResources(ResourcePackMaker pack) {
        for (ScreenBuilder sb : ScreenBuilder.screen_builders) {
            sb.registerPoly(pack);
        }
    }
}
