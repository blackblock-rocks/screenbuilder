package rocks.blackblock.screenbuilder.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.inputs.BooleanInput;
import rocks.blackblock.screenbuilder.inputs.ItemInput;
import rocks.blackblock.screenbuilder.inputs.StringInput;
import rocks.blackblock.screenbuilder.items.GuiItem;
import rocks.blackblock.screenbuilder.slots.SelectSlot;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuiUtils {
    private static final HashMap<String, Item> items = new HashMap();

    public static List<Slot> removePlayerSlots(List<Slot> base) {
        return base.stream().filter(
                (slot) -> !(slot.inventory instanceof PlayerInventory)
        ).collect(Collectors.toList());
    }

    public static void resyncPlayerInventory(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity) {
            resyncPlayerInventory((ServerPlayerEntity)player);
        }
    }

    public static void resyncPlayerInventory(ServerPlayerEntity player) {
        player.currentScreenHandler.syncState();
    }

    /**
     * Register & create all (missing) gui items
     */
    public static void registerGuiItems() {
        GuiItem.get("true");
        GuiItem.get("false");
        GuiItem.get("arrow_left");
        GuiItem.get("checkbox_unchecked");
        GuiItem.get("checkbox_checked");

        SelectSlot.registerScreen();
        StringInput.registerScreen();
        BooleanInput.registerScreen();
        ItemInput.registerScreen();
    }

    /**
     * Get and/or create a gui item
     */
    public static Item getGuiItem(String name) {

        if (items.containsKey(name)) {
            return items.get(name);
        }

        return createGuiItem(name);
    }

    /**
     * Create a GUI item
     */
    public static GuiItem createGuiItem(String name) {

        Identifier identifier = BBSB.id(name);
        GuiItem item = Registry.register(Registry.ITEM, identifier, new GuiItem());

        setGuiItem(name, item);

        return item;
    }

    public static void setGuiItem(String name, Item item) {
        items.put(name, item);
    }

    /**
     * Write some text to a file
     *
     * @param   targetPath   The path to the target file
     * @param   data         The data to write
     *
     * @since   0.1.1
     */
    public static Path writeToPath(Path targetPath, String data) {
        try {

            // Make sure the parent directory exists
            targetPath.toFile().getParentFile().mkdirs();

            // Write the actual file
            return Files.writeString(targetPath, data);
        } catch (IOException e) {
            System.out.println(String.format("Failed to write to '%s'", targetPath.toString()));
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write some data to a file
     *
     * @param   targetPath   The path to the target file
     * @param   data         The data to write
     *
     * @since   0.1.1
     */
    public static Path writeToPath(Path targetPath, byte[] data) {
        try {

            // Make sure the parent directory exists
            targetPath.toFile().getParentFile().mkdirs();

            // Write the actual file
            return Files.write(targetPath, data);
        } catch (IOException e) {
            System.out.println(String.format("Failed to write to '%s'", targetPath.toString()));
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Write a BufferedImagedata to a file
     *
     * @param   targetPath   The path to the target file
     * @param   image        The image to write
     *
     * @since   0.1.1
     */
    public static Path writeToPath(Path targetPath, BufferedImage image) {
        return writeToPath(targetPath, toByteArray(image, "png"));
    }

    /**
     * Find a file in all possible mods
     */
    public static Path findModResourcePath(String namespace, String path) {

        FabricLoader loader = FabricLoader.getInstance();

        // Try the expected mod first
        Optional<ModContainer> modOpt = loader.getModContainer(namespace);

        Path path_in_jar = null;

        if (modOpt.isPresent()) {
            ModContainer mod = modOpt.get();
            path_in_jar = mod.getPath(path);

            System.out.println("Looking in expected mod '" + namespace + "' for '" + path + "'");

            if (Files.exists(path_in_jar)) {
                System.out.println(" -- Found it!");
                return path_in_jar;
            }
        }

        var mods = loader.getAllMods();

        System.out.println("All mods: " + mods.size() + " -- " + mods);

        for (ModContainer mod : mods) {

            path_in_jar = mod.getPath(path);
            System.out.println("Looking in other mod '" + namespace + "' for '" + path + "'");

            if (Files.exists(path_in_jar)) {
                System.out.println(" -- Found it: " + path_in_jar);
                return path_in_jar;
            }
        }

        return null;
    }

    /**
     * Find a file in all possible mods
     */
    public static InputStream findModResource(String namespace, String path) {

        Path path_in_jar = findModResourcePath(namespace, path);

        if (path_in_jar == null) {
            return null;
        }

        try {
            return Files.newInputStream(path_in_jar, StandardOpenOption.READ);
        } catch (IOException e) {
            System.out.println(String.format("Failed to get resource from mod jar '%s' path: '%s'", namespace, path));
        }
        return null;
    }

    /**
     * convert BufferedImage to byte[]
     * @param   bi
     * @param   format
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(BufferedImage bi, String format) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(bi, format, baos);
        } catch (IOException e) {
            System.out.println("Failed to convert BufferedImage to byte[]");
            e.printStackTrace();
            return null;
        }
        byte[] bytes = baos.toByteArray();
        return bytes;
    }

}
