package com.activity.stopwatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.activity.stopwatch.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var isStarted = false
    //define service intent
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            startOrStop()
        }
        binding.btnReset.setOnClickListener {
            reset()
        }
        serviceIntent = Intent(applicationContext,StopWatchService::class.java)
        //register the receiver
        registerReceiver(updateTime, IntentFilter(StopWatchService.UPDATED_TIME))
    }

    private fun startOrStop(){
        if(isStarted){
            stop()
        }else{
            start()
        }
    }

    private fun start(){
        //add current time to the intent and pass the time
        serviceIntent.putExtra(StopWatchService.CURRENT_TIME,time)
        //invoke the startService function passing the intent
        startService(serviceIntent)
        binding.btnStart.text = "Stop"
        isStarted = true
    }

    private fun stop(){
        //invoke stopService passing the intent
        stopService(serviceIntent)
        binding.btnStart.text = "Start"
        isStarted = false
    }

    private fun reset(){
        stop()
        time = 0.0
        binding.tvTime.text = getFormattedTime(time)
    }

    //receive broadcastReceiver
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopWatchService.CURRENT_TIME,0.0)
            //display received time on the textView
            binding.tvTime.text = getFormattedTime(time)
        }

    }

    private fun getFormattedTime(time:Double): String {
        val timeInt = time.roundToInt()
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 84600 % 3600 / 60
        val seconds = timeInt % 84600 % 3600 % 60

        return String.format("%02d:%02d:%02d",hours,minutes,seconds)
    }
}