package rocks.blackblock.screenbuilder.testmod.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.testmod.ScreenbuilderTest;
import rocks.blackblock.screenbuilder.testmod.block.entity.CardboardBoxBlockEntity;

import java.util.ArrayList;
import java.util.List;

public class CardboardBoxBlock extends BlockWithEntity {

    public CardboardBoxBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    /**
     * Create the BlockEntity to go along with the block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    pos     Where the block is
     * @param    state   The current state of the block
     */
    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CardboardBoxBlockEntity(pos, state);
    }

    /**
     * Return the rendertype of this block
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    state   The current state of the block
     */
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return super.getTicker(world, state, type);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {

        if (!world.isClient) {

            //This will call the createScreenHandlerFactory method from BlockWithEntity, which will return our blockEntity casted to
            //a namedScreenHandlerFactory. If your block class does not extend BlockWithEntity, it needs to implement createScreenHandlerFactory.
            NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

            if (screenHandlerFactory != null) {
                //With this call the server will request the client to open the appropriate ScreenHandler
                player.openHandledScreen(screenHandlerFactory);
            }
        }
        return ActionResult.SUCCESS;
    }

    /**
     * The broken block should keep its inventory, but lose durability
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     *
     * @param    world   The world in which the block was broken
     * @param    pos     The position of the block that needs a KeyholeBlockEntity
     * @param    state   The state of the block
     * @param    player  The player that broke the block
     */
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CardboardBoxBlockEntity box) {

            if (!world.isClient && player.isCreative() && !box.isEmpty()) {
                ItemStack itemStack = box.getItemStack();

                if (itemStack != null) {
                    ItemEntity itemEntity = new ItemEntity(world, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, itemStack);
                    itemEntity.setToDefaultPickupDelay();
                    world.spawnEntity(itemEntity);
                }
            } else if (!world.isClient && !player.isCreative()) {

                boolean add_damage = false;

                if (box.isEmpty()) {
                    add_damage = ScreenbuilderTest.hasChance(40);
                } else {
                    add_damage = ScreenbuilderTest.hasChance(80);
                }

                if (add_damage) {
                    box.increaseDamage();
                }

                if (box.doDropCheck()) {
                    return;
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    /**
     * Get the dropped stack of this box
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.0
     * @version  0.1.0
     */
    public List<ItemStack> getDroppedStacks(BlockState state, net.minecraft.loot.context.LootContext.Builder builder) {
        BlockEntity blockEntity = (BlockEntity)builder.getNullable(LootContextParameters.BLOCK_ENTITY);

        if (blockEntity instanceof CardboardBoxBlockEntity box) {

            ItemStack stack = box.getItemStack();

            if (stack != null) {
                List<ItemStack> result = new ArrayList<>();
                result.add(stack);
                return result;
            }
        }

        return super.getDroppedStacks(state, builder);
    }
}