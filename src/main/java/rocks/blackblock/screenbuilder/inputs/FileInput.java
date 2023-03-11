package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.SlotEventListener;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.PaginationWidget;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File selector input
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.3.0
 */
public class FileInput extends EmptyInput implements WidgetDataProvider {

    // The value listener
    protected OnAcceptPathListener on_select_value_listener = null;

    // The page we're on
    protected int page = 1;

    // The root directory (maximum allowed)
    protected Path root = null;

    // The start directory
    protected Path start = null;

    // The curent directory
    protected Path current = null;

    // The current selected path
    protected Path selected = null;

    // All the current options
    protected List<PathEntry> current_options = new ArrayList<>();

    // Map the slots to the entries
    private Map<Integer, PathEntry> slot_map = new HashMap<>();

    // Show files?
    protected boolean show_files = true;

    // Show directories?
    protected boolean show_directories = true;

    // Allow directory creation
    protected boolean allow_directory_creation = false;

    /**
     * Set the select listener with a value
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public void setSelectListener(OnAcceptPathListener listener) {
        this.on_select_value_listener = listener;
    }

    /**
     * Set file visibility
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void showFiles(boolean value) {
        this.show_files = value;
    }

    /**
     * Set directory visibility
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void showDirectories(boolean value) {
        this.show_directories = value;
    }

    /**
     * Allow directory creation
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     */
    public void allowDirectoryCration(boolean value) {
        this.allow_directory_creation = value;
    }


    /**
     * Set the root directory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public void setRoot(Path root) {
        this.root = root;
    }

    /**
     * Set the start directory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public void setStart(Path start) {
        this.start = start;

        if (this.root == null) {
            this.setRoot(start);
        }

        if (this.current == null) {
            this.setCurrent(start);
        }
    }

    /**
     * Set the current directory
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public void setCurrent(Path current_path) {

        if (this.start == null) {
            this.setStart(current_path);
        }

        // If this is not part of the root path,
        // ignore it
        if (!current_path.startsWith(this.root)) {
            return;
        }

        this.current = current_path;
        this.selected = null;

        this.slot_map.clear();

        // Update the current options
        this.current_options = this.getCurrentFiles();
    }

    /**
     * Set the selected path
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.0
     */
    public void setSelected(Path selected_path) {
        this.selected = selected_path;
    }

    /**
     * Get the current files
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public List<PathEntry> getCurrentFiles() {

        List<PathEntry> result = new ArrayList<>();

        if (this.current == null) {
            return result;
        }

        // If the current directory is not the root directory,
        // add the parent directory
        if (!this.current.equals(this.root)) {
            Path parent = this.current.getParent();
            PathEntry parent_entry = new PathEntry(parent);
            parent_entry.is_parent = true;
            result.add(parent_entry);
        }

        File[] files = this.current.toFile().listFiles();

        if (files == null) {
            return result;
        }

        for (File file : files) {
            PathEntry entry = new PathEntry(file.toPath());
            result.add(entry);
        }

        // Sort the files alphabetically:
        // First comes the parent directory,
        // then the directories,
        // then the files
        result.sort((a, b) -> {
            if (a.is_parent) {
                return -1;
            } else if (b.is_parent) {
                return 1;
            } else if (a.is_directory && !b.is_directory) {
                return -1;
            } else if (!a.is_directory && b.is_directory) {
                return 1;
            } else {
                return a.name.compareTo(b.name);
            }
        });

        return result;
    }

    /**
     * Get a screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        FileInput that = this;

        ScreenBuilder sb = new ScreenBuilder("file_widget");
        sb.setNamespace(BBSB.NAMESPACE);
        sb.setFontTexture(BBSB.TOP_FOUR);
        sb.loadTextureItem();
        sb.setCloneSlots(false);

        int slots_per_page = 36;
        int page = this.page;
        int item_count = 0;

        if (this.current_options != null) {
            item_count = this.current_options.size();
        }

        int max_page_value = (int) Math.ceil(item_count / (double) slots_per_page);
        int start = (page - 1) * slots_per_page;
        int end = Math.min(start + slots_per_page, item_count);

        if (page > max_page_value) {
            page = max_page_value;
            this.page = page;
        }

        this.slot_map.clear();

        SlotEventListener slot_listener = (screen, slot) -> {

            NamedScreenHandlerFactory factory = screen.getOriginFactory();

            if (factory instanceof FileInput input) {

                PathEntry entry = input.getEntryBySlotIndex(slot.getScreenIndex());

                if (entry != null) {
                    if (entry.is_directory) {
                        this.setCurrent(entry.path);
                    } else {
                        this.setSelected(entry.path);
                    }
                }
            }

            screen.replaceScreen(this);
        };

        if (item_count > 0) {
            List<PathEntry> items = this.current_options.subList(start, end);

            for (int i = 0; i < items.size(); i++) {
                PathEntry entry = items.get(i);
                ButtonWidgetSlot button = sb.addButton(i);
                button.setTitle(entry.name);

                this.slot_map.put(i, entry);

                if (entry.is_directory) {
                    button.addOverlay(BBSB.FOLDER_ICON.getColoured(TextColor.fromRgb(0xdca100)));

                    if (entry.is_parent) {
                        button.addOverlay(BBSB.ARROW_UP_ICON);
                    }
                } else {
                    button.addOverlay(BBSB.FILE_ICON.getColoured(TextColor.fromRgb(0x00ffff)));
                }

                if (entry.path.equals(this.selected)) {
                    button.addOverlay(BBSB.DOTTED_LINE_ICON.getColoured(TextColor.fromRgb(0x00c710)));
                }

                button.addLeftClickListener(slot_listener);
            }

            if (item_count > 36) {
                PaginationWidget pagination = new PaginationWidget();
                pagination.setId("pagination");
                pagination.setSlotIndex(45);
                pagination.setMaxValue(max_page_value);

                int current_page = page;
                pagination.setOnChangeListener((texturedScreenHandler, widget) -> {

                    if (current_page == this.page) {
                        return;
                    }

                    texturedScreenHandler.replaceScreen(this);
                });

                sb.addWidget(pagination);
            }
        }

        if (this.allow_directory_creation) {
            this.addDirectoryCreateButton(sb, 52);
        }

        if (this.on_select_value_listener != null) {
            // Add the accept button
            this.addAcceptButton(sb, 53);
        }

        return sb;
    }

    /**
     * Add a directory-create button
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public void addDirectoryCreateButton(ScreenBuilder sb, int slot_index) {

        ButtonWidgetSlot directory_button = sb.addButton(slot_index);
        directory_button.setTitle("Create directory");
        directory_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);
        directory_button.addOverlay(BBSB.FOLDER_ICON.getColoured(TextColor.fromRgb(0xdca100)));

        directory_button.addLeftClickListener((screen, slot) -> {

            StringInput string_input = new StringInput();
            string_input.setDisplayName("Create directory");

            string_input.setRenamedListener((text_input, value) -> {
                Path new_path = this.current.resolve(value);

                try {
                    Files.createDirectory(new_path);
                } catch (IOException e) {
                    BBSB.log("Failed to create directory: ", new_path);
                    return;
                }

                this.setCurrent(new_path);
                screen.replaceScreen(this);
            });

            screen.pushScreen(string_input);
        });
    }

    /**
     * Add an accept button
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.0
     */
    public void addAcceptButton(ScreenBuilder sb, int slot_index) {
        ButtonWidgetSlot accept_button = sb.addButton(slot_index);
        accept_button.setTitle("Accept");
        accept_button.setBackgroundType(ButtonWidgetSlot.BackgroundType.SMALL);

        if (this.selected != null) {
            accept_button.addOverlay(BBSB.CHECK_ICON.getColoured(TextColor.fromRgb(0x00c710)));
            accept_button.setLore(this.selected.getFileName().toString());

            accept_button.addLeftClickListener((screen, slot) -> {
                if (this.on_select_value_listener != null) {
                    this.on_select_value_listener.onSelect(screen, this.selected);
                }
            });
        } else {
            accept_button.addOverlay(BBSB.CHECK_ICON.getColoured(TextColor.fromRgb(0x818181)));
        }
    }

    /**
     * Get the value of the given widget
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public Object getWidgetValue(String widget_id) {

        if (widget_id.equals("pagination")) {
            return this.page;
        }

        return null;
    }

    /**
     * Set a widget value
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    @Override
    public void setWidgetValue(String widget_id, Object value) {

        if (widget_id.equals("pagination")) {
            this.page = (int) value;
        }
    }

    /**
     * Get the value of the given stack
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public PathEntry getEntryBySlotIndex(int slot_index) {
        PathEntry entry = this.slot_map.get(slot_index);
        return entry;
    }

    /**
     * Path entry class
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public static class PathEntry {

        public Path path;
        public String name;
        public boolean is_parent = false;
        public boolean is_directory = false;

        public PathEntry(Path path) {
            this.path = path;

            // Check if this is a directory
            this.is_directory = Files.isDirectory(path);

            // Get the name of the file from the path
            this.name = path.getFileName().toString();
        }

        public String toString() {
            return "PathEntry{" + path + "}";
        }
    }

    /**
     * Accept path interface
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.3.0
     */
    public interface OnAcceptPathListener {
        void onSelect(TexturedScreenHandler screen, Path path);
    }
}
