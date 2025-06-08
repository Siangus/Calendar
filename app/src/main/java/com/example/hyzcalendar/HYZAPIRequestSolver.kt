package com.example.hyzcalendar

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

object ApiRequestSolver : HYZWeatherRequestSolver() {

    private const val TAG = "ApiRequestSolver"
    private const val BASE_URL = "https://api.seniverse.com/v3/weather/daily.json"

    private val weatherCache = mutableMapOf<String, WeatherData>()
    private val client = OkHttpClient()

    private var apiKey: String? = null

    override fun init(context: Context, onDataLoaded: (() -> Unit)?) {
        this.onDataLoaded = onDataLoaded
        apiKey = HYZConfigManager.getString(HYZConfigManager.Keys.WEATHER_API_KEY)
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
                val url = "$BASE_URL?key=$key&location=ip&language=zh-Hans&unit=c"
                Log.d(TAG, "请求天气数据: $url")
                val request = Request.Builder().url(url).get().build()
                val response: Response = client.newCall(request).execute()
                val bodyString = response.body()?.string()

                Log.d(TAG, "响应状态: ${response.code()}")
                Log.d(TAG, "响应内容: $bodyString")

                if (response.isSuccessful && !bodyString.isNullOrEmpty()) {
                    parseAndCache(bodyString)
                    Log.i(TAG, "天气数据刷新成功")
                } else {
                    Log.e(TAG, "天气请求失败 code=${response.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "天气请求异常: ${e.message}", e)
            } finally {
                withContext(Dispatchers.Main) {
                    onDataLoaded?.invoke()
                }
            }
        }
    }


    private fun parseAndCache(jsonStr: String) {
        Log.d(TAG, "开始解析天气数据")
        weatherCache.clear()
        val json = JSONObject(jsonStr)
        val results = json.optJSONArray("results") ?: run {
            Log.e(TAG, "results 字段为空")
            return
        }
        if (results.length() == 0) {
            Log.e(TAG, "results 长度为 0")
            return
        }

        val dailyArray = results.getJSONObject(0).optJSONArray("daily") ?: run {
            Log.e(TAG, "daily 字段为空")
            return
        }

        for (i in 0 until dailyArray.length()) {
            val day = dailyArray.getJSONObject(i)
            val date = day.optString("date")
            val conditionText = day.optString("text_day")
            val tempLow = day.optString("low")
            val tempHigh = day.optString("high")
            val iconKey = day.optString("code_day")
            val data = WeatherData(date, conditionText, tempLow, tempHigh, iconKey)
            weatherCache[date] = data
            Log.d(TAG, "已缓存天气数据: $data")
        }
    }

    override fun fetchWeatherData(dateStr: String): Any? {
        val data = getWeatherByDate(dateStr)
        if (data == null) {
            Log.w(TAG, "请求的日期无天气数据: $dateStr")
        } else {
            Log.d(TAG, "返回天气数据: $data")
        }
        return data
    }

    override fun parseIconId(data: Any?): Int {
        if (data !is WeatherData) {
            Log.w(TAG, "天气图标数据无效，返回默认图标")
            return HYZWeatherIconMapper.getIconResId("default")
        }
        return HYZWeatherIconMapper.getIconResId(data.iconKey)
    }

    override fun parseWeatherDescription(data: Any?): String {
        if (data !is WeatherData) return "无数据"
        return "${data.conditionText} ${data.tempLow}℃ ~ ${data.tempHigh}℃"
    }

    fun getWeatherByDate(date: String): WeatherData? {
        return weatherCache[date]
    }
}
