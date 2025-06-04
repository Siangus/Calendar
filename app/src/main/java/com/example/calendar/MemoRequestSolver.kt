package com.example.calendar

object MemoRequestSolver {
    fun getMemoContent(dateStr: String): String {
        // 暂时使用伪数据模拟备忘录内容
        return when (dateStr) {
            "2025-06-05" -> "备忘：提交月报"
            "2025-06-06" -> "备忘：开会 + 天气变冷"
            else -> "暂无备忘"
        }
    }
}
