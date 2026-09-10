package cocm.glass.note.pr.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class DrawingManager(private val context: Context) {
    
    private val drawingsDir: File by lazy {
        File(context.filesDir, "drawings").apply {
            if (!exists()) mkdirs()
        }
    }
    
    suspend fun saveDrawing(bitmap: Bitmap): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = "${UUID.randomUUID()}.png"
            val file = File(drawingsDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            Result.success(fileName)
        } catch (e: Exception) {
            Log.e("DrawingManager", "Error saving drawing", e)
            Result.failure(e)
        }
    }
    
    fun getDrawingFile(fileName: String): File {
        return File(drawingsDir, fileName)
    }
    
    fun deleteDrawing(fileName: String): Boolean {
        return try {
            val file = File(drawingsDir, fileName)
            file.delete()
        } catch (e: Exception) {
            Log.e("DrawingManager", "Error deleting drawing", e)
            false
        }
    }
    
    fun drawingExists(fileName: String): Boolean {
        return File(drawingsDir, fileName).exists()
    }
    
    suspend fun getDrawingBitmap(fileName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(drawingsDir, fileName)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DrawingManager", "Error loading drawing", e)
            null
        }
    }
}
