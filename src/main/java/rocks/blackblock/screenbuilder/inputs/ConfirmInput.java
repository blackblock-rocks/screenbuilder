package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.client.input.Input;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.ImageWidget;

public class ConfirmInput extends EmptyInput {

    public ConfirmInput() {
        this.default_name = "Confirmation required";
    }

    /**
     * Get a basic ScreenBuilder
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    @Override
    public ScreenBuilder createBasicScreenBuilder(String name) {

        BaseInput that = this;

        ScreenBuilder sb = super.createBasicScreenBuilder(name);
        sb.setCloneSlots(false);

        int button_width = 2;

        if (this.show_accept_button != null) {

            for (int i = 0; i < button_width; i++) {

                ButtonWidgetSlot accept_button = new ButtonWidgetSlot();
                accept_button.setStack(BBSB.GUI_TRANSPARENT);
                accept_button.setTitle("Accept");

                accept_button.addLeftClickListener((screen, slot) -> {
                    if (that.on_accept_click != null) {
                        that.on_accept_click.onEvent(screen, that);
                    }
                });

                sb.setSlot(this.show_accept_button + i, accept_button);

                if (i == 0) {
                    ImageWidget accept_image = new ImageWidget(BBSB.BUTTON_ACCEPT);
                    accept_image.addToScreenBuilder(sb, "accept_button", accept_button.getSlotXInPixels(), accept_button.getSlotYInPixels());
                }
            }

        }

        if (this.show_back_button != null) {

            for (int i = 0; i < button_width; i++) {
                ButtonWidgetSlot back_button = new ButtonWidgetSlot();
                back_button.setStack(BBSB.GUI_TRANSPARENT);
                back_button.setTitle("Cancel");

                back_button.addLeftClickListener((screen, slot) -> {
                    screen.showPreviousScreen();
                });

                sb.setSlot(this.show_back_button + i, back_button);

                if (i == 0) {
                    ImageWidget deny_image = new ImageWidget(BBSB.BUTTON_DENY);
                    deny_image.addToScreenBuilder(sb, "deny_button", back_button.getSlotXInPixels(), back_button.getSlotYInPixels());
                }
            }
        }

        return sb;
    }


    @Override
    public ScreenBuilder getScreenBuilder() {
        ScreenBuilder sb = this.createBasicScreenBuilder("confirm_screen");
        return sb;
    }
}
