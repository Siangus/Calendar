package com.example.calendar

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class MemoEditActivity : BaseActivity() {

    private lateinit var titleEdit: EditText
    private lateinit var textEdit: EditText
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnChooseDate: Button
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button

    private var selectedDate: CalendarDay = CalendarDay.today()

    override fun getLayoutResourceId(): Int = R.layout.activity_memo_edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "编辑"
        titleEdit = findViewById(R.id.editTitle)
        textEdit = findViewById(R.id.editText)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnChooseDate = findViewById(R.id.btnChooseDate)
        btnSave = findViewById(R.id.btnSave)
        btnDelete = findViewById<Button>(R.id.btnDelete)
        // 初始化日期
        val passedDate = intent.getStringExtra("date")
        selectedDate = if (passedDate != null) {
            val parts = passedDate.split("-")
            if (parts.size == 3) {
                CalendarDay.from(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            } else CalendarDay.today()
        } else CalendarDay.today()

        updateDateDisplay()

        // 加载备忘内容
        loadMemoForDate(selectedDate)

        // 日期选择按钮逻辑
        btnChooseDate.setOnClickListener {
            val calendarView = MaterialCalendarView(this)
            calendarView.selectedDate = selectedDate

            AlertDialog.Builder(this)
                .setTitle("选择日期")
                .setView(calendarView)
                .setPositiveButton("确定") { _, _ ->
                    calendarView.selectedDate?.let {
                        selectedDate = it
                        updateDateDisplay()
                        loadMemoForDate(it)
                    }
                }
                .setNegativeButton("取消", null)
                .show()
        }

        // 保存按钮逻辑
        btnSave.setOnClickListener {
            val dateStr = "%04d-%02d-%02d".format(
                selectedDate.year, selectedDate.month, selectedDate.day
            )
            MemoRequestSolver.saveMemo(
                dateStr,
                titleEdit.text.toString(),
                textEdit.text.toString()
            )
            finish()
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除该日期的备忘吗？")
                .setPositiveButton("删除") { _, _ ->
                    val dateStr = "%04d-%02d-%02d".format(
                        selectedDate.year, selectedDate.month, selectedDate.day
                    )
                    MemoRequestSolver.deleteMemo(dateStr)
                    Toast.makeText(this, "备忘已删除", Toast.LENGTH_SHORT).show()
                    // 设置结果通知
                    setResult(RESULT_OK, intent.apply { putExtra("action", "delete") })
                    finish()
                }
                .setNegativeButton("取消", null)
                .show()
        }

    }

    private fun updateDateDisplay() {
        val dateStr = "%04d-%02d-%02d".format(
            selectedDate.year, selectedDate.month, selectedDate.day
        )
        tvSelectedDate.text = dateStr
    }

    private fun loadMemoForDate(date: CalendarDay) {
        val dateStr = "%04d-%02d-%02d".format(date.year, date.month, date.day)
        val memo = MemoRequestSolver.getMemoByDate(dateStr)
        titleEdit.setText(memo?.title ?: "")
        textEdit.setText(memo?.text ?: "")
    }
}
