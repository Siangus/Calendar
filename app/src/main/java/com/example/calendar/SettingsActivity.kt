package com.example.calendar
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
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
        when (settingName) {
            "节日显示" -> {
                val options = arrayOf("不显示节日", "仅显示公历节日", "公历与农历节日")
                val currentMode = ConfigManager.getInt(ConfigManager.Keys.SHOW_FESTIVAL, 1)

                AlertDialog.Builder(this)
                    .setTitle("节日显示设置")
                    .setSingleChoiceItems(options, currentMode) { dialog, which ->
                        ConfigManager.set(ConfigManager.Keys.SHOW_FESTIVAL, which)
                        Toast.makeText(this, "节日显示设置已保存", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            "天气显示" -> {
                val input = EditText(this)
                input.setText(ConfigManager.getString(ConfigManager.Keys.WEATHER_API_KEY) ?: "")

                AlertDialog.Builder(this)
                    .setTitle("天气显示设置")
                    .setMessage("请输入天气API Key")
                    .setView(input)
                    .setPositiveButton("保存") { _, _ ->
                        val newKey = input.text.toString().trim()
                        ConfigManager.set(ConfigManager.Keys.WEATHER_API_KEY, newKey)
                        Toast.makeText(this, "API Key已保存", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            "全局背景" -> {
                startActivity(Intent(this, BackgroundSettingActivity::class.java))
            }
        }
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
