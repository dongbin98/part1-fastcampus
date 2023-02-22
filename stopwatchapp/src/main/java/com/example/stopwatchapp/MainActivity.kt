package com.example.stopwatchapp

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.example.stopwatchapp.databinding.ActivityMainBinding
import com.example.stopwatchapp.databinding.DialogCountdownSettingBinding
import java.util.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var countdownSecond = 10
    private var currentCountdownDeciSecond = countdownSecond * 10
    private var currentDeciSecond = 0
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countdownTextView.setOnClickListener {
            showCountdownSettingDialog()
        }

        binding.startButton.setOnClickListener {
            start()
            binding.startButton.isVisible = false
            binding.stopButton.isVisible = false
            binding.pauseButton.isVisible = true
            binding.lapButton.isVisible = true
        }
        binding.stopButton.setOnClickListener {
            showAlertDialog()
        }
        binding.pauseButton.setOnClickListener {
            binding.startButton.isVisible = true
            binding.stopButton.isVisible = true
            binding.pauseButton.isVisible = false
            binding.lapButton.isVisible = false
            pause()
        }
        binding.lapButton.setOnClickListener {
            lap()
        }
        initCountdownView()
    }

    private fun initCountdownView() {
        binding.countdownTextView.text = String.format("%02d", countdownSecond)
        binding.countdownProgressBar.progress = 100
    }

    private fun start() {
        timer = timer(initialDelay = 0, period = 100) {
            if (currentCountdownDeciSecond == 0) {
                currentDeciSecond += 1

                val minutes = currentDeciSecond.div(10) / 60
                val second = currentDeciSecond.div(10) % 60
                val deciSeconds = currentDeciSecond % 10
                runOnUiThread {
                    binding.timeTextView.text = String.format("%02d:%02d", minutes, second)
                    binding.tickTextView.text = deciSeconds.toString()

                    binding.countdownGroup.isVisible = false
                }
            } else {
                currentCountdownDeciSecond -= 1
                val second = currentCountdownDeciSecond.div(10)
                val progress = currentCountdownDeciSecond / countdownSecond.times(10f) * 100

                binding.root.post {
                    binding.countdownTextView.text = String.format("%02d", second)
                    binding.countdownProgressBar.progress = progress.toInt()
                }
            }
            if (currentDeciSecond == 0 && currentCountdownDeciSecond < 31 && currentCountdownDeciSecond % 10 == 0) {
                val toneType = if(currentCountdownDeciSecond == 0) ToneGenerator.TONE_CDMA_HIGH_L else ToneGenerator.TONE_CDMA_ANSWER
                ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME)
                    .startTone(toneType, 100)
            }
        }
    }

    private fun stop() {
        binding.startButton.isVisible = true
        binding.stopButton.isVisible = true
        binding.pauseButton.isVisible = false
        binding.lapButton.isVisible = false

        currentDeciSecond = 0
        binding.timeTextView.text = "00:00"
        binding.tickTextView.text = "0"

        binding.countdownGroup.isVisible = true
        initCountdownView()
        binding.lapContainerLinearLayout.removeAllViews()
    }

    private fun pause() {
        timer?.cancel()
        timer = null
    }

    private fun lap() {
        if (currentDeciSecond == 0) return
        val container = binding.lapContainerLinearLayout
        val lapTextView = TextView(this).apply {
            textSize = 20f
            gravity = Gravity.CENTER
            val minutes = currentDeciSecond.div(10) / 60
            val second = currentDeciSecond.div(10) % 60
            val deciSeconds = currentDeciSecond % 10
            text = container.childCount.inc().toString() + String.format(
                ". %02d:%02d %01d",
                minutes,
                second,
                deciSeconds
            )
            setPadding(30)
        }.let { labTextView ->
            container.addView(labTextView, 0)
        }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("예") { _, _ ->
                stop()
            }
            setNegativeButton("노", null)
        }.show()
    }

    private fun showCountdownSettingDialog() {
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countdownSecondPicker) {
                maxValue = 20
                minValue = 0
                value = countdownSecond
            }
            setTitle("시간설정")
            setView(dialogBinding.root)
            setPositiveButton("화긴") { _, _ ->
                countdownSecond = dialogBinding.countdownSecondPicker.value
                currentCountdownDeciSecond = countdownSecond * 10
                binding.countdownTextView.text = String.format("%02d", countdownSecond)
            }
            setNegativeButton("치소", null)
        }.show()
    }
}