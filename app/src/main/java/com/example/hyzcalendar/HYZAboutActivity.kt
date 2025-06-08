package com.example.hyzcalendar

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView

class HYZAboutActivity : HYZBaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: androidx.drawerlayout.widget.DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun getLayoutResourceId(): Int = R.layout.hyz_activity_about

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化控件
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)


        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)

        // 设置关于内容
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            "未知"
        }

        findViewById<TextView>(R.id.tvAppNameVersion).text = "WeatherCalendar\n版本号：$versionName"
        findViewById<TextView>(R.id.tvCopyright).text =
            "\u00A9 2025 向华 版权所有"

        findViewById<TextView>(R.id.tvContact).text = "联系方式：\nxiangyearxy@163.com\nxiangyear@gmail.com"
        findViewById<TextView>(R.id.tvGithub).text = "GitHub: https://github.com/siangus"
        findViewById<TextView>(R.id.tvThanks).text = """
            鸣谢：
            - 浙江工商大学(扭头)
        """.trimIndent()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> startActivity(Intent(this, HYZSettingsActivity::class.java))
            R.id.nav_mem -> startActivity(Intent(this, HYZMemoActivity::class.java))
            R.id.nav_about -> {  }
            R.id.nav_home -> startActivity(Intent(this, HYZMainActivity::class.java))
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
