package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.screenbuilder.ScreenBuilder;
import rocks.blackblock.screenbuilder.TexturedScreenHandler;
import rocks.blackblock.screenbuilder.interfaces.WidgetDataProvider;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;
import rocks.blackblock.screenbuilder.widgets.TextWidget;
import rocks.blackblock.screenbuilder.widgets.Widget;

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
public class BookletAnswerInput extends BookletInput implements PageableInput<BookletAnswerInput.Answer>, WidgetDataProvider {

    protected List<Answer> answers = new ArrayList<>();

    // The current page we're on. Starts at 1.
    private int page = 1;

    /**
     * Set the current page value
     */
    @Override
    public void setPageValue(int page) {
        this.page = page;
    }

    /**
     * Get the current page value
     */
    @Override
    public int getPageValue() {
        return this.page;
    }

    /**
     * Get the pageable items
     */
    @Override
    @NotNull
    public List<BookletAnswerInput.Answer> getPageableItems() {
        return this.answers;
    }

    /**
     * Get the maximum allowed answers on a single page
     */
    public int getMaxAllowedAnswersPerPageWithoutPaging() {
        return 6;
    }

    /**
     * Get the max allowed items per page when paging is enabled
     */
    public int getMaxAllowedAnswersPerPageWithPaging() {
        return 5;
    }

    /**
     * Get the maximum amount of items per page
     */
    @Override
    public int getMaxItemsPerPage() {

        int max_without_paging = this.getMaxAllowedAnswersPerPageWithoutPaging();

        if (this.getPageableItems().size() > max_without_paging) {
            return this.getMaxAllowedAnswersPerPageWithPaging();
        }

        return max_without_paging;
    }

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
        return this.addAnswer(answer);
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
     * Add an answer instance
     */
    protected Answer addAnswer(Answer answer) {
        this.answers.add(answer);
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
        this.forEachItemsOnCurrentPage((answer, index_on_page, amount_on_this_page) -> {
            Integer slot_index = answer.getSlotIndex();

            if (slot_index == null) {
                slot_index = index_on_page * 9;
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
                    answer.listener.onAnswer(screen, button);
                }
            });

            if (answer.shouldLabelBePrinted()) {
                TextWidget tw = new TextWidget();
                tw.setText(answer.text);
                tw.setX(131);
                tw.setWidth(245);
                //tw.setYLine(button.getFontLineNumber());
                tw.setY(button.getYForVerticallyCenteredText());
                tw.setColor(TextColor.fromRgb(0x0012a5));

                sb.addWidget(tw);
            }
        });

        if (this.isPagingRequired()) {
            this.addPaginationWidget(sb, 50, true);
        }

        return sb;
    }

    @Override
    public <T> T getWidgetValue(Widget<T> widget) {

        var id = this.getPaginationWidgetId();

        if (widget.getId().equals(id)) {
            return (T) (Integer) this.getPage();
        }

        return null;
    }

    @Override
    public <T> void setWidgetValue(Widget<T> widget, T value) {

        var id = this.getPaginationWidgetId();

        if (widget.getId().equals(id)) {
            this.setPage((int) value);
        }
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

        // The original item stack
        protected ItemStack safe_stack = null;

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
            this.safe_stack = item_stack.copy();
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

                    if (this.safe_stack != null) {
                        String item_name = this.safe_stack.getName().getString();

                        if (item_name == null || item_name.isEmpty()) {
                            item_name = this.safe_stack.getItem().getTranslationKey();
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
        void onAnswer(TexturedScreenHandler screen, ButtonWidgetSlot button);
    }
}
