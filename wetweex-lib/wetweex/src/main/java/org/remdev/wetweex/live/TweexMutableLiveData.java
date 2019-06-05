package org.remdev.wetweex.live;

public interface TweexMutableLiveData<T> extends TweexLiveData<T> {

    void setOrPost(T data);
}
