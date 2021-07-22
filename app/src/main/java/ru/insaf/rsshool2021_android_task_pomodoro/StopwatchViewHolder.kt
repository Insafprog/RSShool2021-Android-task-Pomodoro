package ru.insaf.rsshool2021_android_task_pomodoro

import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.insaf.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding
import java.util.*

private const val START_TIME: String = "00:00:00"
private const val START_BUTTON: String = "Start"
private const val PAUSE_BUTTON: String = "Pause"
private const val RESET_BUTTON: String = "Reset"
private const val UNIT_TEN_MS = 100L


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener
    ): RecyclerView.ViewHolder(binding.root),
    LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private var timer: CountDownTimer? = null

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun bind(stopwatch: Stopwatch) {
        binding.timer.text = stopwatch.currentMs.displayTime()

        when (stopwatch.status) {
            StopwatchStatus.STARTED -> {
                startTimer(stopwatch, stopwatch.currentMs)
            }
            StopwatchStatus.FINISHED -> {
                finishTimer(stopwatch)
            }
            StopwatchStatus.PAUSED -> {
                stopTimer(stopwatch)
            }
            StopwatchStatus.NEW -> {
                binding.cvProgress.isVisible = false
            }
        }

        initButtonsListener(stopwatch)
    }


    private fun initButtonsListener(stopwatch: Stopwatch) {
        binding.bTimer.setOnClickListener {
            when (stopwatch.status) {
                StopwatchStatus.STARTED -> {
                    stopTimer(stopwatch)
                }
                StopwatchStatus.PAUSED, StopwatchStatus.NEW -> {
                    startTimer(stopwatch, stopwatch.currentMs)
                }
                StopwatchStatus.FINISHED -> {
                    returnTimer(stopwatch)
                }
            }
        }
        binding.bDelete.setOnClickListener {
            listener.delete(stopwatch)
        }
    }

    private fun startTimer(stopwatch: Stopwatch, period: Long) {
        generateCV(stopwatch)
        listener.start(stopwatch)
        binding.bTimer.text = PAUSE_BUTTON

        binding.cvProgress.isVisible = true
        timer?.cancel()
        timer = getCountDownTimer(stopwatch, period)
        timer?.start()

        binding.indicator.startAnimation()
        binding.background.stopAnimation()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        generateCV(stopwatch)
        listener.pause(stopwatch)
        binding.bTimer.text = START_BUTTON
        binding.cvProgress.isVisible = true
        timer?.cancel()

        binding.indicator.stopAnimation()
        binding.background.stopAnimation()
    }

    private fun finishTimer(stopwatch: Stopwatch) {
        generateCV(stopwatch)
        listener.finish(stopwatch)
        binding.cvProgress.isVisible = true
        binding.timer.text = 0L.displayTime()
        binding.bTimer.text = RESET_BUTTON
        binding.indicator.stopAnimation()
        binding.background.startAnimation()
        stopwatch.startTime = null
    }

    private fun returnTimer(stopwatch: Stopwatch) {
        generateCV(stopwatch)
        listener.restartStopwatch(stopwatch)
        binding.cvProgress.isVisible = false
        listener.restartStopwatch(stopwatch)
        binding.indicator.stopAnimation()
        binding.background.stopAnimation()
        binding.timer.text = stopwatch.currentMs.displayTime()
        binding.bTimer.text = START_BUTTON
    }

    private fun ImageView.startAnimation() {
        isInvisible = false
        (drawable as? Animatable)?.start()
        (background as? AnimationDrawable)?.start()
    }

    private fun ImageView.stopAnimation() {
        isInvisible = true
        (drawable as? Animatable)?.stop()
        (background as? AnimationDrawable)?.stop()
    }

    private fun getCountDownTimer(stopwatch: Stopwatch, period: Long): CountDownTimer {
        return object: CountDownTimer(period, UNIT_TEN_MS) {
            override fun onTick(millisUntilFinished: Long) {
                if (stopwatch.status == StopwatchStatus.STARTED) {
                    generateCV(stopwatch){
                        this.cancel()
                        this.onFinish()
                    }
                }
                else stopTimer(stopwatch)
            }

            override fun onFinish() {
                finishTimer(stopwatch)
            }
        }
    }

    private fun generateCV(stopwatch: Stopwatch, func:() -> Unit = {}) {
        val time = Date().time - (stopwatch.startTime?:Date().time)
        if (time >= stopwatch.currentMs) {
            func()
        }
        else {
            binding.timer.text = (stopwatch.currentMs - time).displayTime()
            binding.cvProgress.setTime(stopwatch.totalMs, time + stopwatch.totalMs - stopwatch.currentMs)
            stopwatch.currentMs = stopwatch.currentMs - Date().time + (stopwatch.startTime?:0)
            stopwatch.startTime = Date().time
        }
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }
}

private fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}
