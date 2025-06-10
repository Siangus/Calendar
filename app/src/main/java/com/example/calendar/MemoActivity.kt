package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class MemoActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MemoAdapter
    private val memoList = mutableListOf<MemoItem>()

    override fun getLayoutResourceId(): Int = R.layout.activity_memo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        supportActionBar?.title = "备忘录"
        // 设置抽屉开关
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val divider = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        recyclerView.addItemDecoration(divider)

        findViewById<Button>(R.id.btnAddMemo).setOnClickListener {
            val intent = Intent(this, MemoEditActivity::class.java)
            intent.putExtra("date", "") // 空表示新建
            startActivity(intent)
        }

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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_mem -> startActivity(Intent(this, MemoActivity::class.java))
            R.id.nav_about -> startActivity(Intent(this, AboutActivity::class.java))
            R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))

        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
