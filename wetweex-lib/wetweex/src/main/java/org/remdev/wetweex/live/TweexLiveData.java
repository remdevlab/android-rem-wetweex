package org.remdev.wetweex.live;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public interface TweexLiveData<T> {

    T getValue();

    void observe(LifecycleOwner owner, Observer<? super T> data);

}
