package org.remdev.wetweex.live;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DefaultValueLiveData<T> extends AnyThreadMutableLiveData<T> {

    @NonNull
    private final T def;

    public DefaultValueLiveData(@NonNull T def) {
        setOrPost(def);
        this.def = def;
    }

    @Nullable
    @Override
    public T getValue() {
        T value = super.getValue();
        if (value == null) {
            return def;
        }
        return value;
    }
}
