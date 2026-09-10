package cocm.glass.note.pr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class DrawingHelper(private val context: Context) {
    
    private val drawingsDir: File by lazy {
        File(context.filesDir, "drawings").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Save a drawing bitmap to internal storage
     * Returns the internal file path or null if failed
     */
    fun saveDrawing(bitmap: Bitmap): String? {
        return try {
            // Generate unique filename
            val filename = "DRAW_${UUID.randomUUID()}.png"
            val file = File(drawingsDir, filename)
            
            // Save as PNG to preserve transparency
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            file.absolutePath
        } catch (e: Exception) {
            Log.e("DrawingHelper", "Failed to save drawing", e)
            null
        }
    }

    /**
     * Load a drawing bitmap from storage
     */
    fun loadDrawing(drawingPath: String): Bitmap? {
        return try {
            val file = File(drawingPath)
            if (file.exists()) {
                BitmapFactory.decodeFile(drawingPath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DrawingHelper", "Failed to load drawing", e)
            null
        }
    }

    /**
     * Delete a drawing file
     */
    fun deleteDrawing(drawingPath: String): Boolean {
        return try {
            val file = File(drawingPath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("DrawingHelper", "Failed to delete drawing", e)
            false
        }
    }

    /**
     * Check if drawing file exists
     */
    fun drawingExists(drawingPath: String): Boolean {
        return File(drawingPath).exists()
    }

    /**
     * Get total drawings storage size
     */
    fun getTotalDrawingsSize(): Long {
        return try {
            drawingsDir.listFiles()?.sumOf { it.length() } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Clear all drawings (use with caution)
     */
    fun clearAllDrawings(): Boolean {
        return try {
            drawingsDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            Log.e("DrawingHelper", "Failed to clear drawings", e)
            false
        }
    }
}
