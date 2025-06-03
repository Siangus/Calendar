package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import java.io.File

// 继承 BaseActivity
class BackgroundSettingActivity : BaseActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var alphaText: TextView
    private lateinit var restartButton: Button

    private val PICK_IMAGE_REQUEST_CODE = 1001
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_background_setting
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        radioGroup = findViewById(R.id.bgRadioGroup)
        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaText = findViewById(R.id.alphaTextView)
        restartButton = findViewById(R.id.restartButton) // 新增按钮

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val alpha = prefs.getFloat("background_alpha", 0.4f)

        // 判断是否有裁剪图Uri，有的话强制选中自定义背景，否则用保存的设置
        val croppedUriString = prefs.getString("custom_background_uri", null)
        val hasCroppedImage = !croppedUriString.isNullOrEmpty()

        if (hasCroppedImage) {
            radioGroup.check(R.id.bgCustom)
            // 禁止默认按钮被选中（如果你希望禁用它，可以额外加禁用逻辑）
        } else {
            val bgOption = prefs.getInt("background_option", 1)
            when (bgOption) {
                1 -> radioGroup.check(R.id.bgDefault)
                2 -> radioGroup.check(R.id.bgCustom)
                else -> radioGroup.check(R.id.bgDefault)
            }
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

        // 重启按钮点击事件，示例重启 Activity 或调用系统重启逻辑
        restartButton.setOnClickListener {
            Toast.makeText(this, "重启生效...", Toast.LENGTH_SHORT).show()
            // 简单重启当前 Activity
            val intent = intent
            finish()
            startActivity(intent)
            // 你也可以改成重启整个应用的逻辑
        }
    }

    // 保存设置（background_color 是 BaseActivity 使用的 key）
    private fun saveBackgroundOption(option: Int, uriString: String?) {
        val editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit()
        editor.putInt("background_color", option)
        if (uriString != null) {
            editor.putString("custom_background_uri", uriString)
        } else {
            // 选默认背景时，清除裁剪Uri
            editor.remove("custom_background_uri")
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
                // 选中自定义背景按钮
                radioGroup.check(R.id.bgCustom)
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

        if (outputFile.exists()) outputFile.delete()

        val destinationUri = FileProvider.getUriForFile(
            this,
            "$packageName.fileprovider",
            outputFile
        )

        val displayMetrics = resources.displayMetrics
        val aspectRatioX = displayMetrics.widthPixels.toFloat()
        val aspectRatioY = displayMetrics.heightPixels.toFloat()

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

        startActivityForResult(cropIntent, com.yalantis.ucrop.UCrop.REQUEST_CROP)
    }

    // 覆写 BaseActivity 中获取窗口透明度的方法，让本界面更透明（阈值你可以调整）
    override fun setBackgroundAlpha(alpha: Float) {
        val limitedAlpha = if (alpha > 0.2f) 0.2f else alpha
        super.setBackgroundAlpha(limitedAlpha)
    }
}
