package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.BaseInputChangeEventListener;
import rocks.blackblock.screenbuilder.slots.CheckboxWidgetSlot;
import rocks.blackblock.screenbuilder.slots.MirrorWidgetSlot;

public class ItemInput extends BaseInput {

    // The registered GUI
    public static ScreenBuilder GUI;

    // What should happen on a change?
    protected ChangeBehaviour change_behaviour = ChangeBehaviour.DO_NOTHING;

    // A listener for any change
    protected BaseInputChangeEventListener on_any_change_listener = null;

    // The current item
    protected ItemStack item_stack = null;

    protected Boolean check_damage = null;
    protected Boolean check_name = null;
    protected Boolean check_nbt = null;

    /**
     * Get the value (the item stack only)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack getValue() {
        return this.item_stack;
    }

    /**
     * Set the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setValue(ItemStack stack) {
        this.item_stack = stack;
    }

    /**
     * Sett he any-change event listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setAnyChangeListener(BaseInputChangeEventListener listener) {
        this.on_any_change_listener = listener;
    }

    /**
     * Get the checkbox values
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public NbtCompound getBooleanValues() {

        NbtCompound result = new NbtCompound();

        if (this.check_damage != null) {
            result.putBoolean("damage", this.check_damage);
        }

        if (this.check_name != null) {
            result.putBoolean("name", this.check_name);
        }

        if (this.check_nbt != null) {
            result.putBoolean("nbt", this.check_nbt);
        }

        return result;
    }

    /**
     * Set the checkbox values
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setBooleanValues(NbtCompound data) {

        this.check_damage = null;
        this.check_name = null;
        this.check_nbt = null;

        if (data == null) {
            return;
        }

        if (data.contains("damage")) {
            this.check_damage = data.getBoolean("damage");
        }

        if (data.contains("name")) {
            this.check_name = data.getBoolean("name");
        }

        if (data.contains("nbt")) {
            this.check_nbt = data.getBoolean("nbt");
        }
    }

    protected void emitAnyChange(TexturedScreenHandler screen) {
        if (this.on_any_change_listener == null) {
            return;
        }

        this.on_any_change_listener.onEvent(screen, this);
    }

    protected void emitStackChange(TexturedScreenHandler screen) {

        // @TODO: add stack listener?

        this.emitAnyChange(screen);
    }

    protected void emitBooleanChange(TexturedScreenHandler screen) {

        // @TODO: add boolean listener?

        this.emitAnyChange(screen);
    }

    @Override
    public ScreenBuilder getScreenBuilder() {

        ItemInput that = this;

        ScreenBuilder sb = this.createBasicScreenBuilder("item_input");

        MirrorWidgetSlot mirror = new MirrorWidgetSlot();
        sb.setSlot(4, mirror);

        ItemStack current_stack = this.getValue();

        if (current_stack != null) {
            mirror.setStack(current_stack);
        }

        mirror.setChangeListener((screen, stack) -> {
            that.item_stack = stack;
            that.emitStackChange(screen);
        });

        CheckboxWidgetSlot check_damage = new CheckboxWidgetSlot();
        check_damage.setTitle("Compare damage values");
        check_damage.setValue(this.check_damage);
        sb.setSlot(19, check_damage);

        check_damage.setChangeListener((screen, new_value) -> {
            that.check_damage = new_value;
            that.emitBooleanChange(screen);
        });

        CheckboxWidgetSlot check_name = new CheckboxWidgetSlot();
        check_name.setTitle("Compare item names");
        check_name.setValue(this.check_name);
        sb.setSlot(21, check_name);

        check_name.setChangeListener((screen, new_value) -> {
            that.check_name = new_value;
            that.emitBooleanChange(screen);
        });

        CheckboxWidgetSlot check_nbt = new CheckboxWidgetSlot();
        check_nbt.setTitle("Compare other NBT data");
        check_nbt.setValue(this.check_nbt);
        sb.setSlot(23, check_nbt);

        check_nbt.setChangeListener((screen, new_value) -> {
            that.check_nbt = new_value;
            that.emitBooleanChange(screen);
        });

        return sb;
    }

    /**
     * Register the GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static ScreenBuilder registerScreen() {

        if (GUI != null) {
            return GUI;
        }

        GUI = new ScreenBuilder("item_input");
        GUI.setNamespace("bbsb");
        GUI.useCustomTexture(true);

        GUI.register();

        return GUI;
    }
}