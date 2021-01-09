package com.aengussong.prioritytime.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun <T> LiveData<T>.getOrAwaitValue(): T = withContext(Dispatchers.Main.immediate) {
    val v = value
    suspendCancellableCoroutine { continuation: CancellableContinuation<T> ->
        val observer = object : Observer<T> {
            override fun onChanged(value: T) {
                if (value != v) {
                    removeObserver(this)
                    continuation.resume(value)
                }
            }
        }

        observeForever(observer)

        continuation.invokeOnCancellation {
            removeObserver(observer)
        }
    }
}