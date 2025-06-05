package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.*
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.File

class BackgroundSettingActivity : BaseActivity() {

    private lateinit var bgSettingListView: ListView
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var alphaText: TextView
    private lateinit var restartButton: Button

    private val PICK_IMAGE_REQUEST_CODE = 1001

    private val options = listOf("默认背景", "自定义背景")

    override fun getLayoutResourceId(): Int = R.layout.activity_background_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "背景设置"
        bgSettingListView = findViewById(R.id.bgSettingListView)
        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaText = findViewById(R.id.alphaTextView)
        restartButton = findViewById(R.id.restartButton)

        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val alpha = prefs.getFloat("background_alpha", 0.4f)
        val bgOption = prefs.getInt("background_option", 1)

        // 设置透明度 SeekBar 和 TextView
        alphaSeekBar.progress = (alpha * 100).toInt()
        alphaText.text = "透明度: ${(alpha * 100).toInt()}%"

        // 给 ListView 设适配器，显示两个选项
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, options)
        bgSettingListView.adapter = adapter
        bgSettingListView.choiceMode = ListView.CHOICE_MODE_SINGLE

        // 选中当前保存的背景选项
        bgSettingListView.setItemChecked(bgOption - 1, true)

        // 点击切换背景选项
        bgSettingListView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // 默认背景
                    saveBackgroundOption(1, null)
                    Toast.makeText(this, "选择默认背景", Toast.LENGTH_SHORT).show()
                }
                1 -> {
                    // 自定义背景，打开图片选择器
                    openImagePicker()
                }
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

        restartButton.setOnClickListener {
            Toast.makeText(this, "重启应用...", Toast.LENGTH_SHORT).show()

            // 获取启动 Activity 的 Intent
            val packageManager = packageManager
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            // 清除任务栈并创建新任务
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK)

            // 延迟一会确保 Toast 显示完整
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(intent)
                // 结束当前进程（可选）
                // android.os.Process.killProcess(android.os.Process.myPid())
            }, 500)
        }
    }

    private fun saveBackgroundOption(option: Int, uriString: String?) {
        val editor = getSharedPreferences("app_settings", MODE_PRIVATE).edit()
        editor.putInt("background_option", option)
        if (uriString != null) {
            editor.putString("custom_background_uri", uriString)
        } else {
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
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: SecurityException) { }
                startCropImage(uri)
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                Toast.makeText(this, "裁剪成功：${resultUri.path}", Toast.LENGTH_SHORT).show()
                Log.d("UCrop", "成功裁剪输出到: ${resultUri.path}")
                saveBackgroundOption(2, resultUri.toString())
                bgSettingListView.setItemChecked(1, true)
            } else {
                Toast.makeText(this, "裁剪失败，Uri为空", Toast.LENGTH_SHORT).show()
                Log.e("UCrop", "裁剪失败：结果 Uri 为空")
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
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

        val options = UCrop.Options().apply {
            setCompressionFormat(Bitmap.CompressFormat.PNG)
            setCompressionQuality(100)
            setFreeStyleCropEnabled(false)
        }

        val uCrop = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(aspectRatioX, aspectRatioY)
            .withMaxResultSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
            .withOptions(options)

        val cropIntent = uCrop.getIntent(this).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }

        startActivityForResult(cropIntent, UCrop.REQUEST_CROP)
    }

    override fun setBackgroundAlpha(alpha: Float) {
        val limitedAlpha = if (alpha > 0.2f) 0.2f else alpha
        super.setBackgroundAlpha(limitedAlpha)
    }
}
