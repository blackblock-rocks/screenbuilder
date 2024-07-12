package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.screen.NamedScreenHandlerFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.widgets.PaginationWidget;

import java.util.List;

/**
 * Make something pageable.
 * Pages start at 1.
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.3.1
 */
public interface PageableInput<T> {

    /**
     * Get the pagination widget id
     *
     * @since   0.3.1
     */
    default String getPaginationWidgetId() {
        return "pagination";
    }

    /**
     * Should the pagination widget always be visible?
     *
     * @since   0.3.1
     */
    default boolean getAlwaysShowPaginationWidget() {
        return true;
    }

    /**
     * Set the current page value
     *
     * @since   0.3.1
     */
    void setPageValue(int page);

    /**
     * Get the current page value
     *
     * @since   0.3.1
     */
    int getPageValue();

    /**
     * Set the current page
     *
     * @since   0.3.1
     */
    default void setPage(int page) {

        if (page < 1) {
            page = 1;
        } else {
            int max_page = this.getPageCount();

            if (page > max_page) {
                page = max_page;
            }
        }

        this.setPageValue(page);
    }

    /**
     * Get the current page
     *
     * @since   0.3.1
     */
    default int getPage() {
        return this.getPageValue();
    }

    /**
     * Get all the available options
     *
     * @since   0.3.1
     */
    @NotNull
    List<T> getPageableItems();

    /**
     * Get the maximum amount of items per page
     *
     * @since   0.3.1
     */
    int getMaxItemsPerPage();

    /**
     * Get the factory that creates this screen
     * (Probably ourselves)
     *
     * @since   0.3.1
     */
    NamedScreenHandlerFactory getScreenHandlerFactory();

    /**
     * Get the amount of reserved slots (of the current page)
     *
     * @since   0.5.0
     */
    default int getReservedSlotsForPage(int page) {
        return 0;
    }

    /**
     * Get the amount of reserved slots for all the pages
     *
     * @since   0.5.0
     */
    default int getReservedSlotsForAllPages() {

        int result = 0;
        int page = 1;
        int reserved = 0;

        while ((reserved = this.getReservedSlotsForPage(page)) > 0) {
            result += reserved;
            page++;
        }

        return result;
    }

    /**
     * Get the amount of pages
     *
     * @since   0.3.1
     */
    default int getPageCount() {
        int items_per_page = this.getMaxItemsPerPage();
        int size = this.getPageableItems().size() + this.getReservedSlotsForAllPages();

        if (size == 0) {
            return 1;
        }

        return Math.max(1, (int) Math.ceil((double) size / (double) items_per_page));
    }

    /**
     * Get a sublist of the options for the given page
     *
     * @since   0.3.1
     */
    default List<T> getPageableItemsForPage(int page) {
        List<T> pageable_items = this.getPageableItems();
        int items_per_page = this.getMaxItemsPerPage();

        // Calculate reserved slots up to the given page
        int total_reserved_slots = 0;
        int reserved_slots_before = 0;
        int reserved_on_this_page = 0;
        for (int i = 1; i <= page; i++) {
            int reserved_on_page = this.getReservedSlotsForPage(i);

            if (i < page) {
                reserved_on_page += reserved_on_page;
            } else if (i == page) {
                reserved_on_this_page = reserved_on_page;
            }

            total_reserved_slots += reserved_on_page;
        }

        // Adjust start and end indices considering reserved slots
        int start = Math.max(0, (page - 1) * items_per_page - reserved_slots_before);
        int end = Math.min(start + items_per_page - reserved_on_this_page, pageable_items.size());

        if (start >= end) {
            return List.of();
        }

        return pageable_items.subList(start, end);
    }

    /**
     * Iterate over all the items on the current page
     *
     * @since   0.3.1
     */
    default void forEachItemsOnCurrentPage(PageableItemAdder<T> adder) {

        int page = this.getPage();
        List<T> items = this.getPageableItemsForPage(page);

        int items_on_this_page = items.size();
        int reserved_slots_on_this_page = this.getReservedSlotsForPage(page);

        for (int i = 0; i < items_on_this_page; i++) {
            adder.add(items.get(i), i + reserved_slots_on_this_page, items_on_this_page);
        }
    }

    /**
     * Add the pagination widget to the given screen builder.
     * If there are not enough items, no widget will be added if `always_add` is false.
     *
     * @since   0.3.1
     */
    @Nullable
    default PaginationWidget addPaginationWidget(ScreenBuilder sb, int slot) {
        return this.addPaginationWidget(sb, slot, this.getAlwaysShowPaginationWidget());
    }

    /**
     * Add the pagination widget to the given screen builder.
     * If there are not enough items, no widget will be added if `always_add` is false.
     *
     * @since   0.3.1
     */
    @Nullable
    default PaginationWidget addPaginationWidget(ScreenBuilder sb, int slot, boolean always_add) {

        if (!always_add && this.getPageCount() <= 1) {
            return null;
        }

        PaginationWidget widget = new PaginationWidget();

        widget.setId(this.getPaginationWidgetId());
        widget.setSlotIndex(slot);
        widget.setMaxValue(this.getPageCount());

        int current_page = this.getPage();
        widget.setOnChangeListener((texturedScreenHandler, pagination_widget) -> {

            if (current_page == this.getPage()) {
                return;
            }

            texturedScreenHandler.replaceScreen(this.getScreenHandlerFactory());
        });

        // The PaginationWidget is 4 slots wide, so mark those as used
        // (This assumes the pagination widget will fit on the screen,
        // and not overflow the row)
        for (int i = 0; i < 4; i++) {
            sb.markSlotAsUsed(slot + i);
        }

        sb.addWidget(widget);

        return widget;
    }

    /**
     * The interface callback to use for adding items to the current page
     *
     * @since   0.3.1
     */
    @FunctionalInterface
    interface PageableItemAdder<T> {
        void add(T item, int index_on_page, int amount_on_this_page);
    }

    /**
     * A simple implementation
     *
     * @since   0.5.0
     */
    class Pager<T> implements PageableInput<T> {

        private List<T> items = null;
        private int current_page = 1;
        private int max_items_per_page = 5;
        private NamedScreenHandlerFactory factory = null;
        private String widget_id;

        public Pager(String widget_id) {
            this.widget_id = widget_id;
        }

        @Override
        public String getPaginationWidgetId() {
            return this.widget_id;
        }

        public void setPaginationWidgetId(String id) {
            this.widget_id = id;
        }

        @Override
        public void setPageValue(int page) {
            this.current_page = page;
        }

        @Override
        public int getPageValue() {
            return this.current_page;
        }

        public void setPageableItems(List<T> items) {
            this.items = items;
        }

        @Override
        public @NotNull List<T> getPageableItems() {

            if (items == null) {
                return List.of();
            }

            return this.items;
        }

        @Override
        public int getMaxItemsPerPage() {
            return this.max_items_per_page;
        }

        public void setMaxItemsPerPage(int max) {
            this.max_items_per_page = max;
        }

        public void setScreenHandlerFactory(NamedScreenHandlerFactory factory) {
            this.factory = factory;
        }

        @Override
        public NamedScreenHandlerFactory getScreenHandlerFactory() {
            return this.factory;
        }
    }
}
