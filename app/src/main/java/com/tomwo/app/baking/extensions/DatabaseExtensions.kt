package com.tomwo.app.baking.extensions

import android.database.sqlite.SQLiteDatabase
import com.tomwo.app.baking.data.db.DirectionsTable
import org.jetbrains.anko.db.MapRowParser
import org.jetbrains.anko.db.SelectQueryBuilder


fun <T : Any> SelectQueryBuilder.parseList(parser: (Map<String, Any?>) -> T): List<T> =
        parseList(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })

fun <T : Any> SelectQueryBuilder.parseOpt(parser: (Map<String, Any?>) -> T): T? =
        parseOpt(object : MapRowParser<T> {
            override fun parseRow(columns: Map<String, Any?>): T = parser(columns)
        })

fun SQLiteDatabase.clear(tableName: String)
{
    this.execSQL("DELETE FROM $tableName")
}


fun SQLiteDatabase.lastInsertRowId(): Long
{
    println("lastInsertRowId() - DatabaseExtensions")
    val cursor = this.rawQuery("SELECT last_insert_rowid() as label",  null )
    var id: Long = -1
    if (cursor != null && cursor.moveToFirst())
    {
        id = cursor.getLong(cursor.getColumnIndex("label"))
        println("id = '$id'")
        cursor.close()
    }
    return id
}

fun SelectQueryBuilder.byId(id: Long) = whereSimple("_id = ?", id.toString() )

fun SelectQueryBuilder.byRefId(refID: Long) = whereSimple("${DirectionsTable.REFID} = ?", refID.toString() )