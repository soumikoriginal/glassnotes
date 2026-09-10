package cocm.glass.note.pr.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageManager(private val context: Context) {
    
    private val imagesDir: File by lazy {
        File(context.filesDir, "images").apply {
            if (!exists()) mkdirs()
        }
    }
    
    suspend fun saveImage(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open image"))
            
            // Decode and compress the image
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (bitmap == null) {
                return@withContext Result.failure(Exception("Cannot decode image"))
            }
            
            // Scale down if too large
            val maxDimension = 2048
            val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
                val scale = minOf(
                    maxDimension.toFloat() / bitmap.width,
                    maxDimension.toFloat() / bitmap.height
                )
                val newWidth = (bitmap.width * scale).toInt()
                val newHeight = (bitmap.height * scale).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true).also {
                    bitmap.recycle()
                }
            } else {
                bitmap
            }
            
            // Save to file
            val fileName = "${UUID.randomUUID()}.jpg"
            val file = File(imagesDir, fileName)
            FileOutputStream(file).use { out ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            scaledBitmap.recycle()
            
            Result.success(fileName)
        } catch (e: Exception) {
            Log.e("ImageManager", "Error saving image", e)
            Result.failure(e)
        }
    }
    
    fun getImageFile(fileName: String): File {
        return File(imagesDir, fileName)
    }
    
    fun deleteImage(fileName: String): Boolean {
        return try {
            val file = File(imagesDir, fileName)
            file.delete()
        } catch (e: Exception) {
            Log.e("ImageManager", "Error deleting image", e)
            false
        }
    }
    
    fun imageExists(fileName: String): Boolean {
        return File(imagesDir, fileName).exists()
    }
    
    suspend fun getImageBitmap(fileName: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = File(imagesDir, fileName)
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ImageManager", "Error loading image", e)
            null
        }
    }
}
