package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class BackgroundSettingActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var alphaText: TextView

    private val PICK_IMAGE_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_setting)

        radioGroup = findViewById(R.id.bgRadioGroup)
        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaText = findViewById(R.id.alphaTextView)

        val prefs = getSharedPreferences("calendar_prefs", MODE_PRIVATE)
        val bgOption = prefs.getInt("background_option", 0)
        val alpha = prefs.getFloat("overlay_alpha", 0.4f)

        // 初始化选中状态
        when (bgOption) {
            0 -> radioGroup.check(R.id.bgNone)
            1 -> radioGroup.check(R.id.bgDefault)
            2 -> radioGroup.check(R.id.bgCustom)
        }

        alphaSeekBar.progress = (alpha * 100).toInt()
        alphaText.text = "透明度: ${(alpha * 100).toInt()}%"

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bgNone -> saveBackgroundOption(0, null)
                R.id.bgDefault -> saveBackgroundOption(1, null)
                R.id.bgCustom -> openImagePicker()
            }
        }

        alphaSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alphaText.text = "透明度: $progress%"
                prefs.edit().putFloat("overlay_alpha", progress / 100f).apply()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun saveBackgroundOption(option: Int, uriString: String?) {
        val editor = getSharedPreferences("calendar_prefs", MODE_PRIVATE).edit()
        editor.putInt("background_option", option)
        if (uriString != null) editor.putString("custom_bg_uri", uriString)
        editor.apply()
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                saveBackgroundOption(2, uri.toString())
                Toast.makeText(this, "自定义背景已保存", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
