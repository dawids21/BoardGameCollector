package xyz.stasiak.boardgamecollector

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

class BoardGameCollectorDbHandler(
    context: Context,
    factory: SQLiteDatabase.CursorFactory?,
) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 8
        private const val DATABASE_NAME = "boardGameCollectorDB"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE config (" +
                    "name TEXT," +
                    "last_sync TEXT" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE games (" +
                    "game_id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "year INTEGER," +
                    "bgg_id INTEGER," +
                    "rank INTEGER," +
                    "image TEXT" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE extensions (" +
                    "extension_id INTEGER PRIMARY KEY," +
                    "title TEXT," +
                    "year INTEGER," +
                    "bgg_id INTEGER," +
                    "image TEXT" +
                    ")"
        )
        db.execSQL(
            "CREATE TABLE ranks(" +
                    "rank_id INTEGER PRIMARY KEY," +
                    "game_id INTEGER," +
                    "date TEXT," +
                    "rank INTEGER," +
                    "FOREIGN KEY(game_id) REFERENCES games(game_id) ON DELETE CASCADE" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS config")
        db.execSQL("DROP TABLE IF EXISTS games")
        db.execSQL("DROP TABLE IF EXISTS extensions")
        db.execSQL("DROP TABLE IF EXISTS ranks")
        onCreate(db)
    }

    fun createConfig(userName: UserName, lastSync: Date) {
        val values = ContentValues()
        values.put("name", userName.name)
        values.put("last_sync", getDateFormat().format(lastSync))
        writableDatabase.insert("config", null, values)
    }

    fun getName(): UserName? {
        val cursor = readableDatabase.rawQuery("SELECT name FROM config", null)
        var userName: UserName? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("name")
            if (columnIndex != -1) {
                val name = cursor.getString(columnIndex)
                userName = UserName(name)
            }
            cursor.close()
        }
        return userName
    }

    fun deleteConfig() {
        writableDatabase.execSQL("DELETE FROM config")
    }

    fun isNameSet(): Boolean {
        val cursor = readableDatabase.rawQuery("SELECT name FROM config", null)
        val count = cursor.count
        cursor.close()
        return count == 1
    }

    fun setLastSync(lastSync: Date) {
        val values = ContentValues()
        values.put("last_sync", getDateFormat().format(lastSync))
        writableDatabase.update("config", values, null, arrayOf())
    }

    fun getLastSync(): Date? {
        val cursor = readableDatabase.rawQuery("SELECT last_sync FROM config", null)
        var lastSync: Date? = null
        if (cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndex("last_sync")
            if (columnIndex != -1) {
                val lastSyncString = cursor.getString(columnIndex)
                lastSync = getDateFormat().parse(lastSyncString)
            }
            cursor.close()
        }
        return lastSync
    }

    private fun getDateFormat(): DateFormat {
        return SimpleDateFormat.getDateTimeInstance()
    }

    fun addGame(game: Game) {
        val values = ContentValues()
        values.put("title", game.title)
        values.put("year", game.year)
        values.put("bgg_id", game.bggId)
        values.put("rank", game.rank)
        values.put("image", game.image)
        val cursor = writableDatabase.rawQuery(
            "SELECT * FROM games WHERE bgg_id = ?",
            arrayOf(game.bggId.toString())
        )
        if (cursor.moveToFirst()) {
            val gameId = cursor.getLong(cursor.getColumnIndexOrThrow("game_id"))
            writableDatabase.update(
                "games",
                values,
                "game_id = ?",
                arrayOf(gameId.toString())
            )
            addRank(Rank(null, gameId, Date.from(Instant.now()), game.rank))
        } else {
            val gameId = writableDatabase.insert("games", null, values)
            addRank(Rank(null, gameId, Date.from(Instant.now()), game.rank))
        }
        cursor.close()
    }

    fun findGame(gameId: Long): Game? {
        val cursor = writableDatabase.rawQuery(
            "SELECT * FROM games WHERE game_id = ?",
            arrayOf(gameId.toString())
        )
        if (!cursor.moveToFirst()) {
            cursor.close()
            return null
        }
        return Game(
            gameId,
            cursor.getString(cursor.getColumnIndexOrThrow("title")),
            cursor.getInt(cursor.getColumnIndexOrThrow("year")),
            cursor.getLong(cursor.getColumnIndexOrThrow("bgg_id")),
            cursor.getInt(cursor.getColumnIndexOrThrow("rank")),
            cursor.getString(cursor.getColumnIndexOrThrow("image"))
        )
    }

    fun findGameBggIds(): List<Long> {
        val cursor = writableDatabase.rawQuery("SELECT bgg_id FROM games", arrayOf())
        if (!cursor.moveToFirst()) {
            cursor.close()
            return listOf()
        }
        val ids = ArrayList<Long>()
        do {
            ids.add(cursor.getLong(cursor.getColumnIndexOrThrow("bgg_id")))
        } while (cursor.moveToNext())
        return ids
    }

    fun deleteGame(bggId: Long) {
        val cursor = writableDatabase.rawQuery(
            "SELECT game_id FROM games WHERE bgg_id = ?",
            arrayOf(bggId.toString())
        )
        if (cursor.moveToFirst()) {
            val gameId = cursor.getLong(cursor.getColumnIndexOrThrow("game_id"))
            writableDatabase.delete("games", "game_id = ?", arrayOf(gameId.toString()))
            writableDatabase.delete("ranks", "game_id = ?", arrayOf(gameId.toString()))
        }
        cursor.close()
    }

    fun deleteGames() {
        writableDatabase.execSQL("DELETE FROM games")
    }

    fun findGamesCursor(): Cursor {
        val query = "SELECT game_id as _id, title, year, rank, image FROM games ORDER BY game_id"
        return readableDatabase.rawQuery(query, null)
    }

    fun findGamesCursorSortByTitle(): Cursor {
        val query = "SELECT game_id as _id, title, year, rank, image FROM games ORDER BY title"
        return readableDatabase.rawQuery(query, null)
    }

    fun findGamesCursorSortByYear(): Cursor {
        val query = "SELECT game_id as _id, title, year, rank, image FROM games ORDER BY year"
        return readableDatabase.rawQuery(query, null)
    }

    fun findGamesCursorSortByRank(): Cursor {
        val query = "SELECT game_id as _id, title, year, rank, image FROM games ORDER BY rank"
        return readableDatabase.rawQuery(query, null)
    }

    fun countGames(): Int {
        val cursor = readableDatabase.rawQuery("SELECT game_id FROM games", null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun addExtension(extension: Extension) {
        val values = ContentValues()
        values.put("title", extension.title)
        values.put("year", extension.year)
        values.put("bgg_id", extension.bggId)
        values.put("image", extension.image)
        val cursor = writableDatabase.rawQuery(
            "SELECT * FROM extensions WHERE bgg_id = ?",
            arrayOf(extension.bggId.toString())
        )
        if (cursor.moveToFirst()) {
            writableDatabase.update(
                "extensions",
                values,
                "extension_id = ?",
                arrayOf(cursor.getLong(0).toString())
            )
        } else {
            writableDatabase.insert("extensions", null, values)
        }
        cursor.close()
    }

    fun deleteExtension(bggId: Long) {
        writableDatabase.delete("extensions", "bgg_id = ?", arrayOf(bggId.toString()))
    }

    fun deleteExtensions() {
        writableDatabase.execSQL("DELETE FROM extensions")
    }

    fun findExtensionsCursor(): Cursor {
        val query =
            "SELECT extension_id as _id, title, year, image FROM extensions ORDER BY extension_id"
        return readableDatabase.rawQuery(query, null)
    }

    fun findExtensionsCursorSortByTitle(): Cursor {
        val query = "SELECT extension_id as _id, title, year, image FROM extensions ORDER BY title"
        return readableDatabase.rawQuery(query, null)
    }

    fun findExtensionsCursorSortByYear(): Cursor {
        val query = "SELECT extension_id as _id, title, year, image FROM extensions ORDER BY year"
        return readableDatabase.rawQuery(query, null)
    }

    fun findExtensionBggIds(): List<Long> {
        val cursor = writableDatabase.rawQuery("SELECT bgg_id FROM extensions", arrayOf())
        if (!cursor.moveToFirst()) {
            cursor.close()
            return listOf()
        }
        val ids = ArrayList<Long>()
        do {
            ids.add(cursor.getLong(cursor.getColumnIndexOrThrow("bgg_id")))
        } while (cursor.moveToNext())
        return ids
    }

    fun countExtensions(): Int {
        val cursor = readableDatabase.rawQuery("SELECT extension_id FROM extensions", null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun addRank(rank: Rank) {
        val values = ContentValues()
        values.put("game_id", rank.gameId)
        values.put("date", getDateFormat().format(rank.date))
        values.put("rank", rank.value)
        writableDatabase.insert("ranks", null, values)
    }

    fun findRanksByGameCursor(gameId: Long): Cursor {
        return readableDatabase.rawQuery(
            "SELECT rank_id as _id, date, rank FROM ranks WHERE game_id = ? ORDER BY date",
            arrayOf(gameId.toString())
        )
    }

    fun deleteRanks() {
        writableDatabase.execSQL("DELETE FROM ranks")
    }
}