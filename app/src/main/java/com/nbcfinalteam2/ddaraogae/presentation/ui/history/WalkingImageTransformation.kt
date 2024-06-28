package com.nbcfinalteam2.ddaraogae.presentation.ui.history

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.nbcfinalteam2.ddaraogae.R
import jp.wasabeef.glide.transformations.BitmapTransformation
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class WalkingImageTransformation(private val timeTaken: String, private val distance: String, private val dogName: String) : BitmapTransformation() {
    override fun transform(
        context: Context,
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        result.setHasAlpha(true)

        val canvas = Canvas(result)
        val paint = Paint()

        val textPaint = TextPaint(paint)
        textPaint.textSize = 40f
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.color = Color.WHITE
        textPaint.textAlign = Paint.Align.RIGHT

        // 위치 설정
        val timeTakenWidth = getTextWidth(timeTaken, textPaint)
        val distanceWidth = getTextWidth(distance, textPaint)
        val dogNameWidth = getTextWidth(dogName, textPaint)

        val timeTakenX = result.width - 50f // 오른쪽 여백
        val timeTakenY = textPaint.textSize + 30f // 상단 여백
        val distanceX = result.width - 50f
        val distanceY = timeTakenY + textPaint.textSize + 30f
        val dogNameX = result.width - 50f
        val dogNameY = distanceY + textPaint.textSize + 30f

        //이미지 그리기
        canvas.drawBitmap(toTransform, 0f, 0f, null)

        //텍스트 배경 그리기
        val backgroundPaint = Paint()
        backgroundPaint.color = ContextCompat.getColor(context, R.color.light_brown)
        backgroundPaint.alpha = 255

        val timeTakenRect = RectF(
            timeTakenX - timeTakenWidth - 10f,
            timeTakenY - textPaint.textSize - 5f,
            timeTakenX + 10f,
            timeTakenY + 10f
        )
        canvas.drawRect(timeTakenRect, backgroundPaint)

        val distanceRect = RectF(
            distanceX - distanceWidth - 10f,
            distanceY - textPaint.textSize - 5f,
            distanceX + 10f,
            distanceY + 10f
        )
        canvas.drawRect(distanceRect, backgroundPaint)

        val nameRect = RectF(
            dogNameX - dogNameWidth - 10f,
            dogNameY - textPaint.textSize - 5f,
            dogNameX + 10f,
            dogNameY + 10f
        )
        canvas.drawRect(nameRect, backgroundPaint)

        //텍스트 그리기
        canvas.drawText(timeTaken, timeTakenX, timeTakenY, textPaint)
        canvas.drawText(distance, distanceX, distanceY, textPaint)
        canvas.drawText(dogName, dogNameX, dogNameY, textPaint)

        return result
    }

    /**
     * 텍스트 너비 측정 함수
     */
    private fun getTextWidth(text: String, textPaint: TextPaint): Float {
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, Int.MAX_VALUE)
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1f)
            .setIncludePad(false)
            .build()
        return staticLayout.getLineWidth(0)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update("text_overlay_transformation_for_history_image".toByteArray(StandardCharsets.UTF_8))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WalkingImageTransformation

        if (distance != other.distance) return false
        if (timeTaken != other.timeTaken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = distance.hashCode()
        result = 31 * result + timeTaken.hashCode()
        return result
    }
}
