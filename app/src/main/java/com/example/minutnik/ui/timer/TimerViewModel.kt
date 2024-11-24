package com.example.minutnik.ui.timer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.minutnik.TimeNumbers
import com.example.minutnik.Timer
import com.example.minutnik.TimerState

class TimerViewModel : ViewModel() {

    private val _timer = Timer(60)
    val timeSet: LiveData<Int> get() = _timer.timeSet
    val timeLeft: LiveData<Int> get() = _timer.timeLeft
    val timerState: LiveData<TimerState> get() = _timer.timerState

    private fun getTimeNumbers(value: Int?): TimeNumbers {
        var seconds = value
        if(seconds != null) {
            val minutes10: Int = seconds / 600
            seconds -= minutes10 * 600
            val minutes1: Int = seconds / 60
            seconds -= minutes1 * 60
            val seconds10: Int = seconds / 10
            seconds -= seconds10 * 10
            val seconds1: Int = seconds
            return TimeNumbers(minutes10, minutes1, seconds10, seconds1)
        }
        return TimeNumbers()

    }

    fun resetTimer() = _timer.resetTimer()

    fun restoreTimer(restoreTimeSet: Int, restoreTimeLeft: Int) = _timer.restoreTimer(restoreTimeSet, restoreTimeLeft)

    fun setTimer(timeNumbers: TimeNumbers) = _timer.setTimer(timeNumbers)

    fun updateTimer(seconds: Int) = _timer.updateTimer(seconds)

    fun startTimer() = _timer.startTimer(viewModelScope)

    fun pauseTimer() = _timer.pauseTimer()

    fun stopTimer() = _timer.stopTimer()

    fun getTimeLeftNumbers() = getTimeNumbers(timeLeft.value)

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}