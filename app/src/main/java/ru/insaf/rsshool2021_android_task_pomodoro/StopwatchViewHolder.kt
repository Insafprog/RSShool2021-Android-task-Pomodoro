package ru.insaf.rsshool2021_android_task_pomodoro

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.os.CountDownTimer
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import ru.insaf.rsshool2021_android_task_pomodoro.databinding.StopwatchItemBinding

private const val START_TIME: String = "00:00:00:00"
private const val UNIT_TEN_MS = 10L

class StopwatchViewHolder(
    private val binding: StopwatchItemBinding,
    private val listener: StopwatchListener
    ): RecyclerView.ViewHolder(binding.root) {

    private var timer: CountDownTimer? = null

    fun bind(stopwatch: Stopwatch) {
        binding.timer.text = stopwatch.currentMs.displayTime()

        if(stopwatch.status == StopwatchStatus.STARTED)
            startTimer(stopwatch, stopwatch.currentMs)
        else
            stopTimer(stopwatch)

        initButtonsListener(stopwatch)
    }

    private fun initButtonsListener(stopwatch: Stopwatch) {
        binding.bTimer.setOnClickListener {
            if (stopwatch.status == StopwatchStatus.STARTED)
                listener.pause(stopwatch.id)
            else if (stopwatch.status == StopwatchStatus.PAUSED || stopwatch.status == StopwatchStatus.NEW)
                listener.start(stopwatch.id)
        }
        binding.bDelete.setOnClickListener {
            listener.delete(stopwatch.id)
        }
    }

    private fun startTimer(stopwatch: Stopwatch, period: Long) {
        binding.bTimer.text = "Pause"

        timer?.cancel()
        timer = getCountDownTimer(stopwatch, period)
        timer?.start()

        binding.indicator.startAnimation()
        binding.background.stopAnimation()
    }

    private fun stopTimer(stopwatch: Stopwatch) {
        binding.bTimer.text = "Start"

        timer?.cancel()

        binding.indicator.stopAnimation()
    }

    private fun finishTimer(stopwatch: Stopwatch) {
        stopwatch.status = StopwatchStatus.FINISHED
        binding.timer.text = 0L.displayTime()
        binding.bTimer.text = "Reset"
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
                binding.timer.text = millisUntilFinished.displayTime()
                stopwatch.currentMs = millisUntilFinished
            }

            override fun onFinish() {
                finishTimer(stopwatch)
            }
        }
    }
}

private fun Long.displayTime(): String {
    if (this <= 0L) {
        return START_TIME
    }
    val h = this / 1000 / 3600
    val m = this / 1000 % 3600 / 60
    val s = this / 1000 % 60
    val ms = this % 1000 /10

    return "${displaySlot(h)}:${displaySlot(m)}:${displaySlot(s)}:${displaySlot(ms)}"
}

private fun displaySlot(count: Long): String {
    return if (count / 10L > 0) {
        "$count"
    } else {
        "0$count"
    }
}
