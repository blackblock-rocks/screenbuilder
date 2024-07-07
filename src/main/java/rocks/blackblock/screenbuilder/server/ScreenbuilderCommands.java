package rocks.blackblock.screenbuilder.server;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import rocks.blackblock.bib.util.BibText;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.inputs.BookletInput;
import rocks.blackblock.screenbuilder.inputs.FileInput;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.text.LineHeightFontCollection;
import rocks.blackblock.screenbuilder.text.MiniText;
import rocks.blackblock.screenbuilder.text.TextBuilder;
import rocks.blackblock.screenbuilder.widgets.NumberPicker;
import rocks.blackblock.screenbuilder.widgets.Widget;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * Registers the BBSB commands.
 */
public class ScreenbuilderCommands {
    public static void registerCommands() {

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("bbsb").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("json")
                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                .then(CommandManager.argument("filename", StringArgumentType.string())
                                    .executes(context -> {
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
                                    })))
                    )
                    .then(literal("screen")
                            .then(CommandManager.argument("targets", EntityArgumentType.players())

                                .then(CommandManager.argument("title", TextArgumentType.text(registryAccess))
                                        .executes((context -> {
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
                                        })))
                            )

                    )
                    .then(literal("booklet")
                            .then(CommandManager.argument("targets", EntityArgumentType.players())
                                    .then(CommandManager.argument("content", StringArgumentType.greedyString())
                                            .executes((context -> {
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
                                            }))
                                    )
                            )
                    )
                    .then(literal("font")
                            .then(CommandManager.argument("targets", EntityArgumentType.players())

                                    .then(CommandManager.argument("name", StringArgumentType.greedyString())
                                            .executes((context -> {
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
                                            }))
                                    )
                            )
                    )
                    .then(literal("debug")
                        .executes((context -> {
                            var source = context.getSource();
                            BBSB.DEBUG = !BBSB.DEBUG;
                            source.sendFeedback(() -> Text.literal("BBSB debug mode is now " + BBSB.DEBUG), false);

                            return Command.SINGLE_SUCCESS;
                        }))
                    )
                    .then(literal("files")
                            .executes((context -> {
                                var source = context.getSource();

                                FileInput input = new FileInput();
                                input.setStart(FabricLoader.getInstance().getGameDir());
                                input.setDisplayName(Text.literal("File Browser"));
                                input.allowDirectoryCration(true);

                                source.getPlayer().openHandledScreen(input);

                                return Command.SINGLE_SUCCESS;
                            }))
                    )
            );
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
