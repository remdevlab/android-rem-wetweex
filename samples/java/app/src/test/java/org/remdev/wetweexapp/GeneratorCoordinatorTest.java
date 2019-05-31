package org.remdev.wetweexapp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.remdev.wetweex.live.TweexMutableLiveData;
import org.remdev.wetweexapp.generator.GeneratorCoordinator;
import org.remdev.wetweexapp.generator.GeneratorViewModel;

public class GeneratorCoordinatorTest {

    private GeneratorCoordinator coordinator;
    private GeneratorViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = TestUtils.mockViewModel(GeneratorViewModel.class);

        TweexMutableLiveData generatedTextMock = Mockito.mock(TweexMutableLiveData.class);
                Mockito.when(viewModel.getGeneratedText()).thenReturn(generatedTextMock);
        coordinator = new GeneratorCoordinator(viewModel, Dependencies.textGenerator());
    }

    @Test
    public void testGenerateString() {
        TestUtils.runCoordinator(coordinator, coordinator -> coordinator.generateString(Strategy.NON_CANCELLABLE, 1000L));

        Mockito.verify(viewModel, Mockito.times(2)).getLoadingInfo();
        Mockito.verify(viewModel, Mockito.times(1)).getGeneratedText();
    }
}
