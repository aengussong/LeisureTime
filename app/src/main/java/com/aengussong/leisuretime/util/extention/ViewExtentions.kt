package com.aengussong.leisuretime.util.extention

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

inline fun <T, R : View> R.doAsync(
    crossinline backgroundTask: suspend () -> T?
) {
    val job = CoroutineScope(Dispatchers.Main)
    val attachListener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(p0: View?) {}
        override fun onViewDetachedFromWindow(p0: View?) {
            job.cancel()
        }
    }
    addOnAttachStateChangeListener(attachListener)
    job.launch {
        try {
            backgroundTask()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        removeOnAttachStateChangeListener(attachListener)
    }
}

fun <T> View.sendAsync(channel: Channel<T>, data: T?) {
    data?.let { doAsync { channel.send(data) } }
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}