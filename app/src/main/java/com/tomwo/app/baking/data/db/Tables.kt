package com.tomwo.app.baking.data.db

object RecipeTable
{
    val NAME = "recipes"

    val ID = "_id"

    val NAME2 = "name"
    val SERVINGS = "servings"
    val IMAGE = "image"
}

object IngredientsTable
{
    val NAME = "ingredients"

    val ID = "_id"

    val REFID = "_refID"
    val QUANTITY = "quantity"
    val MEASURE = "measure"
    val INGREDIENT = "ingredient"

}

object DirectionsTable
{
    val NAME = "directions"

    val ID = "_id"
    val REFID = "_refID"
    val ORDER = "_order"

    val SHORT_DESCRIPTION = "shortDescription"
    val DESCRIPTION = "description"
    val VIDEO_URL = "videoURL"
    val THUMBNAIL_URL = "thumbnailURL"
}