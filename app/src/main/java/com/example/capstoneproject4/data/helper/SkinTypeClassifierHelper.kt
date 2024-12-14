package com.example.capstoneproject4.data.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.capstoneproject4.ml.ModelSkinType
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SkinTypeClassifierHelper(private val context: Context) {
    // Lazy initialization of the model
    private val model: ModelSkinType by lazy {
        ModelSkinType.newInstance(context)
    }

    fun getSkinType(imageUri: Uri): String {
        val (skinType, confidence) = classifySkinType(imageUri)
        return if (confidence > 80.0f) skinType else "Unknown"
    }

    fun classifySkinType(imageUri: Uri): Pair<String, Float> {
        val bitmap = getBitmapFromUri(imageUri)
        val byteBuffer = convertBitmapToByteBuffer(bitmap)

        // Create input tensor for the model
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Get the prediction with the highest confidence
        return getHighestConfidencePrediction(outputFeature0)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        // Resize bitmap to match model input dimensions (224x224)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

        // Allocate buffer with capacity for 1x224x224x3 (FLOAT32)
        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        buffer.order(ByteOrder.nativeOrder())

        // Access pixels and fill the buffer
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = resizedBitmap.getPixel(x, y)
                buffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // Red
                buffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // Green
                buffer.putFloat(((pixel shr 0) and 0xFF) / 255.0f)  // Blue
            }
        }

        return buffer
    }

    private fun getHighestConfidencePrediction(outputFeature: TensorBuffer): Pair<String, Float> {
        val scores = outputFeature.floatArray
        val labels = getLabels() // Fetch skin type labels
        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1

        return if (maxIndex != -1 && maxIndex < labels.size) {
            Pair(labels[maxIndex], scores[maxIndex])
        } else {
            Pair("Unknown", 0.0f)
        }
    }

    private fun getLabels(): List<String> {
        return listOf("Oily", "Dry", "Normal", "Combination") // Example skin type labels
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.createSource(context.contentResolver, uri)
        } else {
            @Suppress("DEPRECATION")
            return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        val originalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(source)
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        Log.d("BitmapConfig", "Original Config: ${originalBitmap.config}")

        // Pastikan bitmap menggunakan ARGB_8888
        val convertedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)

        Log.d("BitmapConfig", "Converted Config: ${convertedBitmap.config}")

        return convertedBitmap
    }

    fun closeModel() {
        model.close()
    }
}