package org.remdev.wetweexapp

import org.remdev.wetweex.live.TweexMutableLiveData
import org.remdev.wetweexapp.generator.GeneratorCoordinator
import org.remdev.wetweexapp.generator.GeneratorViewModel
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class GeneratorCoordinatorTest {

    private lateinit var coordinator: GeneratorCoordinator
    private lateinit var viewModel: GeneratorViewModel

    @Before
    fun setUp() {
        viewModel = mockViewModel()

        val generatedTextMock = Mockito.mock(TweexMutableLiveData::class.java) as TweexMutableLiveData<String>
        Mockito.`when`(viewModel.generatedText).thenReturn(generatedTextMock)
        coordinator =
            GeneratorCoordinator(viewModel, textGenerator)
    }

    @Test
    fun testGenerateString() {
        runCoordinator(coordinator) {
            coordinator.generateString(Strategy.NON_CANCELLABLE, 1000)
        }

        Mockito.verify(viewModel, Mockito.times(2)).loadingInfo
        Mockito.verify(viewModel, Mockito.times(1)).generatedText
    }
}
