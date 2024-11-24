package com.example.minutnik.ui.timer

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.minutnik.MainActivity
import com.example.minutnik.R
import com.example.minutnik.TimeNumbers
import com.example.minutnik.TimerState
import com.example.minutnik.databinding.FragmentTimerBinding

class TimerFragment : Fragment() {

    private val timerViewModel: TimerViewModel by activityViewModels()

    private lateinit var minutes10: TextView
    private lateinit var minutes1: TextView
    private lateinit var seconds10: TextView
    private lateinit var seconds1: TextView
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var stopButton: Button

    private lateinit var minutes10PlusButton: Button
    private lateinit var minutes1PlusButton: Button
    private lateinit var seconds10PlusButton: Button
    private lateinit var seconds1PlusButton: Button
    private lateinit var minutes10MinusButton: Button
    private lateinit var minutes1MinusButton: Button
    private lateinit var seconds10MinusButton: Button
    private lateinit var seconds1MinusButton: Button

    private lateinit var timeStateObserver: Observer<TimerState>

    private var _binding: FragmentTimerBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val appContext = requireContext().applicationContext
        val sharedPreferences = appContext.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val notificationIntent = Intent(appContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        val pendingIntent = PendingIntent.getActivity(appContext, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(appContext, "notificationChannel")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(getString(R.string.notification_timer_title))
            .setContentText(getString(R.string.notification_timer_desc))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        initComponents()

        timeStateObserver = Observer { timerState ->
            when(timerState)
            {
                TimerState.RUNNING -> {
                    startButton.text = appContext.getString(R.string.start_button)
                    startButton.setEnabled(false)
                    pauseButton.setEnabled(true)
                    stopButton.setEnabled(true)
                    sharedPreferences.edit().putBoolean("notification_sent", false).apply()
                }
                TimerState.PAUSED -> {
                    startButton.text = appContext.getString(R.string.start_button)
                    startButton.setEnabled(true)
                    pauseButton.setEnabled(false)
                    stopButton.setEnabled(true)
                    sharedPreferences.edit().putBoolean("notification_sent", false).apply()
                }
                TimerState.STOPPED -> {
                    startButton.text = appContext.getString(R.string.start_button)
                    startButton.setEnabled(true)
                    pauseButton.setEnabled(false)
                    stopButton.setEnabled(false)
                    sharedPreferences.edit().putBoolean("notification_sent", false).apply()
                }
                TimerState.OVER -> {
                    startButton.text = appContext.getString(R.string.reset_button)
                    startButton.setEnabled(true)
                    pauseButton.setEnabled(false)
                    stopButton.setEnabled(false)

                    with(NotificationManagerCompat.from(appContext)) {
                        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                            if (!sharedPreferences.getBoolean("notification_sent", false)) {
                                sharedPreferences.edit().putBoolean("notification_sent", true).apply()
                                notify(1, notification.build())
                            }
                        }
                    }
                }
            }
        }
        timerViewModel.timerState.observeForever(timeStateObserver)
        timerViewModel.timeLeft.observe(viewLifecycleOwner) {
            updateTimerText(timerViewModel.getTimeLeftNumbers())
        }

        startButton.setOnClickListener {
            if (timerViewModel.timerState.value == TimerState.OVER) {
                timerViewModel.resetTimer()
                timerViewModel.stopTimer()
            }
            else {
                timerViewModel.startTimer()
            }
        }
        pauseButton.setOnClickListener {
            timerViewModel.pauseTimer()
        }
        stopButton.setOnClickListener {
            timerViewModel.stopTimer()
        }

        minutes10PlusButton.setOnClickListener {
            timerViewModel.updateTimer(600)
        }
        minutes1PlusButton.setOnClickListener {
            timerViewModel.updateTimer(60)
        }
        seconds10PlusButton.setOnClickListener {
            timerViewModel.updateTimer(10)
        }
        seconds1PlusButton.setOnClickListener {
            timerViewModel.updateTimer(1)
        }
        minutes10MinusButton.setOnClickListener {
            timerViewModel.updateTimer(-600)
        }
        minutes1MinusButton.setOnClickListener {
            timerViewModel.updateTimer(-60)
        }
        seconds10MinusButton.setOnClickListener {
            timerViewModel.updateTimer(-10)
        }
        seconds1MinusButton.setOnClickListener {
            timerViewModel.updateTimer(-1)
        }

        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        timerViewModel.timerState.removeObserver(timeStateObserver)
        onSaveInstanceState(Bundle())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("timeSet", timerViewModel.timeSet.value!!)
        outState.putInt("timeLeft", timerViewModel.timeLeft.value!!)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            timerViewModel.restoreTimer(it.getInt("timeSet"), savedInstanceState.getInt("timeLeft"))
        }
    }

    private fun updateTimerText(timeNumbers: TimeNumbers) {
        minutes10.text = timeNumbers.minutes10.toString()
        minutes1.text = timeNumbers.minutes1.toString()
        seconds10.text = timeNumbers.seconds10.toString()
        seconds1.text = timeNumbers.seconds1.toString()
    }

    private fun initComponents() {
        minutes10 = binding.minutes10
        minutes1 = binding.minutes1
        seconds10 = binding.seconds10
        seconds1 = binding.seconds1
        startButton = binding.startButton
        pauseButton = binding.pauseButton
        stopButton = binding.stopButton

        minutes10PlusButton = binding.minutes10PlusButton
        minutes1PlusButton = binding.minutes1PlusButton
        seconds10PlusButton = binding.seconds10PlusButton
        seconds1PlusButton = binding.seconds1PlusButton
        minutes10MinusButton = binding.minutes10MinusButton
        minutes1MinusButton = binding.minutes1MinusButton
        seconds10MinusButton = binding.seconds10MinusButton
        seconds1MinusButton = binding.seconds1MinusButton
    }
}