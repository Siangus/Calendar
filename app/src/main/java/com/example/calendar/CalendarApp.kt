package com.example.calendar

import android.app.Application

class CalendarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // 单例初始化数据库助手
        MemoRequestSolver.init(this)
    }
}
