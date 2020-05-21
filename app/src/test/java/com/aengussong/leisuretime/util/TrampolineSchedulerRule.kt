package com.aengussong.leisuretime.util

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestWatcher
import org.junit.runner.Description


/**
 * Switches io and main schedulers to trampoline scheduler for safe rx chains unit testing
 * */
class TrampolineSchedulerRule : TestWatcher() {

    override fun starting(description: Description?) {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    override fun finished(description: Description?) {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

}