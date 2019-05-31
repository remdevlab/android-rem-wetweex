package org.remdev.wetweex.viewmodel;

import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweex.model.LoadProgressInfo;

public interface TweexProgressViewModel extends TweexViewModel {

    TweexMutableLiveData<LoadProgressInfo> getLoadingInfo();
}
