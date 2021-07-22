package ru.insaf.rsshool2021_android_task_pomodoro

import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimationDrawable
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.core.view.isInvisible
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

        if(stopwatch.status == StopwatchStatus.STARTED) {
            startTimer(stopwatch, stopwatch.currentMs)
        }
        else if(stopwatch.status == StopwatchStatus.FINISHED) {
            finishTimer(stopwatch)
        }
        else {
            stopTimer()
        }

        initButtonsListener(stopwatch)
    }


    private fun initButtonsListener(stopwatch: Stopwatch) {
        binding.bTimer.setOnClickListener {
            when (stopwatch.status) {
                StopwatchStatus.STARTED -> {
                    listener.pause(stopwatch)
                    stopTimer()
                }
                StopwatchStatus.PAUSED, StopwatchStatus.NEW -> {
                    listener.start(stopwatch)
                    startTimer(stopwatch, stopwatch.currentMs)
                }
                StopwatchStatus.FINISHED -> {
                    listener.restartStopwatch(stopwatch)
                    binding.indicator.stopAnimation()
                    binding.background.stopAnimation()
                    binding.timer.text = stopwatch.currentMs.displayTime()
                    binding.bTimer.text = START_BUTTON
                }
            }
        }
        binding.bDelete.setOnClickListener {
            listener.delete(stopwatch)
        }
    }

    private fun startTimer(stopwatch: Stopwatch, period: Long) {
        binding.bTimer.text = PAUSE_BUTTON

        timer?.cancel()
        timer = getCountDownTimer(stopwatch, period)
        timer?.start()

        binding.indicator.startAnimation()
        binding.background.stopAnimation()
    }

    private fun stopTimer() {
        binding.bTimer.text = START_BUTTON

        timer?.cancel()

        binding.indicator.stopAnimation()
        binding.background.stopAnimation()
    }

    private fun finishTimer(stopwatch: Stopwatch) {
        listener.finish(stopwatch)
        binding.timer.text = 0L.displayTime()
        binding.bTimer.text = RESET_BUTTON
        binding.indicator.stopAnimation()
        binding.background.startAnimation()
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
                val time = Date().time - (stopwatch.startTime?:0)
                if (stopwatch.status == StopwatchStatus.STARTED) {
                    if (time >= stopwatch.currentMs) {
                        this.cancel()
                        this.onFinish()
                    }
                    else binding.timer.text = (stopwatch.currentMs - time).displayTime()
                }
                else stopTimer()
            }

            override fun onFinish() {
                finishTimer(stopwatch)
            }
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
