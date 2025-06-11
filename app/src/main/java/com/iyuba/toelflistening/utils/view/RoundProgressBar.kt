package com.iyuba.toelflistening.utils.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.IntRange
import com.iyuba.toelflistening.R
import kotlin.properties.Delegates

/**
苏州爱语吧科技有限公司
 */

@SuppressLint("CustomViewStyleable")
class RoundProgressBar (context: Context, attrs: AttributeSet) :
    View(context, attrs) {
    private val paint = Paint()
    private val oval = RectF()
    private var roundColor by Delegates.notNull<Int>()
    private var roundProgressColor by Delegates.notNull<Int>()
    private var textColor by Delegates.notNull<Int>()
    private var textSize by Delegates.notNull<Float>()
    private var roundWidth by Delegates.notNull<Float>()
    private var max by Delegates.notNull<Int>()
    private var progress =0
    private var textIsDisplayable by Delegates.notNull<Boolean>()
    private var barStyle by Delegates.notNull<Int>()

    private val store = 0
    private val fill = 1


    init {
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.circleProgressBar)
        typeArray.apply {
            roundColor = getColor(R.styleable.circleProgressBar_newCircleColor, Color.RED)
            roundProgressColor = getColor(R.styleable.circleProgressBar_newCircleProgressColor, Color.GREEN)
            textColor = getColor(R.styleable.circleProgressBar_newTextColor, Color.GREEN)
            textSize = getDimension(R.styleable.circleProgressBar_newTextSize, 16F)
            roundWidth = getDimension(R.styleable.circleProgressBar_newCircleWidth, 3F)
            max = getColor(R.styleable.circleProgressBar_newMax, 100)
            textIsDisplayable = getBoolean(R.styleable.circleProgressBar_newTextIsDisplayable, false)
            barStyle = getColor(R.styleable.circleProgressBar_style, store)
            recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centre = (width / 2).toFloat()
        val radius = (centre - roundWidth / 2 - 2)
        paint.apply {
            color = roundColor
            style = Paint.Style.STROKE
            strokeWidth = roundWidth
            isAntiAlias = true
        }
        canvas?.drawCircle(centre, centre, radius, paint)
        paint.apply {
            color = textColor
            textSize = textSize
            strokeWidth = 0F
            typeface = Typeface.DEFAULT_BOLD
        }
        val percent = (progress / max) * 100
        val textWidth = paint.measureText(percent.toString())
        if (textIsDisplayable && percent != 0 && barStyle == store) {
            canvas?.drawText(percent.toString(), centre - textWidth / 2, centre + textSize / 2, paint)
        }
        paint.apply {
            strokeWidth = roundWidth
            color = roundProgressColor
        }
        val leftAndTop = centre - radius
        val rightAndBottom = centre + radius
        oval.apply {
            left = leftAndTop
            top = leftAndTop
            right = rightAndBottom
            bottom = rightAndBottom
        }
        when (barStyle) {
            store -> {
                paint.style = Paint.Style.STROKE
                canvas?.drawArc(oval, 0F, (360F / max * progress), false, paint)
            }
            fill -> {
                if (progress != 0) {
                    canvas?.drawArc(oval, 0F, (360F / max * progress), true, paint)
                }
                paint.style = Paint.Style.FILL_AND_STROKE
            }
        }
    }

    fun inflateMax(@IntRange(from = 0, to = Long.MAX_VALUE) max: Int) {
        this.max = max*1000
    }

    fun inflateProgress(@IntRange(from = 0, to = Long.MAX_VALUE) outProgress: Int) {
        this.progress = (if (outProgress > max) max else outProgress)
        postInvalidate()
    }
}