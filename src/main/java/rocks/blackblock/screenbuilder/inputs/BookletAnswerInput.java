package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.text.Font;
import rocks.blackblock.screenbuilder.widgets.TextWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * The Booklet "Answer" input:
 * a GUI styled to look like a book where the player
 * can click on the buttons to answer the question.
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.3
 * @version 0.1.3
 */
@SuppressWarnings("unused")
public class BookletAnswerInput extends BookletInput {

    protected List<Answer> answers = new ArrayList<>();

    /**
     * Clear everything
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    @Override
    public void clearAll() {
        this.clearAnswers();
        super.clearAll();
    }

    /**
     * Clear all the answers
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public void clearAnswers() {
        this.answers.clear();
    }

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
     * Get the answers to show
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public List<Answer> getAnswersToShow() {
        return this.answers;
    }

    /**
     * Get a new screenbuilder to actually send to the player
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public ScreenBuilder getScreenBuilder() {
        ScreenBuilder sb = super.getScreenBuilder();
        return this.addToScreenBuilder(sb);
    }

    /**
     * Add these answers to the given screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    public ScreenBuilder addToScreenBuilder(ScreenBuilder sb) {

        BookletAnswerInput self = this;

        int answer_index = -1;

        // Iterate over all the answers and add them
        for (Answer answer : this.getAnswersToShow()) {
            answer_index++;

            Integer slot_index = answer.getSlotIndex();

            if (slot_index == null) {
                slot_index = answer_index * 9;
            }

            // Only allow 6 answers for now
            if (answer_index > 6) {
                break;
            }

            ButtonWidgetSlot button = new ButtonWidgetSlot();

            ButtonWidgetSlot.BackgroundType background_type = answer.getButtonBackground();

            if (background_type != null) {
                button.setBackgroundType(background_type);
            }

            if (answer.item_stack != null) {
                button.setStack(answer.item_stack);
                button.setDummyStack(answer.item_stack);
            }

            button.setTitle(answer.getHoverText());
            sb.setSlot(slot_index, button);

            button.addLeftClickListener((screen, slot) -> {
                if (answer.listener != null) {
                    answer.listener.onAnswer(screen);
                }
            });

            if (answer.shouldLabelBePrinted()) {
                TextWidget tw = new TextWidget();
                tw.setText(answer.text);
                tw.setX(70);
                tw.setWidth(170);
                //tw.setYLine(button.getFontLineNumber());
                tw.setY(button.getYForVerticallyCenteredText());
                tw.setFontCollection(Font.LH_INVENTORY_SLOT);
                tw.setColor(TextColor.fromRgb(0x0012a5));

                sb.addWidget(tw);
            }
        }

        return sb;
    }

    /**
     * The actual Answer class
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     * @version 0.1.3
     */
    public static class Answer {

        // The text to display
        protected String text;

        // The listener to call when the answer is chosen
        protected AnswerListener listener = null;

        // The optional ItemStack to display
        protected ItemStack item_stack = null;

        // The optional hover text
        protected String hover_text = null;

        // The slot index to use
        protected Integer slot_index = null;

        // Print label?
        protected boolean print_label = true;

        // The background type of the button
        protected ButtonWidgetSlot.BackgroundType background_type = ButtonWidgetSlot.BackgroundType.EXTRA_SMALL;

        /**
         * Create a new answer with the given text
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public Answer(String text) {
            this.text = text;
        }

        /**
         * Create a new answer without any text
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public Answer() {
            this.text = null;
        }

        /**
         * Get the button background to use
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setButtonBackground(ButtonWidgetSlot.BackgroundType type) {
            this.background_type = type;
        }

        /**
         * Get the button background to use
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public ButtonWidgetSlot.BackgroundType getButtonBackground() {
            return this.background_type;
        }

        /**
         * Should this label be printed?
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public boolean shouldLabelBePrinted() {

            if (this.text == null || this.text.isEmpty()) {
                return false;
            }

            return this.print_label;
        }

        /**
         * Should we print this label if it's available?
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setPrintLabel(boolean value) {
            this.print_label = value;
        }

        /**
         * Set the text to display
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * Get the text to display
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public String getText() {
            return this.text;
        }

        /**
         * Set the listener to call when the answer is chosen
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setListener(AnswerListener listener) {
            this.listener = listener;
        }

        /**
         * Set the ItemStack to display
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         *
         * @param   item   The item to create a stack out of
         *
         * @return  The ItemStack
         */
        public ItemStack setItemStack(Item item) {
            return this.setItemStack(new ItemStack(item));
        }

        /**
         * Set the ItemStack to display
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         *
         * @param   item_stack  The ItemStack to display (is cloned)
         *
         * @return  A reference to the ItemStack copy used in this answer
         */
        public ItemStack setItemStack(ItemStack item_stack) {
            this.item_stack = item_stack.copy();

            return this.item_stack;
        }

        /**
         * Get the slot index to use
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public Integer getSlotIndex() {
            return this.slot_index;
        }

        /**
         * Set the slot index to use
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setSlotIndex(Integer slot_index) {
            this.slot_index = slot_index;
        }

        /**
         * Get the hovertext
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public String getHoverText() {

            if (this.hover_text == null) {

                if (this.text == null) {

                    if (this.item_stack != null) {
                        String item_name = this.item_stack.getName().getString();

                        if (item_name == null || item_name.isEmpty()) {
                            item_name = this.item_stack.getItem().getTranslationKey();
                        }

                        return "Choose \"" + item_name + "\"";
                    }

                    return "Click to choose this option";
                }

                return "Choose \"" + this.text + "\"";
            }

            return this.hover_text;
        }

        /**
         * Get the hovertext
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public void setHoverText(String hover_text) {
            this.hover_text = hover_text;
        }

        /**
         * Return a string representation of this instance
         *
         * @author  Jelle De Loecker   <jelle@elevenways.be>
         * @since   0.1.3
         */
        public String toString() {
            return "Answer{" +
                    "text='" + text + '\'' +
                    ", item_stack=" + item_stack +
                    ", hover_text='" + hover_text + '\'' +
                    ", slot_index=" + slot_index +
                    ", print_label=" + print_label +
                    '}';
        }
    }

    /**
     * The simple AnswerListener interface
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     * @version 0.1.3
     */
    @FunctionalInterface
    public interface AnswerListener {
        void onAnswer(TexturedScreenHandler screen);
    }
}
