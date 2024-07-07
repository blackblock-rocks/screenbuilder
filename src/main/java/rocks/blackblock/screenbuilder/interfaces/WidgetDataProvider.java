package rocks.blackblock.screenbuilder.interfaces;

import rocks.blackblock.bib.util.BibLog;
import rocks.blackblock.screenbuilder.widgets.Widget;

/**
 * Interface for providing widget data.
 *
 * @since   0.1.1
 */
public interface WidgetDataProvider {

    /**
     * Get the value of the given widget
     *
     * @since 0.5.0
     */
    <T> T getWidgetValue(Widget<T> widget);

    /**
     * Set the value of the given widget
     *
     * @since 0.5.0
     */
    default <T> void setWidgetValue(Widget<T> widget, T value) {
        BibLog.log("Warning! Setting value of widget", widget, "with value", value, "will be ignored");
    }
}
