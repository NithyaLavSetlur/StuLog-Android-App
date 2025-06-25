package com.example.stulogandroidapp.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.stulogandroidapp.R
import java.text.SimpleDateFormat
import java.util.*

class ClockFragment : Fragment() {

    private lateinit var clockTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val timeFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            val currentTime = timeFormat.format(Date())
            clockTextView.text = currentTime
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_clock, container, false)
        clockTextView = view.findViewById(R.id.textClock)
        return view
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTimeRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTimeRunnable)
    }
}
