package org.remdev.wetweexapp.generator;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import by.nalivajr.tweexdemo.R;
import org.remdev.wetweex.activity.BaseTweexProgressObservingActivity;
import org.remdev.wetweex.component.CoordinatorsContainer;
import org.remdev.wetweexapp.DemoAppCoordinatorsContainer;
import org.remdev.wetweexapp.Dependencies;
import org.remdev.wetweexapp.Strategy;

public class GeneratorActivity extends BaseTweexProgressObservingActivity<GeneratorViewModel, GeneratorCoordinator> {

    private TextView text;
    private TextView generateCancellable;
    private TextView generateCrucial;
    private TextView generateNonCancellable;
    private TextView cancel;
    private EditText generationTime;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);

        text = findViewById(R.id.text);
        generateCancellable = findViewById(R.id.generate_button_c);
        generateCrucial = findViewById(R.id.generate_button_cr);
        generateNonCancellable = findViewById(R.id.generate_button_nc);
        cancel = findViewById(R.id.cancel_button);
        generationTime = findViewById(R.id.generation_time);

        getViewModel().getGeneratedText().observe(this, text::setText);
        generateCancellable.setOnClickListener(__ -> startGenertion(Strategy.CANCELLABLE));
        generateNonCancellable.setOnClickListener(__ -> startGenertion(Strategy.NON_CANCELLABLE));
        generateCrucial.setOnClickListener(__ -> startGenertion(Strategy.CRUCIAL));
        cancel.setOnClickListener(__ -> getCoordinator().tryCancelAll());

    }

    private void startGenertion(Strategy strategy) {
        long millis = Long.parseLong(generationTime.getText().toString()) * 1000L;
        getCoordinator().generateString(strategy, millis);
    }

    @NonNull
    @Override
    protected GeneratorViewModel createViewModel() {
        return new GeneratorViewModel();
    }

    @NonNull
    @Override
    protected GeneratorCoordinator createCoordinator(@NonNull GeneratorViewModel generatorViewModel) {
        return new GeneratorCoordinator(generatorViewModel, Dependencies.textGenerator());
    }

    @NonNull
    @Override
    protected CoordinatorsContainer getCoordinatorContainer() {
        return DemoAppCoordinatorsContainer.getInstance();
    }
}
