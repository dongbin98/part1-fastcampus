package com.dbsh.mediaplayerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class LowBatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d("onReceive", "${intent.action}")
        when(intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                Toast.makeText(context, "배터리 부족", Toast.LENGTH_SHORT).show()
            }
        }
    }
}