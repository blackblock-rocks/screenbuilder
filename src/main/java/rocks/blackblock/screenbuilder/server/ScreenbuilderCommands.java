package rocks.blackblock.screenbuilder.server;

import com.mojang.brigadier.Command;
import io.github.theepicblock.polymc.impl.misc.logging.CommandSourceLogger;
import io.github.theepicblock.polymc.impl.misc.logging.SimpleLogger;
import java.util.Collection;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import rocks.blackblock.screenbuilder.ScreenBuilder;

import static net.minecraft.server.command.CommandManager.literal;

/**
 * Registers the BBSB commands.
 */
public class ScreenbuilderCommands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(literal("bbsb").requires(source -> source.hasPermissionLevel(2))
                    .then(literal("screen")
                            .then(CommandManager.argument("targets", EntityArgumentType.players())

                                .then(CommandManager.argument("title", TextArgumentType.text())
                                        .executes((context -> {
                                            var source = context.getSource();
                                            SimpleLogger commandSource = new CommandSourceLogger(source, true);

                                            var target = EntityArgumentType.getPlayers(context, "targets");

                                            var text = TextArgumentType.getTextArgument(context, "title");

                                            for (var entity : target) {
                                                ScreenBuilder builder = new ScreenBuilder("test");
                                                builder.setDisplayName(text);
                                                entity.openHandledScreen(builder);
                                            }

                                            return Command.SINGLE_SUCCESS;
                                        })))
                            )

                    ));
        });
    }
}