package com.tomwo.app.baking.ui.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.widget.RemoteViews
import com.tomwo.app.baking.Config
import com.tomwo.app.baking.R
import com.tomwo.app.baking.ui.activities.MainActivity

/**
 * The app widget's AppWidgetProvider
 */
class RecipeWidgetProvider : AppWidgetProvider()
{
    override fun onReceive(context: Context, widgetIntent: Intent)
    {
        val action = widgetIntent.action
        if (action == REFRESH_ACTION)
        {
            println("REFRESH_ACTION received!")
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, RecipeWidgetProvider::class.java)
            mgr.notifyAppWidgetViewDataChanged( mgr.getAppWidgetIds(cn), R.id.widgetIngredientList )
        }

        super.onReceive(context, widgetIntent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray)
    {
        for (appWidgetId in appWidgetIds)
        {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray)
    {
        // need to remove the key-value pair in SharedPreferences
        // save the primary key to SharedPreferences
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        for (widgetId in appWidgetIds)
        {
            pref.edit().remove(Config.PREF_RECIPE_FOR_APPWIDGET_ID+widgetId).apply()
        }

        super.onDeleted(context, appWidgetIds)
    }

    /**
     * STATIC FIELDS/Fx's
     */
    companion object {
        private const val REFRESH_ACTION = "com.tomwo.app.baking.ui.appwidget.action.REFRESH"

        private fun getRemoteViews(context: Context, appWidgetId: Int): RemoteViews
        {
            // specify service to provide data for the Collection Widget.  Note that we must
            // embed appWidgetId vai the data otherwise it will be ignored
            val intent = Intent(context, RecipeWidgetRemoteViewsService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val rv = RemoteViews(context.packageName, R.layout.widget)
            rv.setRemoteAdapter(R.id.widgetIngredientList, intent)

            // set the empty view to be display if collection is empty.  It must be sibling view
            // of collection view
            rv.setEmptyView(R.id.widgetIngredientList, android.R.id.empty)
            rv.setTextViewText(android.R.id.empty, "No ingredients required.") // is this possible?

            val refreshPendingIntent = PendingIntent.getBroadcast(context, 0, getRefreshBroadcastIntent(context), PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

            // todo: add ability to open detailActivity from item in ListView
            // TODO:   need to add db query based on primary key id

            val openAppIntent = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            val openAppPendingIntent = PendingIntent.getActivity(context, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            rv.setOnClickPendingIntent(R.id.widgetLogo, openAppPendingIntent)

            return rv
        }

        @JvmStatic
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int)
        {
            val rv = this.getRemoteViews(context, appWidgetId)

            // instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, rv)
        }

        private fun getRefreshBroadcastIntent(context: Context): Intent
        {
            return Intent(REFRESH_ACTION).setComponent( ComponentName(context, RecipeWidgetProvider::class.java) )
        }


    }
}

