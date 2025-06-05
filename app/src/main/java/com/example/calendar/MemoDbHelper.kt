package com.example.calendar

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MemoDbHelper private constructor(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "memo_save.db"
        private const val DB_VERSION = 1

        private const val TABLE_NAME = "MemoSave"

        // 单例实例，volatile + 双重检查锁保证线程安全
        @Volatile
        private var INSTANCE: MemoDbHelper? = null

        fun getInstance(context: Context): MemoDbHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MemoDbHelper(context.applicationContext).also { INSTANCE = it }
            }
        }

        private const val CREATE_TABLE_MEMO = """
            CREATE TABLE IF NOT EXISTS $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date TEXT NOT NULL,
                title TEXT NOT NULL,
                text TEXT,
                lastEditTime TEXT NOT NULL
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_MEMO)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 简单升级策略：删表重建，后续可自行扩展
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertMemo(date: String, title: String, text: String, lastEditTime: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("date", date)
            put("title", title)
            put("text", text)
            put("lastEditTime", lastEditTime)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun updateMemo(id: Int, title: String, text: String, lastEditTime: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("text", text)
            put("lastEditTime", lastEditTime)
        }
        return db.update(TABLE_NAME, values, "id = ?", arrayOf(id.toString()))
    }

    fun queryMemoByDate(date: String): MemoItem? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("id", "date", "title", "text", "lastEditTime"),
            "date = ?",
            arrayOf(date),
            null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return MemoItem(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    date = it.getString(it.getColumnIndexOrThrow("date")),
                    title = it.getString(it.getColumnIndexOrThrow("title")),
                    text = it.getString(it.getColumnIndexOrThrow("text")),
                    lastEditTime = it.getString(it.getColumnIndexOrThrow("lastEditTime"))
                )
            }
        }
        return null
    }

    fun queryAllMemosSortedByEditTime(): List<MemoItem> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf("id", "date", "title", "text", "lastEditTime"),
            null, null, null, null,
            "lastEditTime DESC"
        )
        val result = mutableListOf<MemoItem>()
        cursor.use {
            while (it.moveToNext()) {
                result.add(
                    MemoItem(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        date = it.getString(it.getColumnIndexOrThrow("date")),
                        title = it.getString(it.getColumnIndexOrThrow("title")),
                        text = it.getString(it.getColumnIndexOrThrow("text")),
                        lastEditTime = it.getString(it.getColumnIndexOrThrow("lastEditTime"))
                    )
                )
            }
        }
        return result
    }
}
