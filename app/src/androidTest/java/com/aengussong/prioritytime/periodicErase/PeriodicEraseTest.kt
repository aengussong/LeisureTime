package com.aengussong.prioritytime.periodicErase

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.ui.drawer.SettingsDrawer
import com.aengussong.prioritytime.worker.ERASE_DATA_WORKER_NAME
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertTrue

class PeriodicEraseTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun selectNeverOption_dataShouldNotBeCleared() {
        composeTestRule.setContent {
            SettingsDrawer(mutableStateOf(true)) {}
        }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        val workManager = WorkManager.getInstance(context)

        composeTestRule.onNodeWithText(context.resources.getString(R.string.erase_interval))
            .performClick()
        composeTestRule.onNodeWithText(context.resources.getString(R.string.never_option))
            .performClick()
        composeTestRule.onNodeWithText(context.resources.getString(R.string.monthly_option))
            .performClick()
        waitForWorkStart()
        val dataCleanerWorkerEnqueued = workManager.getEraseWorkState() == WorkInfo.State.ENQUEUED
        assertTrue(dataCleanerWorkerEnqueued)

        composeTestRule.onNodeWithText(context.resources.getString(R.string.never_option))
            .performClick()
        waitForWorkStart()
        val dataCleanerWorkerDoNotExist =
            workManager.getEraseWorkState() == WorkInfo.State.CANCELLED
        assertTrue(dataCleanerWorkerDoNotExist)
    }

    private fun WorkManager.getEraseWorkState() =
        getWorkInfosForUniqueWork(ERASE_DATA_WORKER_NAME).get()
            .firstOrNull()?.state

    private fun waitForWorkStart() = Thread.sleep(300)
}