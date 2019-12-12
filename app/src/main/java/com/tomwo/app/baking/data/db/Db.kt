package com.tomwo.app.baking.data.db

import com.tomwo.app.baking.data.db.model.Direction
import com.tomwo.app.baking.data.db.model.Ingredient
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.data.db.model.Recipe as ModelRecipe
import com.tomwo.app.baking.domain.dataSource.IRecipeDataSource
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.extensions.*
import org.jetbrains.anko.db.insert
import org.jetbrains.anko.db.select
import com.tomwo.app.baking.data.db.model.RecipeList as ModelRecipeList

class Db(private val dbHelper: DbHelper = DbHelper.instance,
         private val dataMapper: DbDataMapper = DbDataMapper()) : IRecipeDataSource
{

    override fun requestRecipes() = dbHelper.use {
        val recipes: List<ModelRecipe> = select(RecipeTable.NAME).parseList { ModelRecipe(HashMap(it)) }

        recipes.forEach {

            it.directions = select(DirectionsTable.NAME)
                    .whereSimple("${DirectionsTable.REFID} = ?",it._id.toString())
                    .parseList { Direction(HashMap(it)) }

            it.ingredients = select(IngredientsTable.NAME)
                    .whereSimple("${IngredientsTable.REFID} = ?", it._id.toString() )
                    .parseList { Ingredient(HashMap(it)) }

        }

        dataMapper.convertToDomain( ModelRecipeList(recipes) )
    }

    override fun requestRecipeById(id: Long) = dbHelper.use {
        val recipe = select(RecipeTable.NAME).byId(id).parseOpt { ModelRecipe( HashMap(it) ) }

        println("stop")
        println("")
        recipe?.directions  = select(DirectionsTable.NAME).byRefId(id).parseList { Direction(HashMap(it)) }
        recipe?.ingredients = select(IngredientsTable.NAME).byRefId(id).parseList { Ingredient(HashMap(it)) }

        if (recipe != null) dataMapper.convertRecipeToDomain(recipe) else null
    }

    fun save(recipe: RecipeList) = dbHelper.use {

        clear(RecipeTable.NAME)
        clear(DirectionsTable.NAME)
        clear(IngredientsTable.NAME)

        val result: ModelRecipeList = dataMapper.convertFromDomain(recipe)

        result.recipeList.forEach {
            insert(RecipeTable.NAME, *it.map.toVarargArray())

            val refID = lastInsertRowId()

            it.directions?.forEach  {

                it.refID = refID
                insert(DirectionsTable.NAME, *it.map.toVarargArray())
            }

            it.ingredients?.forEach {

                it.refID = refID
                insert(IngredientsTable.NAME, *it.map.toVarargArray())
            }
        }
        result.recipeList
    }
}