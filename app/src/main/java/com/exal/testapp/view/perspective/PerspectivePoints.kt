package com.exal.testapp.view.perspective

import android.graphics.PointF


class PerspectivePoints(
    val pointLeftTop: PointF = PointF(),
    val pointRightTop: PointF = PointF(),
    val pointRightBottom: PointF = PointF(),
    val pointLeftBottom: PointF = PointF()
) {

    fun set(leftTop: PointF, rightTop: PointF, rightBottom: PointF, leftBottom: PointF) {
        pointLeftTop.set(leftTop)
        pointRightTop.set(rightTop)
        pointRightBottom.set(rightBottom)
        pointLeftBottom.set(leftBottom)
    }
}
