package org.remdev.wetweexapp

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import org.remdev.wetweex.component.Coordinator
import org.remdev.wetweex.live.TweexMutableLiveData
import org.remdev.wetweex.model.LoadProgressInfo
import org.remdev.wetweex.viewmodel.TweexProgressViewModel
import org.remdev.wetweex.viewmodel.TweexViewModel
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch


fun <T : TweexViewModel> waitCoordinatorLatch(coordinator: Coordinator<T>): CountDownLatch {
    val latch = CountDownLatch(1)
    coordinator.addTasksCompletedListener {
        latch.countDown()
    }
    return latch
}

fun <T : TweexViewModel> runCoordinator(coordinator: Coordinator<T>, operation: (Coordinator<T>) -> Unit) {
    val latch = waitCoordinatorLatch(coordinator)
    operation.invoke(coordinator)
    return latch.await()
}

inline fun <reified T> mockViewModel(): T {
    val viewModel = Mockito.mock(T::class.java)
    if (viewModel is TweexProgressViewModel) {
        appendProgressInfoMock(viewModel)
    }
    return viewModel
}

fun appendProgressInfoMock(viewModel: TweexProgressViewModel) {
    val mockLoad = object : TweexMutableLiveData<LoadProgressInfo> {
        private var data: LoadProgressInfo = LoadProgressInfo()

        override fun observe(owner: LifecycleOwner?, data: Observer<in LoadProgressInfo>?) {}

        override fun getValue(): LoadProgressInfo = data

        override fun setOrPost(data: LoadProgressInfo) {
            this.data = data
        }
    }
    Mockito.`when`(viewModel.loadingInfo).thenReturn(mockLoad)
}