package rocks.blackblock.screenbuilder.interfaces;

public interface CompareForScenario<T> {

    /**
     * Does this object have special comparison rules for the given scenario?
     * @param scenario
     * @return
     */
    boolean supportsScenario(String scenario);

    /**
     * Compare this object to another object for the given scenario.
     * @param left
     * @param right
     * @param scenario
     * @return
     */
    Boolean compareForScenario(T left, T right, String scenario);

}
