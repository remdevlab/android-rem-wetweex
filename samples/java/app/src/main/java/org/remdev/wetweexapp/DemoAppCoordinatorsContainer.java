package org.remdev.wetweexapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.component.CoordinatorsContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DemoAppCoordinatorsContainer implements CoordinatorsContainer {

    private final Map<String, Coordinator> container = new ConcurrentHashMap<>();
    private static final DemoAppCoordinatorsContainer instance = new DemoAppCoordinatorsContainer() {};

    private DemoAppCoordinatorsContainer() { }

    @Override
    public void save(@NonNull String id, @NonNull Coordinator coordinator) {
        container.put(id, coordinator);
    }

    @Nullable
    @Override
    public Coordinator restore(@NonNull String id) {
        return container.get(id);
    }

    @Override
    public void remove(@NonNull String id) {
        container.remove(id);
    }

    @Override
    public void clear() {
        container.clear();
    }

    public static DemoAppCoordinatorsContainer getInstance() {
        return instance;
    }
}