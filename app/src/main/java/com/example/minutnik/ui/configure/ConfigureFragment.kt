package com.example.minutnik.ui.configure

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.minutnik.R
import com.example.minutnik.TimeNumbers
import com.example.minutnik.databinding.FragmentConfigureBinding
import com.example.minutnik.ui.timer.TimerViewModel

class ConfigureFragment : Fragment() {

    private val timerViewModel: TimerViewModel by activityViewModels()

    private lateinit var minutes10Picker: NumberPicker
    private lateinit var minutes1Picker: NumberPicker
    private lateinit var seconds10Picker: NumberPicker
    private lateinit var seconds1Picker: NumberPicker

    private var _binding: FragmentConfigureBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val setTimerToast = Toast.makeText(this.activity, R.string.timer_set, Toast.LENGTH_SHORT)
        val resetTimerToast = Toast.makeText(this.activity, R.string.timer_reset, Toast.LENGTH_SHORT)

        _binding = FragmentConfigureBinding.inflate(inflater, container, false)
        initComponents()

        binding.setTimerButton.setOnClickListener {
            val timeNumbers = TimeNumbers(minutes10Picker.value, minutes1Picker.value, seconds10Picker.value, seconds1Picker.value)
            timerViewModel.setTimer(timeNumbers)
            timerViewModel.stopTimer()
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.view?.windowToken, 0)
            setTimerToast.show()
        }
        binding.resetButton.setOnClickListener {
            val timeNumbers = TimeNumbers(0, 0, 0, 0)
            updateNumberPickers(timeNumbers)
            timerViewModel.setTimer(timeNumbers)
            timerViewModel.stopTimer()
            timerViewModel.stopTimer()
            resetTimerToast.show()
        }

        val root: View = binding.root
        return root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("minutes10", minutes10Picker.value)
        outState.putInt("minutes1", minutes1Picker.value)
        outState.putInt("seconds10", seconds10Picker.value)
        outState.putInt("seconds1", seconds1Picker.value)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            minutes10Picker.value = it.getInt("minutes10")
            minutes1Picker.value = it.getInt("minutes1")
            seconds10Picker.value = it.getInt("seconds10")
            seconds1Picker.value = it.getInt("seconds1")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateNumberPickers(timeNumbers: TimeNumbers) {
        minutes10Picker.value = timeNumbers.minutes10
        minutes1Picker.value = timeNumbers.minutes1
        seconds10Picker.value = timeNumbers.seconds10
        seconds1Picker.value = timeNumbers.seconds1
    }

    private fun initComponents() {
        minutes10Picker = binding.minutes10Picker
        minutes1Picker = binding.minutes1Picker
        seconds10Picker = binding.seconds10Picker
        seconds1Picker = binding.seconds1Picker
        minutes10Picker.minValue = 0
        minutes10Picker.maxValue = 9
        minutes1Picker.minValue = 0
        minutes1Picker.maxValue = 9
        seconds10Picker.minValue = 0
        seconds10Picker.maxValue = 5
        seconds10Picker.minValue = 0
        seconds1Picker.maxValue = 9
    }
}