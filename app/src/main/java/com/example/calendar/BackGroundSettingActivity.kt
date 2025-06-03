package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
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
        editor.putInt("background_color", option) // 👈 这是 BaseActivity 用的 key
        if (uriString != null) {
            editor.putString("custom_background_uri", uriString)
        }
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
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: SecurityException) { }
                startCropImage(uri)
            }
        } else if (requestCode == com.yalantis.ucrop.UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = com.yalantis.ucrop.UCrop.getOutput(data!!)
            if (resultUri != null) {
                Toast.makeText(this, "裁剪成功：${resultUri.path}", Toast.LENGTH_SHORT).show()
                Log.d("UCrop", "成功裁剪输出到: ${resultUri.path}")
                saveBackgroundOption(2, resultUri.toString())
            } else {
                Toast.makeText(this, "裁剪失败，Uri为空", Toast.LENGTH_SHORT).show()
                Log.e("UCrop", "裁剪失败：结果 Uri 为空")
            }
        } else if (resultCode == com.yalantis.ucrop.UCrop.RESULT_ERROR) {
            val cropError = com.yalantis.ucrop.UCrop.getError(data!!)
            cropError?.printStackTrace()
            Toast.makeText(this, "裁剪出错: ${cropError?.message}", Toast.LENGTH_LONG).show()
            Log.e("UCrop", "裁剪出错", cropError)
        }
    }





    private fun startCropImage(sourceUri: Uri) {
        val outputFile = File(filesDir, "cropped_bg.png")

        // 清理旧文件
        if (outputFile.exists()) outputFile.delete()

        // 使用 FileProvider 获取 Uri
        val destinationUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            outputFile
        )

        // 屏幕比例作为裁剪比例
        val displayMetrics = resources.displayMetrics
        val aspectRatioX = displayMetrics.widthPixels.toFloat()
        val aspectRatioY = displayMetrics.heightPixels.toFloat()

        // 授权 UCrop 写目标文件
        grantUriPermission(
            "com.yalantis.ucrop",
            destinationUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        val options = com.yalantis.ucrop.UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.PNG)
            setCompressionQuality(100)
            setFreeStyleCropEnabled(false)
        }

        val uCrop = com.yalantis.ucrop.UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(aspectRatioX, aspectRatioY)
            .withMaxResultSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
            .withOptions(options)

        val cropIntent = uCrop.getIntent(this).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        // 启动裁剪
        startActivityForResult(cropIntent, com.yalantis.ucrop.UCrop.REQUEST_CROP)
    }




}


