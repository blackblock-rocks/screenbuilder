package rocks.blackblock.screenbuilder.settings;

import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.values.BooleanValue;
import rocks.blackblock.screenbuilder.values.SettingValue;

public class BooleanSetting extends Setting {

    public BooleanSetting(String name) {
        super(name);
        this.setIcon(BBSB.GUI_BOOLEAN);
    }

    /**
     * Create the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     * @version  0.1.1
     */
    @Override
    public SettingValue createValue() {
        return new SettingValue(this, new BooleanValue());
    }
}
