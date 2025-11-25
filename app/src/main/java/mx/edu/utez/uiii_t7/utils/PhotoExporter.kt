package mx.edu.utez.uiii_t7.utils

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore

object PhotoExporter {

    fun copyAllPhotosToGallery(context: Context): Int {
        val srcDir = context.filesDir
        val photos = srcDir.listFiles { f -> f.extension == "jpg" } ?: return 0

        var copied = 0

        photos.forEach { file ->
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/TripPhotos/")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { out ->
                    file.inputStream().use { input ->
                        input.copyTo(out)
                        copied++
                    }
                }
            }
        }

        return copied
    }
}