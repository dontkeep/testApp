package com.exal.testapp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ozcanalasalvar.datepicker.compose.component.SelectorView
import com.ozcanalasalvar.datepicker.compose.datepicker.WheelDatePicker
import com.ozcanalasalvar.datepicker.model.Date
import com.ozcanalasalvar.datepicker.ui.theme.PickerTheme
import com.ozcanalasalvar.datepicker.ui.theme.colorLightPrimary
import com.ozcanalasalvar.datepicker.ui.theme.colorLightTextPrimary
import com.ozcanalasalvar.datepicker.ui.theme.lightPallet
import com.ozcanalasalvar.datepicker.utils.DateUtils
import com.ozcanalasalvar.datepicker.utils.daysOfDate
import com.ozcanalasalvar.datepicker.utils.monthsOfDate
import com.ozcanalasalvar.datepicker.utils.withDay
import com.ozcanalasalvar.datepicker.utils.withMonth
import com.ozcanalasalvar.datepicker.utils.withYear
import com.ozcanalasalvar.wheelview.SelectorOptions
import com.ozcanalasalvar.wheelview.WheelView
import java.text.DateFormatSymbols

@Composable
fun ScrollBoxesSmooth() {
    CustomWheelDatePicker(
        offset = 4,
        yearsRange = IntRange(1923, 2121),
        startDate = Date(DateUtils.getCurrentTime()),
        textSize = 16,
        selectorEffectEnabled = true,
        onDateChanged = {month, year ->
            val fixedYear: Int = year - 1
            val fixedMonth: Int = when (month) {
                0 -> {
                    11
                } else -> {
                    month - 1
                }
            }

            Log.d("TAG", "ScrollBoxesSmooth: $fixedMonth $fixedYear")
        }
    )
}

@Composable
fun CustomWheelDatePicker(
    offset: Int = 4,
    yearsRange: IntRange = IntRange(1923, 2121),
    startDate: Date = Date(DateUtils.getCurrentTime()),
    textSize: Int = 13,
    selectorEffectEnabled: Boolean = true,
    onDateChanged: (Int, Int) -> Unit = { _, _, -> },
    darkModeEnabled: Boolean = true,
) {

    var selectedDate by remember { mutableStateOf(startDate) }

    val months = selectedDate.monthsOfDate()

    val years = mutableListOf<Int>().apply {
        for (year in yearsRange) {
            add(year)
        }
    }

    LaunchedEffect(selectedDate) {
        onDateChanged(selectedDate.month, selectedDate.year)
    }

    val fontSize = maxOf(13, minOf(19, textSize))


    Box(
        modifier = Modifier
            .height(185.dp)
            .width(120.dp)
            .background(if (darkModeEnabled) PickerTheme.colors.primary else colorLightPrimary),
        contentAlignment = Alignment.Center
    ) {

        val height = (fontSize + 11).dp


        Row(
            modifier = Modifier
                .padding(start = 8.dp, end = 8.dp)
        ) {
            WheelView(modifier = Modifier.weight(2f),
                itemSize = DpSize(50.dp, height),
                selection = maxOf(months.indexOf(selectedDate.month), 0),
                itemCount = months.size,
                rowOffset = offset,
                selectorOption = SelectorOptions().copy(selectEffectEnabled = selectorEffectEnabled, enabled = false),
                onFocusItem = {
                    selectedDate = selectedDate.withMonth(months[it])
                },
                content = {
                    Text(
                        text = DateFormatSymbols().months[months[it]].take(3),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.width(150.dp),
                        fontSize = fontSize.sp,
                        color = if (darkModeEnabled) PickerTheme.colors.textPrimary else colorLightTextPrimary
                    )
                })


            WheelView(modifier = Modifier.weight(1f),
                itemSize = DpSize(150.dp, height),
                selection = years.indexOf(selectedDate.year),
                itemCount = years.size,
                rowOffset = offset,
                isEndless = years.size > offset * 2,
                selectorOption = SelectorOptions().copy(selectEffectEnabled = selectorEffectEnabled, enabled = false),
                onFocusItem = {
                    selectedDate = selectedDate.withYear(years[it])
                },
                content = {
                    Text(
                        text = years[it].toString(),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.width(100.dp),
                        fontSize = fontSize.sp,
                        color = if (darkModeEnabled) PickerTheme.colors.textPrimary else colorLightTextPrimary
                    )
                })
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (darkModeEnabled) PickerTheme.pallets else lightPallet
                    )
                ),
        ) {}

        SelectorView(darkModeEnabled= darkModeEnabled, offset = offset)
    }
}