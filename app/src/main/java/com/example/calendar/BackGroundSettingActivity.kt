package com.example.calendar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import com.yalantis.ucrop.UCrop
import java.io.File

class BackgroundSettingActivity : BaseActivity() {

    private lateinit var listView: ListView
    private lateinit var alphaSeekBar: SeekBar
    private lateinit var alphaText: TextView
    private lateinit var restartButton: Button

    private val bgOptions = listOf("默认背景", "自定义背景")

    private var selectedIndex = 0 // 0默认，1自定义

    override fun getLayoutResourceId(): Int = R.layout.activity_background_setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ConfigManager.init(applicationContext) // 初始化配置管理器

        setContentView(R.layout.activity_background_setting)

        listView = findViewById(R.id.bgSettingListView)
        alphaSeekBar = findViewById(R.id.alphaSeekBar)
        alphaText = findViewById(R.id.alphaTextView)
        restartButton = findViewById(R.id.restartButton)

        // 读取保存的背景设置（注意 key 应和 BaseActivity 统一）
        selectedIndex = ConfigManager.getInt(ConfigManager.Keys.BACKGROUND_OPTION, 0)

        val alpha = ConfigManager.getFloat(ConfigManager.Keys.BACKGROUND_ALPHA, 0.4f)
        alphaSeekBar.progress = (alpha * 100).toInt()
        alphaText.text = "透明度: ${(alpha * 100).toInt()}%"

        val adapter = object : ArrayAdapter<String>(this, R.layout.item_bg_setting, bgOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: layoutInflater.inflate(R.layout.item_bg_setting, parent, false)
                val textView = view.findViewById<TextView>(R.id.bgItemText)
                textView.text = getItem(position)
                // 选中项背景高亮
                view.setBackgroundColor(if (position == selectedIndex) 0xFFE0E0E0.toInt() else 0x00000000)
                return view
            }
        }
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> {
                    // 选默认背景，清除自定义 Uri，保存配置
                    selectedIndex = 0
                    saveBackgroundOption(0, null)
                    Toast.makeText(this, "已切换为默认背景", Toast.LENGTH_SHORT).show()
                    adapter.notifyDataSetChanged()
                }
                1 -> {
                    // 选自定义，打开图片选择
                    openImagePicker()
                }
            }
        }

        alphaSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                alphaText.text = "透明度: $progress%"
                ConfigManager.set(ConfigManager.Keys.BACKGROUND_ALPHA, progress / 100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        restartButton.setOnClickListener {
            Toast.makeText(this, "重启生效...", Toast.LENGTH_SHORT).show()
            finish()
            startActivity(intent) // 重启自身刷新
        }
    }

    private fun saveBackgroundOption(option: Int, uriString: String?) {
        ConfigManager.set(ConfigManager.Keys.BACKGROUND_OPTION, option)
        if (uriString != null) {
            ConfigManager.set(ConfigManager.Keys.CUSTOM_BACKGROUND_URI, uriString)
            selectedIndex = 1
        } else {
            ConfigManager.remove(ConfigManager.Keys.CUSTOM_BACKGROUND_URI)
            selectedIndex = 0
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } catch (_: SecurityException) {}
                startCropImage(uri)
            }
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            if (resultUri != null) {
                Toast.makeText(this, "裁剪成功：${resultUri.path}", Toast.LENGTH_SHORT).show()
                saveBackgroundOption(1, resultUri.toString())
                (listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            } else {
                Toast.makeText(this, "裁剪失败，Uri为空", Toast.LENGTH_SHORT).show()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            cropError?.printStackTrace()
            Toast.makeText(this, "裁剪出错: ${cropError?.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun startCropImage(sourceUri: Uri) {
        val outputFile = File(filesDir, "cropped_bg.png")
        if (outputFile.exists()) outputFile.delete()

        val destinationUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", outputFile)
        val displayMetrics = resources.displayMetrics
        val aspectRatioX = displayMetrics.widthPixels.toFloat()
        val aspectRatioY = displayMetrics.heightPixels.toFloat()

        val options = UCrop.Options().apply {
            setCompressionFormat(android.graphics.Bitmap.CompressFormat.PNG)
            setCompressionQuality(100)
            setFreeStyleCropEnabled(false)
        }

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(aspectRatioX, aspectRatioY)
            .withMaxResultSize(displayMetrics.widthPixels, displayMetrics.heightPixels)
            .withOptions(options)
            .start(this)
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 1001
    }
}
