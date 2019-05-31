package org.remdev.wetweex.utils;

import androidx.annotation.NonNull;

public class Objects {

    @NonNull
    public static <T> T requireNonNull(T obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
        return obj;
    }
}
