package xyz.stasiak.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BoardGameCollectorDbHandler(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?,
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 3
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

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put("title", game.title)
        values.put("original_title", game.originalTitle)
        values.put("year", game.year)
        values.put("bgg_id", game.bggId)
        values.put("rank", game.rank)
        values.put("image", game.image)
        val cursor = writableDatabase.rawQuery(
            "SELECT * FROM games WHERE bgg_id = ?",
            arrayOf(game.bggId.toString())
        )
        if (cursor.moveToFirst()) {
            writableDatabase.update(
                "games",
                values,
                "game_id = ?",
                arrayOf(cursor.getLong(0).toString())
            )
        } else {
            writableDatabase.insert("games", null, values)
        }
        cursor.close()
        writableDatabase.close()
    }

    fun findGames(): List<Game> {
        val query = "SELECT * FROM games"
        val cursor = readableDatabase.rawQuery(query, null)
        val games: ArrayList<Game> = ArrayList()
        if (cursor.moveToNext()) {
            do {
                val game = Game(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getLong(4),
                    cursor.getInt(5),
                    cursor.getBlob(6)
                )
                games.add(game)
            } while (cursor.moveToNext())
            cursor.close()
        }
        writableDatabase.close()
        return games
    }

    fun deleteGame(gameId: Long) {
        writableDatabase.delete("games", "game_id = ?", arrayOf(gameId.toString()))
        writableDatabase.close()
    }

    fun deleteGames() {
        writableDatabase.execSQL("DELETE FROM games")
        writableDatabase.close()
    }

    fun findGamesCursor(): Cursor {
        val query = "SELECT game_id as _id, title, year, rank, image FROM games"
        return readableDatabase.rawQuery(query, null)
    }

    fun countGames(): Int {
        val cursor = readableDatabase.rawQuery("SELECT game_id FROM games", null)
        val count = cursor.count
        cursor.close()
        readableDatabase.close()
        return count
    }
}