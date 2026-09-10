package cocm.glass.note.pr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class ImageHelper(private val context: Context) {
    
    private val imagesDir: File by lazy {
        File(context.filesDir, "images").apply {
            if (!exists()) mkdirs()
        }
    }

    /**
     * Save an image from URI to internal storage
     * Returns the internal file path or null if failed
     */
    fun saveImageFromUri(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            if (bitmap == null) return null
            
            // Resize if too large
            val resizedBitmap = resizeBitmapIfNeeded(bitmap, MAX_IMAGE_DIMENSION)
            
            // Generate unique filename
            val filename = "IMG_${UUID.randomUUID()}.jpg"
            val file = File(imagesDir, filename)
            
            // Save compressed image
            FileOutputStream(file).use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            
            // Cleanup
            if (resizedBitmap != bitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()
            
            file.absolutePath
        } catch (e: Exception) {
            Log.e("ImageHelper", "Failed to save image", e)
            null
        }
    }

    /**
     * Delete an image file
     */
    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ImageHelper", "Failed to delete image", e)
            false
        }
    }

    /**
     * Check if image file exists
     */
    fun imageExists(imagePath: String): Boolean {
        return File(imagePath).exists()
    }

    /**
     * Get file size in bytes
     */
    fun getImageSize(imagePath: String): Long {
        return try {
            File(imagePath).length()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Get total images storage size
     */
    fun getTotalImagesSize(): Long {
        return try {
            imagesDir.listFiles()?.sumOf { it.length() } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Clear all cached images (use with caution)
     */
    fun clearAllImages(): Boolean {
        return try {
            imagesDir.listFiles()?.forEach { it.delete() }
            true
        } catch (e: Exception) {
            Log.e("ImageHelper", "Failed to clear images", e)
            false
        }
    }

    private fun resizeBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val scale = if (width > height) {
            maxDimension.toFloat() / width
        } else {
            maxDimension.toFloat() / height
        }

        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    companion object {
        private const val MAX_IMAGE_DIMENSION = 1920
        private const val JPEG_QUALITY = 85
    }
}
