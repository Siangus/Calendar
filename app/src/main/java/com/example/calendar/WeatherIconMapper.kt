package com.example.calendar

object WeatherIconMapper {
    // key 是API返回的code_day字符串，value 是drawable资源id
    val iconMap = mapOf(
        "0" to R.drawable.ic_weather_0,
        "1" to R.drawable.ic_weather_1,
        "2" to R.drawable.ic_weather_2,
        "3" to R.drawable.ic_weather_3,
        "4" to R.drawable.ic_weather_4,
        "5" to R.drawable.ic_weather_5,
        "6" to R.drawable.ic_weather_6,
        "7" to R.drawable.ic_weather_7,
        "8" to R.drawable.ic_weather_8,
        "9" to R.drawable.ic_weather_9,
        "10" to R.drawable.ic_weather_10,
        "11" to R.drawable.ic_weather_11,
        // ... 继续按需补充
        "default" to R.drawable.ic_weather_default
    )

    // 根据code获取资源id，找不到则返回default
    fun getIconResId(code: String): Int {
        return iconMap[code] ?: iconMap["default"]!!
    }
}
