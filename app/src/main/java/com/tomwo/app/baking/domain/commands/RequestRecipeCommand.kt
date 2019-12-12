package com.tomwo.app.baking.domain.commands

import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.domain.dataSource.RecipeProvider

class RequestRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : Command<RecipeList>
{
    override fun execute(): RecipeList = provider.request()
}