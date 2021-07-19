package ru.insaf.rsshool2021_android_task_pomodoro

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DataSource(resources: Resources) {
    private val stopwatchLiveData = MutableLiveData<List<Stopwatch>>()

    fun addStopwatch(stopwatch: Stopwatch) {
        val currentList = stopwatchLiveData.value
        if (currentList == null) {
            stopwatchLiveData.postValue(listOf(stopwatch))
        }
        else {
            val updatedList = currentList.toMutableList()
            updatedList.add(stopwatch)
            stopwatchLiveData.postValue(updatedList)
        }
    }

    fun removeStopwatch(stopwatch: Stopwatch) {
        stopwatchLiveData.value?.let {
            val updatedList = it.toMutableList()
            updatedList.remove(stopwatch)
            stopwatchLiveData.postValue(updatedList)
        }
    }

    fun startStopwatch(stopwatch: Stopwatch) {
        stopwatchLiveData.value?.let {
            it.forEach { sw ->
                if (sw.id == stopwatch.id) sw.status = StopwatchStatus.STARTED
                else if (sw.status == StopwatchStatus.STARTED) sw.status = StopwatchStatus.PAUSED
            }
            stopwatchLiveData.postValue(it)
        }
    }

    fun pauseStopwatch(stopwatch: Stopwatch) {
        stopwatchLiveData.value?.let {
            it.forEach { sw ->
                if (sw.id == stopwatch.id) sw.status = StopwatchStatus.PAUSED
            }
            stopwatchLiveData.postValue(it)
        }
    }

    fun finishStopwatch(stopwatch: Stopwatch) {
        stopwatchLiveData.value?.let {
            it.forEach { sw ->
                if (sw.id == stopwatch.id) {
                    sw.status = StopwatchStatus.FINISHED
                    sw.currentMs = 0L
                }
            }
            stopwatchLiveData.postValue(it)
        }
    }

    fun returnStopwatch(stopwatch: Stopwatch) {
        stopwatchLiveData.value?.let {
            it.forEach { sw ->
                if (sw.id == stopwatch.id){
                    sw.status = StopwatchStatus.NEW
                    sw.currentMs = sw.totalMs
                }
            }
            stopwatchLiveData.postValue(it)
        }
    }

    val stopwatchList: LiveData<List<Stopwatch>>
        get() = stopwatchLiveData

    companion object {
        private var INSTANCE: DataSource? = null

        fun getDataSource(resources: Resources): DataSource {
            return synchronized(DataSource::class) {
                val newInstance = INSTANCE?: DataSource(resources)
                INSTANCE = newInstance
                newInstance
            }
        }
    }
}