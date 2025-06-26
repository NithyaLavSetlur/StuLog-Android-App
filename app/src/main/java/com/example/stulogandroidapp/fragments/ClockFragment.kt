package com.example.stulogandroidapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.stulogandroidapp.R
import java.text.SimpleDateFormat
import java.util.*

class ClockFragment : Fragment() {

    private lateinit var localTimeText: TextView
    private lateinit var stopwatchText: TextView
    private lateinit var startPauseButton: Button
    private lateinit var resetButton: Button
    private lateinit var logButton: Button
    private lateinit var logContainer: LinearLayout

    private var stopwatchRunning = false
    private var stopwatchTime = 0L
    private val stopwatchHandler = Handler(Looper.getMainLooper())
    private val timeHandler = Handler(Looper.getMainLooper())

    private val stopwatchRunnable = object : Runnable {
        override fun run() {
            if (stopwatchRunning) {
                stopwatchTime += 1000
                updateStopwatchDisplay()
                stopwatchHandler.postDelayed(this, 1000)
            }
        }
    }

    private val timeRunnable = object : Runnable {
        override fun run() {
            updateLocalTimeDisplay()
            timeHandler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_clock, container, false)

        localTimeText = view.findViewById(R.id.localTimeText)
        stopwatchText = view.findViewById(R.id.stopwatchText)
        startPauseButton = view.findViewById(R.id.startPauseButton)
        resetButton = view.findViewById(R.id.resetButton)
        logButton = view.findViewById(R.id.logButton)
        logContainer = view.findViewById(R.id.logContainer)

        startPauseButton.setOnClickListener { toggleStopwatch() }
        resetButton.setOnClickListener { resetStopwatch() }
        logButton.setOnClickListener { logStopwatchTime() }

        timeHandler.post(timeRunnable)
        return view
    }

    private fun toggleStopwatch() {
        stopwatchRunning = !stopwatchRunning
        startPauseButton.text = if (stopwatchRunning) "Pause" else "Start"

        if (stopwatchRunning) {
            stopwatchHandler.post(stopwatchRunnable)
        } else {
            stopwatchHandler.removeCallbacks(stopwatchRunnable)
        }
    }

    private fun resetStopwatch() {
        stopwatchRunning = false
        stopwatchTime = 0L
        startPauseButton.text = "Start"
        updateStopwatchDisplay()
        stopwatchHandler.removeCallbacks(stopwatchRunnable)
    }

    private fun logStopwatchTime() {
        val logText = TextView(requireContext())
        logText.text = formatTime(stopwatchTime)
        logText.textSize = 16f
        logContainer.addView(logText)
    }

    private fun updateStopwatchDisplay() {
        stopwatchText.text = formatTime(stopwatchTime)
    }

    private fun updateLocalTimeDisplay() {
        val formatter = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        localTimeText.text = formatter.format(Date())
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopwatchHandler.removeCallbacks(stopwatchRunnable)
        timeHandler.removeCallbacks(timeRunnable)
    }
}
