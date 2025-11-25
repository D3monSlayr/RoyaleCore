package dev.royalcore.api.settings;

import lombok.Getter;

public class Settings {

    @Getter
    private Darkness darkness = Darkness.NORMAL;

    public void set(Darkness darkness) {
        this.darkness = darkness;
    }

    public enum Darkness {
        NORMAL,
        SPOOKY
    }

}
