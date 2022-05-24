package xyz.stasiak.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BoardGameCollectorDbHandler(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?,
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "boardGameCollectorDB"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE config (name TEXT)")
        db.execSQL(
            "CREATE TABLE games (" +
                    "game_id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "original_title TEXT," +
                    "year INTEGER," +
                    "bgg_id INTEGER," +
                    "rank INTEGER," +
                    "image BLOB" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS config")
        db.execSQL("DROP TABLE IF EXISTS games")
        onCreate(db)
    }

    fun saveName(userName: UserName) {
        val values = ContentValues()
        values.put("name", userName.name)
        writableDatabase.insert("config", null, values)
        writableDatabase.close()
    }

    fun getName(): UserName? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM config", null)
        var userName: UserName? = null
        if (cursor.moveToFirst()) {
            val name = cursor.getString(0)
            userName = UserName(name)
            cursor.close()
        }
        writableDatabase.close()
        return userName
    }

    fun deleteName() {
        writableDatabase.execSQL("DELETE FROM config")
        writableDatabase.close()
    }

    fun isNameSet(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT * FROM config", null)
        val count = cursor.count
        cursor.close()
        readableDatabase.close()
        return count == 1
    }

}