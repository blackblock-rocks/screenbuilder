package rocks.blackblock.screenbuilder.testmod.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.inventories.BaseInventory;
import rocks.blackblock.screenbuilder.testmod.ScreenbuilderTest;

public class CardboardBoxBlockEntity extends BlockEntity implements BaseInventory, NamedScreenHandlerFactory {

    private DefaultedList<ItemStack> contents = null;
    public static ScreenBuilder GUI;
    private Integer damage = 0;

    /**
     * Construct an instance for the given block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public CardboardBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ENTITY, pos, state);
        this.contents = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Cardboard");
    }

    /**
     * This method should return the BlockItem you want to use in case
     * the inventory data should be written to a dropped item
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public BlockItem getDroppedItem() {
        return ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM;
    }

    /**
     * Read the NBT data (fills inventory from storage)
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    nbt   The source NBT data as stored on disk
     */
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.setContentsFromNbt(nbt);

        Integer damage = nbt.getInt("BoxDamage");

        if (damage > 0) {
            this.damage = damage;
        }
    }

    /**
     * Prepare NBT data to be written to storage
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *@param    nbt   The NBT data that will be stored on disk
     */
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        this.writeInventoryToNbt(nbt);

        if (this.damage > 0) {
            nbt.putInt("BoxDamage", this.damage);
        }
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return GUI.createScreenHandler(syncId, inv, this);
    }

    @Override
    public DefaultedList<ItemStack> getContents() {
        return this.contents;
    }

    @Override
    public void setContents(DefaultedList<ItemStack> contents) {
        this.contents = contents;
    }

    @Override
    public void contentsChanged() {

    }

    @Override
    public int size() {
        return 9;
    }

    /**
     * Get the damage of this box
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public int getDamage() {
        return this.damage;
    }

    /**
     * Increase the damage
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public void increaseDamage() {
        this.damage += 1;
    }

    /**
     * Do a drop check
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public boolean doDropCheck() {

        if (this.damage > 16) {
            ItemScatterer.spawn(this.getWorld(), this.getPos(), this);
            return true;
        }

        return false;
    }

    /**
     * Get the item stack
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public ItemStack getItemStack() {

        ItemStack result = BaseInventory.super.getItemStack();

        if (result == null) {
            return null;
        }

        int damage = this.getDamage();

        // If the damage is above 16, nothing should drop!
        if (damage > 16) {
            return null;
        }

        if (damage > 0) {
            // We won't show the first damage done
            if (damage > 1) {
                result.setDamage(damage - 1);
            }

            NbtCompound nbt = result.getSubNbt("BlockEntityTag");

            if (nbt == null) {
                nbt = new NbtCompound();
                result.setSubNbt("BlockEntityTag", nbt);
            }

            nbt.putInt("BoxDamage", damage);
        }

        return result;
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

        // Names do not have to be unique
        GUI = new ScreenBuilder("cardboard");

        GUI.setNamespace("testmod");

        // By just providing a boolean value of `true`,
        // it will automatically create a GUI item & look for the texture in
        // {namespace}/textures/gui/{name}.png - So in this case that will be
        // testmod/textures/gui/cardboard.png
        GUI.useCustomTexture(true);

        // GUIs are always made based on 9x6 inventories.
        // The first number (in buildSlot call) is the index of the slot on the screen,
        // the second number (mapInventory) is the index inside the inventory to link this slot to
        GUI.buildSlot(12).mapInventory(0).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(13).mapInventory(1).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(14).mapInventory(2).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(21).mapInventory(3).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(22).mapInventory(4).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(23).mapInventory(5).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(30).mapInventory(6).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(31).mapInventory(7).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);
        GUI.buildSlot(32).mapInventory(8).deny(ScreenbuilderTest.CARDBOARD_BOX_BLOCK_ITEM);

        GUI.register();

        return GUI;
    }
}
