package com.example.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object LocalPhotoStorageManager {

    private const val DIRECTORY_NAME = "progress_photos"
    private const val TEMP_DIR_NAME = "temp_photos"

    fun getPhotosDirectory(context: Context): File {
        val dir = File(context.filesDir, DIRECTORY_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun createTempImageUriForCamera(context: Context): Pair<Uri, File> {
        val tempDir = File(context.cacheDir, TEMP_DIR_NAME)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        val file = File(tempDir, "temp_capture_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        return Pair(uri, file)
    }

    fun saveImageFromUri(context: Context, sourceUri: Uri): String {
        val targetDir = getPhotosDirectory(context)
        val targetFile = File(targetDir, "progress_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")

        context.contentResolver.openInputStream(sourceUri)?.use { input: InputStream ->
            FileOutputStream(targetFile).use { output: FileOutputStream ->
                input.copyTo(output)
            }
        }
        return targetFile.absolutePath
    }

    fun saveBitmapLocally(context: Context, bitmap: Bitmap): String {
        val targetDir = getPhotosDirectory(context)
        val targetFile = File(targetDir, "progress_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg")
        FileOutputStream(targetFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out)
        }
        return targetFile.absolutePath
    }

    fun deleteLocalPhotoFile(filePath: String) {
        try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {
            // Ignore file cleanup errors
        }
    }

    /**
     * Generates illustrative vector-rendered baseline posture visual guide images
     * to populate initial comparison milestones safely offline.
     */
    fun createSamplePostureBitmap(
        title: String,
        subtitle: String,
        isImproved: Boolean,
        angle: String
    ): Bitmap {
        val width = 600
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Background
        val bgPaint = Paint().apply {
            color = if (isImproved) Color.parseColor("#1B2A22") else Color.parseColor("#24222A")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // Grid lines to represent posture alignment grid
        val gridPaint = Paint().apply {
            color = Color.parseColor("#33FFFFFF")
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        for (y in 80 until height step 60) {
            canvas.drawLine(0f, y.toFloat(), width.toFloat(), y.toFloat(), gridPaint)
        }
        for (x in 60 until width step 60) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), gridPaint)
        }

        // Plumb line (central alignment reference)
        val plumbPaint = Paint().apply {
            color = if (isImproved) Color.parseColor("#4CAF50") else Color.parseColor("#FF9800")
            strokeWidth = 3f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(width / 2f, 100f, width / 2f, height - 100f, plumbPaint)

        // Posture silhouette drawing
        val figurePaint = Paint().apply {
            color = if (isImproved) Color.parseColor("#81C784") else Color.parseColor("#FFB74D")
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val centerX = width / 2f
        val shiftX = if (isImproved) 0f else 15f // Slight forward lean in unimproved

        // Head
        canvas.drawCircle(centerX + (shiftX * 0.8f), 200f, 40f, figurePaint)

        // Neck / Cervical Spine
        canvas.drawRect(centerX - 10f + (shiftX * 0.6f), 240f, centerX + 10f + (shiftX * 0.6f), 270f, figurePaint)

        // Thoraco-lumbar Torso
        val torsoRect = RectF(centerX - 60f + (shiftX * 0.3f), 270f, centerX + 60f + (shiftX * 0.3f), 480f)
        canvas.drawRoundRect(torsoRect, 20f, 20f, figurePaint)

        // Pelvis & Legs
        canvas.drawRoundRect(RectF(centerX - 50f, 480f, centerX - 10f, 680f), 15f, 15f, figurePaint)
        canvas.drawRoundRect(RectF(centerX + 10f, 480f, centerX + 50f, 680f), 15f, 15f, figurePaint)

        // Text Banner
        val textBgPaint = Paint().apply {
            color = Color.parseColor("#D9000000")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, height - 120f, width.toFloat(), height.toFloat(), textBgPaint)

        val titlePaint = Paint().apply {
            color = Color.WHITE
            textSize = 28f
            isFakeBoldText = true
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(title, width / 2f, height - 75f, titlePaint)

        val subtitlePaint = Paint().apply {
            color = Color.parseColor("#B0BEC5")
            textSize = 20f
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("$subtitle • $angle", width / 2f, height - 35f, subtitlePaint)

        return bitmap
    }
}
