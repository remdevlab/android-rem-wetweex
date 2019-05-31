package org.remdev.wetweex.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LoadProgressInfo {

    private boolean loading;
    private boolean indeterminate;
    private Map<String, String> operationTags = new LinkedHashMap<>();

    public void setLoading(boolean loading) {
        if (!loading) {
            operationTags.clear();
        }
        this.loading = loading;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public List<String> getRunningOperationMessages() {
        return new ArrayList<>(operationTags.values());
    }

    public void addOperation(@NonNull String id, @NonNull String operation) {
        setLoading(true);
        operationTags.put(id, operation);
    }

    public void completeOperation(@NonNull String id) {
        operationTags.remove(id);
        setLoading(operationTags.isEmpty() == false);
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }
}
