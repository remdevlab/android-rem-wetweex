package org.remdev.wetweexapp.generator

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import org.remdev.wetweex.activity.BaseTweexProgressObservingActivity
import org.remdev.wetweex.component.CoordinatorsContainer
import org.remdev.wetweexapp.DemoAppCoordinatorsContainer
import by.nalivajr.tweexdemo.R
import org.remdev.wetweexapp.Strategy
import org.remdev.wetweexapp.textGenerator

class GeneratorActivity : BaseTweexProgressObservingActivity<GeneratorViewModel, GeneratorCoordinator>() {

    private val text by lazy { findViewById<TextView>(R.id.text) }
    private val generateCancellable by lazy { findViewById<TextView>(R.id.generate_button_c) }
    private val generateCrucial by lazy { findViewById<TextView>(R.id.generate_button_cr) }
    private val generateNonCancellable by lazy { findViewById<TextView>(R.id.generate_button_nc) }
    private val cancel by lazy { findViewById<TextView>(R.id.cancel_button) }
    private val generationTime by lazy { findViewById<EditText>(R.id.generation_time) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generator)
        viewModel.generatedText.observe(this, Observer<String> {
            text.text = it
        })
        generateCancellable.setOnClickListener {
            startGenertion(Strategy.CANCELLABLE)
        }
        generateNonCancellable.setOnClickListener {
            startGenertion(Strategy.NON_CANCELLABLE)
        }
        generateCrucial.setOnClickListener {
            startGenertion(Strategy.CRUCIAL)
        }
        cancel.setOnClickListener {
            coordinator.tryCancelAll()
        }
    }

    private fun startGenertion(strategy: Strategy) {
        val millis = generationTime.text.toString().toLong() * 1000
        coordinator.generateString(strategy, millis)
    }

    override fun createViewModel(): GeneratorViewModel =
        GeneratorViewModel()

    override fun getCoordinatorContainer(): CoordinatorsContainer = DemoAppCoordinatorsContainer

    override fun createCoordinator(viewModel: GeneratorViewModel): GeneratorCoordinator
            = GeneratorCoordinator(viewModel, textGenerator)
}
