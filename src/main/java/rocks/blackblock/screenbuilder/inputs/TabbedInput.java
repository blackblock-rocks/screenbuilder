package rocks.blackblock.screenbuilder.inputs;

import com.google.common.collect.ImmutableList;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.checkerframework.checker.mustcall.qual.MustCall;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.bib.util.BibText;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.screen.SlotManager;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.textures.IconTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Make an interface have tabs
 *
 * @author  Jelle De Loecker <jelle@elevenways.be>
 * @author  Jade Godwin <icanhasabanana@gmail.com>
 * @since   0.5.0
 */
@MustCall("addTabsToScreenBuilder")
@SuppressWarnings("unused")
public interface TabbedInput {

    List<Integer> HORIZONTAL_AVAILABLE_SLOTS = ImmutableList.copyOf(createAvailableSlotIndexList(true));
    List<Integer> VERTICAL_AVAILABLE_SLOTS = ImmutableList.copyOf(createAvailableSlotIndexList(false));

    /**
     * Create an integer list of available slits
     *
     * @since   0.5.0
     */
    static List<Integer> createAvailableSlotIndexList(boolean horizontal) {

        List<Integer> result;

        if (horizontal) {
            result = new ArrayList<>(45);

            for (int i = 9; i < 54; i++) {
                result.add(i);
            }
        } else {
            result = new ArrayList<>(48);

            for (int i = 0; i < 54; i++) {
                if (i % 9 == 0) continue;
                result.add(i);
            }
        }

        return result;
    }

    /**
     * Get a list of all tabs to show
     *
     * @since   0.5.0
     */
    List<Tab> getAllTabs();

    /**
     * Get the default tab
     *
     * @since   0.5.0
     */
    @Nullable
    default Tab getDefaultTab() {
        var tabs = this.getAllTabs();

        if (tabs != null) {
            return tabs.get(0);
        }

        return null;
    }

    /**
     * Set the current tab scroll index
     *
     * @since   0.5.0
     */
    void setTabScrollIndex(Integer index);

    /**
     * Get the current tab scroll index
     *
     * @since   0.5.0
     */
    Integer getTabScrollIndex();

    /**
     * Set the current active tab
     *
     * @since   0.5.0
     */
    void setActiveTab(Tab tab);

    /**
     * Get the current active tab
     *
     * @since   0.5.0
     */
    Tab getActiveTab();

    /**
     * Add the tab configuration to the given screenbuilder
     *
     * @since   0.5.0
     */
    default void addTabsToScreenBuilder(ScreenBuilder sb, boolean horizontal) {
        int amount_to_add = horizontal ? 1 : 9;
        int max_tabs = horizontal ? 9 : 6;
        int visible_tabs = max_tabs - 1; // One less to accommodate the "next" button when needed
        var available_slots = horizontal ? HORIZONTAL_AVAILABLE_SLOTS : VERTICAL_AVAILABLE_SLOTS;
        var slot_manager = new SlotManager(9, 6);
        slot_manager.setAvailableSlots(available_slots);

        var tabs = this.getAllTabs();
        int tab_count = tabs.size();
        var active_tab = this.getActiveTab();
        if (active_tab == null) {
            active_tab = this.getDefaultTab();
        }

        boolean show_next_button = tab_count > max_tabs;
        int scroll_index = this.getTabScrollIndex();
        int tabs_to_show = Math.min(show_next_button ? visible_tabs : max_tabs, tab_count - scroll_index);

        for (int i = 0; i < tabs_to_show; i++) {
            int tab_index = scroll_index + i;
            var tab = tabs.get(tab_index);
            int button_index = i * amount_to_add;

            ButtonWidgetSlot tab_button = sb.addButton(button_index);
            tab_button.setTitle(tab.asString());

            var description = tab.getTabDescription();
            if (description != null && !description.isEmpty()) {
                tab_button.setLore(description);
            }

            if (tab == active_tab) {
                if (horizontal) {
                    tab_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.TOP_TAB_SELECTED);
                } else {
                    tab_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.LEFT_TAB_SELECTED);
                }
            } else {
                if (horizontal) {
                    tab_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.TOP_TAB_UNSELECTED);
                } else {
                    tab_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.LEFT_TAB_UNSELECTED);
                }
            }

            var icon = tab.getTabIcon();
            if (icon != null) {
                tab_button.addOverlay(icon);
            }

            var lore_list = tab.getTabDescription();

            if (lore_list != null) {
                tab_button.setLore(lore_list);
            }

            SlotEventListener listener = (screen, slot) -> {
                this.setActiveTab(tab);
                screen.replaceScreen(screen.getOriginFactory());
            };
            tab_button.addLeftClickListener(listener);
            tab_button.addRightClickListener(listener);
            tab_button.addMiddleClickListener(listener);
        }

        if (show_next_button) {
            int next_button_index = visible_tabs * amount_to_add;
            ButtonWidgetSlot next_button = sb.addButton(next_button_index);
            next_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
            next_button.setBackground(horizontal ? BBSB.ARROW_RIGHT_ICON : BBSB.ARROW_DOWN_ICON);
            next_button.setTitle("Show next tabs »");

            SlotEventListener onNext = (screen, slot) -> {
                int next_index = scroll_index + 1;
                if (next_index > tab_count - visible_tabs) {
                    next_index = 0;
                }
                this.setTabScrollIndex(next_index);
                screen.replaceScreen(screen.getOriginFactory());
            };
            next_button.addLeftClickListener(onNext);
            next_button.addRightClickListener(onNext);
            next_button.addMiddleClickListener(onNext);
        }

        if (active_tab != null) {
            active_tab.getTabDecorator().decorateScreenBuilder(sb, slot_manager);
        }
    }

    /**
     * Interface so something can be used as a tab
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.5.0
     */
    interface Tab extends StringIdentifiable, BibLog.Argable {

        /**
         * Create a simple tab
         *
         * @since   0.5.0
         */
        static Tab of(String name, IconTexture icon, Decorator decorator) {

            var tab = new TabImpl(decorator);
            tab.setName(name);
            tab.setTabIcon(icon);

            return tab;
        }

        /**
         * Get the decorator to use for this tab
         *
         * @since   0.5.0
         */
        Decorator getTabDecorator();

        /**
         * Get the icon to use for this tab
         *
         * @since   0.5.0
         */
        IconTexture getTabIcon();

        /**
         * Get the tab title
         *
         * @since   0.5.0
         */
        default Text getTabTitleText() {
            return Text.literal(this.asString());
        }

        /**
         * Set the tab description (lore)
         *
         * @since   0.5.0
         */
        default void setTabDescription(BibText.Lore lore) {
            this.setTabDescription(lore.getLines());
        }

        /**
         * Set the tab description (lore)
         *
         * @since   0.5.0
         */
        default void setTabDescription(String description) {
            this.setTabDescription(BibText.createLore(description));
        }

        /**
         * Set the tab description (lore)
         *
         * @since   0.5.0
         */
        void setTabDescription(List<Text> description);

        /**
         * Get the tab description (lore)
         *
         * @since   0.5.0
         */
        default List<Text> getTabDescription() {
            return null;
        }
    }

    /**
     * Represents a single tab
     *
     * @since   0.5.0
     */
    class TabImpl implements Tab {
        private final Decorator decorator;
        private String name;
        private IconTexture icon = null;
        private List<Text> description = null;

        public TabImpl(Decorator decorator) {
            this.decorator = decorator;
        }

        public Decorator getTabDecorator() {
            return this.decorator;
        }

        public IconTexture getTabIcon() {
            return this.icon;
        }

        public void setTabIcon(IconTexture icon) {
            this.icon = icon;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String asString() {
            return this.name;
        }

        @Override
        public void setTabDescription(List<Text> description) {
            this.description = description;
        }

        @Override
        public List<Text> getTabDescription() {
            return this.description;
        }

        @Override
        public String toString() {
            return this.toBBLogArg().toString();
        }

        @Override
        public BibLog.Arg toBBLogArg() {
            var result = BibLog.createArg(this);

            result.add("name", this.name);
            result.add("icon", this.icon);

            return result;
        }
    }

    /**
     * The functional decorator interface
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.5.0
     */
    interface Decorator {
        void decorateScreenBuilder(ScreenBuilder sb, SlotManager slot_manager);
    }
}
