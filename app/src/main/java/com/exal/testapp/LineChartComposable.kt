package com.exal.testapp

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.GridProperties
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ir.ehsannarmani.compose_charts.models.PopupProperties
import ir.ehsannarmani.compose_charts.models.StrokeStyle


val gridProperties = GridProperties(
    xAxisProperties = GridProperties.AxisProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
    yAxisProperties = GridProperties.AxisProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
)
val dividerProperties = DividerProperties(
    xAxisProperties = LineProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    ),
    yAxisProperties = LineProperties(
        thickness = .2.dp,
        color = SolidColor(Color.Gray.copy(alpha = .5f)),
        style = StrokeStyle.Dashed(intervals = floatArrayOf(15f,15f), phase = 10f),
    )
)
val labelProperties = LabelProperties(
    enabled = true,
    padding = 16.dp,
    textStyle = TextStyle(
        Color.White,
    ),
    labels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"),
)

@Composable
fun LineSample() {
    val data = remember {
        listOf(
            Line(
                label = "Windows",
                values = listOf(
                    75.0,
                    5.0,
                    70.0,
                    85.0,
                    0.0,
                    10.0,
                    20.0,
                    30.0,
                    40.0,
                    50.0,
                    60.0,
                    70.0,
                    40.0
                ),
                color = SolidColor(Color(0xFF2B8130)),
                firstGradientFillColor = Color(0xFF66BB6A).copy(alpha = .4f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(.5.dp),
                curvedEdges = true
            ),
        )
    }
    Card(modifier= Modifier.height(270.dp).fillMaxWidth()
        .border(2.dp,Color.Transparent, RoundedCornerShape(12.dp)),
        elevation = CardDefaults.elevatedCardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xff2D2D2D)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp)){
            LineChart(
                labelProperties = labelProperties,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 22.dp),
                data = data,
                animationMode = AnimationMode.Together(delayBuilder = {
                    it * 500L
                }),
                gridProperties = gridProperties,
                dividerProperties = dividerProperties,
                popupProperties = PopupProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                    ),
                    contentBuilder = {
                        it.format(1) + " Million"
                    },
                    containerColor = Color(0xff414141)
                ),
                indicatorProperties = HorizontalIndicatorProperties(
                    textStyle = TextStyle(
                        fontSize = 11.sp,
                        color = Color.White,
                    ),
                    contentBuilder = {
                        it.format(1) + " M"
                    },
                ),
                labelHelperProperties = LabelHelperProperties(textStyle = TextStyle(fontSize = 12.sp, color = Color.White)),
                curvedEdges = false
            )
        }
    }
}