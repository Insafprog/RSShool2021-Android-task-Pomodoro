package ru.insaf.rsshool2021_android_task_pomodoro

data class Stopwatch(
    val id: Int,
    var startTime: Long?,
    var totalMs: Long,
    var currentMs: Long,
    var status: StopwatchStatus,
)
