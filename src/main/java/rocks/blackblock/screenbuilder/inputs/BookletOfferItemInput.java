package rocks.blackblock.screenbuilder.inputs;

import net.minecraft.item.ItemStack;
import rocks.blackblock.screenbuilder.BBSB;
import rocks.blackblock.screenbuilder.ScreenBuilder;
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

    // Offers
    protected List<Answer> offers = new ArrayList<>();

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
        this.offers.add(answer);
        answer.setPrintLabel(false);
        answer.setItemStack(stack);
        answer.setButtonBackground(ButtonWidgetSlot.BackgroundType.LARGE);
        return answer;
    }

    /**
     * Add these item offers to the given screenbuilder
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    @Override
    public List<Answer> getAnswersToShow() {

        List<Answer> result = new ArrayList<>();
        List<Integer> indexes = this.getSlotIndexesToUseForOffers();

        if (indexes.isEmpty()) {
            return result;
        }

        for (int i = 0; i < this.offers.size(); i++) {
            Answer answer = this.offers.get(i);
            int index = indexes.get(i);
            answer.setSlotIndex(index);
            result.add(answer);
        }

        for (int i = 0; i < this.answers.size(); i++) {
            Answer answer = this.answers.get(i);

            // Start the simple answers from the bottom
            int index = 45 - (i * 9);

            answer.setSlotIndex(index);
            result.add(answer);
        }

        return result;
    }

    /**
     * Get the slot indexes to use for the offers
     *
     * @author  Jelle De Loecker   <jelle@elevenways.be>
     * @since   0.1.3
     */
    protected List<Integer> getSlotIndexesToUseForOffers() {

        List<Integer> indexes = new ArrayList<>();

        int available_rows = 6 - this.answers.size();

        // Always reserve 1 row for item offers
        if (available_rows < 1) {
            available_rows = 1;
        }

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

        if (available_rows >= 3) {
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

        return indexes;
    }
}
