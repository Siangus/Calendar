package com.example.calendar

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var backgroundImageView: ImageView
    protected lateinit var rootContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建根容器
        rootContainer = FrameLayout(this)

        // 先初始化背景图层
        backgroundImageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            isClickable = false      // 不拦截触摸事件
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
        val bgType = prefs.getInt("background_color", 0)
        val alpha = prefs.getFloat("background_alpha", 1.0f)

        Log.d("BaseActivity", "applyBackgroundFromPrefs: bgType=$bgType, alpha=$alpha")

        // 透明度限制范围，避免全透明导致看不到背景
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha

        when (bgType) {
            0 -> {
                rootContainer.setBackgroundColor(Color.WHITE)
                val drawable = getDrawable(R.drawable.default_background)
                Log.d("BaseActivity", "Setting default background drawable: $drawable")
                backgroundImageView.setImageDrawable(drawable)
            }
            1 -> {
                rootContainer.setBackgroundColor(Color.LTGRAY)
                backgroundImageView.setImageDrawable(null)
            }
            2 -> {
                rootContainer.setBackgroundColor(Color.parseColor("#ADD8E6"))
                backgroundImageView.setImageDrawable(null)
            }
            3 -> {
                rootContainer.setBackgroundColor(Color.TRANSPARENT)
                val uriStr = prefs.getString("custom_background_uri", null)
                Log.d("BaseActivity", "Custom URI: $uriStr")
                if (uriStr != null) {
                    val uri = Uri.parse(uriStr)
                    backgroundImageView.setImageURI(uri)
                } else {
                    backgroundImageView.setImageDrawable(null)
                }
            }
            else -> {
                rootContainer.setBackgroundColor(Color.WHITE)
                backgroundImageView.setImageDrawable(null)
            }
        }
    }

    fun setBackgroundAlpha(alpha: Float) {
        val clampedAlpha = alpha.coerceIn(0.1f, 0.6f)
        backgroundImageView.alpha = clampedAlpha
        getSharedPreferences("app_settings", MODE_PRIVATE)
            .edit().putFloat("background_alpha", clampedAlpha).apply()
    }
}
