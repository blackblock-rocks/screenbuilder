package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.bv.value.BvElement;
import rocks.blackblock.bib.bv.value.BvList;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.screen.BasescreenFactory;
import rocks.blackblock.screenbuilder.screen.SlotManager;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.PaginationWidget;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Select BvElements based on a tag tree
 *
 * @since   0.5.0
 */
public interface TaxonomyInput<T extends BvElement> extends PageableInput<T> {

    /**
     * Get the pagination widget id
     *
     * @since   0.5.0
     */
    @Override
    default String getPaginationWidgetId() {
        return "taxonomy";
    }

    /**
     * Get the current selected tags
     *
     * @since   0.5.0
     */
    List<BvElement> getActiveTags();

    /**
     * Set the active tags
     *
     * @since   0.5.0
     */
    void setActiveTags(List<BvElement> tags);

    /**
     * Add a tag
     *
     * @since   0.5.0
     */
    default boolean addActiveTag(BvElement tag) {

        var active_tags = this.getActiveTags();

        if (active_tags == null) {
            active_tags = new ArrayList<>();
        } else if (active_tags.contains(tag)) {
            return false;
        }

        active_tags.add(tag);
        this.setActiveTags(active_tags);

        return true;
    }

    /**
     * Pop an active tag
     * (Go up)
     *
     * @since   0.5.0
     */
    default boolean popActiveTag() {

        var active_tags = this.getActiveTags();

        if (active_tags == null || active_tags.isEmpty()) {
            return false;
        }

        active_tags.remove(active_tags.size() - 1);
        this.setActiveTags(active_tags);

        return true;
    }

    /**
     * Add the pagination widget to the given screen builder.
     * If there are not enough items, no widget will be added if `always_add` is false.
     *
     * @since   0.5.0
     */
    @Nullable
    @Override
    default PaginationWidget addPaginationWidget(ScreenBuilder sb, int slot, boolean always_add) {

        var active_tags = this.getActiveTags();
        boolean has_tag_selection = false;

        if (active_tags != null && !active_tags.isEmpty()) {
            always_add = true;
            has_tag_selection = true;
        }

        var result = PageableInput.super.addPaginationWidget(sb, slot, always_add);

        if (has_tag_selection) {
            var pop_button = sb.addButton(slot + 4);
            pop_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
            pop_button.addOverlay(BBSB.ARROW_UP_ICON);
            pop_button.setTitle("Go up one level");

            pop_button.addLeftClickListener((screen, slot1) -> {
                this.popActiveTag();
                screen.rerender();
            });
        }

        return result;
    }

    /**
     * Set the root list of elements
     *
     * @since   0.5.0
     */
    void setRootList(BvList<T> root_list);

    /**
     * Get the root list of elements
     *
     * @since   0.5.0
     */
    BvList<T> getRootList();

    /**
     * Get all the root tags
     *
     * @since   0.5.0
     */
    Set<BvElement> getRootTags();

    /**
     * Get the amount of reserved slots (of the current page)
     *
     * @since   0.5.0
     */
    default int getReservedSlotsForPage(int page) {

        Set<BvElement> selectable_tags = this.getSelectableTags();

        if (selectable_tags == null || selectable_tags.isEmpty()) {
            return 0;
        }

        // How much space is there on this page anyway?
        int max_size_of_page = this.getMaxItemsPerPage();

        // How many tags are there to show?
        int total_amount_of_tags = selectable_tags.size();

        // Calculate all the skipped slots (slots on previous pages)
        int skipped_slots = max_size_of_page * (page - 1);

        // Calculate the amount of tags left to show on this page
        int rest_tags = total_amount_of_tags - skipped_slots;

        if (rest_tags <= 0) {
            return 0;
        }

        return rest_tags;
    }

    /**
     * Get all the tags that can currently be selected.
     * This should be based on the active tags.
     * If there are no active tags, the "root" tags are shown.
     * If there are active tags, it's al the other tags the elements have in common
     *
     * @since   0.5.0
     */
    default Set<BvElement> getSelectableTags() {

        List<BvElement> active_tags = this.getActiveTags();

        var all_tags = this.getRootTags();

        if (active_tags == null || active_tags.isEmpty()) {
            return all_tags;
        }

        Set<BvElement> result = new HashSet<>();

        var matching_elements = this.getPageableItems();

        // Turn this into an almost faceted search:
        // Add all the tags all the other elements have
        for (BvElement entry : matching_elements) {
            Set<BvElement> tags_of_entry = entry.getTags();

            if (tags_of_entry == null || tags_of_entry.isEmpty()) {
                continue;
            }

            for (BvElement tag : tags_of_entry) {
                if (!active_tags.contains(tag)) {
                    result.add(tag);
                }
            }
        }

        return result;
    }

    /**
     * Iterate over all the tags on the current page
     *
     * @since   0.5.0
     */
    default void forEachTagOnCurrentPage(PageableItemAdder<BvElement> adder) {

        Set<BvElement> selectable_tags = this.getSelectableTags();

        if (selectable_tags == null || selectable_tags.isEmpty()) {
            return;
        }

        int page = this.getPage();
        int max_size_of_page = this.getMaxItemsPerPage();
        int total_amount_of_tags = selectable_tags.size();
        int skipped_slots = max_size_of_page * (page - 1);
        int rest_tags = total_amount_of_tags - skipped_slots;

        if (rest_tags <= 0) {
            return;
        }

        List<BvElement> tag_list = selectable_tags.stream().toList();
        int amount_of_tags_on_this_page = Math.min(max_size_of_page, rest_tags);
        int start = skipped_slots;
        int end = start + amount_of_tags_on_this_page;

        for (int i = start; i < end; i++) {
            adder.add(tag_list.get(i), i, total_amount_of_tags);
        }
    }

    /**
     * A simple implementation
     *
     * @since   0.5.0
     */
    class Pager<T extends BvElement> implements TaxonomyInput<T> {

        private BvList<T> root_list;
        private List<BvElement> active_tags = null;
        private NamedScreenHandlerFactory factory = null;
        private Set<BvElement> root_tags = null;
        private int max_items_per_page = 8;
        private int page = 1;
        private String widget_id;
        private BiConsumer<TexturedScreenHandler, T> on_selection = null;

        public Pager(String widget_id, BvList<T> root_list) {
            this.widget_id = widget_id;
            this.setRootList(root_list);
        }

        public void setSelectionHandler(BiConsumer<TexturedScreenHandler, T> on_selection) {
            this.on_selection = on_selection;
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
            this.page = page;
        }

        @Override
        public int getPageValue() {
            return this.page;
        }

        /**
         * Get all the available options
         *
         * @since 0.3.1
         */
        @Override
        public @NotNull List<T> getPageableItems() {

            var root_list = this.getRootList();

            if (root_list == null) {
                return List.of();
            }

            var active_tags = this.getActiveTags();

            if (active_tags == null || active_tags.isEmpty()) {
                return root_list;
            }

            var values = root_list.getTaggedValues(new HashSet<>(active_tags));

            return values;
        }

        @Override
        public void setActiveTags(List<BvElement> tags) {
            this.active_tags = tags;
            this.setPage(1);
        }

        @Override
        public List<BvElement> getActiveTags() {
            return this.active_tags;
        }

        @Override
        public void setRootList(BvList<T> root_list) {
            this.root_list = root_list;

            if (root_list == null) {
                this.root_tags = null;
                return;
            }

            this.root_tags = new HashSet<>();

            // Iterate over all the entries & their tags,
            // and use the ones without a parent
            for (T entry : root_list) {
                Set<BvElement> entry_tags = entry.getTags();

                if (entry_tags == null) {
                    continue;
                }

                for (BvElement tag : entry_tags) {
                    var parents = tag.getTags();

                    if (parents == null || parents.isEmpty()) {
                        this.root_tags.add(tag);
                    }
                }
            }
        }

        @Override
        public BvList<T> getRootList() {
            return this.root_list;
        }

        /**
         * Get all the root tags
         *
         * @since 0.5.0
         */
        @Override
        public Set<BvElement> getRootTags() {
            return this.root_tags;
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

        /**
         * Populate the given ScreenBuilder
         *
         * @since 0.5.0
         */
        public void decorateScreenBuilder(ScreenBuilder sb, SlotManager available_slots, BasescreenFactory factory) {

            // We'll put the pagination and the "up" button on the bottom row
            var bottom_row = available_slots.reserveBottomFreeRow();
            this.addPaginationWidget(sb, bottom_row.get(0));

            // The remaining amount of slots is what can be shown on the page
            this.setMaxItemsPerPage(available_slots.countAvailableSlots());

            // Iterate over all the current selectable tags and add them
            this.forEachTagOnCurrentPage((item, index_on_page, amount_on_this_page) -> {
                int slot_index = available_slots.get(index_on_page);

                var entry_button = sb.addButton(slot_index);

                // Use a medium-type button background
                entry_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.MEDIUM);

                // Show tags on an aque button
                entry_button.setBackgroundColour(Formatting.AQUA);

                // Decorate the button some more
                var item_icon = item.getItemIcon();

                if (item_icon == null) {
                    entry_button.addOverlay(BBSB.FOLDER_ICON);
                } else {
                    entry_button.setStack(item.getItemIcon());
                }

                entry_button.setTitle(item.getDisplayTitle());
                entry_button.setLore(item.getDisplayDescription());

                entry_button.addAllClicksListener((screen, slot) -> {
                    this.addActiveTag(item);

                    if (factory != null) {
                        factory.rerender();
                    }
                });
            });

            // Now iterate over the actual items to show on this page
            this.forEachItemsOnCurrentPage((item, index_on_page, amount_on_this_page) -> {

                int slot_index = available_slots.get(index_on_page);

                var entry_button = sb.addButton(slot_index);
                entry_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.MEDIUM);

                // Decorate the button some more
                entry_button.setStack(item.getItemIcon());
                entry_button.setTitle(item.getDisplayTitle());
                entry_button.setLore(item.getDisplayDescription());

                entry_button.addAllClicksListener((screen, slot) -> {
                    if (this.on_selection != null) {
                        this.on_selection.accept(screen, item);
                    }
                });
            });
        }
    }
}
