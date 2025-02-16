package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import rocks.blackblock.screenbuilder.slots.ButtonWidgetSlot;

import java.util.ArrayList;
import java.util.List;

/**
 * The Booklet "OfferItem" input:
 * a GUI styled to look like a book where the player
 * can click on items to answer the question.
 * Normal, button-answers can also be added
 *
 * @author  Jelle De Loecker   <jelle@elevenways.be>
 * @since   0.1.3
 * @version 0.1.3
 */
@SuppressWarnings("unused")
public class BookletOfferItemInput extends BookletAnswerInput {

    // Item offers
    protected List<Answer> offers = new ArrayList<>();

    /**
     * Get the maximum allowed answers on a single page
     */
    @Override
    public int getMaxAllowedAnswersPerPageWithoutPaging() {

        int item_count = this.offers.size();
        int answer_count = this.answers.size();

        int max_items = Math.min(item_count, this.getAvailableItemColumns());
        int max_answers = Math.min(answer_count, 3);

        return max_items + max_answers;
    }

    /**
     * Get the max allowed items per page when paging is enabled
     */
    @Override
    public int getMaxAllowedAnswersPerPageWithPaging() {

        int item_count = this.offers.size();
        int answer_count = this.answers.size();

        int max_items = Math.min(item_count, this.getAvailableItemColumns());
        int max_answers = Math.min(answer_count, 2);

        return max_items + max_answers;
    }

    /**
     * Get the pageable items
     */
    @Override
    @NotNull
    public List<BookletAnswerInput.Answer> getPageableItems() {
        var result = new ArrayList<>(this.answers);
        result.addAll(this.offers);
        return result;
    }

    /**
     * Get a sublist of the options for the given page
     *
     * @since   0.3.1
     */
    @Override
    public List<Answer> getPageableItemsForPage(int page) {

        List<Answer> result = new ArrayList<>();
        List<Integer> indexes = this.getSlotIndexesToUseForOffers();

        if (indexes.isEmpty()) {
            return result;
        }

        int current_page = this.getPage();
        int item_start = (current_page - 1) * this.getAvailableItemColumns();
        int item_end = Math.min(item_start + this.getAvailableItemColumns(), this.offers.size());

        for (int i = item_start; i < item_end; i++) {
            Answer answer = this.offers.get(i);
            int slot_i = i - item_start;

            if (indexes.size() <= slot_i) {
                break;
            }

            int index = indexes.get(slot_i);
            answer.setSlotIndex(index);
            result.add(answer);
        }

        int answer_start;
        int answer_end;
        int answer_slot_start = 45;

        if (this.isPagingRequired()) {
            answer_start = (current_page - 1) * 2;
            answer_end = Math.min(answer_start + 2, this.answers.size());
            answer_slot_start = 36;
        } else {
            answer_start = 0;
            answer_end = this.answers.size();
        }

        for (int i = answer_start; i < answer_end; i++) {
            Answer answer = this.answers.get(i);
            int answer_i = i - answer_start;

            // Start the simple answers from the bottom
            int index = answer_slot_start - (answer_i * 9);

            answer.setSlotIndex(index);
            result.add(answer);
        }

        return result;
    }

    /**
     * Clear everything
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    @Override
    public void clearAll() {
        this.clearOffers();
        super.clearAll();
    }

    /**
     * Clear the offers
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.3.1
     */
    public void clearOffers() {
        this.offers.clear();
    }

    /**
     * Add an item to offer
     *
     * @author   Jelle De Loecker   <jelle@elevenways.be>
     * @since    0.1.3
     */
    public Answer addOffer(ItemStack stack) {
        Answer answer = new Answer();
        answer.setPrintLabel(false);
        answer.setItemStack(stack);
        answer.setButtonBackground(ButtonWidgetSlot.BackgroundType.LARGE);
        this.offers.add(answer);
        return answer;
    }

    /**
     * Count the amount of available item-rows
     */
    protected int getAvailableItemRows() {

        int answer_count = this.answers.size();

        if (answer_count == 0) {
            return 5;
        }

        return 3;
    }

    /**
     * Count the amount of available item columns
     */
    protected int getAvailableItemColumns() {

        int rows = this.getAvailableItemRows();

        if (rows == 5) {
            return 14;
        }

        return 9;
    }

    /**
     * Get the slot indexes to use for the offers
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    protected List<Integer> getSlotIndexesToUseForOffers() {

        List<Integer> indexes = new ArrayList<>();

        int available_rows = this.getAvailableItemRows();

        int item_offers = this.offers.size();

        if (available_rows == 1) {
            switch (item_offers) {
                case 1 -> indexes.add(4);
                case 2 -> {
                    indexes.add(3);
                    indexes.add(5);
                }
                case 3 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                }
                case 4 -> {
                    indexes.add(1);
                    indexes.add(3);
                    indexes.add(5);
                    indexes.add(7);
                }
                case 5 -> {
                    indexes.add(2);
                    indexes.add(3);
                    indexes.add(4);
                    indexes.add(5);
                    indexes.add(6);
                }
                case 6 -> {
                    indexes.add(1);
                    indexes.add(2);
                    indexes.add(3);
                    indexes.add(5);
                    indexes.add(6);
                    indexes.add(7);
                }
                case 7 -> {
                    indexes.add(1);
                    indexes.add(2);
                    indexes.add(3);
                    indexes.add(4);
                    indexes.add(5);
                    indexes.add(6);
                    indexes.add(7);
                }
                case 8 -> {
                    indexes.add(0);
                    indexes.add(1);
                    indexes.add(2);
                    indexes.add(3);
                    indexes.add(5);
                    indexes.add(6);
                    indexes.add(7);
                    indexes.add(8);
                }

                default -> {
                    indexes.add(0);
                    indexes.add(1);
                    indexes.add(2);
                    indexes.add(3);
                    indexes.add(4);
                    indexes.add(5);
                    indexes.add(6);
                    indexes.add(7);
                    indexes.add(8);
                }
            }
        }

        if (available_rows == 2) {
            switch (item_offers) {
                case 1 -> indexes.add(4);
                case 2 -> {
                    indexes.add(3);
                    indexes.add(5);
                }
                case 3 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                }
                case 4 -> {
                    indexes.add(1);
                    indexes.add(3);
                    indexes.add(5);
                    indexes.add(7);
                }
                case 5 -> {
                    indexes.add(2);
                    indexes.add(12);
                    indexes.add(4);
                    indexes.add(14);
                    indexes.add(6);
                }
                case 6 -> {
                    indexes.add(1);
                    indexes.add(11);
                    indexes.add(3);
                    indexes.add(5);
                    indexes.add(15);
                    indexes.add(7);
                }
                case 7 -> {
                    indexes.add(1);
                    indexes.add(11);
                    indexes.add(3);
                    indexes.add(13);
                    indexes.add(5);
                    indexes.add(15);
                    indexes.add(7);
                }
                case 8 -> {
                    indexes.add(0);
                    indexes.add(10);
                    indexes.add(2);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(6);
                    indexes.add(16);
                    indexes.add(8);
                }
                default -> {
                    indexes.add(0);
                    indexes.add(10);
                    indexes.add(2);
                    indexes.add(12);
                    indexes.add(4);
                    indexes.add(14);
                    indexes.add(6);
                    indexes.add(16);
                    indexes.add(8);
                }
            }
        }

        if (available_rows == 3 || available_rows == 4) {
            switch (item_offers) {
                case 1 -> indexes.add(13);
                case 2 -> {
                    indexes.add(12);
                    indexes.add(14);
                }
                case 3 -> {
                    indexes.add(11);
                    indexes.add(13);
                    indexes.add(15);
                }
                case 4 -> {
                    indexes.add(10);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(16);
                }
                case 5 -> {
                    indexes.add(2);
                    indexes.add(6);
                    indexes.add(13);
                    indexes.add(20);
                    indexes.add(24);
                }
                case 6 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(20);
                    indexes.add(22);
                    indexes.add(24);
                }
                case 7 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(20);
                    indexes.add(24);
                }
                case 8 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(20);
                    indexes.add(22);
                    indexes.add(24);
                }
                default -> {
                    indexes.add(0);
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(8);
                    indexes.add(19);
                    indexes.add(21);
                    indexes.add(23);
                    indexes.add(25);
                }
            }
        }

        if (available_rows >= 5) {
            switch (item_offers) {
                case 1 -> indexes.add(13);
                case 2 -> {
                    indexes.add(12);
                    indexes.add(14);
                }
                case 3 -> {
                    indexes.add(11);
                    indexes.add(13);
                    indexes.add(15);
                }
                case 4 -> {
                    indexes.add(10);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(16);
                }
                case 5 -> {
                    indexes.add(2);
                    indexes.add(6);
                    indexes.add(13);
                    indexes.add(20);
                    indexes.add(24);
                }
                case 6 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(20);
                    indexes.add(22);
                    indexes.add(24);
                }
                case 7 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(20);
                    indexes.add(24);
                }
                case 8 -> {
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(12);
                    indexes.add(14);
                    indexes.add(20);
                    indexes.add(22);
                    indexes.add(24);
                }
                case 9 -> {
                    indexes.add(0);
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(8);
                    indexes.add(19);
                    indexes.add(21);
                    indexes.add(23);
                    indexes.add(25);
                }
                default -> {
                    indexes.add(0);
                    indexes.add(2);
                    indexes.add(4);
                    indexes.add(6);
                    indexes.add(8);
                    indexes.add(19);
                    indexes.add(21);
                    indexes.add(23);
                    indexes.add(25);
                    indexes.add(36);
                    indexes.add(38);
                    indexes.add(40);
                    indexes.add(42);
                    indexes.add(44);
                }
            }
        }

        return indexes;
    }
}
