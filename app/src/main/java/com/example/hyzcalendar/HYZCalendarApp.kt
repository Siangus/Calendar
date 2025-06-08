package com.example.hyzcalendar

import android.app.Application

class HYZCalendarApp : Application() {

    override fun onCreate() {
        super.onCreate()
        HYZMemoRequestSolver.init(this)
    }
}
