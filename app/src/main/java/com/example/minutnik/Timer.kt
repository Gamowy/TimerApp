package com.example.minutnik

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class TimerState {
    RUNNING, PAUSED, STOPPED, OVER
}
data class TimeNumbers(val minutes10: Int = 0, val minutes1: Int = 0, val seconds10: Int = 0, val seconds1: Int = 0)

class Timer(seconds: Int) {
    var timerState = MutableLiveData<TimerState>().apply {
        value = TimerState.STOPPED
    }
    var timeSet = MutableLiveData<Int>().apply {
        value = seconds
    }
    var timeLeft = MutableLiveData<Int>().apply {
        value = seconds
    }
    private var timerJob: Job? = null

    fun resetTimer() {
        timeLeft.value = timeSet.value
    }

    fun restoreTimer(restoreTimeSet: Int, restoreTimeLeft: Int) {
        timeSet.value = restoreTimeSet
        timeLeft.value = restoreTimeLeft
    }

    fun setTimer(timeNumbers: TimeNumbers) {
        timeSet.value = timeNumbers.minutes10 * 10 * 60 + timeNumbers.minutes1 * 60 + timeNumbers.seconds10 * 10 + timeNumbers.seconds1
        timeLeft.value = timeSet.value
    }

    fun updateTimer(seconds: Int) {
        var newTime = timeLeft.value!! + seconds
        newTime = if (newTime < 0) 0 else newTime
        timeLeft.value = newTime
        if(timeLeft.value == 0) {
            timeOver()
        }
    }

    fun startTimer(scope: CoroutineScope) {
        if (timerState.value!! != TimerState.RUNNING) {
            timerJob = scope.launch(Dispatchers.Main) {
                timerState.value = TimerState.RUNNING
                while (timeLeft.value!! > 0 && isActive) {
                    delay(1000L)
                    timeLeft.value = timeLeft.value!! - 1
                }
                if(timeLeft.value == 0) {
                    timeOver()
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        timerJob = null
        timerState.value = TimerState.PAUSED
    }

    fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        timerState.value = TimerState.STOPPED
        resetTimer()
    }

    private fun timeOver() {
        timerJob?.cancel()
        timerJob = null
        timerState.value = TimerState.OVER
    }
}