package com.example.calendar

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var weatherSolver: WeatherRequestSolver  // 用抽象父类引用
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherInfo: TextView
    private lateinit var memoInfo: TextView
    private lateinit var festivalInfo: TextView


    override fun getLayoutResourceId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        weatherIcon = findViewById(R.id.weatherIcon)
        weatherInfo = findViewById(R.id.weatherInfo)
        memoInfo = findViewById(R.id.memoInfo)
        festivalInfo = findViewById(R.id.festivalInfo)
        weatherSolver = ApiRequestSolver
        weatherSolver.init(this) {
            val today = CalendarDay.today()
            val dateStr = "%04d-%02d-%02d".format(today.year, today.month, today.day)
            updateInfoForDate(dateStr)
        }

        // 初始化 Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // 初始化 DrawerLayout 和 NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // 设置抽屉开关
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // 初始化日历控件，默认选中今天
        calendarView = findViewById(R.id.calendarView)
        calendarView.selectedDate = CalendarDay.today()
        calendarView.setOnDateChangedListener { _, date, _ ->
            val dateStr = "%04d-%02d-%02d".format(date.year, date.month, date.day)

            Toast.makeText(this, "选中日期: $dateStr", Toast.LENGTH_SHORT).show()
            updateInfoForDate(dateStr)
        }
        //触发当天的更新
        val today = CalendarDay.today()
        val dateStr = "%04d-%02d-%02d".format(today.year, today.month, today.day)
        updateInfoForDate(dateStr)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_mem -> startActivity(Intent(this, MemoActivity::class.java))
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
    private fun updateInfoForDate(dateStr: String) {
        // 用weatherSolver实例调用
        val resId = weatherSolver.getWeatherIcon(dateStr)
        weatherIcon.setImageResource(resId)
        weatherInfo.text = weatherSolver.getWeatherInfo(dateStr)

        val memo = MemoRequestSolver.getMemoContent(dateStr)
        memoInfo.text = memo
        festivalInfo.text = FestivalSupporter.getFestival(dateStr)
    }


}
