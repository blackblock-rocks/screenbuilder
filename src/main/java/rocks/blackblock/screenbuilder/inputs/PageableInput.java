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
     * Get the amount of pages
     *
     * @since   0.3.1
     */
    default int getPageCount() {
        int items_per_page = this.getMaxItemsPerPage();
        int size = this.getPageableItems().size();

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
        int start = (page - 1) * items_per_page;
        int end = Math.min(start + items_per_page, pageable_items.size());

        return pageable_items.subList(start, end);
    }

    /**
     * Iterate over all the items on the current page
     *
     * @since   0.3.1
     */
    default void forEachItemsOnCurrentPage(PageableItemAdder<T> adder) {
        List<T> items = this.getPageableItemsForPage(this.getPage());

        int items_on_this_page = items.size();

        for (int i = 0; i < items_on_this_page; i++) {
            adder.add(items.get(i), i, items_on_this_page);
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
}
