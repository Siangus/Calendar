package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File

class BackgroundSettingActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var alphaText: TextView

    private val PICK_IMAGE_REQUEST_CODE = 1001
    private val CROP_REQUEST_CODE = 2001

    private var cropImageUri: Uri? = null  // 裁剪结果Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_background_setting)

        radioGroup = findViewById(R.id.bgRadioGroup)
        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaText = findViewById(R.id.alphaTextView)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val bgOption = prefs.getInt("background_option", 1)
        val alpha = prefs.getFloat("background_alpha", 0.4f)

        when (bgOption) {
            1 -> radioGroup.check(R.id.bgDefault)
            2 -> radioGroup.check(R.id.bgCustom)
            else -> radioGroup.check(R.id.bgDefault)
        }

        alphaSeekBar.progress = (alpha * 100).toInt()
        alphaText.text = "透明度: ${(alpha * 100).toInt()}%"

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bgDefault -> saveBackgroundOption(1, null)
                R.id.bgCustom -> openImagePicker()
            }
        }

        alphaSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alphaText.text = "透明度: $progress%"
                prefs.edit().putFloat("background_alpha", progress / 100f).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun saveBackgroundOption(option: Int, uriString: String?) {
        val editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit()
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
                Log.d("CropDebug", "用户选择了图片: $uri")
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    startCropImage(uri)
                } catch (e: SecurityException) {
                    Log.e("CropDebug", "权限申请失败: ${e.message}")
                    Toast.makeText(this, "读取图片权限被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            cropImageUri?.let { uri ->
                val file = File(uri.path ?: "")
                Log.d("CropDebug", "裁剪返回 URI: $uri")
                Log.d("CropDebug", "裁剪输出文件是否存在: ${file.exists()}")
                saveBackgroundOption(2, uri.toString())
                Toast.makeText(this, "自定义背景已保存", Toast.LENGTH_SHORT).show()
            } ?: run {
                Log.e("CropDebug", "裁剪失败，Uri 为空")
                Toast.makeText(this, "裁剪失败，未获取到 Uri", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun startCropImage(sourceUri: Uri) {
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        fun gcd(a: Int, b: Int): Int {
            return if (b == 0) a else gcd(b, a % b)
        }
        val divisor = gcd(width, height)
        val aspectX = width / divisor
        val aspectY = height / divisor

        val outputFile = File(cacheDir, "cropped_bg.png")
        if (outputFile.exists()) {
            outputFile.delete()
            Log.d("CropDebug", "已删除旧裁剪文件")
        }

        val created = outputFile.createNewFile()
        Log.d("CropDebug", "创建新裁剪输出文件: ${outputFile.absolutePath}, 成功: $created")

        cropImageUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            outputFile
        )

        val cropIntent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(sourceUri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", aspectX)
            putExtra("aspectY", aspectY)
            putExtra("outputX", width)
            putExtra("outputY", height)
            putExtra("scale", true)
            putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri)
            putExtra("return-data", false)
            addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        Log.d("CropDebug", "准备启动裁剪，源图 URI: $sourceUri, 裁剪输出 URI: $cropImageUri")

        if (cropIntent.resolveActivity(packageManager) != null) {
            Log.d("CropDebug", "找到裁剪应用，启动裁剪")
            startActivityForResult(cropIntent, CROP_REQUEST_CODE)
        } else {
            Log.e("CropDebug", "未找到可处理裁剪的应用")
            Toast.makeText(this, "设备不支持图片裁剪", Toast.LENGTH_SHORT).show()
            saveBackgroundOption(2, sourceUri.toString())
        }
    }

}


