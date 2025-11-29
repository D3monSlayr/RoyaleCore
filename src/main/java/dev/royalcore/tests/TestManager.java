package dev.royalcore.tests;

import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.annotations.NotForDeveloperUse;
import lombok.Getter;

/**
 * Manages and executes internal test implementations.
 */
@MarkedForRemoval
@NotForDeveloperUse
public class TestManager {

    /**
     * Singleton instance of the test manager.
     */
    @Getter
    private static final TestManager testManager = new TestManager();

    /**
     * Creates a new {@link TestManager} instance.
     * <p>
     * Private to enforce the singleton pattern; use {@link #getTestManager()} to access
     * the shared instance.
     */
    private TestManager() {
    }

    /**
     * Activates the given test implementation.
     *
     * @param testInterface the test implementation to activate
     */
    public void activateTest(TestInterface testInterface) {
        testInterface.activate();
    }

}
