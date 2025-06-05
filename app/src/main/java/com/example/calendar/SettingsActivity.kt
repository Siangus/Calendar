package com.example.calendar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import android.os.Handler
import android.widget.EditText

class SettingsActivity : BaseActivity() {

    private val settingItems = listOf("节日显示", "天气显示", "全局背景")

    override fun getLayoutResourceId(): Int = R.layout.activity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            /*我看得清（bushi）感觉麻烦先不做了
            "字体大小" -> {
                val options = arrayOf("小", "中", "大")
                AlertDialog.Builder(this)
                    .setTitle("选择字体大小")
                    .setSingleChoiceItems(options, -1) { dialog, which ->
                        // TODO: 保存字体大小设置逻辑
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }

             */
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
                // 这里用ConfigManager.getString()返回的是String?，所以用 ?: "" 防止空指针
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
}
