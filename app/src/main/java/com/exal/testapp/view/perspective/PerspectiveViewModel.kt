package com.exal.testapp.view.perspective

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exal.testapp.data.DataRepository
import com.exal.testapp.data.Resource
import com.exal.testapp.data.network.response.ScanImageResponse
import com.exal.testapp.helper.createCustomTempFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class PerspectiveViewModel @Inject constructor(private val dataRepository: DataRepository): ViewModel() {

    private val _originalBitmap = MutableLiveData<Bitmap?>()
    val originalBitmap: LiveData<Bitmap?> get() = _originalBitmap

    private val _transformedBitmap = MutableLiveData<Bitmap?>()
    val transformedBitmap: LiveData<Bitmap?> get() = _transformedBitmap

    private val _perspectivePoints = MutableLiveData<PerspectivePoints?>()
    val perspectivePoints: LiveData<PerspectivePoints?> get() = _perspectivePoints

    private val _scanState = MutableLiveData<Resource<ScanImageResponse>>()
    val scanState: LiveData<Resource<ScanImageResponse>> get() = _scanState

    fun setOriginalBitmap(bitmap: Bitmap) {
        _originalBitmap.value = bitmap
    }

    fun setPerspectivePoints(leftTop: PointF, rightTop: PointF, rightBottom: PointF, leftBottom: PointF) {
        val points = PerspectivePoints()
        points.set(leftTop, rightTop, rightBottom, leftBottom)
        _perspectivePoints.value = points
    }

    private fun isOpenCVInitialized(): Boolean {
        return OpenCVLoader.initDebug()
    }

    fun performPerspectiveTransform() {
        if (!isOpenCVInitialized()){
         Log.d("ViewModelPerspective", "OpenCV not initialized")
        }else {
            val points = _perspectivePoints.value
            val bitmap = _originalBitmap.value

            if (bitmap != null && points != null && isValidPoints(points)) {
                _transformedBitmap.value = transformBitmap(bitmap, points)
            }
        }
    }

    fun isValidPoints(points: PerspectivePoints): Boolean {
        val uniquePoints = setOf(
            points.pointLeftTop,
            points.pointRightTop,
            points.pointLeftBottom,
            points.pointRightBottom
        )
        return uniquePoints.size == 4
    }

    private fun transformBitmap(bitmap: Bitmap, points: PerspectivePoints): Bitmap {
        val width = calculateDistance(points.pointLeftTop, points.pointRightTop).toInt()
        val height = calculateDistance(points.pointLeftTop, points.pointLeftBottom).toInt()

        val src = MatOfPoint2f(
            Point(points.pointLeftTop.x.toDouble(), points.pointLeftTop.y.toDouble()),
            Point(points.pointRightTop.x.toDouble(), points.pointRightTop.y.toDouble()),
            Point(points.pointRightBottom.x.toDouble(), points.pointRightBottom.y.toDouble()),
            Point(points.pointLeftBottom.x.toDouble(), points.pointLeftBottom.y.toDouble())
        )

        val dst = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(width.toDouble(), 0.0),
            Point(width.toDouble(), height.toDouble()),
            Point(0.0, height.toDouble())
        )

        val transformMatrix = Imgproc.getPerspectiveTransform(src, dst)

        val srcMat = Mat()
        Utils.bitmapToMat(bitmap, srcMat)

        val dstMat = Mat()
        Imgproc.warpPerspective(
            srcMat,
            dstMat,
            transformMatrix,
            Size(width.toDouble(), height.toDouble())
        )

        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dstMat, outputBitmap)

        return outputBitmap
    }

    private fun calculateDistance(p1: android.graphics.PointF, p2: android.graphics.PointF): Float {
        return kotlin.math.sqrt((p2.x - p1.x).pow(2) + (p2.y - p1.y).pow(2))
    }

    fun scanImage(file: MultipartBody.Part) {
        viewModelScope.launch {
            dataRepository.scanImage(file).collect { resource ->
                _scanState.postValue(resource)
            }
        }
    }
}



