package ru.insaf.rsshool2021_android_task_pomodoro

import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimationDrawable
//import android.os.CountDownTimer
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import ru.insaf.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

private const val START_TIME: String = "00:00:00"
private const val START_BUTTON: String = "Start"
private const val PAUSE_BUTTON: String = "Pause"
private const val RESET_BUTTON: String = "Reset"
private const val UNIT_TEN_MS = 1000L


class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener
    ): RecyclerView.ViewHolder(binding.root),
    LifecycleOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private var timer: TimerTask? = null

    init {
        lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
    }

    fun bind(stopwatch: Stopwatch) {

        when (stopwatch.status) {
            StopwatchStatus.STARTED -> start(stopwatch)
            StopwatchStatus.FINISHED -> finish(stopwatch)
            else -> pause(stopwatch)
        }

        initButtonsListener(stopwatch)
    }


    private fun initButtonsListener(stopwatch: Stopwatch) {
        binding.bTimer.setOnClickListener {
            when (stopwatch.status) {
                StopwatchStatus.STARTED -> {
                    start(stopwatch)

                }
                StopwatchStatus.PAUSED, StopwatchStatus.NEW -> {
                    pause(stopwatch)
                }
                StopwatchStatus.FINISHED -> {
                    finish(stopwatch)
                }
            }
        }
        binding.bDelete.setOnClickListener {
            listener.delete(stopwatch)
        }
    }

    private fun start(stopwatch: Stopwatch) {
        startTimer(stopwatch)
        binding.bTimer.text = PAUSE_BUTTON
        binding.timer.text = stopwatch.currentMs.displayTime()
        listener.start(stopwatch)
        binding.indicator.startAnimation()
        binding.background.stopAnimation()
    }

    private fun pause(stopwatch: Stopwatch) {
        startTimer(stopwatch)
        binding.bTimer.text = START_BUTTON
        binding.timer.text = stopwatch.currentMs.displayTime()
        listener.pause(stopwatch)
        binding.indicator.stopAnimation()
        binding.background.stopAnimation()
    }

    private fun finish(stopwatch: Stopwatch) {
        startTimer(stopwatch)
        binding.bTimer.text = RESET_BUTTON
        binding.timer.text = stopwatch.currentMs.displayTime()
        listener.finish(stopwatch)
        binding.indicator.stopAnimation()
        binding.background.startAnimation()
    }


    private fun startTimer(stopwatch: Stopwatch) {
        binding.bTimer.text = PAUSE_BUTTON

        timer?.cancel()
        timer = getCountDownTimer(stopwatch)
        timer?.start()

        binding.indicator.startAnimation()
        binding.background.stopAnimation()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.bTimer.text = START_BUTTON

        timer?.cancel()

        binding.indicator.stopAnimation()
        binding.background.stopAnimation()
    }
//
//    private fun finishTimer(stopwatch: Stopwatch) {
//        listener.finish(stopwatch)
//        binding.timer.text = 0L.displayTime()
//        binding.bTimer.text = RESET_BUTTON
//        binding.indicator.stopAnimation()
//        binding.background.startAnimation()
//    }

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

    private fun getCountDownTimer(stopwatch: Stopwatch): TimerTask {
        return  TimerTask {
            listener.updateStopwatchCurrentMs(stopwatch, 100)
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
