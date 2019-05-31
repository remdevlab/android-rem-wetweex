package org.remdev.wetweex.component;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Allows to store coordinators for different views (activity, fragments)
 * Usually one per application
 */
public interface CoordinatorsContainer {

    void save(@NonNull String id, @NonNull Coordinator coordinator);

    @Nullable Coordinator restore(@NonNull String id);

    void remove(@NonNull String id);

    void clear();
}
