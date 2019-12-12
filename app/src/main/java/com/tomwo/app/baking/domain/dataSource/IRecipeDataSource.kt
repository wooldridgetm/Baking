package com.tomwo.app.baking.domain.dataSource

import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList

interface IRecipeDataSource
{
    fun requestRecipes(): RecipeList?

    fun requestRecipeById(id: Long): Recipe?
}