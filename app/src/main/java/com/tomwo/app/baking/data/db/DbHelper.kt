package com.tomwo.app.baking.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.tomwo.app.baking.App
import org.jetbrains.anko.db.*

class DbHelper(ctx: Context = App.instance) : ManagedSQLiteOpenHelper(ctx, DbHelper.DB_NAME, null, DbHelper.DB_VERSION)
{
    companion object
    {
        val DB_NAME = "baking.db"
        val DB_VERSION = 1
        val instance by lazy { DbHelper() }
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        println("Fx onCreate() - DbHelper")

        db.createTable(RecipeTable.NAME, true,
                RecipeTable.ID to INTEGER + PRIMARY_KEY,
                RecipeTable.NAME2 to TEXT,
                RecipeTable.SERVINGS to INTEGER,
                RecipeTable.IMAGE to TEXT)

        db.createTable(DirectionsTable.NAME, true,
                DirectionsTable.ID to INTEGER + PRIMARY_KEY,
                DirectionsTable.REFID to INTEGER,
                DirectionsTable.ORDER to INTEGER,
                DirectionsTable.SHORT_DESCRIPTION to TEXT,
                DirectionsTable.DESCRIPTION to TEXT,
                DirectionsTable.VIDEO_URL to INTEGER,
                DirectionsTable.THUMBNAIL_URL to TEXT
        )

        db.createTable(IngredientsTable.NAME, true,
                IngredientsTable.ID to INTEGER + PRIMARY_KEY,
                IngredientsTable.REFID to INTEGER,
                IngredientsTable.INGREDIENT to TEXT,
                IngredientsTable.MEASURE to TEXT,
                IngredientsTable.QUANTITY to REAL )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        db.dropTable(RecipeTable.NAME2, true)
        db.dropTable(DirectionsTable.NAME, true)
        db.dropTable(IngredientsTable.NAME, true)

        this.onCreate(db)
    }


}