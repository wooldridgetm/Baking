package com.tomwo.app.baking.domain.commands

import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.dataSource.RecipeProvider

/**
 * Created by wooldridgetm on 2/21/18.
 */
class RequestSingleRecipeCommand(val id: Long,
                                 private val provider: RecipeProvider = RecipeProvider()) : Command<Recipe>
{
    override fun execute() = provider.requestById(id)

}