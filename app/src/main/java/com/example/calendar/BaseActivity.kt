package com.example.calendar

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var backgroundImageView: ImageView
    protected lateinit var rootContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rootContainer = FrameLayout(this)

        backgroundImageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        rootContainer.addView(backgroundImageView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        // inflate 子类布局并添加
        val content = layoutInflater.inflate(getLayoutResourceId(), null)
        rootContainer.addView(content)

        setContentView(rootContainer)

        applyBackgroundFromPrefs()
    }

    abstract fun getLayoutResourceId(): Int

    protected fun applyBackgroundFromPrefs() {
        val prefs = getSharedPreferences("app_settings", MODE_PRIVATE)
        val bgType = prefs.getInt("background_color", 0)
        val alpha = prefs.getFloat("background_alpha", 0.3f)

        backgroundImageView.alpha = alpha

        when (bgType) {
            0 -> {
                rootContainer.setBackgroundColor(Color.WHITE)
                backgroundImageView.setImageDrawable(null)
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
        backgroundImageView.alpha = alpha
        getSharedPreferences("app_settings", MODE_PRIVATE)
            .edit().putFloat("background_alpha", alpha).apply()
    }
}
