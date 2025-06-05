package com.example.calendar

import android.content.Context
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

data class WeatherData(
    val date: String,
    val conditionText: String,
    val tempLow: String,
    val tempHigh: String,
    val iconKey: String
)

object ApiRequestSolver : WeatherRequestSolver() {

    private const val TAG = "ApiRequestSolver"
    private const val BASE_URL = "https://api.seniverse.com/v3/weather/daily.json"

    private val weatherCache = mutableMapOf<String, WeatherData>()
    private val client = OkHttpClient()

    private var apiKey: String? = null

    override fun init(context: Context) {
        apiKey = ConfigManager.getString(ConfigManager.Keys.WEATHER_API_KEY)
        if (apiKey.isNullOrEmpty()) {
            Log.e(TAG, "API Key 未配置")
            return
        }
        refreshWeatherData()
    }

    fun refreshWeatherData() {
        val key = apiKey ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "$BASE_URL?key=$key&location=ip&language=zh-Hans&unit=c&days=3"
                val request = Request.Builder().url(url).get().build()
                val response: Response = client.newCall(request).execute()
                val bodyString = response.body()?.string()
                if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                    parseAndCache(bodyString)
                    Log.i(TAG, "天气数据刷新成功")
                } else {
                    Log.e(TAG, "天气请求失败 code=${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "天气请求异常: ${e.message}")
            }
        }
    }

    private fun parseAndCache(jsonStr: String) {
        weatherCache.clear()
        val json = JSONObject(jsonStr)
        val results = json.optJSONArray("results") ?: return
        if (results.length() == 0) return

        val dailyArray = results.getJSONObject(0).optJSONArray("daily") ?: return

        for (i in 0 until dailyArray.length()) {
            val day = dailyArray.getJSONObject(i)
            val date = day.optString("date")
            val conditionText = day.optString("text_day")
            val tempLow = day.optString("low")
            val tempHigh = day.optString("high")
            val iconKey = day.optString("code_day")
            val data = WeatherData(date, conditionText, tempLow, tempHigh, iconKey)
            weatherCache[date] = data
        }
    }

    override fun fetchWeatherData(dateStr: String): Any? {
        return getWeatherByDate(dateStr)
    }

    override fun parseIconId(data: Any?): Int {
        if (data !is WeatherData) return WeatherIconMapper.getIconResId("default")
        return WeatherIconMapper.getIconResId(data.iconKey)
    }

    override fun parseWeatherDescription(data: Any?): String {
        if (data !is WeatherData) return "无数据"
        return "${data.conditionText} ${data.tempLow}℃ ~ ${data.tempHigh}℃"
    }

    fun getWeatherByDate(date: String): WeatherData? {
        return weatherCache[date]
    }
}
