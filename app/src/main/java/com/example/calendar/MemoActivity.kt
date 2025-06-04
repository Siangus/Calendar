package com.example.calendar

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MemoActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemoAdapter
    private val memoList = mutableListOf<MemoItem>()

    override fun getLayoutResourceId(): Int = R.layout.activity_memo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 这里通过 MemoRequestSolver 获取数据列表
        memoList.addAll(MemoRequestSolver.getMemoList())
        adapter = MemoAdapter(memoList) { memoItem ->
            // 点击条目跳转编辑页面，传递日期
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("date", memoItem.date)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        // 返回时刷新列表
        memoList.clear()
        memoList.addAll(MemoRequestSolver.getMemoList())
        adapter.notifyDataSetChanged()
    }
}
