package rocks.blackblock.screenbuilder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.theepicblock.polymc.api.resource.ModdedResources;
import io.github.theepicblock.polymc.api.resource.PolyMcResourcePack;
import io.github.theepicblock.polymc.api.resource.json.JElement;
import io.github.theepicblock.polymc.api.resource.json.JGuiLight;
import io.github.theepicblock.polymc.api.resource.json.JModel;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import io.github.theepicblock.polymc.impl.resource.json.JModelImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.util.BibItem;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.screen.ErrorAreaInfo;
import rocks.blackblock.screenbuilder.screen.ScreenInfo;
import rocks.blackblock.screenbuilder.slots.*;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.text.TextGroup;
import rocks.blackblock.screenbuilder.textures.GuiTexture;
import rocks.blackblock.screenbuilder.unit.Unit;
import rocks.blackblock.screenbuilder.widgets.Widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Create a Screen
 *
 * @author   Jelle De Loecker   <jelle@elevenways.be>
 * @since    0.1.0
 * @version  0.1.0
 */
public class ScreenBuilder implements NamedScreenHandlerFactory {

    public static HashMap<String, Item> gui_items = new HashMap<>();

    public static final Slot AIR_SLOT = new StaticSlot(new ItemStack(Items.AIR));
    public static ArrayList<ScreenBuilder> screen_builders = new ArrayList<>();

    // The name of this gui
    private String name;

    // The optional name of this gui
    private String title;

    // The title text
    private Text title_text;

    // The namespace to use for the item
    private String namespace = null;

    // The dimensions of this screen (always 9x6)
    private int width = 9;
    private int height = 6;

    // The texture to use for this GUI (if any)
    private String texture_path = null;

    // The item to use for the GUI
    private Item gui_item = null;

    // The name of the gui item
    private String gui_item_name = null;

    // The identifier for the gui item
    private Identifier gui_item_identifier = null;

    // The slot coordinates to sacrifice for the GUI
    private Integer texture_slot_x;
    private Integer texture_slot_y;

    // The main slots of the screen
    // (excluding the player inventory and hotbar)
    protected DefaultedList<Slot> main_slots;

    // The player's own slots (inventory + hotbar)
    protected DefaultedList<Slot> player_slots;

    // Has this been registered?
    private boolean has_been_registered = false;

    // A function that should be called when something changes
    private Consumer<TexturedScreenHandler> call_on_change = null;

    // All non-slot widgets (font-based widgets)
    protected HashMap<String, Widget> widgets = new HashMap<>();

    // All units (composed widgets/slots)
    protected List<Unit> units = new ArrayList<>();

    // Should the slots be cloned?
    protected boolean clone_slots = true;

    // The type of screen to use on the client-side
    protected ScreenHandlerType<?> screen_type;

    // The info on the screen type
    protected ScreenInfo screen_info;

    // The font texture to use
    protected GuiTexture font_texture = null;

    // Should the player inventory be shown?
    protected boolean show_player_inventory = true;

    // Should the player hotbar be shown?
    protected boolean show_player_hotbar = true;

    // The Y coordinate where errors should be printed
    protected Integer show_error_y = -16;

    // Error messages
    protected List<String> error_messages = null;

    // Keep track of the slots that have been used
    protected Map<Integer, Boolean> used_slots = new HashMap<>();

    /**
     * Create a new ScreenBuilder with the 9x6 generic container
     *
     * @param   name
     *
     * @since   0.1.0
     */
    public ScreenBuilder(String name) {
        this(name, ScreenHandlerType.GENERIC_9X6);
    }

    /**
     * Create a new ScreenBuilder with the given screen_type
     *
     * @param   name
     * @param   screen_type
     *
     * @since   0.1.1
     */
    public ScreenBuilder(String name, ScreenHandlerType<?> screen_type) {
        this.name = name;
        this.setType(screen_type);
    }

    /**
     * Should the player inventory be shown?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public boolean getShowPlayerInventory() {
        return this.show_player_inventory;
    }

    /**
     * Should the player inventory be shown?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void setShowPlayerInventory(boolean show) {
        this.show_player_inventory = show;
    }

    /**
     * Should the player hotbar be shown?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public boolean getShowPlayerHotbar() {
        return this.show_player_hotbar;
    }

    /**
     * Should the player inventory be shown?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void setShowPlayerHotbar(boolean show) {
        this.show_player_hotbar = show;
    }

    /**
     * Get the amount of slots the underlying screen type has.
     * This does not include the player inventory or the hotbar.
     *
     * @since   0.1.1
     */
    public int getScreenTypeSlotCount() {
        return this.screen_info.getOwnSlotCount();
    }

    /**
     * Get the total amount of slots on the screen
     *
     * @since   0.3.1
     */
    public int getVisibleSlotCount() {
        return this.screen_info.getTotalSlotCount();
    }

    /**
     * Get the ScreenInfo for this screen type
     *
     * @since   0.1.1
     */
    public ScreenInfo getScreenInfo() {
        return this.screen_info;
    }

    /**
     * Set the custom texture for this screen for use with magic fonts
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    texture_path   The texture to use
     * @param    x              The x coordinate of where the original texture starts
     * @param    y              The y coordinate of where the original texture starts
     */
    public GuiTexture useFontTexture(Identifier texture_path, int x, int y) {

        // Make sure the item-based texture is disabled
        this.texture_path = null;

        // Get the GuiTexture instance to use
        this.font_texture = GuiTexture.get(texture_path, x, y).setScreenBuilder(this);

        // And return it
        return this.font_texture;
    }

    /**
     * Set the custom texture for this screen for use with magic fonts
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    texture_path   The texture to use
     */
    public GuiTexture useFontTexture(Identifier texture_path) {
        return this.useFontTexture(texture_path, 0, 0);
    }

    /**
     * Set the font texture directly
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     *
     * @param    gui_texture   The texture to use
     */
    public GuiTexture setFontTexture(GuiTexture gui_texture) {
        this.font_texture = gui_texture;
        return this.font_texture;
    }

    /**
     * Get the GuiTexture
     *
     * @since   0.1.1
     */
    public GuiTexture getFontTexture() {
        return this.font_texture;
    }

    /**
     * Set the custom texture to use for this GUI by using a retextured item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     *
     * @param    texture_path   The texture to use
     * @param    slot_x         The X-position of the slot to sacrifice
     * @param    slot_y         The Y-position of the slot to sacrifice
     */
    public void useItemTexture(String texture_path, int slot_x, int slot_y) {
        this.font_texture = null;
        this.texture_path = texture_path;
        this.texture_slot_x = slot_x;
        this.texture_slot_y = slot_y;
    }

    /**
     * Set the custom texture to use for this GUI
     * In order to use a custom texture, you have to sacrifice an inventory slot.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    texture_path   The texture to use
     * @param    slot_x         The X-position of the slot to sacrifice
     * @param    slot_y         The Y-position of the slot to sacrifice
     */
    public void useCustomTexture(String texture_path, int slot_x, int slot_y) {
        this.useItemTexture(texture_path, slot_x, slot_y);
    }

    /**
     * Enable the default custom texture for this GUI
     * In order to use a custom texture, you have to sacrifice an inventory slot.
     * You'll have to put the item texture in "{modid}/gui/{name}"
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    enable    Enable or disable custom texture
     * @param    slot_x    The X-position of the slot to sacrifice
     * @param    slot_y    The Y-position of the slot to sacrifice
     */
    public void useCustomTexture(boolean enable, int slot_x, int slot_y) {
        if (enable) {
            this.useItemTexture(this.namespace + ":" + "gui/" + this.name, slot_x, slot_y);
        } else {
            this.texture_path = null;
            this.texture_slot_x = null;
            this.texture_slot_y = null;
        }
    }

    /**
     * Enable the default custom texture for this GUI.
     * This used to use the inventory-slot method, but has now switched to the font method.
     * You'll have to put the item texture in "{modid}/gui/{name}"
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    enable    Enable or disable custom texture
     */
    public void useCustomTexture(boolean enable) {

        if (!enable) {
            this.font_texture = null;
            this.texture_path = null;
            return;
        }

        Identifier identifier = Identifier.of(this.namespace, "gui/" + this.name);
        this.useFontTexture(identifier, 0, 0);
    }

    /**
     * Set the custom texture to use for this GUI
     * In order to use a custom texture, you have to sacrifice an inventory slot.
     * By default it'll use the top-left slot.
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    texture_name   The texture to use
     */
    public void useCustomTexture(String texture_name) {
        this.useFontTexture(Identifier.tryParse(texture_name));
    }

    /**
     * Get a widget by its ID
     *
     * @since    0.5.0
     */
    public Widget<?> getWidget(String id) {
        return this.widgets.get(id);
    }

    /**
     * Add a widget to this screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void addWidget(String id, Widget widget) {
        this.widgets.put(id, widget);
        widget.setScreenBuilder(this);
    }

    /**
     * Add a widget to this screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void addWidget(Widget widget) {
        this.addWidget(widget.getId(), widget);
    }

    /**
     * Get the namespace to use for the GUI item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Set the namespace to use for the GUI item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Set the title of this gui
     * (Only used when this is used as a factory)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Should the slots be cloned when creating a handler?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void setCloneSlots(boolean clone) {
        this.clone_slots = clone;
    }

    /**
     * Should the slots be cloned when creating a handler?
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean getCloneSlots() {
        return this.clone_slots;
    }

    /**
     * Calculate the index of a slot in this GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x   The X-coordinate of the slot inside this GUI
     * @param    y   The Y-coordinate of the slot inside this GUI
     */
    public int calculateSlotIndex(int x, int y) {
        return x + (y * this.width);
    }

    /**
     * Change the type of screen
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public ScreenBuilder setType(ScreenHandlerType<?> type) {
        this.screen_type = type;
        this.screen_info = ScreenInfo.get(screen_type);
        this.main_slots = DefaultedList.ofSize(this.getScreenTypeSlotCount(), AIR_SLOT);
        this.player_slots = DefaultedList.ofSize(36, AIR_SLOT);
        return this;
    }

    /**
     * Change the type of screen to an anvil
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public ScreenBuilder useAnvil() {
        var result = this.setType(ScreenHandlerType.ANVIL);

        // When using an anvil, there always has to be an item in the input slot,
        // or else text input will not work
        if (this.font_texture != null) {
            StaticSlot transparent = new StaticSlot();
            transparent.setStack(BBSB.GUI_TRANSPARENT);
            this.setSlot(0, transparent);
        }

        return result;
    }

    /**
     * Add a back button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The index of the slot inside this GUI
     */
    public ButtonWidgetSlot setBackButton(int index) {
        return this.setBackButton(index, (screen, slot) -> {
            screen.showPreviousScreen();
        });
    }

    /**
     * Add a back button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    index   The index of the slot inside this GUI
     */
    public ButtonWidgetSlot setBackButton(int index, SlotEventListener click_handler) {

        ButtonWidgetSlot back_button = new ButtonWidgetSlot();
        back_button.setStack(GuiItem.get("arrow_left"));
        back_button.setTitle("Back");

        back_button.addLeftClickListener(click_handler);

        this.setSlot(index, back_button);

        return back_button;
    }

    /**
     * Get all the slots
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public List<Slot> getAllSlots() {
        List<Slot> slots = new ArrayList<>();

        slots.addAll(this.getMainSlots());
        slots.addAll(this.getPlayerSlots());

        return slots;
    }

    /**
     * Get all the slots of the main screen
     * (Excluding the player inventory & hotbar)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public List<Slot> getMainSlots() {
        List<Slot> slots = new ArrayList<>();

        // Iterate over all the widgets
        for (Widget widget : this.widgets.values()) {
            widget.prepareSlots(this);
        }

        slots.addAll(this.main_slots);

        return slots;
    }

    /**
     * Get all the player slots
     * (Inventory & hotbar)
     * Preparing should have been done by the main slots already
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    @NotNull
    public List<Slot> getPlayerSlots() {
        List<Slot> slots = new ArrayList<>();
        slots.addAll(this.player_slots);
        return slots;
    }

    /**
     * Specifically mark a slot as not available
     *
     * @since    0.3.1
     *
     * @param    index   The index of the slot inside this GUI
     */
    public void markSlotAsUsed(int index) {
        this.used_slots.put(index, true);
    }

    /**
     * Is the slot at the given index available?
     *
     * @since    0.3.1
     *
     * @param    index   The index of the slot inside this GUI
     */
    public boolean isSlotUsed(int index) {

        Boolean result = this.used_slots.get(index);

        if (result == null) {
            return false;
        }

        return result;
    }

    /**
     * Set a Slot with the given ItemStack at the specified index
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     *
     * @param    index   The index of the slot inside this GUI
     * @param    slot    The slot to set
     */
    public Slot setSlot(int index, Slot slot) {

        this.used_slots.put(index, true);

        if (index >= this.getScreenTypeSlotCount()) {
            int player_slot_index = index - this.getScreenTypeSlotCount();
            this.player_slots.set(player_slot_index, slot);
        } else {
            this.main_slots.set(index, slot);
        }

        if (slot instanceof SlotBuilder build_slot) {
            // SlotBuilder slots should always be cloned
            build_slot.setScreenIndex(index);
        } else if (slot instanceof BaseSlot base_slot) {
            base_slot.setScreenIndex(index);

            // BaseSlots can sometimes be kept as-is
            base_slot.setCloneBeforeScreen(this.clone_slots);

            // Give it a reference to this screenbuilder
            base_slot.setScreenBuilder(this);
        }

        return slot;
    }

    /**
     * Set a slot at the specified coordinate
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x      The X-coordinate of the slot inside this GUI
     * @param    y      The Y-coordinate of the slot inside this GUI
     * @param    slot   The slot item to set
     *
     * @return   The same slot will be returned
     */
    public Slot setSlot(int x, int y, Slot slot) {
        int index = this.calculateSlotIndex(x, y);
        return this.setSlot(index, slot);
    }

    /**
     * Set a StaticSlot with the given ItemStack at the specified coordinate
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x   The X-coordinate of the slot inside this GUI
     * @param    y   The Y-coordinate of the slot inside this GUI
     */
    public Slot setSlot(int x, int y, ItemStack stack) {
        StaticSlot slot = new StaticSlot(stack);
        return this.setSlot(x, y, slot);
    }

    /**
     * Set a StaticSlot with the given Item at the specified coordinate
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x   The X-coordinate of the slot inside this GUI
     * @param    y   The Y-coordinate of the slot inside this GUI
     */
    public Slot setSlot(int x, int y, Item item) {
        StaticSlot slot = new StaticSlot(new ItemStack(item));
        return this.setSlot(x, y, slot);
    }

    /**
     * Build a slot for the given coordinates
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x   The X-coordinate of the slot inside this GUI
     * @param    y   The Y-coordinate of the slot inside this GUI
     */
    public SlotBuilder buildSlot(int x, int y) {
        SlotBuilder slot = new SlotBuilder();
        this.setSlot(x, y, slot);
        return slot;
    }

    /**
     * Build a slot for the given index
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The slot index inside this GUI
     */
    public SlotBuilder buildSlot(int index) {
        SlotBuilder slot = new SlotBuilder();
        this.setSlot(index, slot);
        return slot;
    }

    /**
     * Add a button
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The slot index inside this GUI
     */
    public ButtonWidgetSlot addButton(int index) {
        ButtonWidgetSlot slot = new ButtonWidgetSlot();
        this.setSlot(index, slot);
        return slot;
    }

    /**
     * Add a select-slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The slot index inside this GUI
     */
    public SelectSlot addSelect(int index) {
        SelectSlot slot = new SelectSlot();
        this.setSlot(index, slot);

        // Select slots always have to be cloned
        slot.setCloneBeforeScreen(true);

        return slot;
    }

    /**
     * Add a select-slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The slot index inside this GUI
     */
    public SelectSlot addSelect(int index, SelectSlot slot) {
        slot = slot.clone(null, null);
        this.setSlot(index, slot);
        return slot;
    }

    /**
     * Apply a clone of the given slot
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    x      The X-coordinate of the slot inside this GUI
     * @param    y      The Y-coordinate of the slot inside this GUI
     * @param    slot   The slot to add
     */
    public SlotBuilder buildSlot(int x, int y, SlotBuilder slot) {
        slot = slot.clone();
        this.setSlot(x, y, slot);
        return slot;
    }

    /**
     * Apply a clone of the given slot for the given index
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    index   The slot index inside this GUI
     * @param    slot    The slot to add
     */
    public SlotBuilder buildSlot(int index, SlotBuilder slot) {
        slot = slot.clone();
        this.setSlot(index, slot);
        return slot;
    }

    /**
     * Call the given function on change
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    fnc
     */
    public ScreenBuilder onChange(Consumer<TexturedScreenHandler> fnc) {
        this.call_on_change = fnc;
        return this;
    }

    /**
     * Create a screen handler to show to a player
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    sync_id
     * @param    player
     * @param    player_inventory
     * @param    inventory
     */
    public TexturedScreenHandler createScreenHandler(int sync_id, PlayerEntity player, PlayerInventory player_inventory, Inventory inventory) {
        return new TexturedScreenHandler(sync_id, this, player, player_inventory, inventory);
    }

    /**
     * Create a screen handler to show to a player
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    sync_id
     * @param    player
     * @param    player_inventory
     */
    public TexturedScreenHandler createScreenHandler(int sync_id, PlayerEntity player, PlayerInventory player_inventory) {
        return new TexturedScreenHandler(sync_id, this, player, player_inventory, new SimpleInventory(this.getScreenTypeSlotCount()));
    }

    /**
     * Create a screen handler to show to a player
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    sync_id
     * @param    player_inventory
     * @param    inventory
     */
    public TexturedScreenHandler createScreenHandler(int sync_id, PlayerInventory player_inventory, Inventory inventory) {
        return this.createScreenHandler(sync_id, player_inventory.player, player_inventory, inventory);
    }

    /**
     * Create a screen handler to show to a player
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    sync_id
     * @param    player_inventory
     */
    public TexturedScreenHandler createScreenHandler(int sync_id, PlayerInventory player_inventory) {
        return this.createScreenHandler(sync_id, player_inventory.player, player_inventory);
    }

    /**
     * The TexturedScreenHandler is saying this has been closed
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    player
     */
    public void handleClose(PlayerEntity player) {

    }

    /**
     * The TexturedScreenHandler is reporting a change
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    screen
     */
    public void screenHasChanged(TexturedScreenHandler screen) {

        if (this.call_on_change == null) {
            return;
        }

        this.call_on_change.accept(screen);
    }

    /**
     * Add the texture item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void loadTextureItem() {
        if (this.texture_path != null) {
            if (this.gui_item == null) {
                this.gui_item_name = "gui_" + this.name;

                if (gui_items.containsKey(this.gui_item_name)) {
                    this.gui_item = gui_items.get(this.gui_item_name);
                } else {

                    // Create a dummy item
                    this.gui_item = new Item(new Item.Settings());

                    this.gui_item_identifier = Identifier.of(this.namespace, this.gui_item_name);
                    Registry.register(Registries.ITEM, this.gui_item_identifier, this.gui_item);

                    gui_items.put(this.gui_item_name, this.gui_item);
                }
            }

            ItemStack gui_stack = new ItemStack(this.gui_item);
            String title = "";

            if (this.title != null) {
                title = this.title;
            }

            BibItem.setCustomName(gui_stack, Text.literal(title).setStyle(Style.EMPTY.withItalic(false)));

            this.setSlot(this.texture_slot_x, this.texture_slot_y, gui_stack);
        }
    }


    /**
     * Register this GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void register() {

        if (this.has_been_registered) {
            throw new AssertionError("ScreenBuilder: attempted to register a screen twice");
        }

        this.has_been_registered = true;

        // Make sure the font texture pieces have been generated
        if (this.font_texture != null) {
            this.font_texture.calculate();
        }

        // Iterate over all the widgets
        for (Widget widget : this.widgets.values()) {
            widget.register();
        }

        //this.loadTextureItem();

        screen_builders.add(this);
    }

    /**
     * Add GUI data to the ResourcePackMaker
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void registerPoly(ModdedResources moddedResources, PolyMcResourcePack pack, SimpleLogger logger) {

        if (this.texture_path == null) {
            return;
        }

        JModel item_model = new JModelImpl();
        JsonParser parser = new JsonParser();

        item_model.setGuiLight(JGuiLight.FRONT);
        item_model.getTextures().put("gui", this.texture_path);

        String elements_json = null;
        String display_json = null;

        display_json = "{\"gui\":{\"rotation\":[0,0,-22.5],\"scale\":[4,4,4],\"translation\":[0,0,-80]}}";

        if (this.screen_type == ScreenHandlerType.GENERIC_9X6) {
            elements_json = "[{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,0,4,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[0.8872734552782248,10.504879619094389,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,4,4,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[20.839527064652682,14.473629619094389,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,8,4,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[40.791780674027144,18.442379619094385,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,12,4,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[60.744034283401604,22.411129619094385,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,0,8,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-4.612726544721775,38.15524682578655,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,4,8,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[15.339527064652687,42.12399682578655,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,8,8,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[35.29178067402714,46.09274682578654,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,12,8,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[55.244034283401604,50.06149682578655,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,0,12,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-10.112726544721774,65.80561403247872,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,4,12,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[9.839527064652685,69.77436403247872,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,8,12,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[29.791780674027144,73.7431140324787,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,12,12,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[49.744034283401604,77.71186403247872,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,0,16,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-15.612726544721768,93.45598123917088,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,4,16,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[4.339527064652685,97.42473123917087,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,8,16,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[24.291780674027148,101.39348123917088,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,4.03125,8],\"to\":[13.5,11.96875,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,12,16,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[44.2440342834016,105.36223123917087,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}}]";
        } else if (this.screen_type == ScreenHandlerType.ANVIL) {
            elements_json = "[{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,0,4,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-19.28220207972363,-5.919426674704497,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,4,4,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-7.970688222440465,-3.669426674704496,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,8,4,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[3.3408256348426892,-1.419426674704502,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[0,12,4,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[14.65233949212585,0.8305733252954979,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,0,8,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-24.782202079723618,21.730940531987663,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,4,8,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-13.470688222440462,23.98094053198766,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,8,8,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-2.15917436515731,26.230940531987663,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[4,12,8,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[9.152339492125849,28.48094053198766,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,0,12,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-30.282202079723625,49.38130773867983,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,4,12,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-18.970688222440465,51.63130773867983,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,8,12,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-7.659174365157309,53.881307738679816,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[8,12,12,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[3.652339492125851,56.13130773867982,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,0,16,4],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-35.78220207972362,77.03167494537199,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,4,16,8],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-24.470688222440454,79.28167494537198,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,8,16,12],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-13.159174365157307,81.531674945372,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}},{\"from\":[2.5,5.75,8],\"to\":[13.5,10.25,-8.000000000000002],\"faces\":{\"south\":{\"uv\":[12,12,16,16],\"texture\":\"#gui\"}},\"rotation\":{\"origin\":[-1.8476605078741486,83.781674945372,-8.000000000000002],\"axis\":\"z\",\"angle\":22.5}}]";
        }

        if (elements_json == null) {
            return;
        }

        JsonElement elements = parser.parse(elements_json);

        List<JElement> j_elements = item_model.getElements();

        // @TODO: Make old-style item guis work again with new polymc

        /*

        item_model.elements = elements.getAsJsonArray();

        Type display_type = new TypeToken<Map<String, JsonModel.DisplayEntry>>() {}.getType();
        item_model.display = pack.getGson().fromJson(display_json, display_type);

        Identifier server_item_id = Registry.ITEM.getId(this.gui_item);
        Identifier item_model_path = new Identifier(server_item_id.getNamespace(), "item/" + server_item_id.getPath());

        pack.putPendingModel(item_model_path, item_model);

        Identifier texture_identifier = new Identifier(this.texture_path);

        pack.copyTexture(texture_identifier.getNamespace(), texture_identifier.getPath());

        */
    }

    /**
     * Set the display name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public void setDisplayName(String display_name) {
        this.setDisplayName(Text.literal(display_name));
    }

    /**
     * Set the display name
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void setDisplayName(Text display_name) {
        this.title_text = display_name;
        this.title = null;
    }

    /**
     * Return the display name
     * (When ScreenBuilder is used as a factory)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    @Override
    public Text getDisplayName() {

        if (this.title_text != null) {
            return this.title_text;
        }

        String title = this.title;

        if (title == null) {
            title = this.name;
        }

        return Text.literal(title);
    }

    /**
     * Populate the TextBuilder with font texture data
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.1
     */
    public void addToTextBuilder(TextBuilder text_builder) {

        // Create the root space group
        text_builder.ensureSpaceGroup();

        // If this screenbuilder has a font texture, use it now
        if (this.font_texture != null) {
            this.font_texture.addToTextBuilder(text_builder);
        } else {
            text_builder.setOffsetsFrom(this.getScreenInfo());
        }

        // Iterate over all the WidgetSlots
        for (Slot slot : this.getAllSlots()) {
            if (slot instanceof BaseSlot baseSlot) {
                baseSlot.addToTextBuilder(text_builder);
            }
        }

        // Iterate over all the key-value font_widgets
        for (Map.Entry<String, Widget> entry : this.widgets.entrySet()) {
            String id = entry.getKey();
            Widget widget = entry.getValue();

            widget.addToTextBuilder(text_builder);
        }

        this.printErrors(text_builder);
    }

    /**
     * Create the menu when used as a factory
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    @Nullable
    @Override
    public TexturedScreenHandler createMenu(int sync_id, PlayerInventory inv, PlayerEntity player) {

        // Create the screen handler
        TexturedScreenHandler handler = this.createScreenHandler(sync_id, inv);

        // This screenbuilder should be seen as the origin factory
        handler.setOriginFactory(this);

        return handler;
    }

    /**
     * Add a unit to the screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public void addUnit(Unit unit) {
        this.units.add(unit);
    }

    /**
     * Convert the Y coordinate in the current GUI to the Y coordinate
     * relative to the underlying screen's title baseline Y
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   gui_y   The Y coordinate inside the current GUI
     */
    public int convertToUnderlyingTitleY(int gui_y) {

        // Prepare the result value
        int result = gui_y;

        // See if there's a GuiTexture in use
        GuiTexture gui_texture = this.getFontTexture();

        if (gui_texture != null) {
            result = gui_texture.getContainerY(gui_y);
        }

        ScreenInfo underlying_screen_info = this.getScreenInfo();

        if (underlying_screen_info != null) {
            result -= underlying_screen_info.getTitleBaselineY();
        }

        return result;
    }

    /**
     * Turn the Y coordinate in the (possible) GUI texture
     * into the underlying screen's Y coordinate.
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   gui_y   The Y coordinate in the GUI texture
     */
    public int getContainerY(int gui_y) {

        // See if there's a GuiTexture in use
        GuiTexture gui_texture = this.getFontTexture();

        if (gui_texture != null) {
            return gui_texture.getContainerY(gui_y);
        }

        return gui_y;
    }

    /**
     * Turn the X coordinate in the (possible) GUI texture
     * into the underlying screen's X coordinate.
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   gui_x   The X coordinate in the GUI texture
     */
    public int getContainerX(int gui_x) {

        // See if there's a GuiTexture in use
        GuiTexture gui_texture = this.getFontTexture();

        if (gui_texture != null) {
            return gui_texture.getContainerX(gui_x);
        }

        return gui_x;
    }

    /**
     * Turn the Y coordinate in the underlying screen into a Y coordinate
     * in the current screen's GUI texture
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   container_y   The Y coordinate in the underlying screen
     *                        (NOT relative to the original title, just starting at the top left)
     */
    public int getGuiY(int container_y) {

        // Prepare the result value
        int result = container_y;

        // See if there's a GuiTexture in use
        GuiTexture gui_texture = this.getFontTexture();

        if (gui_texture != null) {
            int original_y = gui_texture.getOriginalY();
            result += original_y;
        }

        return result;
    }

    /**
     * Turn the X coordinate in the underlying screen into a X coordinate
     * in the current screen's GUI texture
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     *
     * @param   container_x   The X coordinate in the underlying screen
     *                        (NOT relative to the original title, just starting at the top left)
     */
    public int getGuiX(int container_x) {

        // Prepare the result value
        int result = container_x;

        // See if there's a GuiTexture in use
        GuiTexture gui_texture = this.getFontTexture();

        if (gui_texture != null) {
            int original_x = gui_texture.getOriginalX();
            result += original_x;
        }

        return result;
    }

    /**
     * Remove the current errors, if any
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     */
    public void clearErrors() {
        if (this.error_messages != null) {
            this.error_messages.clear();
        }
    }

    /**
     * Set a new error message
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.1
     */
    public void setError(String message) {
        this.clearErrors();
        this.addError(message);
    }

    /**
     * Add an error message
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public void addError(String message) {

        if (this.error_messages == null) {
            this.error_messages = new ArrayList<>();
        }

        this.error_messages.add(message);
    }

    /**
     * Print the error messages
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    protected void printErrors(TextBuilder builder) {

        if (this.error_messages == null) {
            return;
        }

        ErrorAreaInfo info = null;
        GuiTexture texture = this.getFontTexture();

        if (texture != null) {
            info = texture.getErrorAreaInfo();
        }

        if (info == null) {
            info = new ErrorAreaInfo();

            int y = this.show_error_y;

            // Will break for inputs that are taller than normal, oh well
            if (y < 0) {
                info.setShouldStartFromBottom(true);
                //y -= (this.error_messages.size() * 8);
            } else {
                info.setShouldStartFromBottom(false);
            }

            info.setStartY(y);
            info.setStartX(0);
            info.setWidth(176);
            info.setHeight(100);
        }

        TextGroup error_group = builder.createNewGroup();
        error_group.setColor(TextColor.fromRgb(0xFF0000));

        for (int i = 0; i < this.error_messages.size(); i++) {
            String message = this.error_messages.get(i);

            int y = info.getYForLine(i);
            int x = info.getXForLine(i, message);

            x = this.getContainerX(x);
            y = this.getContainerY(y);

            Font font = Font.ABSOLUTE_DEFAULT_COLLECTION.getClosestFont(y);
            builder.setCursor(x);
            builder.print(message, font);
        }
    }

    /**
     * Get the coordinates of a slot in the current applied GUI
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.2.2
     *
     * @param    slot_index   The index of the slot
     */
    public ScreenInfo.Coordinates getSlotCoordinates(int slot_index) {
        ScreenInfo.Coordinates original_slot_coordinates = this.getScreenInfo().getSlotCoordinates(slot_index);

        int new_y = this.getGuiY(original_slot_coordinates.y);
        int new_x = this.getGuiX(original_slot_coordinates.x);

        return new ScreenInfo.Coordinates(new_x, new_y);
    }
}