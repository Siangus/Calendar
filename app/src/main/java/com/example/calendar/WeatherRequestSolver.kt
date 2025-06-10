package com.example.calendar

import android.content.Context

abstract class WeatherRequestSolver {

    protected var onDataLoaded: (() -> Unit)? = null
//所有天气相关的调用都应该用这里的方法。使用时实例化一个这个的子类

    abstract fun init(context: Context, onDataLoaded: (() -> Unit)? = null)

    fun getWeatherIcon(dateStr: String): Int {
        val data = fetchWeatherData(dateStr)
        return parseIconId(data)
    }

    fun getWeatherInfo(dateStr: String): String {
        val data = fetchWeatherData(dateStr)
        return parseWeatherDescription(data)
    }

    protected abstract fun fetchWeatherData(dateStr: String): Any?
    protected abstract fun parseIconId(data: Any?): Int
    protected abstract fun parseWeatherDescription(data: Any?): String
}
