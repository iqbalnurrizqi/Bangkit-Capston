package com.example.capstoneproject4.data.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.capstoneproject4.ml.ModelSkinDisease
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class SkinDiseaseClassifierHelper(private val context: Context) {

    // Lazy initialization of the model
    private val model: ModelSkinDisease by lazy {
        ModelSkinDisease.newInstance(context)
    }

    fun classifySkinImageWithThreshold(imageUri: Uri, threshold: Float = 75.0f): List<String> {
        val predictions = classifySkinImage(imageUri)
        return predictions.filter { it.second > threshold }.map { it.first }
    }

    fun classifySkinImage(imageUri: Uri): List<Pair<String, Float>> {
        val bitmap = getBitmapFromUri(imageUri)
        val processedBitmap = ensureBitmapConfig(bitmap)
        val byteBuffer = convertBitmapToByteBuffer(processedBitmap)

        // Create input feature
        val inputFeature = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature.loadBuffer(byteBuffer)

        // Run inference
        val outputs = model.process(inputFeature)
        val outputFeature = outputs.outputFeature0AsTensorBuffer

        // Return all predictions with confidence
        return getAllPredictions(outputFeature)
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(224 * 224)
        resizedBitmap.getPixels(intValues, 0, 224, 0, 0, 224, 224)

        for (pixelValue in intValues) {
            val r = (pixelValue shr 16 and 0xFF) / 255.0f
            val g = (pixelValue shr 8 and 0xFF) / 255.0f
            val b = (pixelValue and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        return byteBuffer
    }

    private fun getAllPredictions(outputFeature: TensorBuffer): List<Pair<String, Float>> {
        val scores = outputFeature.floatArray
        val labels = getLabels()

        // Gabungkan label dengan confidence score
        return labels.mapIndexed { index, label ->
            label to scores.getOrElse(index) { 0.0f }
        }
    }


    private fun getLabels(): List<String> {
        return listOf("Acne", "Blackhead", "Darkspot", "Enlarged Pore", "Redness", "Wrinkles")
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Decode bitmap using ImageDecoder
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
            } else {
                // Decode bitmap using MediaStore for older APIs
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to decode bitmap from Uri: $uri", e)
        }
    }

    private fun ensureBitmapConfig(bitmap: Bitmap): Bitmap {
        return if (bitmap.config != Bitmap.Config.ARGB_8888) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }
    }

    fun closeModel() {
        model.close()
    }
}