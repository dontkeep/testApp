package com.exal.testapp.view.perspective

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

class EditPerspectiveImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val POINT_RADIUS_DP = 10
        private const val LINE_WIDTH_DP = 2
        private const val MIN_POINT_DISTANCE_DP = 20
    }

    private val paint = Paint()
    private var bitmap: Bitmap? = null
    private val perspectivePoints = PerspectivePoints()
    private val pointRadiusPx = dpToPx(POINT_RADIUS_DP)
    private val minPointDistancePx = dpToPx(MIN_POINT_DISTANCE_DP)
    private var activePoint: PointF? = null
    private var bitmapRect: RectF = RectF()

    fun setBitmap(newBitmap: Bitmap?) {
        bitmap = newBitmap
        invalidate()
        resetPoints()
    }

    fun getPerspective(): PerspectivePoints {
        // Transform points back to the bitmap's original scale
        val scaleX = bitmap?.width?.toFloat()?.div(bitmapRect.width()) ?: 1f
        val scaleY = bitmap?.height?.toFloat()?.div(bitmapRect.height()) ?: 1f

        return PerspectivePoints(
            PointF(
                (perspectivePoints.pointLeftTop.x - bitmapRect.left) * scaleX,
                (perspectivePoints.pointLeftTop.y - bitmapRect.top) * scaleY
            ),
            PointF(
                (perspectivePoints.pointRightTop.x - bitmapRect.left) * scaleX,
                (perspectivePoints.pointRightTop.y - bitmapRect.top) * scaleY
            ),
            PointF(
                (perspectivePoints.pointRightBottom.x - bitmapRect.left) * scaleX,
                (perspectivePoints.pointRightBottom.y - bitmapRect.top) * scaleY
            ),
            PointF(
                (perspectivePoints.pointLeftBottom.x - bitmapRect.left) * scaleX,
                (perspectivePoints.pointLeftBottom.y - bitmapRect.top) * scaleY
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { bmp ->
            // Draw the bitmap scaled to fit within the view
            canvas.drawBitmap(bmp, null, bitmapRect, null)

            val scale: Float = min(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
            val scaledWidth = bmp.width * scale
            val scaledHeight = bmp.height * scale
            val left = (width - scaledWidth) / 2f
            val top = (height - scaledHeight) / 2f

            bitmapRect.set(left, top, left + scaledWidth, top + scaledHeight)

            // Draw perspective lines
            paint.color = Color.BLUE
            paint.strokeWidth = dpToPx(LINE_WIDTH_DP)
            paint.style = Paint.Style.STROKE
            drawLine(canvas, perspectivePoints.pointLeftTop, perspectivePoints.pointRightTop)
            drawLine(canvas, perspectivePoints.pointRightTop, perspectivePoints.pointRightBottom)
            drawLine(canvas, perspectivePoints.pointRightBottom, perspectivePoints.pointLeftBottom)
            drawLine(canvas, perspectivePoints.pointLeftBottom, perspectivePoints.pointLeftTop)

            // Draw perspective points
            paint.color = Color.RED
            paint.style = Paint.Style.FILL
            drawPoint(canvas, perspectivePoints.pointLeftTop)
            drawPoint(canvas, perspectivePoints.pointRightTop)
            drawPoint(canvas, perspectivePoints.pointRightBottom)
            drawPoint(canvas, perspectivePoints.pointLeftBottom)
        }
    }

    private fun resetPoints() {
        bitmap?.let { bmp ->
            // Calculate how the bitmap should be scaled and positioned to fit the view
            val scale: Float = min(width.toFloat() / bmp.width, height.toFloat() / bmp.height)
            val scaledWidth = bmp.width * scale
            val scaledHeight = bmp.height * scale
            val left = (width - scaledWidth) / 2f
            val top = (height - scaledHeight) / 2f

            bitmapRect.set(left, top, left + scaledWidth, top + scaledHeight)

            // Set default points for perspective adjustment
            perspectivePoints.set(
                PointF(bitmapRect.left, bitmapRect.top),
                PointF(bitmapRect.right, bitmapRect.top),
                PointF(bitmapRect.right, bitmapRect.bottom),
                PointF(bitmapRect.left, bitmapRect.bottom)
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        resetPoints()
    }

    private fun drawLine(canvas: Canvas, start: PointF, end: PointF) {
        canvas.drawLine(start.x, start.y, end.x, end.y, paint)
    }

    private fun drawPoint(canvas: Canvas, point: PointF) {
        canvas.drawCircle(point.x, point.y, pointRadiusPx, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                activePoint = getClosestPoint(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                activePoint?.let {
                    it.x = event.x.coerceIn(bitmapRect.left, bitmapRect.right)
                    it.y = event.y.coerceIn(bitmapRect.top, bitmapRect.bottom)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                activePoint = null
            }
        }
        return true
    }

    private fun getClosestPoint(x: Float, y: Float): PointF? {
        val points = listOf(
            perspectivePoints.pointLeftTop,
            perspectivePoints.pointRightTop,
            perspectivePoints.pointRightBottom,
            perspectivePoints.pointLeftBottom
        )

        return points.minByOrNull { distance(it.x, it.y, x, y) }
            ?.takeIf { distance(it.x, it.y, x, y) <= minPointDistancePx }
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return kotlin.math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        )
    }
}
