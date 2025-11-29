package dev.royalcore.tests;

import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.annotations.NotForDeveloperUse;

/**
 * Simple test interface used by internal test utilities.
 */
@MarkedForRemoval
@NotForDeveloperUse
public interface TestInterface {

    /**
     * Activates the test implementation.
     */
    void activate();

}
