package rocks.blackblock.screenbuilder.settings;

import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.values.NumberValue;
import rocks.blackblock.screenbuilder.values.SettingValue;

public class NumberSetting extends Setting {

    public NumberSetting(String name) {
        super(name);
        this.setIcon(BBSB.GUI_NUMBER);
    }

    @Override
    public SettingValue createValue() {
        return new SettingValue(this, new NumberValue());
    }
}
