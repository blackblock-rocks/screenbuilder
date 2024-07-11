package rocks.blackblock.screenbuilder.screen;

import org.jetbrains.annotations.NotNull;
import rocks.blackblock.screenbuilder.ScreenBuilder;

import java.util.*;

/**
 * Keep track of slots used
 *
 * @since   0.5.0
 */
@SuppressWarnings("unused")
public class SlotManager implements Iterable<Integer> {

    private static final int PLAYER_INVENTORY_ROWS = 3;
    private static final int PLAYER_INVENTORY_COLS = 9;
    private static final int PLAYER_HOTBAR_ROWS = 1;

    protected boolean use_player_inventory = false;
    protected boolean use_toolbar = false;

    private ScreenBuilder screen_builder = null;
    private int top_row_slot_count;
    private int top_col_slot_count;
    private int top_total_slot_count;
    private int bottom_count;
    private Set<Integer> used_slots;

    public SlotManager(ScreenBuilder builder) {
        this.setScreenBuilder(builder);
        this.top_row_slot_count = 6;
        this.top_col_slot_count = 9;
    }

    public SlotManager(int top_row_slot_count, int top_col_slot_count) {
        this.top_row_slot_count = top_row_slot_count;
        this.top_col_slot_count = top_col_slot_count;
        this.top_total_slot_count = top_row_slot_count * top_col_slot_count;
        this.used_slots = new HashSet<>();
    }

    public void setScreenBuilder(ScreenBuilder builder) {
        this.screen_builder = builder;

        if (builder != null) {
            this.use_player_inventory = !builder.getShowPlayerInventory();
            this.use_toolbar = !builder.getShowPlayerHotbar();
        }
    }

    public void setAvailableSlots(List<Integer> available_slots) {
        this.used_slots.clear();

        for (var index : this.getAvailableSlots()) {
          if (!available_slots.contains(index)) {
              this.markSlotAsUsed(index);
          }
        }
    }

    public void setAllowBottomSlots(boolean use_player_inventory, boolean use_toolbar) {
        this.use_player_inventory = use_player_inventory;
        this.use_toolbar = use_toolbar;

        if (this.use_player_inventory) {
            if (this.use_toolbar) {
                this.bottom_count = (PLAYER_INVENTORY_ROWS + PLAYER_HOTBAR_ROWS) * PLAYER_INVENTORY_COLS;
            } else {
                this.bottom_count = PLAYER_INVENTORY_ROWS * PLAYER_INVENTORY_COLS;
            }
        } else {
            this.bottom_count = 0;
        }
    }

    /**
     * Get the slot at the given relative index
     */
    public Integer get(int relative_index) {

        var slots = this.getAvailableSlots();

        if (relative_index < 0) {
            relative_index = slots.size() + relative_index;
        }

        return this.getAvailableSlots().get(relative_index);
    }

    /**
     * How many slots are available?
     */
    public int countAvailableSlots() {
        return this.getAvailableSlots().size();
    }

    /**
     * Reset all the used slots
     */
    public void reset() {
        this.used_slots.clear();
    }

    /**
     * Mark a slot as being used (without flushing to the ScreenBuilder)
     */
    public void markSlotAsUsed(int slot) {
        this.used_slots.add(slot);
    }

    /**
     * See if the given slot index has already been used.
     * Also checks the ScreenBuilder if it is available.
     */
    public boolean isSlotUsed(int slot) {

        if (this.screen_builder != null && this.screen_builder.isSlotUsed(slot)) {
            return true;
        }

        return this.used_slots.contains(slot);
    }

    /**
     * Register all the used slots to the ScreenBuilder
     */
    public boolean commitToScreenBuilder() {

        if (this.screen_builder == null) {
            return false;
        }

        for (int index : this.used_slots) {
            this.screen_builder.markSlotAsUsed(index);
        }

        return true;
    }

    public List<Integer> getAvailableSlots() {
        List<Integer> availableSlots = new ArrayList<>();

        // Top section
        for (int i = 0; i < this.top_total_slot_count; i++) {
            if (!this.isSlotUsed(i)) {
                availableSlots.add(i);
            }
        }

        if (this.use_player_inventory) {
            // Player inventory and hotbar
            int playerInventoryStart = this.top_total_slot_count;

            for (int i = 0; i < this.bottom_count; i++) {
                int slot = playerInventoryStart + i;
                if (!this.isSlotUsed(slot)) {
                    availableSlots.add(slot);
                }
            }
        }

        return availableSlots;
    }

    public List<Row> getAvailableRows() {
        List<Row> availableRows = new ArrayList<>();

        // Top section
        for (int row = 0; row < top_row_slot_count; row++) {
            List<Integer> availableSlots = new ArrayList<>();
            for (int col = 0; col < top_col_slot_count; col++) {
                int slot = row * top_col_slot_count + col;
                if (!this.isSlotUsed(slot)) {
                    availableSlots.add(slot);
                }
            }
            if (!availableSlots.isEmpty()) {
                availableRows.add(new Row(availableSlots));
            }
        }

        if (this.use_player_inventory) {
            // Player inventory and hotbar
            int playerInventoryStart = this.top_total_slot_count;

            for (int row = 0; row < this.bottom_count / PLAYER_INVENTORY_COLS; row++) {
                List<Integer> availableSlots = new ArrayList<>();
                for (int col = 0; col < PLAYER_INVENTORY_COLS; col++) {
                    int slot = playerInventoryStart + (row * PLAYER_INVENTORY_COLS) + col;
                    if (!this.isSlotUsed(slot)) {
                        availableSlots.add(slot);
                    }
                }
                if (!availableSlots.isEmpty()) {
                    availableRows.add(new Row(availableSlots));
                }
            }
        }

        return availableRows;
    }

    public List<Column> getAvailableColumns() {
        List<Column> availableColumns = new ArrayList<>();

        // Top section
        for (int col = 0; col < top_col_slot_count; col++) {
            List<Integer> availableSlots = new ArrayList<>();
            for (int row = 0; row < top_row_slot_count; row++) {
                int slot = row * top_col_slot_count + col;
                if (!this.isSlotUsed(slot)) {
                    availableSlots.add(slot);
                }
            }
            if (!availableSlots.isEmpty()) {
                availableColumns.add(new Column(availableSlots));
            }
        }

        return availableColumns;
    }

    public Row reserveTopFreeRow() {
        return reserveFreeRow(true);
    }

    public Row reserveBottomFreeRow() {
        return reserveFreeRow(false);
    }

    private Row reserveFreeRow(boolean fromTop) {
        int totalRows = top_row_slot_count + (use_player_inventory ? bottom_count / PLAYER_INVENTORY_COLS : 0);
        int startRow = fromTop ? 0 : totalRows - 1;
        int endRow = fromTop ? totalRows : -1;
        int step = fromTop ? 1 : -1;

        for (int row = startRow; row != endRow; row += step) {
            Row availableSlotsInRow = getAvailableSlotsInRow(row);
            if (!availableSlotsInRow.isEmpty()) {
                // Mark these slots as used
                availableSlotsInRow.forEach(this::markSlotAsUsed);
                return availableSlotsInRow;
            }
        }

        return null; // No free slots found in any row
    }

    private Row getAvailableSlotsInRow(int row) {
        List<Integer> availableSlotsInRow = new ArrayList<>();
        int startSlot = row < top_row_slot_count ? row * top_col_slot_count :
                top_total_slot_count + (row - top_row_slot_count) * PLAYER_INVENTORY_COLS;
        int endSlot = row < top_row_slot_count ? startSlot + top_col_slot_count :
                startSlot + PLAYER_INVENTORY_COLS;

        for (int slot = startSlot; slot < endSlot; slot++) {
            if (!isSlotUsed(slot)) {
                availableSlotsInRow.add(slot);
            }
        }

        return new Row(availableSlotsInRow);
    }

    @Override
    public Iterator<Integer> iterator() {
        return this.getAvailableSlots().iterator();
    }

    /**
     * Represent a list of slot indexes
     *
     * @since   0.5.0
     */
    public static class SlotIndexList implements Iterable<Integer> {
        private final List<Integer> slots;

        public SlotIndexList(List<Integer> slots) {
            this.slots = slots;
        }

        public Integer get(int index) {

            if (index < 0) {
                index = this.slots.size() + index;
            }

            return this.slots.get(index);
        }

        public boolean isEmpty() {
            return this.slots.isEmpty();
        }

        public int size() {
            return this.slots.size();
        }

        @NotNull
        @Override
        public Iterator<Integer> iterator() {
            return this.slots.iterator();
        }
    }

    /**
     * Represent a column of slot indexes
     *
     * @since   0.5.0
     */
    public static class Column extends SlotIndexList {
        public Column(List<Integer> slots) {
            super(slots);
        }
    }

    /**
     * Represent a row of slot indexes
     *
     * @since   0.5.0
     */
    public static class Row extends SlotIndexList {
        public Row(List<Integer> slots) {
            super(slots);
        }
    }
}
