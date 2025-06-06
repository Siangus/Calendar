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
        "12" to R.drawable.ic_weather_12,
        "13" to R.drawable.ic_weather_13,
        "14" to R.drawable.ic_weather_14,
        "15" to R.drawable.ic_weather_15,
        "16" to R.drawable.ic_weather_16,
        "17" to R.drawable.ic_weather_17,
        "18" to R.drawable.ic_weather_18,
        "19" to R.drawable.ic_weather_19,
        "20" to R.drawable.ic_weather_20,
        "21" to R.drawable.ic_weather_21,
        "22" to R.drawable.ic_weather_22,
        "23" to R.drawable.ic_weather_23,
        "24" to R.drawable.ic_weather_24,
        "25" to R.drawable.ic_weather_25,
        "26" to R.drawable.ic_weather_26,
        "27" to R.drawable.ic_weather_27,
        "28" to R.drawable.ic_weather_28,
        "29" to R.drawable.ic_weather_29,
        "30" to R.drawable.ic_weather_30,
        "31" to R.drawable.ic_weather_31,
        "32" to R.drawable.ic_weather_32,
        "33" to R.drawable.ic_weather_33,
        "34" to R.drawable.ic_weather_34,
        "35" to R.drawable.ic_weather_35,
        "36" to R.drawable.ic_weather_36,
        "37" to R.drawable.ic_weather_37,
        "38" to R.drawable.ic_weather_38,
        "99" to R.drawable.ic_weather_99,
        // ... 后面可能看有没有极端点的天气决定是否还要塞，但是没有图标的也有默认可用
        "default" to R.drawable.ic_weather_default
    )

    // 根据code获取资源id，找不到则返回default
    fun getIconResId(code: String): Int {
        return iconMap[code] ?: iconMap["default"]!!
    }
}
