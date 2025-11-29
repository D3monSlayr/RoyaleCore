package dev.royalcore.tests;

import dev.royalcore.annotations.MarkedForRemoval;
import dev.royalcore.annotations.NotForDeveloperUse;
import lombok.Getter;

@MarkedForRemoval
@NotForDeveloperUse
public class TestManager {

    @Getter
    private static final TestManager testManager = new TestManager();

    private TestManager() {
    }

    public void activateTest(TestInterface testInterface) {
        testInterface.activate();
    }

}
