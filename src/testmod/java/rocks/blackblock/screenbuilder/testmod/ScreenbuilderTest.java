package rocks.blackblock.screenbuilder.testmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import rocks.blackblock.screenbuilder.testmod.block.CardboardBoxBlock;
import rocks.blackblock.screenbuilder.testmod.block.entity.CardboardBoxBlockEntity;
import rocks.blackblock.screenbuilder.utils.GuiUtils;

import java.util.Random;

public class ScreenbuilderTest implements ModInitializer {

    public static final String MOD_ID = "testmod";
    public static final Random random = new Random();

    // Cardboard Box Block
    public static final Identifier CARDBOARD_BOX_IDENTIFIER = new Identifier(MOD_ID, "cardboard_box");
    public static final CardboardBoxBlock CARDBOARD_BOX_BLOCK = Registry.register(Registry.BLOCK, CARDBOARD_BOX_IDENTIFIER, new CardboardBoxBlock(FabricBlockSettings.copyOf(Blocks.BARREL).strength(1.0F, 1.0F)));
    public static final BlockItem CARDBOARD_BOX_BLOCK_ITEM = Registry.register(Registry.ITEM, CARDBOARD_BOX_IDENTIFIER, new BlockItem(CARDBOARD_BOX_BLOCK, new Item.Settings().group(ItemGroup.INVENTORY).maxDamage(16)));
    public static final BlockEntityType<CardboardBoxBlockEntity> CARDBOARD_BOX_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, CARDBOARD_BOX_IDENTIFIER, FabricBlockEntityTypeBuilder.create(CardboardBoxBlockEntity::new, CARDBOARD_BOX_BLOCK).build(null));

    @Override
    public void onInitialize() {
        GuiUtils.registerGuiItems();
        CardboardBoxBlockEntity.registerScreen();
    }

    /**
     * Use the randomizer to return true or false,
     * based on the percentage chance
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static boolean hasChance(int percentage) {
        int result = random.nextInt(100);
        return result <= percentage;
    }

    /**
     * Do a 50% chance test
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public static boolean hasChance() {
        return hasChance(50);
    }
}
