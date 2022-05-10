package rocks.blackblock.screenbuilder.inputs;

import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;

public class EmptyInput extends BaseInput {

    @Override
    public ScreenBuilder createBasicScreenBuilder(String name) {
        ScreenBuilder sb = new ScreenBuilder(name);
        sb.setNamespace(BBSB.NAMESPACE);
        sb.setFontTexture(BBSB.EMPTY_54);
        return sb;
    }

    @Override
    public ScreenBuilder getScreenBuilder() {
        ScreenBuilder sb = this.createBasicScreenBuilder("empty_screen");
        this.printErrors(sb);
        return sb;
    }
}
