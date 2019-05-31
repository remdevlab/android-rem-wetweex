package org.remdev.wetweexapp;

import androidx.core.util.Consumer;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import org.mockito.Mockito;
import org.remdev.wetweex.component.Coordinator;
import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweex.model.LoadProgressInfo;
import org.remdev.wetweex.viewmodel.TweexProgressViewModel;
import org.remdev.wetweex.viewmodel.TweexViewModel;

import java.util.concurrent.CountDownLatch;

public class TestUtils {

    public static <T extends TweexViewModel> CountDownLatch waitCoordinatorLatch(Coordinator<T> coordinator) {
        final CountDownLatch latch = new CountDownLatch(1);
        coordinator.addTasksCompletedListener(latch::countDown);
        return latch;
    }

    public static <T extends TweexViewModel, C extends Coordinator<T>> void runCoordinator(C coordinator,
                                                                                           Consumer<C> operation) {
        final CountDownLatch latch = waitCoordinatorLatch(coordinator);
        operation.accept(coordinator);
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T mockViewModel(Class<T> cls) {
        T viewModel = Mockito.mock(cls);
        if (viewModel instanceof TweexProgressViewModel) {
            appendProgressInfoMock((TweexProgressViewModel) viewModel);
        }
        return viewModel;
    }

    private static void appendProgressInfoMock(TweexProgressViewModel viewModel) {
        final TweexMutableLiveData mockLoad = new TweexMutableLiveData<LoadProgressInfo>() {

            private LoadProgressInfo data = new LoadProgressInfo();

            @Override
            public void setOrPost(LoadProgressInfo loadProgressInfo) {
                this.data = data;
            }

            @Override
            public void observe(LifecycleOwner lifecycleOwner, Observer<? super LoadProgressInfo> observer) {

            }

            @Override
            public LoadProgressInfo getValue() {
                return data;
            }
        };
        Mockito.when(viewModel.getLoadingInfo()).thenReturn(mockLoad);
    }
}
