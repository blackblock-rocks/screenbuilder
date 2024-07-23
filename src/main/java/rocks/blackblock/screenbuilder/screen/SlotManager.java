package rocks.blackblock.screenbuilder.screen;

import org.jetbrains.annotations.NotNull;
import rocks.blackblock.bib.util.BibLog;
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
    private int top_column_count;
    private int top_row_count;
    private int top_total_slot_count;
    private int bottom_row_count;
    private int bottom_slot_count;
    private Set<Integer> used_slots;

    public SlotManager(ScreenBuilder builder) {
        this.setScreenBuilder(builder);
        this.top_column_count = 6;
        this.top_row_count = 9;
    }

    public SlotManager(int top_column_count, int top_row_count) {
        this.top_column_count = top_column_count;
        this.top_row_count = top_row_count;
        this.top_total_slot_count = top_column_count * top_row_count;
        this.used_slots = new HashSet<>();
    }

    /**
     * Set the screenbuilder being used
     *
     * @since   0.5.0
     */
    public void setScreenBuilder(ScreenBuilder builder) {
        this.screen_builder = builder;

        if (builder != null) {
            this.use_player_inventory = !builder.getShowPlayerInventory();
            this.use_toolbar = !builder.getShowPlayerHotbar();
        }
    }

    /**
     * Get all the available slot indexes we can use
     *
     * @since   0.5.0
     */
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

            for (int i = 0; i < this.bottom_slot_count; i++) {
                int slot = playerInventoryStart + i;
                if (!this.isSlotUsed(slot)) {
                    availableSlots.add(slot);
                }
            }
        }

        return availableSlots;
    }

    /**
     * Override the available slots we can use
     *
     * @since   0.5.0
     */
    public void setAvailableSlots(List<Integer> available_slots) {
        this.used_slots.clear();

        for (var index : this.getAvailableSlots()) {
          if (!available_slots.contains(index)) {
              this.markSlotAsUsed(index);
          }
        }
    }

    /**
     * Allow the bottom slots (player inventory & hotbar) to be used
     *
     * @since   0.5.0
     */
    public void setAllowBottomSlots(boolean use_player_inventory, boolean use_toolbar) {
        this.use_player_inventory = use_player_inventory;
        this.use_toolbar = use_toolbar;

        if (this.use_player_inventory) {
            if (this.use_toolbar) {
                this.bottom_row_count = PLAYER_INVENTORY_ROWS + PLAYER_HOTBAR_ROWS;
            } else {
                this.bottom_row_count = PLAYER_INVENTORY_ROWS;
            }

            this.bottom_slot_count = this.bottom_row_count * PLAYER_INVENTORY_COLS;
        } else {
            this.bottom_row_count = 0;
            this.bottom_slot_count = 0;
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

    public List<Row> getAvailableRows() {
        List<Row> availableRows = new ArrayList<>();

        // Top section
        for (int row_index = 0; row_index < top_row_count; row_index++) {
            Row row = this.getAvailableSlotsInRow(row_index);

            if (!row.isEmpty()) {
                availableRows.add(row);
            }
        }

        if (this.use_player_inventory) {

            for (int row_index = 0 + this.top_row_count; row_index < this.top_row_count + this.bottom_row_count; row_index++) {
                Row row = this.getAvailableSlotsInRow(row_index);

                if (!row.isEmpty()) {
                    availableRows.add(row);
                }
            }
        }

        return availableRows;
    }

    public List<Column> getAvailableColumns() {
        List<Column> availableColumns = new ArrayList<>();

        // Top section
        for (int col = 0; col < top_row_count; col++) {
            List<Integer> availableSlots = new ArrayList<>();
            for (int row = 0; row < top_column_count; row++) {
                int slot = row * top_row_count + col;
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

    private Row reserveFreeRow(boolean from_top) {
        int total_rows = top_row_count + this.bottom_row_count;
        int start_row = from_top ? 0 : total_rows - 1;
        int end_row = from_top ? total_rows : -1;
        int step = from_top ? 1 : -1;

        for (int row = start_row; row != end_row; row += step) {
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
        int col_count;

        if (row <= this.top_row_count) {
            col_count = this.top_column_count;
        } else {
            col_count = PLAYER_INVENTORY_COLS;
        }

        int start = row * col_count;
        int end = start + col_count;

        for (int slot = start; slot < end; slot++) {
            if (!this.isSlotUsed(slot)) {
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
    public static class SlotIndexList implements Iterable<Integer>, BibLog.Argable {
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

        /**
         * Get the Arg representation for this instance
         */
        @Override
        public BibLog.Arg toBBLogArg() {
            var result = BibLog.createArg(this);

            for (int i = 0; i < this.size(); i++) {
                result.add("" + i, this.get(i));
            }

            return result;
        }

        @Override
        public String toString() {
            return this.toBBLogArg().toString();
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
