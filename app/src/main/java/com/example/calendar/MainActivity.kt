package com.example.calendar

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var calendarView: MaterialCalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 Toolbar 并设置为 ActionBar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // 设置抽屉开关，连接 Toolbar 和 DrawerLayout
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 初始化日历控件
        calendarView = findViewById(R.id.calendarView)

        // 默认选中今天
        calendarView.selectedDate = CalendarDay.today()

        // 日期选择监听
        calendarView.setOnDateChangedListener { _, date, _ ->
            Toast.makeText(
                this,
                "选中日期: ${date.year}/${date.month + 1}/${date.day}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // 侧边栏菜单点击事件回调
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // 这里可以根据 item.itemId 做不同的操作
        when (item.itemId) {
  //          R.id.nav_some_option -> {
            //Toast.makeText(this, "点击了菜单项", Toast.LENGTH_SHORT).show()
  //          }
            // 其他菜单项处理...
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // 按返回键时，若抽屉打开，则先关闭抽屉
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
