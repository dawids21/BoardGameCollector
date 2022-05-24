package xyz.stasiak.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserNameDbHandler(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?,
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "boardGameCollectorDB"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE config (name TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS config")
        onCreate(db)
    }

    fun saveName(userName: UserName) {
        val values = ContentValues()
        values.put("name", userName.name)
        writableDatabase.insert("config", null, values)
        writableDatabase.close()
    }

    fun getName(): UserName? {
        val cursor = writableDatabase.rawQuery("SELECT * FROM config", null)
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

}