package com.tomwo.app.baking.data.db


import com.tomwo.app.baking.domain.Direction
import com.tomwo.app.baking.domain.Ingredient
import com.tomwo.app.baking.domain.Recipe
import com.tomwo.app.baking.domain.RecipeList
import com.tomwo.app.baking.utils.RegExr.Companion.PARENTHESIS
import com.tomwo.app.baking.utils.RegExr.Companion.PATTERN
import com.tomwo.app.baking.data.db.model.Direction as ModelDirection
import com.tomwo.app.baking.data.db.model.Ingredient as ModelIngredient
import com.tomwo.app.baking.data.db.model.Recipe as ModelRecipe
import com.tomwo.app.baking.data.db.model.RecipeList as ModelRecipeList

class DbDataMapper
{
    /**
     * Saving to the databasee
     */
    fun convertFromDomain(recipe: RecipeList) = with (recipe) {
        val recipes = recipeList.map { convertRecipeFromDomain(it) }
        ModelRecipeList(recipes)
    }

    private fun convertRecipeFromDomain(item: Recipe): ModelRecipe
    {
        val directions  = item.steps?.map { convertStepFromDomain(it) }
        val ingredients = item.ingredient?.map { convertIngredientFromDomain(it) }

        with (item) {
            return ModelRecipe(name, servings, image, directions, ingredients)
        }
    }

    private fun convertStepFromDomain(step: Direction): ModelDirection = with (step) {
        ModelDirection(order, title, detail, videoURL, thumbnailURL)
    }

    private fun convertIngredientFromDomain(item: Ingredient) = with (item) {
        ModelIngredient(quantity, measure, ingredient)
    }


    /**
     * Grabbing from the database
     */
    fun convertToDomain(recipe: ModelRecipeList) = with(recipe) {
        val recipes = recipeList.map { convertRecipeToDomain(it) }
        RecipeList(recipes)
    }

    fun convertRecipeToDomain(recipe: ModelRecipe): Recipe
    {
        val directions: List<Direction>?   = recipe.directions?.map  { convertStepToDomain(it) }
        val ingredients: List<Ingredient>? = recipe.ingredients?.map { convertIngredientToDomain(it) }

        with (recipe) {
            return Recipe(_id, name, servings, image,directions,ingredients)
        }
    }

    private fun convertStepToDomain(item: ModelDirection): Direction
    {
        with(item) {

            val desc = description.trim().split(".").joinToString(".\n").trim()

            val step = Direction(_id, _order, shortDescription, description, desc, videoURL, thumbnailURL)
            step.refID = refID

            return step
        }
    }

    private fun convertIngredientToDomain(item: ModelIngredient) = with (item) {

        val noSpace = "[a-zA-Z0-9]{1,}+,+[a-zA-Z0-9]{1,}".toRegex()

        // massage the data
        var temp = ingredient.replace(PATTERN," ").replace(PARENTHESIS, " (")
        if (temp.matches(noSpace)) {
            temp = temp.replace(",",", ")
        }
        val arr = temp.split(" ").map {
            var s = it.trim().toLowerCase()

            if (s != "or" && s.indexOf("(") == -1 && s.indexOf(")") == -1 && s != "and" && s != "the" && s != "other" && s != "a")
                s = s.capitalize()
            s

        }
        temp = arr.joinToString(" ")
        Ingredient(quantity, measure, temp)

    }

}