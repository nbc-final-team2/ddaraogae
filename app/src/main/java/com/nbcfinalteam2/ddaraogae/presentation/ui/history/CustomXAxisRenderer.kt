package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.nbcfinalteam2.ddaraogae.R

class CustomXAxisRenderer(
    private val context: Context,
    viewPortHandler: ViewPortHandler?,
    xAxis: XAxis?,
    transformer: Transformer?,
    private val currentDate: String,
    private val dates: List<String>
) : XAxisRenderer(viewPortHandler, xAxis, transformer) {

    override fun drawLabel(
        c: Canvas,
        formattedLabel: String?,
        x: Float,
        y: Float,
        anchor: MPPointF,
        angleDegrees: Float
    ) {
        val paintColor = if (formattedLabel == currentDate) {
            ContextCompat.getColor(context, R.color.orange)
        } else {
            ContextCompat.getColor(context, R.color.black)
        }
        mAxisLabelPaint.color = paintColor
        super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees)
    }
}




