package com.example.calendar

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.File

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var backgroundImageView: ImageView
    protected lateinit var rootContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 ConfigManager（防止未初始化）
        ConfigManager.init(applicationContext)

        // 创建根容器
        rootContainer = FrameLayout(this)

        // 初始化背景图层
        backgroundImageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_XY
            isClickable = false
            isFocusable = false
        }

        // 添加背景图层（底层）
        rootContainer.addView(
            backgroundImageView,
            0,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // 添加内容层
        val content = layoutInflater.inflate(getLayoutResourceId(), null)
        rootContainer.addView(content)

        setContentView(rootContainer)

        applyBackgroundFromConfig()
    }

    abstract fun getLayoutResourceId(): Int

    protected fun applyBackgroundFromConfig() {
        val alpha = ConfigManager.getFloat(ConfigManager.Keys.BACKGROUND_ALPHA, 1.0f)
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha
        rootContainer.setBackgroundColor(Color.WHITE)

        when (ConfigManager.getInt(ConfigManager.Keys.BACKGROUND_OPTION, 1)) {
            2 -> {
                val croppedFile = getFileStreamPath("cropped_bg.png")
                if (croppedFile.exists() && croppedFile.length() > 0) {
                    val bitmap = BitmapFactory.decodeFile(croppedFile.absolutePath)
                    if (bitmap != null) {
                        backgroundImageView.setImageBitmap(bitmap)
                        return
                    } else {
                        Log.e("BaseActivity", "自定义背景文件存在但解析失败")
                    }
                } else {
                    Log.w("BaseActivity", "自定义背景文件不存在")
                }
            }
        }

        // 默认背景（可以自定义）
        backgroundImageView.setImageResource(R.drawable.default_background)
    }

    open fun setBackgroundAlpha(alpha: Float) {
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha
        ConfigManager.set(ConfigManager.Keys.BACKGROUND_ALPHA, clampedAlpha)
    }
}
