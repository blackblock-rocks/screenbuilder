package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.widgets.TextWidget;

import java.util.ArrayList;
import java.util.List;

public class BookletAnswerInput extends BookletInput {

    protected List<Answer> answers = new ArrayList<>();

    /**
     * Add an answer
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public Answer addAnswer(String text) {
        Answer answer = new Answer(text);
        this.answers.add(answer);
        return answer;
    }

    /**
     * Add an answer with a callback
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public Answer addAnswer(String text, AnswerListener listener) {
        Answer answer = this.addAnswer(text);
        answer.setListener(listener);
        return answer;
    }

    /**
     * Get a new screenbuilder to actually send to the player
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public ScreenBuilder getScreenBuilder() {

        BookletAnswerInput self = this;
        ScreenBuilder sb = super.getScreenBuilder();

        int answer_index = -1;

        // Iterate over all the answers and add them
        for (Answer answer : this.answers) {
            answer_index++;
            int slot_index = answer_index * 9;

            // Only allow 6 answers for now
            if (answer_index > 6) {
                break;
            }

            ButtonWidgetSlot button = new ButtonWidgetSlot();
            button.setBackgroundType(ButtonWidgetSlot.BackgroundType.EXTRA_SMALL);
            button.setTitle("Choose '" + answer.text + "'");
            sb.setSlot(slot_index, button);

            button.addLeftClickListener((screen, slot) -> {
                if (answer.listener != null) {
                    answer.listener.onAnswer(screen);
                }
            });

            TextWidget tw = new TextWidget();
            tw.setText(answer.text);
            tw.setX(70);
            tw.setWidth(170);
            tw.setYLine(button.getSlotY());
            tw.setFontCollection(Font.LH_INVENTORY_SLOT);
            tw.setColor(TextColor.fromRgb(0x0012a5));

            sb.addWidget(tw);
        }

        return sb;
    }

    public static class Answer {
        protected String text;
        protected AnswerListener listener;

        public Answer(String text) {
            this.text = text;
        }

        public void setListener(AnswerListener listener) {
            this.listener = listener;
        }
    }

    @FunctionalInterface
    public interface AnswerListener {
        void onAnswer(TexturedScreenHandler screen);
    }
}
