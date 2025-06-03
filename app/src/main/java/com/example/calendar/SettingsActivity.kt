package com.example.calendar
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private val settingItems = listOf("字体大小", "节日显示", "天气显示", "全局背景")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val listView = findViewById<ListView>(R.id.settingListView)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, settingItems)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selected = settingItems[position]
            showSettingDialog(selected)
        }
    }

    private fun showSettingDialog(settingName: String) {
        when (settingName) {
            "字体大小" -> {
                val options = arrayOf("小", "中", "大")
                AlertDialog.Builder(this)
                    .setTitle("选择字体大小")
                    .setSingleChoiceItems(options, -1) { dialog, which ->
                        // 这里根据which保存对应设置
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            "节日显示" -> {
                AlertDialog.Builder(this)
                    .setTitle("节日显示设置")
                    .setMessage("这里可以配置节日显示相关的设置。")
                    .setPositiveButton("确定", null)
                    .show()
            }
            "天气显示" -> {
                AlertDialog.Builder(this)
                    .setTitle("天气显示设置")
                    .setMessage("这里可以配置天气显示相关的设置。")
                    .setPositiveButton("确定", null)
                    .show()
            }
            "全局背景" -> {
                val backgrounds = arrayOf("白色", "浅灰", "浅蓝")
                AlertDialog.Builder(this)
                    .setTitle("选择全局背景颜色")
                    .setSingleChoiceItems(backgrounds, -1) { dialog, which ->
                        // 保存选择
                        saveBackgroundSetting(which)
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        }
    }
    private fun saveBackgroundSetting(selectedIndex: Int) {
        val sharedPref = getSharedPreferences("app_settings", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("background_color", selectedIndex)
            apply()
        }
        Toast.makeText(this, "背景设置已保存，请重新启动应用生效", Toast.LENGTH_SHORT).show()
    }
}

