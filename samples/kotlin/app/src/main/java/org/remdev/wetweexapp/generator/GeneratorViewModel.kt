package org.remdev.wetweexapp.generator

import org.remdev.wetweex.live.AnyThreadMutableLiveData
import org.remdev.wetweex.live.TweexMutableLiveData
import org.remdev.wetweex.viewmodel.BaseTweexProgressViewModel

open class GeneratorViewModel : BaseTweexProgressViewModel() {

    open val generatedText: TweexMutableLiveData<String> = AnyThreadMutableLiveData<String>()
}