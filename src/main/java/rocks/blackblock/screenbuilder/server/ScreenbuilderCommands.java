package rocks.blackblock.screenbuilder.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rocks.blackblock.bib.bv.value.BvLootTableSet;
import rocks.blackblock.bib.command.CommandCreator;
import rocks.blackblock.bib.command.CommandLeaf;
import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.bib.util.BibLoot;
import rocks.blackblock.bib.util.BibText;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.inputs.*;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.LineHeightFontCollection;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.widgets.MirrorWidget;
import rocks.blackblock.screenbuilder.widgets.NumberPicker;
import rocks.blackblock.screenbuilder.widgets.Widget;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registers the BBSB commands.
 */
public class ScreenbuilderCommands {

    public static void registerCommands() {

        CommandLeaf bbsb = CommandCreator.getPermissionRoot("bbsb", "blackblock.mod");

        addJsonTestCommand(bbsb);
        addScreenTestCommand(bbsb);
        addBookletTestCommand(bbsb);
        addFontTestCommand(bbsb);
        addDebugTestCommand(bbsb);
        addFilesTestCommand(bbsb);
    }

    private static void addFilesTestCommand(CommandLeaf bbsb) {

        var files = bbsb.getChild("files");

        files.onExecute(context -> {
            var source = context.getSource();

            FileInput input = new FileInput();
            input.setStart(FabricLoader.getInstance().getGameDir());
            input.setDisplayName(Text.literal("File Browser"));
            input.allowDirectoryCration(true);

            source.getPlayer().openHandledScreen(input);

            return Command.SINGLE_SUCCESS;
        });
    }

    private static void addDebugTestCommand(CommandLeaf bbsb) {

        var debug = bbsb.getChild("debug");

        debug.onExecute(context -> {
            var source = context.getSource();
            BBSB.DEBUG = !BBSB.DEBUG;
            source.sendFeedback(() -> Text.literal("BBSB debug mode is now " + BBSB.DEBUG), false);

            return Command.SINGLE_SUCCESS;
        });

        var tab = debug.getChild("tab-test");
        var horizontal = tab.getChild("horizontal");

        horizontal.onExecute(context -> {
            TabTestInput empty = new TabTestInput(true);
            context.getSource().getPlayer().openHandledScreen(empty);

            return Command.SINGLE_SUCCESS;
        });

        var vertical = tab.getChild("vertical");

        vertical.onExecute(context -> {

            TabTestInput empty = new TabTestInput(false);
            context.getSource().getPlayer().openHandledScreen(empty);

            return Command.SINGLE_SUCCESS;
        });
    }

    /**
     * A test input that uses tabs
     */
    private static class TabTestInput extends EmptyInput implements TabbedInput, WidgetDataProvider {

        private Tab active = null;
        private List<Tab> all_tabs = new ArrayList<>();
        private boolean horizontal;
        private PageableInput.Pager<String> string_pager = new PageableInput.Pager<>("string_pager");
        private ItemStack mirror_stack = null;
        private MirrorWidget mirror_widget = new MirrorWidget();
        private TaxonomyInput.Pager<BvLootTableSet> loot_pager = new TaxonomyInput.Pager<>("loot_pager", BibLoot.LOOT_TABLES);

        /**
         * Initialize the instance
         */
        public TabTestInput(boolean horizontal) {
            this.horizontal = horizontal;

            this.mirror_widget.setId("mirror_widget");

            List<String> values = new ArrayList<>(30);

            for (int i = 0; i < 30; i++) {
                values.add("Entry: " + i);
            }

            this.string_pager.setPageableItems(values);
            this.string_pager.setScreenHandlerFactory(this);
            this.loot_pager.setScreenHandlerFactory(this);

            all_tabs.add(Tab.of("First (String pager)", BBSB.PENCIL_ICON, (sb, available_slots) -> {
                BibLog.log("Adding first contents");
                var button = sb.addButton(available_slots.get(0));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab one");

                var rows = available_slots.getAvailableRows();
                var last_row = rows.get(rows.size() - 1);

                this.string_pager.addPaginationWidget(sb, last_row.get(0));

                this.string_pager.forEachItemsOnCurrentPage((item, index_on_page, amount_on_this_page) -> {
                    var entry_button = sb.addButton(available_slots.get(index_on_page));
                    entry_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.MEDIUM);
                    entry_button.setTitle(item);
                });

            }));

            all_tabs.add(Tab.of("Second (Mirror Widget)", BBSB.CHECK_ICON, (sb, available_slots) -> {
                BibLog.log("Adding second contents");
                var button = sb.addButton(available_slots.get(1));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab two");

                this.mirror_widget.setSlotIndex(available_slots.get(3));
                sb.addWidget(this.mirror_widget);

            }));

            all_tabs.add(Tab.of("Third (Tags)", BBSB.CITY_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(2));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab three");

                var bottom_row = available_slots.reserveBottomFreeRow();

                this.loot_pager.addPaginationWidget(sb, bottom_row.get(0));
                this.loot_pager.setMaxItemsPerPage(available_slots.countAvailableSlots());

                this.loot_pager.forEachTagOnCurrentPage((item, index_on_page, amount_on_this_page) -> {
                    int slot_index = available_slots.get(index_on_page);

                    var entry_button = sb.addButton(slot_index);
                    entry_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.MEDIUM);
                    entry_button.setBackgroundColour(Formatting.AQUA);
                    entry_button.setStack(item.getItemIcon());
                    entry_button.setTitle("TAG: " + item.getDisplayTitle());

                    entry_button.addLeftClickListener((screen, slot) -> {
                        this.loot_pager.addActiveTag(item);
                        this.rerender();
                    });
                });

                this.loot_pager.forEachItemsOnCurrentPage((item, index_on_page, amount_on_this_page) -> {

                    int slot_index = available_slots.get(index_on_page);

                    var entry_button = sb.addButton(slot_index);
                    entry_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.MEDIUM);
                    entry_button.setStack(item.getItemIcon());
                    entry_button.setTitle("Entry: " + item.getDisplayTitle());

                    entry_button.setLore(item.getLore());
                });
            }));

            all_tabs.add(Tab.of("Fourth", BBSB.ASTERISK_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(3));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab four");
            }));

            all_tabs.add(Tab.of("Fifth", BBSB.CUBE_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(4));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab five");
            }));

            all_tabs.add(Tab.of("Sixth", BBSB.CLOUD_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(5));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab six");
            }));

            all_tabs.add(Tab.of("Seventh", BBSB.DIAMOND_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(6));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab seven");
            }));

            all_tabs.add(Tab.of("Eighth", BBSB.COG_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(7));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab eight");
            }));

            all_tabs.add(Tab.of("Ninth", BBSB.FOLDER_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(8));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab nine");
            }));

            all_tabs.add(Tab.of("Tenth", BBSB.INGOT_ICON, (sb, available_slots) -> {
                var button = sb.addButton(available_slots.get(9));
                button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
                button.setTitle("Button on tab ten");
            }));
        }

        @Override
        public <T> T getWidgetValue(Widget<T> widget) {

            if (widget.getId().equals("string_pager")) {
                return (T) (Integer) this.string_pager.getPage();
            }

            if (widget == this.mirror_widget) {
                return (T) this.mirror_stack;
            }

            if (widget.getId().equals(this.loot_pager.getPaginationWidgetId())) {
                return (T) (Integer) this.loot_pager.getPage();
            }

            return null;
        }

        @Override
        public <T> void setWidgetValue(Widget<T> widget, T value) {
            if (widget.getId().equals("string_pager")) {
                this.string_pager.setPage((int) value);
            }

            if (widget == this.mirror_widget) {
                this.mirror_stack = (ItemStack) value;
            }

            if (widget.getId().equals(this.loot_pager.getPaginationWidgetId())) {
                this.loot_pager.setPage((int) value);
            }
        }

        @Override
        public List<Tab> getAllTabs() {
            return this.all_tabs;
        }

        private int tab_scroll_index = 0;

        @Override
        public void setTabScrollIndex(Integer index) {
            this.tab_scroll_index = index;
        }

        @Override
        public Integer getTabScrollIndex() {
            return this.tab_scroll_index;
        }

        @Override
        public ScreenBuilder getScreenBuilder() {
            ScreenBuilder sb = this.createBasicScreenBuilder("empty_screen");
            this.printErrors(sb);
            sb.setCloneSlots(false);
            this.addTabsToScreenBuilder(sb, this.horizontal);
            return sb;
        }

        @Override
        public void setActiveTab(Tab tab) {
            this.active = tab;
        }

        @Override
        public Tab getActiveTab() {
            return this.active;
        }
    }

    private static void addFontTestCommand(CommandLeaf bbsb) {

        var font_leaf = bbsb.getChild("font");
        var targets_leaf = font_leaf.getChild("targets");
        targets_leaf.setType(EntityArgumentType.players());

        var name_leaf = targets_leaf.getChild("name");
        name_leaf.setType(StringArgumentType.greedyString());

        name_leaf.onExecute(context -> {
            var source = context.getSource();

            var target = EntityArgumentType.getPlayers(context, "targets");

            String name = StringArgumentType.getString(context, "name");

            for (var entity : target) {
                try {

                    TestBuilder builder = new TestBuilder("test");

                    builder.setDisplayName(new MiniText(name));
                    builder.setShowPlayerInventory(false);
                    builder.setShowPlayerHotbar(false);

                    LineHeightFontCollection font;

                    if (name.equalsIgnoreCase("lh09")) {
                        font = Font.LH09;
                    } else if (name.equalsIgnoreCase("lh04") || name.equalsIgnoreCase("lh01")) {
                        font = Font.LH01;
                    } else if (name.equalsIgnoreCase("lh11")) {
                        font = Font.LH11;
                    } else {
                        font = Font.ABSOLUTE_DEFAULT_COLLECTION;
                    }

                    BBSB.log("Using font collection", font);

                    if (font == Font.ABSOLUTE_DEFAULT_COLLECTION) {
                        for (int i = -15; i < 29; i++) {
                            int y = i * 8;
                            int line = font.convertYToLine(y);

                            builder.addFontString(i, "|^_^| Index " + i + " @ Y " + y + " (line " + line + ")", font.getFontForLine(line));
                        }
                    } else {
                        for (int i = -10; i < 20; i++) {
                            builder.addFontString(i, "|^_^| Line " + i, font.getFontForLine(i));
                        }
                    }


                    builder.addError("              Error line 1");
                    builder.addError("              Error line 2");

                    entity.openHandledScreen(builder);
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    private static void addBookletTestCommand(CommandLeaf bbsb) {

        var booklet_leaf = bbsb.getChild("booklet");
        var targets_leaf = booklet_leaf.getChild("targets");
        targets_leaf.setType(EntityArgumentType.players());

        var content_leaf = targets_leaf.getChild("content");
        content_leaf.setType(StringArgumentType.greedyString());

        content_leaf.onExecute(context -> {
            var source = context.getSource();
            var target = EntityArgumentType.getPlayers(context, "targets");

            for (var entity : target) {
                try {
                    BookletInput input = new BookletInput();
                    input.printLine(StringArgumentType.getString(context, "content"));
                    input.printLine("Extra 1");
                    input.printLine("Extra 2");
                    input.printLine("Extra 3");
                    entity.openHandledScreen(input);
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return 1;
        });
    }

    private static void addScreenTestCommand(CommandLeaf bbsb) {

        var screen_leaf = bbsb.getChild("screen");
        var targets_leaf = screen_leaf.getChild("targets");
        targets_leaf.setType(EntityArgumentType.players());

        var title_leaf = targets_leaf.getChild("title");

        // @TODO: Make TextArgumentType work (without registry access)
        //title_leaf.setType(TextArgumentType.text());

        title_leaf.onExecute(context -> {
            var source = context.getSource();

            var target = EntityArgumentType.getPlayers(context, "targets");

            var text = TextArgumentType.getTextArgument(context, "title");

            for (var entity : target) {
                try {

                    TestBuilder builder = new TestBuilder("test");

                    builder.setDisplayName(text);
                    builder.setShowPlayerInventory(false);
                    builder.setShowPlayerHotbar(false);

                    NumberPicker numberPicker = new NumberPicker();
                    numberPicker.setId("picker");
                    builder.addWidget(numberPicker);

                    NumberPicker numberPicker_two = new NumberPicker();
                    numberPicker_two.setId("picker_two");
                    numberPicker_two.setSlotIndex(30);
                    builder.addWidget(numberPicker_two);

                    entity.openHandledScreen(builder);
                } catch (Exception e) {
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    private static void addJsonTestCommand(CommandLeaf bbsb) {

        var json_leaf = bbsb.getChild("json");
        var targets_leaf = json_leaf.getChild("targets");
        targets_leaf.setType(EntityArgumentType.players());

        var filename_leaf = targets_leaf.getChild("filename");
        filename_leaf.setType(StringArgumentType.string());

        filename_leaf.onExecute(context -> {
            var source = context.getSource();

            var target = EntityArgumentType.getPlayers(context, "targets");

            String path_to_json = StringArgumentType.getString(context, "filename");

            for (var entity : target) {
                ScreenBuilder builder = new ScreenBuilder("test_json");

                // Open the json file
                Path json_file_path = Paths.get(path_to_json);

                String json_string;

                // Read the contents of the json_file_path
                try {
                    json_string = Files.readString(json_file_path);
                } catch (Exception e) {
                    json_string = "";
                }

                // Read in the json file in a string reader
                MutableText text = BibText.deserializeFromJson(json_string);

                BBSB.log("Using text: " + text);
                BBSB.log(" -- json:", BibText.serializeToJson(text));

                builder.setDisplayName(text);
                builder.setShowPlayerInventory(false);
                builder.setShowPlayerHotbar(false);

                entity.openHandledScreen(builder);
            }

            return Command.SINGLE_SUCCESS;
        });
    }

    private static class TestBuilder extends ScreenBuilder implements WidgetDataProvider {

        private Map<String, Object> values = new HashMap<>();
        private Map<Integer, FontString> font_strings = new HashMap<>();

        public TestBuilder(String name) {
            super(name);
        }

        @Override
        public Object getWidgetValue(Widget widget) {
            return values.get(widget.getId());
        }

        @Override
        public void setWidgetValue(Widget widget, Object value) {
            values.put(widget.getId(), value);
        }

        public void addFontString(int id, String string, Font font) {
            FontString entry = new FontString(string, font);
            this.addFontString(id, entry);
        }

        public void addFontString(int id, FontString font_string) {
            font_strings.put(id, font_string);
        }

        @Override
        public void addToTextBuilder(TextBuilder builder) {
            super.addToTextBuilder(builder);

            for (Integer index : font_strings.keySet()) {
                FontString entry = font_strings.get(index);

                builder.setCursor(-65);
                builder.print(entry.text(), entry.font());
            }
        }
    }

    private static record FontString(String text, Font font) {

    }
}
