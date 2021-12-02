package rocks.blackblock.screenbuilder.inventories;

import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public class DummyInventory implements BaseInventory {

    public final static DummyInventory EMPTY = new DummyInventory(0);

    private final int size;
    private DefaultedList<ItemStack> contents;
    private List<InventoryChangedListener> listeners;

    public DummyInventory(int size) {
        this.size = size;
        this.setContents(DefaultedList.ofSize(size, ItemStack.EMPTY));
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
        return this.size;
    }
}
