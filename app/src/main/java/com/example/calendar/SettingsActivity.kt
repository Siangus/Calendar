package com.example.calendar
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class SettingsActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val settingItems = listOf("节日显示", "天气显示", "全局背景")

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar

    override fun getLayoutResourceId(): Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val listView = findViewById<ListView>(R.id.settingListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, settingItems)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = settingItems[position]
            showSettingDialog(selected)
        }

        val restartButton = findViewById<Button>(R.id.restartButton)
        restartButton.setOnClickListener {
            Toast.makeText(this, "重启应用...", Toast.LENGTH_SHORT).show()

            val packageManager = packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            intent?.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )

            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                // android.os.Process.killProcess(android.os.Process.myPid())
            }, 500)
        }
    }

    private fun showSettingDialog(settingName: String) {
        // 保持你已有的实现不变
        // ...
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> drawerLayout.closeDrawer(GravityCompat.START)  // 当前页关闭抽屉
            R.id.nav_mem -> startActivity(Intent(this, MemoActivity::class.java))
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
