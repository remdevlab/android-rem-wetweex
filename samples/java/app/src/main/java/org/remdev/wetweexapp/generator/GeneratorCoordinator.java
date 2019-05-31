package org.remdev.wetweexapp.generator;

import androidx.annotation.NonNull;
import org.remdev.wetweex.component.ProgressWatchingCoordinator;
import org.remdev.wetweex.execution.ExecutableTask;
import org.remdev.wetweexapp.Strategy;
import org.remdev.wetweexapp.domain.services.TextGenerator;

import java.util.concurrent.Callable;

public class GeneratorCoordinator extends ProgressWatchingCoordinator<GeneratorViewModel> {

    private final TextGenerator textGenerator;

    public GeneratorCoordinator(@NonNull GeneratorViewModel viewModel, TextGenerator textGenerator) {
        super(viewModel);
        this.textGenerator = textGenerator;
    }

    public void generateString(Strategy strategy, Long millis) {
        final Callable<String> generationAction = () -> textGenerator.generateText(millis).getText();
        ExecutableTask<String> task;
        switch(strategy) {
            case NON_CANCELLABLE:
                task = ExecutableTask.nonCancellable(generationAction);
                break;
            case CRUCIAL:
                task = ExecutableTask.crucial(generationAction);
                break;
            case CANCELLABLE:
            default:
                task = ExecutableTask.cancellable(generationAction);
                break;
        }

        task.onSuccess(it -> getViewModel().getGeneratedText().setOrPost(it))
                .onCancel(() -> getViewModel().getGeneratedText().setOrPost("Cancelled"))
                .onError(it -> getViewModel().getGeneratedText().setOrPost(it.getMessage()));

        submitTask("Generate string: " + strategy, task);
    }
}