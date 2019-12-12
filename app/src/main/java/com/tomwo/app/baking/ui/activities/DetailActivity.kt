package com.tomwo.app.baking.ui.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomwo.app.baking.R
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.extensions.DelegatesExt
import com.tomwo.app.baking.ui.fragments.DetailFrag
import java.io.InvalidObjectException

class DetailActivity : AppCompatActivity(), WinManager
{
    private var recipe: Recipe by DelegatesExt.notNullSingleValue()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        this.initWindow(this, window, resources.getBoolean(R.bool.light_navigation))
        super.onCreate(savedInstanceState)

        if (this.intent.extras != null)
        {
            this.recipe = this.intent.extras!!.getParcelable(Recipe.ARG_RECIPE_ITEM) ?: throw InvalidObjectException("Recipe is null")
        }

        val fm = supportFragmentManager
        if (fm.findFragmentByTag(DetailFrag.TAG) == null)
        {
            fm.beginTransaction()
                    .add(android.R.id.content, DetailFrag.newInstance(this.recipe), DetailFrag.TAG )
                    .commit()
        }
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        this.overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
    }

}
