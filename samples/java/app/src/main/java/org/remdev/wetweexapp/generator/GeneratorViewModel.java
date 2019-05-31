package org.remdev.wetweexapp.generator;

import org.remdev.wetweex.live.AnyThreadMutableLiveData;
import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweex.viewmodel.BaseTweexProgressViewModel;

public class GeneratorViewModel extends BaseTweexProgressViewModel {

    private TweexMutableLiveData<String> generatedText = new AnyThreadMutableLiveData<>();

    public TweexMutableLiveData<String> getGeneratedText() {
        return generatedText;
    }
}