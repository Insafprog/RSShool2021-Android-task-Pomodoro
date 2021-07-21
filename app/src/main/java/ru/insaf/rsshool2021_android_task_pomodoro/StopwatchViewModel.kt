package ru.insaf.rsshool2021_android_task_pomodoro

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StopwatchViewModel(private val dataSource: DataSource): ViewModel() {

    val stopwatchLiveData = dataSource.stopwatchList

    fun insertStopwatch(stopwatchId: Int, stopwatchTotalMs: Long) {
        val newStopwatch = Stopwatch(
            stopwatchId,
            stopwatchTotalMs,
            stopwatchTotalMs,
            StopwatchStatus.NEW
        )

        dataSource.addStopwatch(newStopwatch)
    }

    fun removeStopwatch(stopwatch: Stopwatch) {
        dataSource.removeStopwatch(stopwatch)
    }

    fun pauseStopwatch(stopwatch: Stopwatch) {
        dataSource.pauseStopwatch(stopwatch)
    }

    fun startStopwatch(stopwatch: Stopwatch) {
        dataSource.startStopwatch(stopwatch)
    }

    fun finishedStopwatch(stopwatch: Stopwatch) {
        dataSource.finishStopwatch(stopwatch)
    }

    fun returnStopwatch(stopwatch: Stopwatch) {
        dataSource.returnStopwatch(stopwatch)
    }

    fun updateStopwatchCurrentMs(stopwatch: Stopwatch, ms: Long) {
        dataSource.updateCurrentMs(stopwatch, ms)
    }

}

class StopwatchViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StopwatchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StopwatchViewModel(
                dataSource = DataSource.getDataSource(context.resources)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}