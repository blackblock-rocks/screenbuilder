package rocks.blackblock.screenbuilder.server;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import rocks.blackblock.screenbuilder.ScreenBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * Registers the BBSB commands.
 */
public class ScreenbuilderCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
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
                                            StringReader reader = new StringReader(json_string);
                                            MutableText text = Text.Serializer.fromJson(reader);

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

                                .then(CommandManager.argument("title", TextArgumentType.text())
                                        .executes((context -> {
                                            var source = context.getSource();

                                            var target = EntityArgumentType.getPlayers(context, "targets");

                                            var text = TextArgumentType.getTextArgument(context, "title");

                                            for (var entity : target) {
                                                ScreenBuilder builder = new ScreenBuilder("test");

                                                builder.setDisplayName(text);
                                                builder.setShowPlayerInventory(false);
                                                builder.setShowPlayerHotbar(false);

                                                entity.openHandledScreen(builder);
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })))
                            )

                    ));
        });
    }
}
