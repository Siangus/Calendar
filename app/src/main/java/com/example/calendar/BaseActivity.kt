package com.example.calendar

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
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

        // 创建根容器
        rootContainer = FrameLayout(this)

        // 先初始化背景图层
        backgroundImageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.FIT_XY
            isClickable = false
            isFocusable = false
        }

        // 添加背景图层（最底层，index 0）
        rootContainer.addView(
            backgroundImageView,
            0,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        // 添加子类布局（内容层，覆盖在背景图上）
        val content = layoutInflater.inflate(getLayoutResourceId(), null)
        rootContainer.addView(content)

        setContentView(rootContainer)

        // 确保backgroundImageView已初始化后调用
        applyBackgroundFromPrefs()
    }

    abstract fun getLayoutResourceId(): Int

    protected fun applyBackgroundFromPrefs() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val alpha = prefs.getFloat("background_alpha", 1.0f)
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha

        rootContainer.setBackgroundColor(Color.WHITE)  // 白底，始终保持

        val croppedFile = getFileStreamPath("cropped_bg.png")
        if (croppedFile.exists() && croppedFile.length() > 0) {
            val bitmap = android.graphics.BitmapFactory.decodeFile(croppedFile.absolutePath)
            if (bitmap != null) {
                backgroundImageView.setImageBitmap(bitmap)
                return
            }
        }
        // 没有裁剪图，加载默认背景图（假设默认背景图是纯白）
        backgroundImageView.setImageResource(R.drawable.default_background)
    }



    open fun setBackgroundAlpha(alpha: Float) {
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha
        getSharedPreferences("app_settings", MODE_PRIVATE)
            .edit().putFloat("background_alpha", clampedAlpha).apply()
    }
}
