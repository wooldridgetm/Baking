package com.tomwo.app.baking.ui.appwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.tomwo.app.baking.Config
import com.tomwo.app.baking.R
import com.tomwo.app.baking.domain.Ingredient
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.commands.RequestSingleRecipeCommand

/**
 * This is the service that provides the factory to be bound to the collection service
 */
class RecipeWidgetRemoteViewsService : RemoteViewsService()
{
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory
    {
        Log.i("RemoteViewsService", "starting things up…")
        return WidgetRemoteViewsFactory( applicationContext, intent)
    }

    /**
     * This is the factory that will provide data to the collection widget.
     */
    private class WidgetRemoteViewsFactory(private val context: Context,
                                   private val intent: Intent) : RemoteViewsService.RemoteViewsFactory
    {
        companion object {
            private const val TAG = "WidgetRemoteViewsFact"
        }

        private val appWidgetId: Int = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        private var recipe: Recipe? = null

        init
        {
            if (this.appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                throw IllegalArgumentException("AppWidgetId is bad!")
            }
        }


        override fun onCreate()
        {
            // we reload the cursor in onDataSetChanged()
        }

        override fun onDestroy() { }
        override fun getLoadingView(): RemoteViews
        {
            return RemoteViews(context.packageName, R.layout.widget_header)
        }
        override fun getViewTypeCount() = 2  //  2 distinct types of views
        override fun getItemId(position: Int): Long
        {
            val ll = position.toLong()
            return ll
        }
        override fun hasStableIds() = true

        override fun getCount(): Int
        {
            recipe?.let {
                if (it.ingredient == null || it.ingredient.isEmpty())
                    return 0

                return it.ingredient.size
            }

            return 0
        }

        override fun getViewAt(position: Int): RemoteViews
        {
            Log.d(TAG,"Fx getViewAt($position)")
            val rv: RemoteViews

            when(position)
            {
                // put the name of the recipe first
                0 -> {
                    rv = RemoteViews(context.packageName, R.layout.widget_header)
                    rv.setTextViewText(R.id.widget_recipe_name, recipe?.name)
                }
                else -> {

                    rv = RemoteViews(context.packageName, R.layout.widget_ingredient_item)

                    // -1 b/c the first index is for the name of the recipee
                    val ingredient: Ingredient? = recipe?.ingredient?.get( position-1 )

                    // put the ingredient  &&  quantity+ measure
                    rv.setTextViewText(R.id.label, ingredient?.label)
                    rv.setTextViewText(R.id.accessoryLabel, ingredient?.accessoryLabel)
                }
            }

            return rv
        }

        override fun onDataSetChanged()
        {
            this.init()
        }

        private fun init()
        {
            Log.w(TAG,"Fx init() - WidgetRemoteViewsFactory")
            val default: Long = -1

            // get the id
            // for right now, just set it to 3
            val refID: Long = PreferenceManager.getDefaultSharedPreferences( context ).getLong( Config.PREF_RECIPE_FOR_APPWIDGET_ID+this.appWidgetId, -1)
            if (refID == default) return

            // get the data
            recipe = RequestSingleRecipeCommand(refID).execute()
            Log.d(TAG, "appWidgetId of '$appWidgetId' has recipe ${recipe?.name}")
            Log.d(TAG, Config.LINESPACE)
        }
    }
}