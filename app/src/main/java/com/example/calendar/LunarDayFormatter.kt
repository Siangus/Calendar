package com.example.calendar
import com.nlf.calendar.Solar
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.DayFormatter

class LunarDayFormatter : DayFormatter {
    override fun format(day: CalendarDay): String {
        val solarDay = day.day
        val showLunar = ConfigManager.getInt(ConfigManager.Keys.SHOW_LUNAR)
        val lunar = Solar(day.year, day.month, day.day).lunar

        return when {

            showLunar == 1 -> { // 只显示农历
                val lunarDay = lunar.getDayInChinese()
                "$solarDay\n$lunarDay"
            }
            else -> { // 默认只显示公历
                solarDay.toString()
            }
        }
    }
}

