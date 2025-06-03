package com.example.calendar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var backgroundImageView: ImageView  // 把 private 改成 protected
    protected lateinit var rootContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 创建根容器
        rootContainer = FrameLayout(this)

        // 添加背景 ImageView
        backgroundImageView = ImageView(this).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            alpha = 0.3f // 默认透明度，可以从 SharedPreferences 设置
        }

        rootContainer.addView(backgroundImageView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        // 添加子类布局
        val content = layoutInflater.inflate(getLayoutResourceId(), null)
        rootContainer.addView(content)

        // 设置整个布局为 Activity 的内容
        setContentView(rootContainer)

        // 子类可选择设置背景
        applyBackgroundImage()
    }

    abstract fun getLayoutResourceId(): Int

    open fun applyBackgroundImage() {
        // 可以从 SharedPreferences 获取路径
        val drawable: Drawable? = getDrawable(R.drawable.default_background)
        backgroundImageView.setImageDrawable(drawable)
    }

    fun setBackgroundAlpha(alpha: Float) {
        backgroundImageView.alpha = alpha
    }
}
