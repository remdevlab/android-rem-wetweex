package org.remdev.wetweex.live;

import android.os.Looper;
import androidx.lifecycle.MutableLiveData;

public class AnyThreadMutableLiveData<T> extends MutableLiveData<T> implements TweexMutableLiveData<T> {

    public void setOrPost(T value) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            setValue(value);
        } else {
            postValue(value);
        }
    }
}
