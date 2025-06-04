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
    fun getMemoList(): List<MemoItem> {
        return listOf(
            MemoItem("2025-06-03", "标题2", "Ori永远喜欢我"),
            MemoItem("2025-06-05", "标题1", "我永远喜欢Ori"),
            MemoItem("2025-06-06", "标题3", "Nibel的精灵"),

        )
    }
    fun getMemoByDate(date: String): MemoItem? {
        return getMemoList().find { it.date == date }
    }

    fun saveMemo(date: String, title: String, text: String) {
        // 这里写保存逻辑，比如写数据库或内存列表更新
    }
}
