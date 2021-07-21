package ru.insaf.rsshool2021_android_task_pomodoro

import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean

class TimerTask internal constructor(
    action: suspend () -> Unit
) {
    private val isRunning = AtomicBoolean(true)
    private var job: Job? = null
    private val tryAction = suspend {
        action()
    }

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            delay(100)
            if (isRunning.get()) {
                tryAction()
            }
        }
    }

    private fun shutdown() {
        isRunning.set(false)
    }

    fun cancel() {
        shutdown()
        job?.cancel("cancel() called")
    }
}