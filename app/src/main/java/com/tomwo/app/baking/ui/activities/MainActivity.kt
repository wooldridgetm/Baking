package com.tomwo.app.baking.ui.activities

import android.animation.Animator
import android.appwidget.AppWidgetManager
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.tomwo.app.baking.Config
import com.tomwo.app.baking.R
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.domain.commands.RequestRecipeCommand
import com.tomwo.app.baking.ui.adapters.RecipeListAdapter
import com.tomwo.app.baking.ui.appwidget.RecipeWidgetProvider
import com.tomwo.app.baking.ui.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.getStackTraceString
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), Runnable
{
    companion object {
        private const val TAG = "MainActivity"
    }
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    private val handler = Handler()
    private val model    by lazy { ViewModelProviders.of(this).get( ListViewModel::class.java ) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT == 26 && resources.getBoolean(R.bool.light_navigation))
        {
            // This attribute can only be set in code on API 26. It's in the theme in 27+.
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        intent.extras?.let { appWidgetId = it.getInt( AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID ) }

        // we do not want the tablet layout inflated when this is be used as the WidgetConfig Activity for large devices
        setContentView( if (isConfig()) R.layout.widget_config_act else R.layout.activity_main )

        if (model.recipeList == null) {
            this.loadRecipes()
            return
        }

        this.onDataLoaded()
    }

    private fun loadRecipes() = doAsync {

        model.recipeList = try {
            RequestRecipeCommand().execute()
        }
        catch (e: NoSuchElementException)
        {
            Log.e(TAG,e.toString())
            Log.e(TAG,e.getStackTraceString())

            RecipeList( ArrayList() )
        }

        uiThread {
            onDataLoaded()
        }
    }

    private fun onDataLoaded()
    {
        // if the progressBar is visible, then slowly fade it out
        if (progressBar.visibility == View.VISIBLE)
        {
            progressBar.animate().apply {
                alpha(0F)
                duration = 350
                setListener(object : Animator.AnimatorListener {
                    override fun onAnimationEnd(animation: Animator?) { progressBar.visibility = View.GONE }
                    override fun onAnimationStart(animation: Animator?) { }
                    override fun onAnimationCancel(animation: Animator?) { }
                    override fun onAnimationRepeat(animation: Animator?) { }
                })
            }
        } // if

        // if we don't have any data, then show the error View
        emptyView.visibility = if (model.recipeList!!.size > 0) View.GONE else View.VISIBLE

        val defaultFx: (Int) -> Unit = { clicked(it) }
        val widgetFx:  (Int) -> Unit = { widgetSelected(it) }

        val adapter = RecipeListAdapter(model.recipeList!!, if (isConfig()) widgetFx else defaultFx )
        recipeList.adapter = adapter
        recipeList.isClickable = true
        recipeList.isFocusable = true
    }

    private var selectedIndex: Int by Delegates.notNull()

    private fun clicked(index: Int)
    {
        this.selectedIndex = index

        // let the UI breathe
        handler.postDelayed(this,25)
    }

    /**
     * @see Runnable.run
     */
    override fun run()
    {
        val recipe = model.recipeList!![this.selectedIndex]

        startActivity<DetailActivity>(Recipe.ARG_RECIPE_ITEM to recipe)
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
    }


    /**
     * when this is a configuration for a widget, then this function will fire
     */
    private fun widgetSelected(index: Int)
    {
        val recipe = model.recipeList!![index]

        // save the primary key to SharedPreferences
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        pref.edit().putLong(Config.PREF_RECIPE_FOR_APPWIDGET_ID+this.appWidgetId, recipe.id).apply()

        // update App Widget
        val mgr = AppWidgetManager.getInstance(this)
        RecipeWidgetProvider.updateAppWidget(this, mgr, this.appWidgetId)

        val value = Intent()
        value.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, value)
        finish()          // exit the activity
    }

    private fun isConfig() = appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID
}