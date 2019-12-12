package com.tomwo.app.baking.domain.dataSource

import com.tomwo.app.baking.data.db.Db
import com.tomwo.app.baking.data.server.RecipeServer
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.extensions.firstResult

class RecipeProvider(private val sources: List<IRecipeDataSource> = RecipeProvider.SOURCES)
{
    companion object
    {
        val SOURCES = listOf(Db(), RecipeServer())
    }

    fun request(): RecipeList = requestToSources {
        val res = it.requestRecipes()
        if (res != null && res.size > 0) res else null
    }

    fun requestById(id: Long): Recipe = requestToSources { it.requestRecipeById(id) }

    private fun <T: Any> requestToSources(r: (IRecipeDataSource) -> T?): T = sources.firstResult { r(it) }

}