package org.remdev.wetweex.live;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

public interface TweexMutableLiveData<T> extends TweexLiveData<T> {

    void setOrPost(T data);

    void observe(LifecycleOwner owner, Observer<? super T> data);
}
