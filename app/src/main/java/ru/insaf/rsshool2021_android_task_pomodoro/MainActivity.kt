package ru.insaf.rsshool2021_android_task_pomodoro

import StopwatchAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import androidx.recyclerview.widget.LinearLayoutManager
import ru.insaf.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private var nextId = 0
    private val stopwatchViewModel by viewModels<StopwatchViewModel> {
        StopwatchViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        stopwatchViewModel.stopwatchLiveData.observe(this, {
            it?.let {
                stopwatchAdapter.submitList(it as MutableList<Stopwatch>)
            }
        })

        binding.bAdd.setOnClickListener {
            stopwatchViewModel.insertStopwatch(nextId++, binding.etMinutes.text.toString().toLong() * 1000)
        }
    }

    override fun start(stopwatch: Stopwatch) {
        stopwatchViewModel.startStopwatch(stopwatch)
    }

    override fun pause(stopwatch: Stopwatch) {
        stopwatchViewModel.pauseStopwatch(stopwatch)
    }

    override fun delete(stopwatch: Stopwatch) {
        stopwatchViewModel.removeStopwatch(stopwatch)
    }

    override fun finish(stopwatch: Stopwatch) {
        stopwatchViewModel.finishedStopwatch(stopwatch)
    }

    override fun restartStopwatch(stopwatch: Stopwatch) {
        stopwatchViewModel.returnStopwatch(stopwatch)
    }
}