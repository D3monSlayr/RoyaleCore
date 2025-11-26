package dev.royalcore.api.consumer;

import dev.royalcore.Main;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.rmi.AlreadyBoundException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchedulerConsumer {

    @Getter
    private final Map<List<Duration>, Runnable> schedules = new HashMap<>();

    @Getter
    private final List<Runnable> runnables = new ArrayList<>();

    public void schedule(Duration start, Duration stop, Runnable runnable) {

        if (schedules.containsValue(runnable)) {
            Main.getPlugin().getComponentLogger().error(Component.text("A runnable is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        schedules.put(List.of(start, stop), runnable);
    }

    public void schedule(Duration start, Runnable runnable) {

        if (schedules.containsValue(runnable)) {
            Main.getPlugin().getComponentLogger().error(Component.text("A runnable is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        schedules.put(List.of(start), runnable);
    }

    public void run(Runnable runnable) {

        if (schedules.containsValue(runnable)) {
            Main.getPlugin().getComponentLogger().error(Component.text("A runnable is already included in the registry!"), new AlreadyBoundException());
            return;
        }

        runnables.add(runnable);
    }

}
