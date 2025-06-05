package com.example.calendar
class APIRequestSolver(private val apiKey: String) : WeatherRequestSolver() {
    private val baseUrl = "https://api.example.com/weather"

    override fun fetchWeatherData(dateStr: String): Any? {
        // 这里用baseUrl和apiKey拼请求URL
        val url = "$baseUrl?date=$dateStr&apikey=$apiKey"
        // 发请求拿数据，示例中简化返回假数据
        // 真实场景用OkHttp/Retrofit协程实现异步
        return """
            {
                "date": "$dateStr",
             a   "weather": "晴",
                "icon": "sunny",
                "temp_min": 15,
                "temp_max": 25
            }
        """
    }
    // 其余解析逻辑不变
}
