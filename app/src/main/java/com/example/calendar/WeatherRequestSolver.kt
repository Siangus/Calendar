package com.example.calendar

object WeatherRequestSolver {

    /**
     * 传入日期字符串 yyyy-MM-dd，返回 drawable 资源 id（这里固定返回 R.drawable.w2 用于测试）
     */
    fun getWeatherIcon(dateStr: String): Int {
//后续应该根据日期String返回对应的icon的资源ID
        return R.drawable.w2
    }

    /**
     * 传入日期字符串，返回天气描述字符串（这里固定返回测试内容）
     */
    fun getWeatherInfo(dateStr: String): String {
        return "测试天气信息"
    }

    /**
     * 后续可扩展：处理 JSON 数据的示例接口
     */
    fun processWeatherData(jsonData: String): Map<String, Any> {
        // TODO: 实现 JSON 解析和数据处理
        return emptyMap()
    }
}
