package rocks.blackblock.screenbuilder.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.BaseInput;
import rocks.blackblock.screenbuilder.inputs.SelectInput;
import rocks.blackblock.screenbuilder.interfaces.OptionsGetterInterface;
import rocks.blackblock.screenbuilder.interfaces.SelectSlotEventListener;

import java.util.ArrayList;

public class SelectSlot extends ListenerWidgetSlot implements NamedScreenHandlerFactory {

    protected ArrayList<ItemStack> options = null;
    protected OptionsGetterInterface get_options = null;

    // A select listener
    protected SelectSlotEventListener on_select_listener = null;

    // What should happen on a change?
    protected BaseInput.ChangeBehaviour change_behaviour = BaseInput.ChangeBehaviour.SHOW_PREVIOUS_SCREEN;

    /**
     * SelectSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public SelectSlot() {
        super();
    }

    /**
     * SelectSlot constructor
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public SelectSlot(Inventory inventory, Integer index) {
        super(inventory, index);
    }

    /**
     * Set the select listener
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setSelectListener(SelectSlotEventListener listener) {
        this.on_select_listener = listener;
    }

    /**
     * Set what should happen when the user changes the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setChangeBehaviour(BaseInput.ChangeBehaviour behaviour) {
        this.change_behaviour = behaviour;
    }

    /**
     * Get what should happen when the user changes the value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public BaseInput.ChangeBehaviour getChangeBehaviour() {
        return this.change_behaviour;
    }

    /**
     * Add an option
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public SelectSlot addOption(ItemStack stack) {

        if (this.options == null) {
            this.options = new ArrayList<>();
        }

        this.options.add(stack);
        return this;
    }

    /**
     * Show the user the select screen when left-clicking
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void onLeftClick() {
        super.onLeftClick();
        this.showSelectScreen();
    }

    /**
     * Show the user the select screen when left-clicking
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void showSelectScreen() {
        this.active_handler.pushScreen(this);
        //this.active_handler.getPlayer().openHandledScreen(this);
    }

    /**
     * Show the previous screen (when in the Select screen!)
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void showPreviousScreen() {

        NamedScreenHandlerFactory factory = this.active_handler.getOriginFactory();

        if (factory == null) {
            // @TODO: close?
            return;
        }

        this.active_handler.pushScreen(factory);
    }

    @Override
    public Text getDisplayName() {
        return new LiteralText("Select an option ...");
    }

    /**
     * Get all the available options
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public ArrayList<ItemStack> getOptions() {

        if (this.options != null) {
            return this.options;
        }

        if (this.get_options != null) {
            return this.get_options.getOptions(this.active_handler, this);
        }

        return null;
    }

    /**
     * Set the method to get options
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    public void setOptionsGetter(OptionsGetterInterface getter) {
        this.get_options = getter;
    }

    /**
     * Create the menu for this slot
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @version 0.1.0
     * @since   0.1.0
     */
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {

        SelectInput select_screen = new SelectInput();
        select_screen.options = this.getOptions();
        select_screen.setChangeBehaviour(BaseInput.ChangeBehaviour.DO_NOTHING);

        SelectSlot that = this;

        select_screen.setSelectListener((screen, stack) -> {

            if (that.on_select_listener != null) {
                that.on_select_listener.onSelect(screen, that, stack);
            }

            that.setValue(stack.copy());

            BaseInput.ChangeBehaviour behaviour = that.getChangeBehaviour();

            if (behaviour == BaseInput.ChangeBehaviour.CLOSE_SCREEN) {
                screen.close();
            } else if (behaviour == BaseInput.ChangeBehaviour.SHOW_PREVIOUS_SCREEN) {
                that.showPreviousScreen();
            }
        });

        TexturedScreenHandler screen_handler = select_screen.createMenu(syncId, inv, player);

        return screen_handler;
    }

    /**
     * Set the value of this select slot
     * (Basically a fancy `setStack` method)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setValue(ItemStack stack) {

        Inventory inventory = this.getInventory();

        if (inventory == null) {
            return;
        }

        Integer inventory_index = this.getInventoryIndex();

        if (inventory_index == null || inventory_index < 0) {
            return;
        }

        inventory.setStack(inventory_index, stack);
    }

    /**
     * Get the value of this select slot
     * (Basically a fancy `getStack` method)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack getValue() {

        Inventory inventory = this.getInventory();

        if (inventory == null) {
            return null;
        }

        Integer inventory_index = this.getInventoryIndex();

        if (inventory_index == null || inventory_index < 0) {
            return null;
        }

        return inventory.getStack(inventory_index);
    }

    /**
     * Copy over properties to the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    slot   The target slot
     */
    public void copyPropertiesToSlot(SelectSlot slot) {

        super.copyPropertiesToSlot(slot);
        slot.get_options = this.get_options;
        slot.on_select_listener = this.on_select_listener;
        slot.change_behaviour = this.change_behaviour;

        if (this.options != null) {
            ArrayList<ItemStack> options = new ArrayList<>();

            for (ItemStack option : options) {
                options.add(option);
            }

            slot.options = options;
        }
    }

    /**
     * Clone this SelectSlot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    @Override
    public SelectSlot clone(Inventory inventory, Integer index) {

        SelectSlot clone;

        if (inventory == null) {
            clone = new SelectSlot();
        } else {
            clone = new SelectSlot(inventory, index);
        }

        this.copyPropertiesToSlot(clone);

        return clone;
    }

    /**
     * Create a ScreenBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static ScreenBuilder createScreenBuilder() {
        ScreenBuilder sb = new ScreenBuilder("select_widget");
        sb.setNamespace(BBSB.NAMESPACE);
        sb.useCustomTexture(true);
        sb.loadTextureItem();
        return sb;
    }

    /**
     * Register the GUI used for Selecting a value
     * (Actually a dummy ScreenBuilder, used for registering the custom texture)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static ScreenBuilder registerScreen() {
        ScreenBuilder sb = createScreenBuilder();
        sb.register();
        return sb;
    }
}