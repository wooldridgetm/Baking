package com.tomwo.app.baking.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import com.tomwo.app.baking.R.id.recipeList
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.domain.commands.RequestRecipeCommand
import org.jetbrains.anko.doAsync

class ListViewModel(app: Application): AndroidViewModel(app)
{
    var recipeList: RecipeList? = null
}