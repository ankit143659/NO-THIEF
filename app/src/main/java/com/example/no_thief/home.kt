package com.example.no_thief

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.FileChannel.MapMode.READ_ONLY
import java.util.concurrent.Executor

class home : Fragment() {

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricPromptExecutor: Executor

    private lateinit var requestCameraPermission: ActivityResultLauncher<String>
    private var savedFaceFeatures: FloatArray? = null
    private lateinit var tfliteInterpreter: Interpreter

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (savedFaceFeatures == null) {
            bitmap?.let {
                extractFaceFeatures(it) { features ->
                    savedFaceFeatures = features
                    saveFaceImage(it, "saved_face_image.png")
                    Toast.makeText(requireContext(), "Face captured and saved successfully", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            bitmap?.let {
                extractFaceFeatures(it) { newFaceFeatures ->
                    if (savedFaceFeatures != null) {
                        val isMatch =
                            newFaceFeatures?.let { it1 -> compareFaces(savedFaceFeatures!!, it1) }
                        if (isMatch == true) {
                            Toast.makeText(requireContext(), "User matched", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "User not matched", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "No saved face for comparison", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val authButton: Button = view.findViewById(R.id.auth)
        val checkButton: Button = view.findViewById(R.id.check)

        requestCameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                takePictureLauncher.launch(null)
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

        biometricPromptExecutor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(requireActivity(), biometricPromptExecutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(requireContext(), "Authentication error: $errString", Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(requireContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(requireContext(), "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        authButton.setOnClickListener {
            showBiometricPrompt()
        }

        checkButton.setOnClickListener {
            if (savedFaceFeatures == null) {
                Toast.makeText(requireContext(), "Please complete authentication", Toast.LENGTH_SHORT).show()
            } else {
                takePictureLauncher.launch(null)
            }
        }

        // Initialize TensorFlow Lite interpreter
        tfliteInterpreter = Interpreter(loadModelFile("face_recognition_model.tflite"))

        return view
    }

    private fun showBiometricPrompt() {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Authenticate with your fingerprint")
            .setNegativeButtonText("Cancel")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun saveFaceImage(bitmap: Bitmap, filename: String) {
        try {
            val fileOutputStream = requireContext().openFileOutput(filename, Context.MODE_PRIVATE)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun extractFaceFeatures(bitmap: Bitmap, callback: (FloatArray?) -> Unit) {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector = FaceDetection.getClient(options)
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        detector.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    // Here we need to convert face data to embeddings
                    val faceFeatures = extractFaceEmbedding(face)
                    callback(faceFeatures)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    private fun extractFaceEmbedding(face: Face): FloatArray {
        // Placeholder method: Extract face embeddings from the face object.
        // This requires a pre-trained model to convert face data to embeddings.
        // For this example, we are returning an empty array.
        return FloatArray(128) // Example size, replace with actual model output size
    }

    private fun compareFaces(savedFeatures: FloatArray, newFeatures: FloatArray): Boolean {
        // Compare features using Euclidean distance or cosine similarity
        val distance = euclideanDistance(savedFeatures, newFeatures)
        return distance < 0.6 // Threshold, adjust based on model performance
    }

    private fun euclideanDistance(vec1: FloatArray, vec2: FloatArray): Float {
        var sum = 0f
        for (i in vec1.indices) {
            val diff = vec1[i] - vec2[i]
            sum += diff * diff
        }
        return Math.sqrt(sum.toDouble()).toFloat()
    }

    private fun loadModelFile(modelFileName: String): MappedByteBuffer {
        val fileDescriptor = requireContext().assets.openFd(modelFileName)
        val fileInputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(READ_ONLY, startOffset, declaredLength)
    }
}
