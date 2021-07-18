package ru.insaf.rsshool2021_android_task_pomodoro

interface StopwatchListener {
    fun start(id: Int)
    fun pause(id: Int)
    fun delete(id: Int)
}