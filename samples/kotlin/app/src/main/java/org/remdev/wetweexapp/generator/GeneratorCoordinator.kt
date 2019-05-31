package org.remdev.wetweexapp.generator

import org.remdev.wetweex.component.ProgressWatchingCoordinator
import org.remdev.wetweex.execution.ExecutableTask
import org.remdev.wetweexapp.Strategy
import org.remdev.wetweexapp.domain.services.TextGenerator

class GeneratorCoordinator(generatorViewModel: GeneratorViewModel, private val textGenerator: TextGenerator)
    : ProgressWatchingCoordinator<GeneratorViewModel>(generatorViewModel) {

    fun generateString(strategy: Strategy, millis: Long) {
        val generationAction = {
            textGenerator.generateText(millis).text
        }
        val task = when(strategy) {
            Strategy.CANCELLABLE -> ExecutableTask.cancellable(generationAction)
            Strategy.NON_CANCELLABLE -> ExecutableTask.nonCancellable(generationAction)
            Strategy.CRUCIAL -> ExecutableTask.crucial(generationAction)
        }.onSuccess {
            viewModel.generatedText.setOrPost(it)
        }.onCancel {
            viewModel.generatedText.setOrPost("Cancelled")
        }.onError {
            viewModel.generatedText.setOrPost(it.message)
        }

        submitTask("Generate string", task)
    }
}