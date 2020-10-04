package com.aengussong.prioritytime.util.extention

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

inline fun LifecycleCoroutineScope.doWhileActive(
    crossinline asyncJob: suspend () -> Unit
) {
    launch {
        while (isActive) {
            asyncJob()
        }
    }
}