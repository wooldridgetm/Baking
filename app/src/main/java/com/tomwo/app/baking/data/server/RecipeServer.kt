package com.tomwo.app.baking.data.server

import com.tomwo.app.baking.data.db.Db
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.domain.dataSource.IRecipeDataSource

class RecipeServer(private val dataMapper: ServerDataMapper = ServerDataMapper(),
                   private val db: Db = Db()): IRecipeDataSource
{
    override fun requestRecipes(): RecipeList
    {
        val result = RecipeRequest().execute()
        val domain = dataMapper.convertToDomain(result)
        db.save(domain)
        return db.requestRecipes()
    }

    override fun requestRecipeById(id: Long) = throw UnsupportedOperationException()
}