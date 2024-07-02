package sakura.llar.notepadapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.app.PendingIntent
import android.content.Intent

class WidgetNote : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val mainIntent = Intent(context, MainActivity::class.java)
    val mainPendingIntent = PendingIntent.getActivity(
        context,
        0,
        mainIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val createNoteIntent = Intent(context.applicationContext, WidgetActivity::class.java)
    val createNotePendingIntent = PendingIntent.getActivity(
        context.applicationContext,
        2,
        createNoteIntent,

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    )

    val views = RemoteViews(context.packageName, R.layout.widget_note)

    views.setOnClickPendingIntent(R.id.button_main, mainPendingIntent)
    views.setOnClickPendingIntent(R.id.button_create_note, createNotePendingIntent)
    appWidgetManager.updateAppWidget(appWidgetId, views)
}