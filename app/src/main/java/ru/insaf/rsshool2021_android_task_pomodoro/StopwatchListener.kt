package ru.insaf.rsshool2021_android_task_pomodoro

interface StopwatchListener {
    fun start(stopwatch: Stopwatch)
    fun pause(stopwatch: Stopwatch)
    fun delete(stopwatch: Stopwatch)
    fun finish(stopwatch: Stopwatch)
    fun restartStopwatch(stopwatch: Stopwatch)
    fun updateStopwatchCurrentMs(stopwatch: Stopwatch, ms: Long)
}