package com.example.calendar

import android.app.Application

class CalendarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MemoRequestSolver.init(this)
    }
}
