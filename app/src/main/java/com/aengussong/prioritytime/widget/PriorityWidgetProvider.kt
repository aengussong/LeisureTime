package com.aengussong.prioritytime.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.aengussong.prioritytime.R
import com.aengussong.prioritytime.usecase.GetTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

const val ACTION_WIDGET_UPDATE = "com.aengussong.prioritytime.PRIORITY_UPDATE"

class PriorityWidgetProvider : AppWidgetProvider(), KoinComponent {

    companion object {
        fun getUpdateWidgetIntent(context: Context) =
            Intent(context, PriorityWidgetProvider::class.java).apply {
                action = ACTION_WIDGET_UPDATE
            }
    }

    private val getTaskUseCase: GetTaskUseCase by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == ACTION_WIDGET_UPDATE) {
            val widgetManager = AppWidgetManager.getInstance(context.applicationContext)
            val thisWidget =
                ComponentName(context.packageName, PriorityWidgetProvider::class.java.name)
            val ids = widgetManager.getAppWidgetIds(thisWidget)
            onUpdate(context, widgetManager, ids)
            return
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val minTask = getTaskUseCase.getMinTask()
            withContext(Dispatchers.Main) {
                appWidgetIds?.forEach { widgetId ->
                    val views = RemoteViews(context.packageName, R.layout.layout_widget).apply {
                        setTextViewText(
                            R.id.widget_task_name,
                            minTask?.name ?: context.getString(R.string.empty_list)
                        )
                        val pIntent = PendingIntent.getBroadcast(
                            context,
                            0,
                            Intent().setClass(context, PriorityBroadcast::class.java),
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                        setOnClickPendingIntent(R.id.widget_task_name, pIntent)
                    }
                    appWidgetManager?.updateAppWidget(widgetId, views)
                }
            }
        }
    }
}