package ru.insaf.rsshool2021_android_task_pomodoro

import StopwatchAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import ru.insaf.rsshool2021_android_task_pomodoro.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), StopwatchListener {

    private lateinit var binding: ActivityMainBinding

    private val stopwatchAdapter = StopwatchAdapter(this)
    private val stopwatches = mutableListOf<Stopwatch>()
    private var nextId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = stopwatchAdapter
        }

        binding.bAdd.setOnClickListener {
            stopwatches.add(Stopwatch(nextId++, binding.etMinutes.text.toString().toLong() * 1000, binding.etMinutes.text.toString().toLong() * 1000, StopwatchStatus.NEW))
            stopwatchAdapter.submitList(stopwatches.toList())
        }
    }

    override fun start(id: Int) {
        changeStopwatch(id, StopwatchStatus.STARTED)
    }

    override fun pause(id: Int) {
        changeStopwatch(id, StopwatchStatus.PAUSED)
    }

    override fun delete(id: Int) {
        stopwatches.remove(stopwatches.find { it.id == id })
        stopwatchAdapter.submitList(stopwatches.toList())
    }

    private fun changeStopwatch(id: Int, status: StopwatchStatus, allPause: Boolean = false) {
        val newTimers = mutableListOf<Stopwatch>()
        stopwatches.forEach {
            if (it.id == id) {
                newTimers.add(Stopwatch(it.id, it.totalMs, it.currentMs, status))
            }
            else {
                newTimers.add(it)
            }
        }
        stopwatchAdapter.submitList(stopwatches)
        stopwatches.clear()
        stopwatches.addAll(newTimers)
    }
}