package org.remdev.wetweex;

import androidx.annotation.NonNull;

import java.util.List;

public interface ProgressView {

    void show();

    void show(@NonNull String message);

    void show(boolean indeterminate, List<String> messages);

    void hide();
}
