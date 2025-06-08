package com.example.hyzcalendar

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HYZMemoRequestSolver {
    private lateinit var dbHelper: HYZMemoDbHelper

    fun init(context: Context) {
        dbHelper = HYZMemoDbHelper.getInstance(context)
    }

    fun getMemoContent(dateStr: String): String {
        val memo = dbHelper.queryMemoByDate(dateStr)
        return memo?.text ?: "暂无备忘"
    }

    fun getMemoList(): List<HYZMemoItem> {
        return dbHelper.queryAllMemosSortedByEditTime()
    }

    fun getMemoByDate(date: String): HYZMemoItem? {
        return dbHelper.queryMemoByDate(date)
    }

    fun saveMemo(date: String, title: String, text: String) {
        val now = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(Date())
        val existingMemo = dbHelper.queryMemoByDate(date)
        if (existingMemo == null) {
            dbHelper.insertMemo(date, title, text, now)
        } else {
            dbHelper.updateMemo(existingMemo.id, title, text, now)
        }
    }
    fun deleteMemo(date: String) {
        val db = dbHelper.writableDatabase
        db.delete("MemoSave", "date = ?", arrayOf(date))
    }

}
