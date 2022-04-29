package rocks.blackblock.screenbuilder.settings;

import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.values.NumberValue;
import rocks.blackblock.screenbuilder.values.SettingValue;
import rocks.blackblock.screenbuilder.values.StringValue;

public class StringSetting extends Setting {

    public StringSetting(String name) {
        super(name);
        this.setIcon(BBSB.GUI_TEXT);
    }

    @Override
    public SettingValue createValue() {
        return new SettingValue(this, new StringValue());
    }
}
