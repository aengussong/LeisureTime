package com.aengussong.prioritytime.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PriorityBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        PriorityWidgetService.enqueueWork(context)
    }
}