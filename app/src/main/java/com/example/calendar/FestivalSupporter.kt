package com.example.calendar

import com.nlf.calendar.Solar

object FestivalSupporter {

    private val solarFestivals = mapOf(
        "01-01" to "元旦",
        "05-01" to "劳动节",
        "10-01" to "国庆节",
        "06-01" to "儿童节",
        "03-08" to "妇女节",
        "12-25" to "圣诞节",
        "08-01" to "建军节"
    )

    fun getFestival(dateStr: String): String {
        val parts = dateStr.split("-")
        if (parts.size != 3) return "日期格式错误"

        val year = parts[0].toIntOrNull() ?: return "日期格式错误"
        val month = parts[1].toIntOrNull() ?: return "日期格式错误"
        val day = parts[2].toIntOrNull() ?: return "日期格式错误"

        val resultParts = mutableListOf<String>()
        val mode = ConfigManager.getInt(ConfigManager.Keys.SHOW_FESTIVAL, 1)

        val monthDayKey = "%02d-%02d".format(month, day)

        // 公历节日
        if (mode >= 1) {
            solarFestivals[monthDayKey]?.let { resultParts.add(it) }
        }

        // 农历节日
        if (mode == 2) {
            val solar = Solar.fromYmd(year, month, day)
            val lunarFestivals = solar.lunar.festivals
            if (lunarFestivals.isNotEmpty()) {
                resultParts.add(lunarFestivals.joinToString("、"))
            }
        }

        return if (resultParts.isNotEmpty()) {
            resultParts.joinToString(" / ")
        } else {
            "无节日"
        }
    }
}
