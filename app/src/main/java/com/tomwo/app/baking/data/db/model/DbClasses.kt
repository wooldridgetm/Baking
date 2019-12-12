package com.tomwo.app.baking.data.db.model

class RecipeList(val recipeList: List<Recipe>)
{
    val size: Int
        get() = recipeList.size

    operator fun get(index: Int) = recipeList[index]
}

class Recipe(val map: MutableMap<String, Any?>, var directions: List<Direction>? = null, var ingredients: List<Ingredient>? = null)
{
    var _id: Long by map
    var name: String by map
    var servings: Int by map
    var image: String by map

    constructor(name: String, servings: Int, image:String, directions: List<Direction>? = null, ingredients: List<Ingredient>? = null): this(HashMap(), directions, ingredients)
    {
        this.name = name
        this.servings = servings
        this.image = image
    }
}

class Direction(var map: MutableMap<String, Any?>)
{
    var _id: Long by map
    var _order: Int by map
    var shortDescription: String by map
    var description: String by map
    var videoURL: String by map
    var thumbnailURL: String by map

    private var _refID: Long by map
    var refID: Long
        get() = _refID
        set(value) {
            this._refID = value
        }

    constructor(order: Int, shortDescription: String, description: String, videoURL: String, thumbnailURL: String): this(HashMap())
    {
        this._order = order

        this.shortDescription = shortDescription
        this.description = description
        this.videoURL = videoURL
        this.thumbnailURL = thumbnailURL
    }
}

class Ingredient(var map: MutableMap<String, Any?>)
{
    var _id: Long by map

    private var _refID: Long by map
    var refID: Long
        get() = _refID
        set(value) {
            _refID = value
        }

    var quantity:Double by map
    var measure:String by map
    var ingredient:String by map

    constructor(quantity: Double, measure: String, ingredient: String): this(HashMap())
    {
        this.quantity = quantity
        this.measure = measure
        this.ingredient = ingredient
    }
}