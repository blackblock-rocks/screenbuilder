package rocks.blackblock.screenbuilder.values;


import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.inputs.BaseInput;
import rocks.blackblock.screenbuilder.inputs.StringInput;
import rocks.blackblock.screenbuilder.utils.NbtUtils;

public class StringValue extends Value {

    @Override
    public Item getIcon() {
        return BBSB.GUI_TEXT;
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public ItemStack getStack(ItemStack result) {

        MutableText lore = (Text.literal("Value: ")).setStyle(Style.EMPTY.withItalic(false).withColor(Formatting.GRAY));
        MutableText value;

        String str = this.getValue();

        if (str == null) {
            value = Text.literal("undefined").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true));
        } else if (str.length() == 0) {
            value = Text.literal("empty").setStyle(Style.EMPTY.withColor(Formatting.RED).withItalic(true));
        } else {
            value = Text.literal(str).setStyle(Style.EMPTY.withColor(Formatting.GREEN));
        }

        NbtUtils.appendLore(result, lore.append(value));

        return result;
    }

    @Override
    public String getValue() {
        Object value = super.getValue();

        if (value == null || !(value instanceof String str)) {
            return "";
        }

        return str;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("String");
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        String value = nbt.getString("value");
        this.setValue(value);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putString("value", this.getValue());
    }

    @Override
    public Boolean isTruthy() {
        String str = this.getValue();

        if (str != null && str.length() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Double getNumber() {
        return null;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        StringValue that = this;

        StringInput input = new StringInput();

        ScreenBuilder builder = input.getScreenBuilder();

        input.setRenamedListener((screen, new_value) -> {
            that.setValue(new_value);

            if (screen != null) {

                var factory = screen.getOriginFactory();

                if (factory instanceof BlockEntity be) {
                    be.markDirty();
                }
            }
        });

        input.setChangeBehaviour(BaseInput.ChangeBehaviour.SHOW_PREVIOUS_SCREEN);

        TexturedScreenHandler handler = builder.createScreenHandler(syncId, inv);
        handler.setOriginFactory(input);

        return handler;
    }
}