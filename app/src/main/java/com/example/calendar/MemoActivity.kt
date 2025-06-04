package com.example.calendar

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
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

        // 加入分割线
        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        recyclerView.addItemDecoration(divider)

        // 获取数据
        memoList.addAll(MemoRequestSolver.getMemoList())
        adapter = MemoAdapter(memoList) { memoItem ->
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
