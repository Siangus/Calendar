package com.example.calendar

import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MemoEditActivity : BaseActivity() {

    private lateinit var date: String
    private lateinit var titleEdit: EditText
    private lateinit var textEdit: EditText

    override fun getLayoutResourceId(): Int = R.layout.activity_memo_edit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        date = intent.getStringExtra("date") ?: ""
        titleEdit = findViewById(R.id.editTitle)
        textEdit = findViewById(R.id.editText)

        val memo = MemoRequestSolver.getMemoByDate(date)
        titleEdit.setText(memo?.title ?: "")
        textEdit.setText(memo?.text ?: "")

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            // 保存逻辑，比如调用 MemoRequestSolver.saveMemo(...)
            val newTitle = titleEdit.text.toString()
            val newText = textEdit.text.toString()
            MemoRequestSolver.saveMemo(date, newTitle, newText)
            finish()
        }
    }
}
