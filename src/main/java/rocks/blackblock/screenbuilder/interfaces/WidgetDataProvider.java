package rocks.blackblock.screenbuilder.interfaces;

/**
 * Interface for providing widget data.
 *
 * @since   0.1.1
 */
public interface WidgetDataProvider {

    Object getWidgetValue(String widget_id);

    default void setWidgetValue(String widget_id, Object value) {
        // Ignore by default
    }
}
